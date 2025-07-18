package com.example.touchgrass_finalproject;

import java.util.Date;
import java.util.UUID;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Comment extends RealmObject {

    @PrimaryKey
    private String uuid = UUID.randomUUID().toString();
    private String postID;
    private String userID;
    private String username;
    private String userProfPic_url;
    private String comment_content;

    @Override
    public String toString(){

        return "Post{" +
                "uuid='" + uuid + '\'' +
                ", postUUID=" + postID +
                ", username='" + username + '\'' +
                ", userProfPic_url='" + userProfPic_url + '\'' +
                ", content='" + comment_content +
                '}';
    }

    public String getUserUUID() {

        return userID;

    }

    public void setUserID(String userID) {

        this.userID = userID;
    }

    public String getCommentUuid(){

        return uuid;

    }

    public void setCommentUuid( String uuid ){

        this.uuid = uuid;

    }

    public String getPostID(){

        return postID;

    }

    public void setPostID( String PostUUID ){

        this.postID = PostUUID;

    }

    public String getUsername(){

        return username;

    }

    public void setUsername( String username ){

        this.username = username;

    }

    public String getUserProfPic_url(){

        return userProfPic_url;

    }

    public void setUserProfPic_url( String userProfPic_url ){

        this.userProfPic_url = userProfPic_url;

    }

    public String getCommentContent(){

        return comment_content;

    }

    public void setCommentContent(String comment_content){

        this.comment_content = comment_content;

    }
}
