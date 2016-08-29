package android.tumb.com.tumb.Main.Feed;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.tumb.com.tumb.Data.API.DashboardService;
import android.tumb.com.tumb.Data.PostWrapper;
import android.tumb.com.tumb.Misc.CacheStorage;
import android.tumb.com.tumb.Misc.DividerItemDecoration;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.tumb.com.tumb.R;
import android.widget.ProgressBar;

import com.tumblr.jumblr.types.Post;
import java.util.ArrayList;
import java.util.List;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.subjects.PublishSubject;
import rx.subjects.SerializedSubject;
import rx.subscriptions.CompositeSubscription;

public class FeedFragment extends Fragment{
    private DashboardService dashboardService;
    private RecyclerView feedRecycler;
    private View view;
    private LinearLayoutManager mLayoutManager;
    private FeedAdapter adapter;
    private List<PostWrapper> postList;
    private CompositeSubscription running;
    private boolean isLoading;
    private SerializedSubject<List<Post>, List<Post>> postReceived =
            PublishSubject.<List<Post>>create().toSerialized();
    private SerializedSubject<Boolean, Boolean> hasNetworkSubject =
            PublishSubject.<Boolean>create().toSerialized();
    private Boolean hasNetwork = false;
    private Boolean initial = true;
    private CacheStorage cacheStorage;
    private ProgressBar mProgressBar;
    private SwipeRefreshLayout refreshLayout;
    private Subscription postRecievedSubscription;

    public FeedFragment() {
        // Required empty public constructor
    }

    public static FeedFragment newInstance(String param1, String param2) {
        FeedFragment fragment = new FeedFragment();
        Bundle args = new Bundle();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        cacheStorage = new CacheStorage(getContext());
    }

    public void attachDashBoardService(DashboardService service){
        dashboardService = service;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.fragment_feed, container, false);
        feedRecycler = (RecyclerView) view.findViewById(R.id.feed_recycler);
        mLayoutManager = new LinearLayoutManager(this.getContext());
        feedRecycler.setLayoutManager(mLayoutManager);
        feedRecycler.addItemDecoration(new DividerItemDecoration(this.getContext()));
        postList = new ArrayList<PostWrapper>();

        //Feed Adapter
        adapter = new FeedAdapter(postList, this.getContext());
        feedRecycler.setAdapter(adapter);

        //ProgressBar
        mProgressBar = (ProgressBar) view.findViewById(R.id.progress_bar);
        mProgressBar.setVisibility(View.VISIBLE);

        // Pagination
        feedRecycler.addOnScrollListener(mRecyclerViewOnScrollListener);

        //SwipeRefresh
        refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.question_recycler_view_container);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                postList.clear();
                isLoading = true;
                loadPosts(0, 10);
            }
        });

        //Check Connection
        ConnectivityManager cm = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        hasNetwork = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        if (!hasNetwork){
            showNoNetworkSnack();
        }

        isLoading = true;
        loadPosts(0, 10);
        mProgressBar.setVisibility(View.GONE);
        return view;
    }

    private void showNoNetworkSnack() {
        Snackbar.make(view, R.string.no_connection_message, Snackbar.LENGTH_INDEFINITE)
                .setAction("OK", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //Dismiss the snackbar
                    }
                }).show();
    }


    private RecyclerView.OnScrollListener
            mRecyclerViewOnScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView,
                                         int newState) {
            super.onScrollStateChanged(recyclerView, newState);
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            int visibleItemCount = mLayoutManager.getChildCount();
            int totalItemCount = mLayoutManager.getItemCount();
            int firstVisibleItemPosition = mLayoutManager.findFirstVisibleItemPosition();
            if (dy>0 && !isLoading){
                if (firstVisibleItemPosition >= totalItemCount - 7){
                    isLoading = true;
                    mProgressBar.setVisibility(View.VISIBLE);
                    loadPosts(totalItemCount, totalItemCount + 10);
                }
            }

        }
    };

    public void loadPosts(int start, int end){
        if (hasNetwork) {
            postReceived = dashboardService.getPosts(start, end);
            postRecievedSubscription =  postReceived
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnNext(new Action1<List<Post>>() {
                        @Override
                        public void call(List<Post> posts) {
                            updateAdapter(createWrappers(posts));
                        }
                    }).subscribe();
        }
        else {
            updateAdapter(cacheStorage.readFromCache(start, end));
        }



    }

    private void updateAdapter(List<PostWrapper> posts) {
        feedRecycler = (RecyclerView) view.findViewById(R.id.feed_recycler);
        postList.addAll(posts);
        adapter.notifyItemInserted(postList.size() - 1);
        isLoading = false;
        if (postRecievedSubscription!=null) {
            postRecievedSubscription.unsubscribe();
            postRecievedSubscription = null;
        }
        mProgressBar.setVisibility(View.GONE);
        refreshLayout.setRefreshing(false);
    }

    private List<PostWrapper> createWrappers(List<Post> posts) {
        List<PostWrapper> wrapperList = new ArrayList<>();
        for (Post thisPost : posts){
            wrapperList.add(new PostWrapper(thisPost, getContext()));
        }
        return wrapperList;
    }

    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
        running = new CompositeSubscription();
        running.add(
                hasNetworkSubject.doOnNext(new Action1<Boolean>() {
                    @Override
                    public void call(Boolean network) {
                        hasNetwork = network;
                        if (!network) {
                            showNoNetworkSnack();
                        }
                        else {
                            Snackbar.make(view, R.string.got_connection_message, Snackbar.LENGTH_LONG).show();
                        }
                    }
                }).subscribe()
        );
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (running != null){
            running.unsubscribe();
            running = null;
        }
        if (postRecievedSubscription!=null) {
            postRecievedSubscription.unsubscribe();
            postRecievedSubscription = null;
        }
        if (postReceived != null){
            postReceived = null;
        }
    }

    public void networkChange(Boolean hasNetwork) {
        this.hasNetworkSubject.onNext(hasNetwork);
    }
}
