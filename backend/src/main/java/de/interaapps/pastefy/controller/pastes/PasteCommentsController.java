package de.interaapps.pastefy.controller.pastes;

import de.interaapps.pastefy.controller.HttpController;
import de.interaapps.pastefy.exceptions.HTTPException;
import de.interaapps.pastefy.exceptions.NotFoundException;
import de.interaapps.pastefy.exceptions.PermissionsDeniedException;
import de.interaapps.pastefy.model.database.AuthKey;
import de.interaapps.pastefy.model.database.Paste;
import de.interaapps.pastefy.model.database.PasteComment;
import de.interaapps.pastefy.model.database.User;
import de.interaapps.pastefy.model.requests.paste.CreatePasteCommentRequest;
import de.interaapps.pastefy.model.responses.ActionResponse;
import de.interaapps.pastefy.model.responses.paste.PasteCommentMarkerResponse;
import de.interaapps.pastefy.model.responses.paste.PasteCommentResponse;
import de.interaapps.pastefy.model.responses.user.PublicUserResponse;
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

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@PathPrefix("/api/v2/paste")
public class PasteCommentsController extends HttpController {
    private static final int DEFAULT_PAGE_LIMIT = 10;
    private static final int MAX_PAGE_LIMIT = 30;
    private static final int MAX_CONTENT_LENGTH = 2000;
    private static final int MAX_LINE_RANGE = 1000;

    @Get("/{pasteId}/comments")
    @With({"auth-login-required-read", "awaiting-access-check", "blocked-check"})
    public List<PasteCommentResponse> getComments(Exchange exchange, @Path("pasteId") String pasteId, @Attrib("user") User user) {
        Paste.getAccessiblePasteOrFail(pasteId, user);

        int page = positiveQueryParameter(exchange, "page", 1);
        int pageLimit = Math.min(positiveQueryParameter(exchange, "page_limit", DEFAULT_PAGE_LIMIT), MAX_PAGE_LIMIT);

        Query<PasteComment> query = Repo.get(PasteComment.class)
                .where("paste", pasteId)
                .whereNull("parentId");

        if (exchange.query("line") != null) {
            int line = positiveQueryParameter(exchange, "line", 1);
            query.where("lineFrom", line);
        }

        return query
                .order("created_at", true)
                .limit(pageLimit)
                .offset((page - 1) * pageLimit)
                .all()
                .stream()
                .map(comment -> new PasteCommentResponse(comment, true))
                .collect(Collectors.toList());
    }

    @Get("/{pasteId}/comments/markers")
    @With({"auth-login-required-read", "awaiting-access-check", "blocked-check"})
    public List<PasteCommentMarkerResponse> getLineMarkers(@Path("pasteId") String pasteId, @Attrib("user") User user) {
        Paste.getAccessiblePasteOrFail(pasteId, user);
