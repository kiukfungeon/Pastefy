package de.interaapps.pastefy.ai;

import de.interaapps.pastefy.Pastefy;
import de.interaapps.pastefy.jobs.BackgroundJobService;
import de.interaapps.pastefy.model.database.BackgroundJob;
import de.interaapps.pastefy.model.database.Paste;
import de.interaapps.pastefy.model.database.PasteAIInfo;
import de.interaapps.pastefy.model.database.algorithm.PublicPasteEngagement;
import org.javawebstack.abstractdata.AbstractArray;
import org.javawebstack.abstractdata.AbstractElement;
import org.javawebstack.abstractdata.AbstractObject;
import org.javawebstack.orm.Repo;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.HashSet;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

public class PasteAIInfoService {
    private static final Logger LOGGER = Logger.getLogger(PasteAIInfoService.class.getName());
    private static final int PROMPT_VERSION = 1;
    private static final Pattern VALID_TAG_PATTERN = Pattern.compile("^[a-z0-9-]{1,30}$");

    private final Pastefy pastefy;
    private final BackgroundJobService jobs;

    public PasteAIInfoService(Pastefy pastefy, BackgroundJobService jobs) {
        this.pastefy = pastefy;
        this.jobs = jobs;
        jobs.register(BackgroundJob.Type.PASTE_AI_INFO, this::handle);
    }

    public void start() {
        if (!pastefy.aiEnabled()
                || !pastefy.getConfig().get("ai.jobs.sweeperenabled", "false").equalsIgnoreCase("true")) return;

        long sweepInterval = Math.max(60_000, pastefy.getConfig().getInt("ai.jobs.sweepintervalmillis", 300_000));
        jobs.scheduleWithFixedDelay(this::sweepSafely, 0, sweepInterval, TimeUnit.MILLISECONDS);
    }

    public void enqueueIfEligible(Paste paste, boolean force) {
        if ((!pastefy.aiEnabled() || !isEligible(paste)) && !force) return;

        int sourceVersion = paste.getVersion() == null ? 0 : paste.getVersion();
        PasteAIInfo current = Repo.get(PasteAIInfo.class).get(paste.getId());
        if (isCurrent(current, sourceVersion) && !force) return;

        jobs.enqueue(BackgroundJob.Type.PASTE_AI_INFO, paste.getId(), sourceVersion, PROMPT_VERSION);
    }

    public void enqueueIfEligible(Paste paste) {
        enqueueIfEligible(paste, false);
    }

    public void enqueue(Paste paste) {
        enqueueIfEligible(paste, true);
    }


