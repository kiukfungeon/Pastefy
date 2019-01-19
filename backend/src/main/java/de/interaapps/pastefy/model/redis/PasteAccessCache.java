package de.interaapps.pastefy.model.redis;

import de.interaapps.pastefy.Pastefy;
import de.interaapps.pastefy.model.database.Paste;
import redis.clients.jedis.Jedis;

public class PasteAccessCache {
    public static long getAccessCount(Paste paste) {
        if (!Pastefy.getInstance().isRedisEnabled()) return 0;
        try {
            return Long.parseLong(Pastefy.getInstance().getRedisClient().get("paste:" + paste.getId() + ":accessCount"));
        } catch (Exception e) {
            if (Pastefy.getInstance().isDevMode()) e.printStackTrace();
            return 0;
        }
    }

