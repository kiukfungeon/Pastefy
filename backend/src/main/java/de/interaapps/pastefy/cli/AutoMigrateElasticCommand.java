package de.interaapps.pastefy.cli;

import de.interaapps.pastefy.Pastefy;
import de.interaapps.pastefy.helper.elastic.ElasticMigrator;
import org.javawebstack.orm.ORM;
import picocli.CommandLine;

import java.util.concurrent.Callable;

@CommandLine.Command(
        name = "automigrateelastic",
