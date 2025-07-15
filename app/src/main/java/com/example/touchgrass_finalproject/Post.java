package com.example.touchgrass_finalproject;

import java.util.Date;
import java.util.UUID;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Post extends RealmObject {
    @PrimaryKey
    private String uuid = UUID.randomUUID().toString();
    private User user;
    private String description;
    private Date date;

    @Override
    public String toString() {
        return "Post{" +
                "uuid='" + uuid + '\'' +
                ", user=" + user +
                ", description='" + description + '\'' +
                ", date=" + date +
                '}';
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
