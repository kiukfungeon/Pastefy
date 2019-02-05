package de.interaapps.pastefy.auth.strategies.oauth2.providers;

import de.interaapps.pastefy.auth.strategies.oauth2.OAuth2Profile;
import de.interaapps.pastefy.auth.strategies.oauth2.OAuth2Provider;
import org.javawebstack.abstractdata.AbstractObject;
import org.javawebstack.abstractdata.util.QueryString;
import org.javawebstack.http.router.util.MimeType;
import org.javawebstack.httpclient.HTTPClient;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class TwitchOAuth2Provider implements OAuth2Provider {
    private String clientId;
    private String secret;
    private String[] scopes = {"user:read:email"}; // reference: https://dev.twitch.tv/docs/authentication/#scopes

    private boolean forceVerify = false;
    private String state;

    public TwitchOAuth2Provider(String clientId, String secret) {
        this.clientId = clientId;
        this.secret = secret;
    }

    public TwitchOAuth2Provider setScopes(String... scopes) {
        this.scopes = scopes;
        return this;
    }

    public OAuth2Callback callback(AbstractObject queryParameters, String callbackUrl) {
        AbstractObject abstractObject = new HTTPClient("https://id.twitch.tv").post("/oauth2/token")
                .formBodyString(new QueryString()
                        .set("client_id", clientId)
                        .set("client_secret", secret)
                        .set("code", queryParameters.string("code"))
                        .set("grant_type", "authorization_code")
                        .set("redirect_uri", callbackUrl)
                        .toString())
                .header("Accept", MimeType.JSON.getMimeTypes().get(0))
                .data().object();

        if (abstractObject.has("scope")) {
            return new OAuth2Callback(this, abstractObject.string("access_token"), abstractObject.string("refresh_token"), clientId);
