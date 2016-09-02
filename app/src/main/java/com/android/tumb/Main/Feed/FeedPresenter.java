package com.android.tumb.Main.Feed;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import com.android.tumb.Data.PostWrapper;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by trust on 9/1/2016.
 */
public class FeedPresenter implements FeedModelInterface{

    private List<PostWrapper> postList;
    private FeedInterface listener;
    private FeedModel model;
    private Boolean hasNetwork;
    private FeedAdapter adapter;
    private boolean isLoading;
    private LinearLayoutManager mLayoutManager;
    private Context context;
    private RecyclerView recyclerView;

    public FeedPresenter(Context context, FeedInterface feedInterface, RecyclerView recylerView, LinearLayoutManager layoutManager){
        listener = feedInterface;
        this.recyclerView = recylerView;
        mLayoutManager = layoutManager;
        this.context = context;
        postList = new ArrayList<PostWrapper>();
        isLoading = false;
        adapter = new FeedAdapter(postList, context);
        recylerView.setAdapter(adapter);
    }

    public void attach(FeedModel model) {
        this.model = model;
        model.attachInterface(this);

        //Check Connection
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        hasNetwork = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        if (!hasNetwork){
            listener.showNoNetworkSnack(hasNetwork);
        }


        loadPosts(0, 10);
    }

    public void loadPosts(int start, int end){
        isLoading = true;
        listener.setProgressVisibility(View.VISIBLE);
        model.loadPosts(start, end, hasNetwork);
    }

    @Override
    public void updateAdapter(List<PostWrapper> wrapperList) {
        postList.addAll(wrapperList);
        adapter.notifyItemInserted(postList.size() - 1);
        listener.updateRefreshing(false);
        listener.setProgressVisibility(View.GONE);
        isLoading = false;
    }

    public void notifyNetworkChange(Boolean hasNetwork) {
        this.hasNetwork = hasNetwork;
    }

    public void refreshAdapter(){
        postList.clear();
        loadPosts(0, 10);
    }


    public RecyclerView.OnScrollListener
            mRecyclerViewOnScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView,
                                         int newState) {
            super.onScrollStateChanged(recyclerView, newState);
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            int totalItemCount = mLayoutManager.getItemCount();
            int firstVisibleItemPosition = mLayoutManager.findFirstVisibleItemPosition();
            if (dy>0 && !isLoading){
                if (firstVisibleItemPosition >= totalItemCount - 7){
                    loadPosts(totalItemCount, totalItemCount + 10);
                }
            }

        }
    };

    public void drop() {
        if (model != null){
            model.detach();
        }
    }
}
