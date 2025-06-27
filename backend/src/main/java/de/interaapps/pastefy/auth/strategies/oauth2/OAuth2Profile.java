package de.interaapps.pastefy.auth.strategies.oauth2;

import org.javawebstack.abstractdata.AbstractObject;

public class OAuth2Profile extends AbstractObject {
    public String id;
    public String name;
    public String mail;
    public String avatar;

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }
