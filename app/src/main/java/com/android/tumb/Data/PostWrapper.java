package com.android.tumb.Data;

import android.util.Xml;

import com.tumblr.jumblr.types.PhotoPost;
import com.tumblr.jumblr.types.PhotoSize;
import com.tumblr.jumblr.types.Post;
import com.tumblr.jumblr.types.TextPost;
import com.tumblr.jumblr.types.VideoPost;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.Serializable;
import java.io.StringReader;
import java.util.List;

import rx.subjects.PublishSubject;
import rx.subjects.SerializedSubject;

/**
 * Created by trust on 8/27/2016.
 * Wrapper file for dashboard posts
 */
public class PostWrapper implements Serializable {
    Post post;
    public String blogAvatar = "";
    private SerializedSubject<String, String> blogAvatarSerializable =
            PublishSubject.<String>create().toSerialized();
    private String blogName;
    private String notesCount;
    private String photoURL;
    private String sourceTitle;
    private String tags;
    private String type;
    private String caption;

    public PostWrapper(Post post){
        this.post = post;
        setBlogName();
        setNotes();
        setPhotoURL();
        setSource();
        setType();
        setTags();
    }

    public PostWrapper(PostSerializable thisPostSerializable) {
        this.blogName = thisPostSerializable.blogName;
        this.blogAvatar = thisPostSerializable.blogAvatar;
        this.notesCount = thisPostSerializable.getNotesCount();
        this.photoURL = thisPostSerializable.getPhotoURL();
        this.sourceTitle = thisPostSerializable.getSourceTitle();
        this.tags = thisPostSerializable.getTags();
        this.type = thisPostSerializable.getType();
        this.caption = thisPostSerializable.getCaption();
    }

    public void setCaption(String s){
        caption = s;
    }

    public void setBlogName(){
        blogName = post.getBlogName();
    }

    public void setTags(){
        List<String> tagsList = post.getTags();
        StringBuilder sb = new StringBuilder("");
        if (tagsList != null) {
            for (String thisTag : tagsList) {
                sb.append('#');
                sb.append(thisTag);
                sb.append(' ');
            }
        }
        tags = sb.toString();
    }

    public void setSource(){
        sourceTitle = post.getSourceTitle();
    }

    public void setNotes(){
        notesCount = post.getNoteCount() + " notes";
    }

    public void setPhotoURL(){
        if (post.getType().equals("photo")) {
            PhotoPostWrapper photoPostWrapper = new PhotoPostWrapper(post);
            photoURL =  photoPostWrapper.getPhotoURI();
            caption = photoPostWrapper.getPhotoCaption();
        }
        else {
            photoURL = null;
            if (post.getType().equals("text")) {
                TextPost textPost = (TextPost) (post);
                if (textPost != null) {
                    caption = getCaptionFromAnyPost(textPost.getBody());
                }
            }
            else if (post.getType().equals("video"))  {
                VideoPost videoPost = (VideoPost) (post);
                if (videoPost != null){
                    caption = getCaptionFromAnyPost(videoPost.getCaption());
                }

            }
            else caption = "No caption";

        }
    }

    public String getBlog(){
        return blogName;
    }

    public void setBlogAvatar(String blogAvatar) {
        this.blogAvatar = blogAvatar;
    }

    public void setType(){
        type = post.getType();
    }

    public String getType(){
        return type;
    }

    public String getTags(){
        return tags;
    }
    public String getSource(){
        if (sourceTitle != null) {
            return "Source: " + sourceTitle;
        }
        return "";
    }
    public String getNotes(){
        return notesCount;
    }


    public String getPhotoUrl(){
        return photoURL;
    }

    public String getBlogAvatar() {
        return blogAvatar;
    }

    public String getCaption(){
        return caption;
    }


    public class PhotoPostWrapper implements Serializable{
        PhotoPost post;
        public PhotoPostWrapper(Post post){
            this.post = (PhotoPost) post;
        }

        public String getPhotoURI(){
            List<PhotoSize> sizes = post.getPhotos().get(0).getSizes();
            int maxSize = Integer.MIN_VALUE;
            StringBuilder maxUrl = new StringBuilder(' ');
            for (PhotoSize thisSize : sizes){
                if (thisSize.getWidth() > maxSize){
                    maxSize = thisSize.getWidth();
                    maxUrl.delete(0, maxUrl.length());
                    maxUrl.append(thisSize.getUrl());
                }
            }
            return maxUrl.toString();
        }

        public String getPhotoCaption(){
            return getCaptionFromAnyPost(post.getCaption());
        }

    }

    public String getCaptionFromAnyPost(String postCaption){
        XmlPullParser parser = Xml.newPullParser();
        StringBuilder sb = new StringBuilder("");
        try {
            parser.setInput( new StringReader(postCaption));
            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                String tagname = parser.getName();
                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        if (tagname.equalsIgnoreCase("blockquote")) {
                            while (eventType != XmlPullParser.END_DOCUMENT){
                                eventType = parser.next();
                                tagname = parser.getName();
                                switch (eventType){
                                    case XmlPullParser.TEXT:
                                        sb.append(parser.getText());
                                        break;

                                    case XmlPullParser.END_TAG:
                                        if (tagname.equalsIgnoreCase("blockquote")){
                                            break;
                                        }
                                }
                            }
                        }
                        break;
                    default:
                        break;
                }
                eventType = parser.next();
            }
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return sb.toString();
    }

}
