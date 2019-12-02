package de.interaapps.pastefy.model.redis;

import de.interaapps.pastefy.Pastefy;
import de.interaapps.pastefy.model.database.Paste;

public class PasteContentCache {
    public static String getCachedContent(Paste paste) {
        if (!Pastefy.getInstance().isRedisEnabled()) return null;
        try {
            return Pastefy.getInstance().getRedisClient().get("paste:" + paste.getId());
        } catch (Exception e) {
            System.out.println("Failed to get cached content for paste " + paste.getId());
            if (Pastefy.getInstance().isDevMode()) e.printStackTrace();
            return null;
        }
