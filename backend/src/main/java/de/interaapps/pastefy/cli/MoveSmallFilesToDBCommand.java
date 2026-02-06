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
