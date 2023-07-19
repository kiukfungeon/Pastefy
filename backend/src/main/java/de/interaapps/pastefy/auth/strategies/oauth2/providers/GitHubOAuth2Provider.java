package de.interaapps.pastefy.auth.strategies.oauth2.providers;

import de.interaapps.pastefy.auth.strategies.oauth2.OAuth2Profile;
import de.interaapps.pastefy.auth.strategies.oauth2.OAuth2Provider;
import org.javawebstack.abstractdata.AbstractObject;
import org.javawebstack.abstractdata.util.QueryString;
import org.javawebstack.http.router.util.MimeType;
import org.javawebstack.httpclient.HTTPClient;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class GitHubOAuth2Provider implements OAuth2Provider {
    private String clientId;
    private String secret;
    private String[] scopes = {"read:user", "user:email"};

    public GitHubOAuth2Provider(String clientId, String secret) {
        this.clientId = clientId;
        this.secret = secret;
    }

    public GitHubOAuth2Provider setScopes(String... scopes) {
        this.scopes = scopes;
        return this;
    }

    public OAuth2Callback callback(AbstractObject queryParameters, String callbackUrl) {
        AbstractObject abstractObject = new HTTPClient("https://github.com").post("/login/oauth/access_token")
                .formBodyString(new QueryString()
                        .set("client_id", clientId)
                        .set("client_secret", secret)
                        .set("code", queryParameters.string("code"))
                        .toString())
                .header("Accept", MimeType.JSON.getMimeTypes().get(0))
                .data().object();


        if (abstractObject.has("scope")/* && abstractObject.get("scope").string().equals("read:user,user:email")*/) {
            return new OAuth2Callback(this, abstractObject.string("access_token"), abstractObject.has("refresh_token") ? abstractObject.string("refresh_token") : null);
        }

        return null;
    }

    public String redirect(String callbackUrl) {
