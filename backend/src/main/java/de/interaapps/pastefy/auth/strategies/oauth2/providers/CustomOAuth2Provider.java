package de.interaapps.pastefy.auth.strategies.oauth2.providers;

import de.interaapps.pastefy.auth.strategies.oauth2.OAuth2Profile;
import de.interaapps.pastefy.auth.strategies.oauth2.OAuth2Provider;
import org.javawebstack.abstractdata.AbstractObject;
import org.javawebstack.abstractdata.util.QueryString;
import org.javawebstack.httpclient.HTTPClient;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class CustomOAuth2Provider implements OAuth2Provider {
    private final String clientId;
    private final String secret;
    private final String authEndpoint;
    private final String tokenEndpoint;
    private final String userInfoEndpoint;
    private String[] scopes = {"openid", "email", "profile"};

    public CustomOAuth2Provider(String clientId, String secret, String authEndpoint, String tokenEndpoint, String userInfoEndpoint) {
        this.clientId = clientId;
        this.secret = secret;
        this.authEndpoint = authEndpoint;
        this.tokenEndpoint = tokenEndpoint;
        this.userInfoEndpoint = userInfoEndpoint;
    }

    public CustomOAuth2Provider setScopes(String... scopes) {
        this.scopes = scopes;
        return this;
