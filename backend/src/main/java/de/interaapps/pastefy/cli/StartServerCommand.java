package de.interaapps.pastefy.cli;

import de.interaapps.pastefy.Pastefy;
import picocli.CommandLine;

import java.util.concurrent.Callable;

@CommandLine.Command(
    name = "start",
    mixinStandardHelpOptions = true,
    description = "Automatically migrate the database to the latest version"
)
