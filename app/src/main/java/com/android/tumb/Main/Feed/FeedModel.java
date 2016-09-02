package com.android.tumb.Main.Feed;

import android.content.Context;
import com.android.tumb.Data.API.DashboardService;
import com.android.tumb.Data.PostWrapper;
import com.android.tumb.Misc.CacheStorage;

import com.tumblr.jumblr.types.Post;

import java.util.ArrayList;
import java.util.List;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.subjects.PublishSubject;
import rx.subjects.SerializedSubject;

/**
 * Created by trust on 9/2/2016.
 */
public class FeedModel {
    private FeedModelInterface listener;
    private CacheStorage cacheStorage;
    private DashboardService service;

    private SerializedSubject<List<Post>, List<Post>> postReceived =
            PublishSubject.<List<Post>>create().toSerialized();
    private Subscription postRecievedSubscription;

    public FeedModel(Context context, DashboardService service){
        cacheStorage = new CacheStorage(context);
        this.service = service;
    }

    public void attachInterface(FeedModelInterface feedModelInterface){
        listener = feedModelInterface;
    }



    public void loadPosts(int start, int end, Boolean hasNetwork){
        if (hasNetwork) {
            postReceived = service.getPosts(start, end);
            postRecievedSubscription =  postReceived
                    .take(1)
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnNext(new Action1<List<Post>>() {
                        @Override
                        public void call(List<Post> posts) {
                            listener.updateAdapter(createWrappers(posts));
                        }
                    }).subscribe();
        }
        else {
            listener.updateAdapter(cacheStorage.readFromCache(start, end));
        }

    }

    private List<PostWrapper> createWrappers(List<Post> posts) {
        List<PostWrapper> wrapperList = new ArrayList<>();
        for (Post thisPost : posts){
            wrapperList.add(new PostWrapper(thisPost));
        }
        return wrapperList;
    }


    public void detach() {
        listener = null;
        if (postRecievedSubscription != null){
            postRecievedSubscription.unsubscribe();
            postRecievedSubscription = null;
        }
        if (postReceived != null){
            postReceived = null;
        }
    }
}
