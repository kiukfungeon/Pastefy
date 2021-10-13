package de.interaapps.pastefy.model.elastic;

import de.interaapps.pastefy.model.database.User;

import java.sql.Timestamp;

public class ElasticUser {
    public String id;
    public String name;
    public String uniqueName;
    public String eMail;
    public String avatar;
    public String authId;
    public User.AuthenticationProvider authProvider;
    public User.Type type = User.Type.USER;
    public Timestamp createdAt;
    public Timestamp updatedAt;
