package de.interaapps.pastefy.controller.auth;

import de.interaapps.pastefy.Pastefy;
import de.interaapps.pastefy.auth.strategies.oauth2.OAuth2Provider;
import de.interaapps.pastefy.controller.HttpController;
import de.interaapps.pastefy.exceptions.AuthenticationException;
import de.interaapps.pastefy.exceptions.NotFoundException;
import de.interaapps.pastefy.model.database.AuthKey;
import de.interaapps.pastefy.model.database.User;
import de.interaapps.pastefy.model.requests.auth.InteraAppsExternalAccessRequest;
import org.javawebstack.http.router.Exchange;
import org.javawebstack.http.router.router.annotation.PathPrefix;
import org.javawebstack.http.router.router.annotation.params.Body;
import org.javawebstack.http.router.router.annotation.verbs.Post;
import org.javawebstack.orm.Repo;
import org.javawebstack.webutils.config.Config;

import java.util.Map;

@PathPrefix("/api/v2/auth")
public class InteraAppsExternalAccessController extends HttpController {
    @Post("/iaea")
    public String iaea(@Body InteraAppsExternalAccessRequest request, Exchange exchange) {
        Map<String, OAuth2Provider> providers = Pastefy.getInstance().getOAuth2Strategy().getProviders();

        if (providers.containsKey("interaapps")) {

            Config config = Pastefy.getInstance().getConfig();

