package de.interaapps.pastefy.model.database;

import org.javawebstack.orm.Model;
import org.javawebstack.orm.Repo;
import org.javawebstack.orm.annotation.Column;
import org.javawebstack.orm.annotation.Dates;
import org.javawebstack.orm.annotation.Table;

import java.sql.Timestamp;

@Dates
@Table("paste_comments")
public class PasteComment extends Model {
    @Column
    private int id;

    @Column(size = 8)
    private String paste;

    @Column(size = 8)
    private String userId;

    @Column(size = 2000)
    private String content;

