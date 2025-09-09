package de.interaapps.pastefy.controller.publicarea;

import de.interaapps.pastefy.controller.HttpController;
import de.interaapps.pastefy.exceptions.NotFoundException;
import de.interaapps.pastefy.model.database.User;
import de.interaapps.pastefy.model.responses.user.PublicUserResponse;
import org.javawebstack.http.router.router.annotation.PathPrefix;
import org.javawebstack.http.router.router.annotation.With;
import org.javawebstack.http.router.router.annotation.params.Path;
import org.javawebstack.http.router.router.annotation.verbs.Get;

@With("public-pastes-endpoint")
@PathPrefix("/api/v2/public/user")
public class PublicUserController extends HttpController {
