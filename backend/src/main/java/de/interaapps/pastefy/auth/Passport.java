package de.interaapps.pastefy.auth;

import de.interaapps.pastefy.auth.strategies.Strategy;
import org.javawebstack.http.router.HTTPRouter;

import java.util.HashMap;
import java.util.Map;

public class Passport {

    private Map<String, Strategy> strategies = new HashMap<>();
    private String prefixUrl = "";

    public Passport() {
    }

    public Passport(String prefixUrl) {
        this.prefixUrl = prefixUrl;
    }

