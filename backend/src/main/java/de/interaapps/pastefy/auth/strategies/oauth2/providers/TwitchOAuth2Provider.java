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
