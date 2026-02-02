package de.interaapps.pastefy.controller;

import de.interaapps.pastefy.exceptions.NotFoundException;
import de.interaapps.pastefy.helper.RequestHelper;
import de.interaapps.pastefy.model.database.AuthKey;
import de.interaapps.pastefy.model.database.Folder;
import de.interaapps.pastefy.model.database.User;
import de.interaapps.pastefy.model.requests.CreateFolderRequest;
import de.interaapps.pastefy.model.responses.ActionResponse;
import de.interaapps.pastefy.model.responses.folder.CreateFolderResponse;
import de.interaapps.pastefy.model.responses.folder.FolderResponse;
import org.javawebstack.http.router.Exchange;
import org.javawebstack.http.router.router.annotation.PathPrefix;
import org.javawebstack.http.router.router.annotation.With;
import org.javawebstack.http.router.router.annotation.params.Attrib;
import org.javawebstack.http.router.router.annotation.params.Body;
import org.javawebstack.http.router.router.annotation.params.Path;
import org.javawebstack.http.router.router.annotation.verbs.Delete;
import org.javawebstack.http.router.router.annotation.verbs.Get;
import org.javawebstack.http.router.router.annotation.verbs.Post;
import org.javawebstack.orm.Repo;
import org.javawebstack.orm.query.Query;

import java.util.List;
import java.util.stream.Collectors;

@PathPrefix("/api/v2/folder")
public class FolderController extends HttpController {
    @Post
    @With({"auth", "awaiting-access-check", "blocked-check"})
    public CreateFolderResponse createFolder(Exchange exchange, @Body CreateFolderRequest request, @Attrib("user") User user, @Attrib("authkey") AuthKey authKey) {
        if (authKey != null) {
            authKey.checkPermission("folders:create");
        }

        CreateFolderResponse response = new CreateFolderResponse();

        Folder folder = new Folder();
        folder.setUserId(user.getId());
        folder.setName(request.name);

        Folder parent = Repo.get(Folder.class).where("key", request.parent).first();

        if (parent != null && parent.getUserId().equals(user.getId())) {
            folder.setParent(parent);
        }

        folder.save();
        response.success = true;
        response.folder = new FolderResponse(folder);

        return response;
    }
