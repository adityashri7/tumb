package com.android.tumb.Data;

import java.io.Serializable;

/**
 * Created by trust on 8/27/2016.
 */
public class PostSerializable implements Serializable {
    public String blogName;
    public String blogAvatar;
    public String notesCount;
    public String sourceTitle;
    public String photoURL;
    public String tags;
    public String type;
    public String caption;

    public PostSerializable(String blog, String blogAvatar, String notes, String source, String photoUrl, String tags, String type, String caption) {
        this.blogName = blog;
        this.blogAvatar = blogAvatar;
        this.notesCount = notes;
        this.sourceTitle = source;
        this.photoURL = photoUrl;
        this.tags = tags;
        this.type = type;
        this.caption = caption;
    }

    public void setBlogName(String s){
        this.blogName = s;
    }

    public void setBlogAvatar(String s){
        this.blogAvatar = s;
    }

    public void setNotesCount(String s){
        this.notesCount = s;
    }

    public void setSourceTitle(String s){
        this.sourceTitle = s;
    }

    public void setPhotoURL(String s){
        this.photoURL = s;
    }

    public void setTags(String s){
        this.tags = s;
    }

    public void setType(String s){
        this.type = s;
    }

    public void setCaption(String s){
        this.caption = s;
    }


    public String getBlogName(){
        return blogName;
    }

    public String getBlogAvatar(){
        return blogAvatar;
    }

    public String getNotesCount(){
        return notesCount;
    }

    public String getSourceTitle(){
        return sourceTitle;
    }

    public String getPhotoURL(){
        return photoURL;
    }

    public String getTags(){
        return tags;
    }

    public String getType(){
        return type;
    }

    public String getCaption() {
        return caption;
    }
}
