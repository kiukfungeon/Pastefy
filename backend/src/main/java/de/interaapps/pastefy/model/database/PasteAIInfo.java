package de.interaapps.pastefy.model.database;

import org.javawebstack.abstractdata.AbstractArray;
import org.javawebstack.abstractdata.AbstractElement;
import org.javawebstack.orm.Model;
import org.javawebstack.orm.annotation.Column;
import org.javawebstack.orm.annotation.Dates;
import org.javawebstack.orm.annotation.Table;

import java.sql.Timestamp;

@Dates
@Table("paste_ai_info")
public class PasteAIInfo extends Model {
    @Column(id = true, ai = false)
    public int pasteId;

    @Column
    public int sourcePasteVersion;

    @Column
    public int promptVersion;

    @Column(size = 30)
    public String provider;

    @Column(size = 100)
    public String model;

    @Column(size = 1000)
    public String description;

