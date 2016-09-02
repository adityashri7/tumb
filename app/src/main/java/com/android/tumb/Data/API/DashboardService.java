package com.android.tumb.Data.API;

import android.util.Log;

import com.tumblr.jumblr.JumblrClient;
import com.tumblr.jumblr.exceptions.JumblrException;
import com.tumblr.jumblr.types.Post;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;
import rx.subjects.SerializedSubject;

/**
 * Created by trust on 8/27/2016.
 * Class to fetch dashboard posts
 */
public class DashboardService {
    String oAuthToken;
    String oAuthTokenSecret;
    String consumerKey;
    String consumerSecret;
    private final SerializedSubject<Boolean, Boolean> getPostsCalled =
            PublishSubject.<Boolean>create().toSerialized();
    private final SerializedSubject<String, String> fetchBlogUrl =
            PublishSubject.<String>create().toSerialized();
    private final SerializedSubject<String, String> gotWrapper =
            PublishSubject.<String>create().toSerialized();
    private final SerializedSubject<List<Post>, List<Post>> postsReceived =
            PublishSubject.<List<Post>>create().toSerialized();
    private int start;
    private int end;


    public DashboardService(String conKey, String conSecret, final String oAuthToken, final String oAuthTokenSecret) {
        this.oAuthToken = oAuthToken;
        this.oAuthTokenSecret = oAuthTokenSecret;
        this.consumerKey = conKey;
        this.consumerSecret = conSecret;


        getPostsCalled
                .observeOn(Schedulers.newThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe(new Action1<Boolean>() {
                    @Override
                    public void call(Boolean aBoolean) {
                        if (aBoolean){
                            getPostsCalled.onNext(false);
                            JumblrClient client = new JumblrClient(consumerKey, consumerSecret);
                            client.setToken(oAuthToken, oAuthTokenSecret);
                            Map<String, Integer> options = new HashMap<String, Integer>();
                            options.put("offset", start);
                            postsReceived.onNext(client.userDashboard(options));
                            getPostsCalled.onNext(false);
                        }
                    }
                });

        fetchBlogUrl
                .subscribeOn(Schedulers.newThread())
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String blog) {
                        JumblrClient client = new JumblrClient(consumerKey, consumerSecret);
                        client.setToken(oAuthToken, oAuthTokenSecret);
                        try {
                            gotWrapper.onNext((client.blogAvatar(blog)));
                        }
                        catch (JumblrException e){
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Log.e("Error", throwable.getMessage());
                    }
                });

    }

    public SerializedSubject<List<Post>, List<Post>> getPosts(int start, int end) {
        this.start = start;
        this.end = end;
        getPostsCalled.onNext(true);
        return postsReceived;
    }

}
