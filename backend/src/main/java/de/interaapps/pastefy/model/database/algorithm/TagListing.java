package de.interaapps.pastefy.model.database.algorithm;

import de.interaapps.pastefy.Pastefy;
import de.interaapps.pastefy.ai.PasteAI;
import de.interaapps.pastefy.model.database.PasteTag;
import org.javawebstack.orm.Model;
import org.javawebstack.orm.Repo;
import org.javawebstack.orm.annotation.Column;
import org.javawebstack.orm.annotation.Filterable;
import org.javawebstack.orm.annotation.Searchable;
import org.javawebstack.orm.annotation.Table;


@Table("tag_listing")
public class TagListing extends Model {
    @Column(id = true, size = 30)
    @Filterable
    @Searchable
    public String tag;

    @Column
    public String displayName;

    @Column
    public String imageUrl;

    @Column
    public String description;

    @Column
    public String website;

    @Column
    public String icon;
