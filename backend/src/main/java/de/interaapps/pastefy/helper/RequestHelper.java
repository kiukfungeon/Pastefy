package de.interaapps.pastefy.helper;

import de.interaapps.pastefy.Pastefy;
import de.interaapps.pastefy.exceptions.PermissionsDeniedException;
import de.interaapps.pastefy.model.database.Paste;
import de.interaapps.pastefy.model.database.PasteTag;
import de.interaapps.pastefy.model.database.User;
import org.javawebstack.abstractdata.AbstractObject;
import org.javawebstack.http.router.Exchange;
import org.javawebstack.orm.query.Query;

public class RequestHelper {

    public static void pagination(Query<?> query, Exchange exchange) {
        int page = 0;
        if (exchange.query("page") != null)
            page = Integer.parseInt(exchange.query("page")) - 1;

        int limit = 10;

        if (exchange.query("page_limit") != null)
            limit = exchange.query("page_limit", Integer.class);

        if (Pastefy.getInstance().getConfig().has("pastefy.paginaton.pagelimit")) {
            int configLimit = Pastefy.getInstance().getConfig().getInt("pastefy.paginaton.pagelimit", 50);
            if (limit > configLimit)
                limit = configLimit;
        }

        query.limit(limit).offset(page * limit);

        exchange.header("PAGINATION_LIMIT", String.valueOf(limit));
        exchange.header("PAGINATION_PAGE", String.valueOf(page));
    }
