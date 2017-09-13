package de.interaapps.pastefy.cli;

import de.interaapps.pastefy.model.database.Paste;
import de.interaapps.pastefy.model.elastic.ElasticPaste;
import org.javawebstack.orm.Repo;
import picocli.CommandLine;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@CommandLine.Command(
        name = "syncelastic",
        mixinStandardHelpOptions = true,
        description = "Sync data to elasticsearch"
)
public class SyncToElasticCommand implements Callable<Integer> {
    @CommandLine.Option(names = {"-s", "--size"}, description = "Batch size")
    private int batchSize = 100;

