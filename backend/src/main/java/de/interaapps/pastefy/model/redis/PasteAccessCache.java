package de.interaapps.pastefy.model.redis;

import de.interaapps.pastefy.Pastefy;
import de.interaapps.pastefy.model.database.Paste;
import redis.clients.jedis.Jedis;

public class PasteAccessCache {
    public static long getAccessCount(Paste paste) {
