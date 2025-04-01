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
