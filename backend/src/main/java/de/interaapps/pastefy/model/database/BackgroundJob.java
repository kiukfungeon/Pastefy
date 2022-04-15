package de.interaapps.pastefy.model.database;

import org.javawebstack.orm.Model;
import org.javawebstack.orm.annotation.Column;
import org.javawebstack.orm.annotation.Dates;
import org.javawebstack.orm.annotation.Table;

import java.sql.Timestamp;

@Dates
@Table("background_jobs")
public class BackgroundJob extends Model {
    @Column(id = true, ai = false, size = 255)
    public String key;

    @Column
    public Type type;

    @Column
    public int entityId;

    @Column
    public int sourceVersion;

    @Column
    public int promptVersion;

    @Column
    public Status status = Status.PENDING;

    @Column
