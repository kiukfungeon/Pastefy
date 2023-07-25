package de.interaapps.pastefy.controller.user;

import de.interaapps.pastefy.controller.HttpController;
import de.interaapps.pastefy.exceptions.PermissionsDeniedException;
import de.interaapps.pastefy.helper.RequestHelper;
import de.interaapps.pastefy.model.database.*;
import de.interaapps.pastefy.model.queryparams.PasteQueryParameters;
import de.interaapps.pastefy.model.responses.folder.FolderResponse;
import de.interaapps.pastefy.model.responses.paste.PasteResponse;
import de.interaapps.pastefy.model.responses.user.UserPastesResponse;
import de.interaapps.pastefy.model.responses.user.UserResponse;
import de.interaapps.pastefy.services.PasteService;
import org.javawebstack.http.router.Exchange;
import org.javawebstack.http.router.router.annotation.PathPrefix;
import org.javawebstack.http.router.router.annotation.With;
import org.javawebstack.http.router.router.annotation.params.Attrib;
import org.javawebstack.http.router.router.annotation.verbs.Get;
import org.javawebstack.orm.Repo;
import org.javawebstack.orm.query.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@PathPrefix("/api/v2/user")
public class UserController extends HttpController {

    @Get
    public UserResponse getUser(@Attrib("user") User user) {
        return new UserResponse(user);
    }

    @Get("/overview")
    @With({"auth", "awaiting-access-check", "blocked-check"})
    public UserPastesResponse userPastes(Exchange exchange, @Attrib("user") User user, @Attrib("authkey") AuthKey authKey) {
        if (authKey != null && (!authKey.hasPermission("pastes:read") || !authKey.hasPermission("folders:read")))
            throw new PermissionsDeniedException();

        UserPastesResponse response = new UserPastesResponse();
        int page = 0;
