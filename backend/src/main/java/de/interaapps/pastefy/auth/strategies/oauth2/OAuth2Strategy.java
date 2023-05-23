package de.interaapps.pastefy.auth.strategies.oauth2;

import de.interaapps.pastefy.auth.strategies.Strategy;
import org.javawebstack.http.router.Exchange;
import org.javawebstack.http.router.HTTPRouter;

import java.util.HashMap;
import java.util.Map;

public class OAuth2Strategy extends Strategy {

    private Map<String, OAuth2Provider> providers = new HashMap<>();
    private HttpCallbackHandler httpCallbackHandler;
    private String host;


    public OAuth2Strategy(String host) {

        this.host = host;
    }
