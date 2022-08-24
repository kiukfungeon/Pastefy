package de.interaapps.pastefy.model.database.algorithm;

import de.interaapps.pastefy.model.database.Paste;
import de.interaapps.pastefy.model.elastic.ElasticPaste;
import de.interaapps.pastefy.Pastefy;
import org.javawebstack.orm.Model;
import org.javawebstack.orm.Repo;
import org.javawebstack.orm.annotation.Column;
import org.javawebstack.orm.annotation.Dates;

import java.sql.Timestamp;

@Dates
public class PublicPasteEngagement extends Model {
    @Column
    public int id;

    @Column
    public int pasteId;

    @Column
    public int score = 0;

    @Column
    public Timestamp createdAt;

