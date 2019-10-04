package de.interaapps.pastefy.model.elastic;

import co.elastic.clients.json.JsonData;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import de.interaapps.pastefy.Pastefy;
import de.interaapps.pastefy.model.database.Paste;
import de.interaapps.pastefy.model.database.User;
import org.javawebstack.orm.Repo;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ElasticPaste {
    public int id;
    public String key;
    public String title;
    public String content;
    public String userId;
    public String forkedFrom;
    public boolean encrypted = false;
    public String folder;
    public Paste.Type type = Paste.Type.PASTE;
    public Paste.Visibility visibility;
    public Timestamp expireAt = null;
    public Timestamp createdAt;
    public Timestamp updatedAt;
    public Paste.StorageType storageType = Paste.StorageType.DATABASE;
    public Integer version = 0;
    public Integer engagementScore = 0;
    public String[] tags = {};
    public Integer starCount = 0;
    public String[] starredBy = {};

    public ElasticUser user;

    public static ElasticPaste fromPaste(Paste paste) {
        ElasticPaste elasticPaste = new ElasticPaste();
        elasticPaste.id = paste.getId();
        elasticPaste.key = paste.getKey();
