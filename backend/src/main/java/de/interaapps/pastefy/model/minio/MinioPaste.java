package de.interaapps.pastefy.model.minio;

import de.interaapps.pastefy.Pastefy;
import de.interaapps.pastefy.model.database.Paste;
import io.minio.*;
import io.minio.errors.*;
import org.javawebstack.abstractdata.AbstractElement;
import org.javawebstack.abstractdata.AbstractObject;
import org.javawebstack.orm.Repo;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

public class MinioPaste extends AbstractObject {
    public MinioPaste setObjectName(String objectName) {
        set("objectName", objectName);
        return this;
    }
    public MinioPaste setBucket(String bucket) {
        set("bucket", bucket);
        return this;
    }
    public MinioPaste setServer(String server) {
        set("server", server);
        return this;
    }
    public MinioPaste setRegion(String region) {
        set("region", region);
        return this;
    }
    public MinioPaste setETAG(String etag) {
        set("etag", etag);
        return this;
    }

    public String getObjectName() {
        return get("objectName").string();
    }

    public String getBucket() {
        return get("bucket", AbstractElement.fromObject(Pastefy.getInstance().getConfig().get("minio.bucket", "pastefy"))).string();
    }

    public String getServer() {
        return get("bucket", AbstractElement.fromObject(Pastefy.getInstance().getConfig().get("server.bucket"))).string();
    }

    public String getRegion() {
        return get("region").string();
    }
