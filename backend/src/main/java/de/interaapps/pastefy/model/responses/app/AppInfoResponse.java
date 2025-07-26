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
    public boolean loginRequiredForCreate;

    public boolean encryptionIsDefault;
    public boolean publicPastesEnabled;

    public AppInfoResponse(Pastefy pastefy) {
        Config config = pastefy.getConfig();

        if (config.has("pastefy.info.custom.logo"))
            customLogo = config.get("pastefy.info.custom.logo");

        if (config.has("pastefy.info.custom.name"))
            customName = config.get("pastefy.info.custom.name");

        aiEnabled = pastefy.aiEnabled();
        analyticsEnabled = pastefy.analyticsEnabled();

