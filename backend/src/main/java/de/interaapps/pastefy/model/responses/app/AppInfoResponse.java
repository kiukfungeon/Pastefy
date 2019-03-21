package de.interaapps.pastefy.model.responses.app;

import de.interaapps.pastefy.Pastefy;
import org.javawebstack.webutils.config.Config;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class AppInfoResponse {

    public String customLogo;
    public String customName;
    public boolean aiEnabled = false;
    public boolean analyticsEnabled = false;
    public Map<String, String> customFooter;
    public boolean loginRequiredForRead;
