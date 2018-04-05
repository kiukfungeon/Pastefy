package de.interaapps.pastefy.auth.strategies.oauth2.providers;

import de.interaapps.pastefy.auth.strategies.oauth2.OAuth2Profile;
import de.interaapps.pastefy.auth.strategies.oauth2.OAuth2Provider;
import org.javawebstack.abstractdata.AbstractElement;
import org.javawebstack.abstractdata.AbstractObject;
import org.javawebstack.httpclient.HTTPClient;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.List;

public class InteraAppsOAuth2Provider implements OAuth2Provider {
    private String clientId;
    private String secret;
    private String[] scopes = {"user:read"};
    private HTTPClient interaAppsAccountsClient;

    public InteraAppsOAuth2Provider(String clientId, String secret) {
        this.clientId = clientId;
        this.secret = secret;
        interaAppsAccountsClient = new HTTPClient("https://accounts.interaapps.de/api/v2");
    }

    public InteraAppsOAuth2Provider setScopes(String... scopes) {
        this.scopes = scopes;
        return this;
    }

    public OAuth2Callback callback(AbstractObject queryParameters, String callbackUrl) {
        AbstractObject data = interaAppsAccountsClient.post("/authorization/oauth2/access_token")
                .jsonBodyElement(new AbstractObject()
                        .set("client_id", clientId)
                        .set("client_secret", secret)
                        .set("code", queryParameters.string("code"))
                )
