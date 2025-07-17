package com.example.touchgrass_finalproject;


import java.util.UUID;

import io.realm.RealmObject;
import io.realm.RealmList;
import io.realm.annotations.PrimaryKey;

public class User extends RealmObject {
    @PrimaryKey
    private String uuid = UUID.randomUUID().toString();
    private String name;
    private String password;

    private RealmList<Post> savedPosts;


    @Override
    public String toString() {
        return "User{" +
                "uuid='" + uuid + '\'' +
                ", name='" + name + '\'' +
                ", password='" + password + '\'' +
                '}';
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public RealmList<Post> getSavedPosts() {
        return savedPosts;
    }

    public void setSavedPosts(RealmList<Post> savedPosts) {
        this.savedPosts = savedPosts;
    }
}
