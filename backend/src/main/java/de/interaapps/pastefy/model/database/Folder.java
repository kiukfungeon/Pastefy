package de.interaapps.pastefy.model.database;

import org.javawebstack.orm.Model;
import org.javawebstack.orm.Repo;
import org.javawebstack.orm.annotation.*;
import org.javawebstack.orm.query.Query;
import org.javawebstack.webutils.util.RandomUtil;

import java.sql.Timestamp;
import java.util.List;

@Dates
@Table("folder")
public class Folder extends Model {
    @Column
    private int id;

    @Column(size = 8)
    private String key;

    @Column
    @Searchable
    private String name = "";

    @Column(size = 8)
    @Filterable
    public String userId;

    @Column
    @Searchable
    @Filterable
    private String parent;

    @Column
    @Searchable
    public Timestamp createdAt;

    @Column
    public Timestamp updatedAt;

    public Folder() {
        key = RandomUtil.string(8);
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getId() {
