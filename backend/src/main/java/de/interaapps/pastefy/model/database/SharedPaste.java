package de.interaapps.pastefy.model.database;

import org.javawebstack.orm.Model;
import org.javawebstack.orm.Repo;
import org.javawebstack.orm.annotation.Column;
import org.javawebstack.orm.annotation.Dates;
import org.javawebstack.orm.annotation.Table;

import java.sql.Timestamp;

@Dates
@Table("shared_pastes")
public class SharedPaste extends Model {
    @Column
    private int id;

    @Column(size = 8)
    private String userId;

    @Column(size = 8)
    private String targetId;

    @Column
    private String paste;

    @Column
    public Timestamp createdAt;

    @Column
    public Timestamp updatedAt;


    public void setUser(User user) {
        this.userId = user.getId();
    }

    public String getUserId() {
        return userId;
    }

    public String getTargetId() {
        return targetId;
    }

    public void setTargetId(String targetId) {
        this.targetId = targetId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
