package de.interaapps.pastefy.auth;

import de.interaapps.pastefy.exceptions.AwaitingAccessException;
import de.interaapps.pastefy.model.database.User;
import org.javawebstack.http.router.Exchange;
import org.javawebstack.http.router.handler.Middleware;

public class AwaitingAccessMiddleware implements Middleware {
    @Override
