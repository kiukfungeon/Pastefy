package de.interaapps.pastefy.model.database;

import de.interaapps.pastefy.exceptions.PermissionsDeniedException;
import org.javawebstack.abstractdata.AbstractArray;
import org.javawebstack.orm.Model;
import org.javawebstack.orm.annotation.Column;
import org.javawebstack.orm.annotation.Dates;
import org.javawebstack.orm.annotation.Table;
import org.javawebstack.webutils.util.RandomUtil;

import java.sql.Timestamp;

@Dates
@Table("auth_keys")
public class AuthKey extends Model {
    @Column
    public int id;

    @Column(size = 60)
    private String key;

    @Column(size = 255)
