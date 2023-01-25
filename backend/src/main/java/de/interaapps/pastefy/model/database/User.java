package de.interaapps.pastefy.model.database;

import co.elastic.clients.elasticsearch._types.Script;
import co.elastic.clients.elasticsearch.core.UpdateRequest;
import co.elastic.clients.json.JsonData;
import de.interaapps.pastefy.Pastefy;
import de.interaapps.pastefy.auth.strategies.oauth2.OAuth2Provider;
import de.interaapps.pastefy.auth.strategies.oauth2.providers.*;
import de.interaapps.pastefy.model.database.algorithm.PublicPasteEngagement;
import de.interaapps.pastefy.model.elastic.ElasticPaste;
import de.interaapps.pastefy.model.elastic.ElasticStars;
import de.interaapps.pastefy.model.responses.folder.FolderResponse;
import org.javawebstack.orm.Model;
import org.javawebstack.orm.Repo;
import org.javawebstack.orm.annotation.*;
import org.javawebstack.webutils.util.RandomUtil;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Dates
@Table("users")
public class User extends Model {

    @Column(size = 8, id = true)
    public String id;

    @Column
    @Searchable
    public String name;

    @Column(size = 33)
    @Filterable
    public String uniqueName;

    @Column(name = "email")
    @Filterable
    public String eMail;

    @Column
    public String avatar;

    @Column(size = 455)
