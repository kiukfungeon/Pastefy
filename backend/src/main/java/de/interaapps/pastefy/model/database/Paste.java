package de.interaapps.pastefy.model.database;

import de.interaapps.pastefy.Pastefy;
import de.interaapps.pastefy.exceptions.NotFoundException;
import de.interaapps.pastefy.exceptions.PastePrivateException;
import de.interaapps.pastefy.model.database.algorithm.PublicPasteEngagement;
import de.interaapps.pastefy.model.database.algorithm.TagListing;
import de.interaapps.pastefy.model.elastic.ElasticPaste;
import de.interaapps.pastefy.model.minio.MinioPaste;
import de.interaapps.pastefy.model.redis.PasteAccessCache;
import de.interaapps.pastefy.model.redis.PasteContentCache;
import io.minio.GetObjectResponse;
import org.javawebstack.abstractdata.AbstractArray;
import org.javawebstack.abstractdata.AbstractElement;
import org.javawebstack.http.router.Exchange;
import org.javawebstack.orm.Model;
import org.javawebstack.orm.Repo;
import org.javawebstack.orm.annotation.Column;
import org.javawebstack.orm.annotation.Filterable;
import org.javawebstack.orm.annotation.Searchable;
import org.javawebstack.orm.annotation.Table;
import org.javawebstack.webutils.util.IO;
import org.javawebstack.webutils.util.RandomUtil;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.HexFormat;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

// @Dates
@Table("pastes")
public class Paste extends Model {
    @Column(size = 8)
    @Searchable
    @Filterable
    public String folder;
    @Column
    public Timestamp expireAt = null;
    @Column
    @Searchable
    public Timestamp createdAt;
    @Column
    public Timestamp updatedAt;
    protected String cachedContents = null;
    @Column
    private int id;
    @Column(size = 8)
    @Searchable
    @Filterable
    private String key;
    @Column
    @Searchable
    @Filterable
    private String title;
    @Column(size = 16777215)
    @Searchable
    private String content;
    @Column(size = 8)
    @Filterable
    private String userId;
    @Column(size = 8)
    @Filterable
    private String forkedFrom;
    @Column
    @Filterable
    private boolean encrypted = false;
    @Column
    @Searchable
    @Filterable
    private Type type = Type.PASTE;
    @Column
    @Filterable
    private Visibility visibility = Visibility.UNLISTED;
    @Column
    private StorageType storageType = StorageType.DATABASE;
    @Column
    private Integer version = 0;
    @Column
    private Boolean indexedInElastic = false;

