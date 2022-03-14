package de.interaapps.pastefy.cli;


import de.interaapps.pastefy.model.database.Paste;
import de.interaapps.pastefy.model.elastic.ElasticPaste;
import de.interaapps.pastefy.model.minio.MinioPaste;
import org.javawebstack.orm.ORM;
import org.javawebstack.orm.Repo;
import picocli.CommandLine;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@CommandLine.Command(
        name = "movesmallfilestodb",
        mixinStandardHelpOptions = true,
        description = "Automatically moves small S3 files back to DB."
)
public class MoveSmallFilesToDBCommand implements Callable<Integer> {
    @CommandLine.Option(names = {"-s", "--size"}, description = "Batch size")
    private int batchSize = 30;

    @CommandLine.Option(names = {"-i", "--iterations"}, description = "Iterations")
    private int iterations = 20000;

    @CommandLine.Option(names = {"-t", "--threads"}, description = "Number of threads")
    private int threadCount = 32;

    @CommandLine.Option(names = {"-m", "--maxlength"}, description = "Max length of pastefy content")
    private int maxLength = 3000;

    @CommandLine.Option(names = {"-o", "--offset"}, description = "Offset")
    private int offset = 0;

    @Override
    public Integer call() throws Exception {
        System.out.println("syncing");
        final int size = this.batchSize;
        final int iterations = this.iterations;
        final int threadCount = 32;

        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
