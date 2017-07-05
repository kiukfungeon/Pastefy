package de.interaapps.pastefy.controller.user;

import de.interaapps.pastefy.controller.HttpController;
import de.interaapps.pastefy.model.database.AuthKey;
import de.interaapps.pastefy.model.database.Notification;
import de.interaapps.pastefy.model.database.User;
import de.interaapps.pastefy.model.responses.ActionResponse;
import org.javawebstack.http.router.Exchange;
import org.javawebstack.http.router.router.annotation.PathPrefix;
import org.javawebstack.http.router.router.annotation.With;
import org.javawebstack.http.router.router.annotation.params.Attrib;
import org.javawebstack.http.router.router.annotation.verbs.Get;
import org.javawebstack.http.router.router.annotation.verbs.Post;
import org.javawebstack.orm.Repo;
import org.javawebstack.orm.query.Query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@PathPrefix("/api/v2/user/notification")
public class NotificationController extends HttpController {
    /**
     * Send the logged in user a notification
     */
    @Post
    @With("auth")
    public ActionResponse add(@Attrib("user") User user, @Attrib("authkey") AuthKey authKey) {
        if (authKey != null)
            authKey.checkPermission("notifications:create");

