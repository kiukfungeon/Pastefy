package de.interaapps.pastefy.controller.pastes;

import de.interaapps.pastefy.Pastefy;
import de.interaapps.pastefy.controller.HttpController;
import de.interaapps.pastefy.exceptions.NotFoundException;
import de.interaapps.pastefy.exceptions.PastePrivateException;
import de.interaapps.pastefy.exceptions.PermissionsDeniedException;
import de.interaapps.pastefy.helper.RequestHelper;
import de.interaapps.pastefy.model.database.*;
import de.interaapps.pastefy.model.database.algorithm.PublicPasteEngagement;
import de.interaapps.pastefy.model.database.algorithm.TagListing;
import de.interaapps.pastefy.model.queryparams.PasteQueryParameters;
import de.interaapps.pastefy.model.requests.paste.AddFriendToPasteRequest;
import de.interaapps.pastefy.model.requests.paste.CreatePasteRequest;
import de.interaapps.pastefy.model.requests.paste.EditPasteRequest;
import de.interaapps.pastefy.model.responses.ActionResponse;
import de.interaapps.pastefy.model.responses.paste.CreatePasteResponse;
import de.interaapps.pastefy.model.responses.paste.PasteResponse;
import de.interaapps.pastefy.services.PasteService;
import org.javawebstack.abstractdata.AbstractElement;
import org.javawebstack.abstractdata.AbstractObject;
import org.javawebstack.http.router.Exchange;
import org.javawebstack.http.router.router.annotation.PathPrefix;
import org.javawebstack.http.router.router.annotation.With;
import org.javawebstack.http.router.router.annotation.params.Attrib;
import org.javawebstack.http.router.router.annotation.params.Body;
import org.javawebstack.http.router.router.annotation.params.Path;
import org.javawebstack.http.router.router.annotation.verbs.Delete;
import org.javawebstack.http.router.router.annotation.verbs.Get;
import org.javawebstack.http.router.router.annotation.verbs.Post;
import org.javawebstack.http.router.router.annotation.verbs.Put;
import org.javawebstack.orm.Repo;
import org.javawebstack.orm.query.Query;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@PathPrefix("/api/v2/paste")
public class PasteController extends HttpController {

    @Post
    @With({"rate-limiter", "auth-login-required-create", "awaiting-access-check", "blocked-check"})
    public CreatePasteResponse create(Exchange exchange, @Body CreatePasteRequest request, @Attrib("user") User user, @Attrib("authkey") AuthKey authKey) {
        if (authKey != null)
            authKey.checkPermission("pastes:create", "pastes:write");


        CreatePasteResponse response = new CreatePasteResponse();

        Paste paste = new Paste();

        Folder folder = Repo.get(Folder.class).where("key", request.folder).first();

        if (user != null) {
            paste.setUserId(user.getId());

            if (folder != null && folder.getUserId().equals(user.getId()))
                paste.setFolder(folder);
        }

        paste.setTitle(request.title);
        paste.setContent(request.content);
        paste.setEncrypted(request.encrypted);
        paste.setType(request.type);

        paste.setVisibility(request.visibility);

        if (request.forkedFrom != null) {
            Paste forkedFrom = Repo.get(Paste.class).where("key", request.forkedFrom).first();
            if (forkedFrom != null) {
                paste.setForkedFrom(request.forkedFrom);
                if (forkedFrom.isPublic()) {
                    Pastefy.getInstance().executeAsync(() -> PublicPasteEngagement.addInterestFromPaste(forkedFrom, 10));
                }
            }
        }


        if (request.expireAt != null && request.expireAt.length() >= 16) {
            paste.setExpireAt(request.expireAt);
        }

        paste.save();


        if (request.tags != null) {
            for (String tag : request.tags) {
                paste.addTag(tag);
            }
