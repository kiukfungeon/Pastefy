package de.interaapps.pastefy.cli;

import picocli.CommandLine;

@CommandLine.Command(
    name = "pastefy",
    mixinStandardHelpOptions = true,
    version = "Pastefy CLI",
    description = "Command line interface for Pastefy",
        subcommands = {
            AutoMigrateCommand.class,
            StartServerCommand.class,
