package de.interaapps.pastefy.model.plugins;

import com.google.gson.Gson;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

public class PastefyPlugin {
    public PastefyPluginConfig config;
    public PastefyBackendPlugin backendPlugin;
    public String path;

    public PastefyPlugin(String folder) throws IOException {
