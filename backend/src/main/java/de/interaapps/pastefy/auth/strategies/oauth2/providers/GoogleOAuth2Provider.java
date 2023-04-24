package de.interaapps.pastefy.auth.strategies.oauth2.providers;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.oauth2.Oauth2;
import com.google.api.services.oauth2.model.Userinfo;
import de.interaapps.pastefy.auth.strategies.oauth2.OAuth2Profile;
import de.interaapps.pastefy.auth.strategies.oauth2.OAuth2Provider;
import org.javawebstack.abstractdata.AbstractObject;
import org.javawebstack.httpclient.HTTPClient;

import java.io.IOException;
import java.util.Arrays;

public class GoogleOAuth2Provider implements OAuth2Provider {
    private String clientId;
    private String secret;
    private GoogleAuthorizationCodeFlow googleAuthorizationCodeFlow;

    public GoogleOAuth2Provider(String clientId, String secret) {
        this.clientId = clientId;
        this.secret = secret;

        googleAuthorizationCodeFlow = new GoogleAuthorizationCodeFlow.Builder(
                new NetHttpTransport(),
                GsonFactory.getDefaultInstance(),
                clientId,
                secret,
                Arrays.asList(
                        "https://www.googleapis.com/auth/userinfo.profile",
                        "https://www.googleapis.com/auth/userinfo.email"
                )
        ).build();
    }

    public GoogleOAuth2Provider setScopes(String[] scopes) {
        googleAuthorizationCodeFlow.getScopes().clear();
        for (String scope : scopes) {
            googleAuthorizationCodeFlow.getScopes().add(scope);
        }
        return this;
    }

    public OAuth2Callback callback(AbstractObject queryParameters, String callbackUrl) {
        try {
            GoogleTokenResponse code = googleAuthorizationCodeFlow.newTokenRequest(queryParameters.string("code"))
                    .setRedirectUri(callbackUrl)
                    .execute();
            return new OAuth2Callback(this, code.getAccessToken(), code.getRefreshToken(), clientId, secret);
        } catch (IOException e) {
            return null;
        }
