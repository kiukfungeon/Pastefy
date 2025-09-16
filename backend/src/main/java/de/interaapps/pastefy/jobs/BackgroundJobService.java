package de.interaapps.pastefy.jobs;

import de.interaapps.pastefy.Pastefy;
import de.interaapps.pastefy.model.database.BackgroundJob;
import org.javawebstack.orm.Repo;
import org.javawebstack.orm.query.Query;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BackgroundJobService {
    private static final Logger LOGGER = Logger.getLogger(BackgroundJobService.class.getName());

    private final Pastefy pastefy;
    private final Map<BackgroundJob.Type, BackgroundJobHandler> handlers = new EnumMap<>(BackgroundJob.Type.class);
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private final ExecutorService workers;
    private final AtomicBoolean started = new AtomicBoolean(false);
    private final AtomicInteger activeWorkers = new AtomicInteger(0);
    private final int workerCount;

    public BackgroundJobService(Pastefy pastefy) {
        this.pastefy = pastefy;
        workerCount = Math.max(1, pastefy.getConfig().getInt("ai.jobs.workers", 2));
        workers = Executors.newFixedThreadPool(workerCount);
    }

    public void register(BackgroundJob.Type type, BackgroundJobHandler handler) {
        handlers.put(type, handler);
    }

    public void start() {
        if (!started.compareAndSet(false, true)) return;

        long pollInterval = Math.max(1000, pastefy.getConfig().getInt("ai.jobs.pollintervalmillis", 5000));
        scheduler.scheduleWithFixedDelay(this::pollSafely, 0, pollInterval, TimeUnit.MILLISECONDS);
    }

    public void scheduleWithFixedDelay(Runnable task, long initialDelay, long delay, TimeUnit unit) {
        scheduler.scheduleWithFixedDelay(task, initialDelay, delay, unit);
    }

    public synchronized void enqueue(BackgroundJob.Type type, int entityId, int sourceVersion, int promptVersion) {
        String key = type + ":" + entityId + ":" + sourceVersion + ":" + promptVersion;
        BackgroundJob existingJob = Repo.get(BackgroundJob.class).get(key);
        if (existingJob != null) {
            if (existingJob.status == BackgroundJob.Status.DONE) {
                Map<String, Object> update = new HashMap<>();
                update.put("status", BackgroundJob.Status.PENDING);
                update.put("attempts", 0);
                update.put("available_at", Timestamp.from(Instant.now()));
                update.put("lease_until", null);
                update.put("lease_token", null);
                update.put("last_error", null);
                Repo.get(BackgroundJob.class).where("key", key).update(update);
            }
            return;
        }

        BackgroundJob job = new BackgroundJob();
        job.key = key;
        job.type = type;
        job.entityId = entityId;
        job.sourceVersion = sourceVersion;
        job.promptVersion = promptVersion;
        job.availableAt = Timestamp.from(Instant.now());

        try {
            job.save();
        } catch (RuntimeException exception) {
            // Another instance may have inserted the deterministic key concurrently.
            if (Repo.get(BackgroundJob.class).get(key) == null) throw exception;
        }
    }

    private void pollSafely() {
        try {
            poll();
        } catch (RuntimeException exception) {
            LOGGER.log(Level.WARNING, "Unable to poll background jobs", exception);
        }
    }

    private void poll() {
        int availableWorkers = workerCount - activeWorkers.get();
        for (int i = 0; i < availableWorkers; i++) {
            BackgroundJob job = claimNext();
            if (job == null) return;

            activeWorkers.incrementAndGet();
