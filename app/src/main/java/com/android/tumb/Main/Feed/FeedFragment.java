package com.android.tumb.Main.Feed;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import com.android.tumb.Data.API.DashboardService;
import com.android.tumb.Data.PostWrapper;
import com.android.tumb.Misc.DividerItemDecoration;
import com.android.tumb.R;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ProgressBar;
import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.functions.Action1;
import rx.subjects.PublishSubject;
import rx.subjects.SerializedSubject;
import rx.subscriptions.CompositeSubscription;

public class FeedFragment extends Fragment implements FeedInterface, SwipeRefreshLayout.OnRefreshListener{
    private DashboardService dashboardService;
    private View view;
    private LinearLayoutManager mLayoutManager;
    private FeedAdapter adapter;
    private CompositeSubscription running;


    private SerializedSubject<Boolean, Boolean> hasNetworkSubject =
            PublishSubject.<Boolean>create().toSerialized();

    private FeedPresenter presenter;

    @Bind(R.id.feed_recycler) RecyclerView feedRecycler;
    @Bind(R.id.progress_bar) ProgressBar mProgressBar;
    @Bind(R.id.question_recycler_view_container) SwipeRefreshLayout refreshLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void attachDashBoardService(DashboardService service){
        dashboardService = service;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view =  inflater.inflate(R.layout.fragment_feed, container, false);
        ButterKnife.bind(this, view);

        return view;
    }

    @Override public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {


        //RecyclerView
        mLayoutManager = new LinearLayoutManager(this.getContext());
        feedRecycler.setLayoutManager(mLayoutManager);
        feedRecycler.addItemDecoration(new DividerItemDecoration(this.getContext()));


        //Presenter and Model
        presenter = new FeedPresenter(getContext(), this, feedRecycler, mLayoutManager);
        FeedModel model = new FeedModel(getContext(), dashboardService);
        presenter.attach(model);

        // Pagination
        feedRecycler.addOnScrollListener(presenter.mRecyclerViewOnScrollListener);

        //RefreshLayout
        refreshLayout.setOnRefreshListener(this);
    }

    @Override
    public void showNoNetworkSnack(Boolean network) {
        if (!network) {
            Snackbar.make(view, R.string.no_connection_message, Snackbar.LENGTH_INDEFINITE)
                    .setAction("OK", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            //Dismiss the snackbar
                        }
                    }).show();
        }
        else {
            Snackbar.make(view, R.string.got_connection_message, Snackbar.LENGTH_LONG).show();
        }
    }


    @Override
    public void updateRefreshing(boolean refreshing) {
        refreshLayout.setRefreshing(refreshing);
    }

    @Override
    public void setProgressVisibility(int visible) {
        mProgressBar.setVisibility(visible);
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
                        showNoNetworkSnack(network);

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
        presenter.drop();
    }


    public void networkChange(Boolean hasNetwork) {
        if (presenter != null) {
            presenter.notifyNetworkChange(hasNetwork);
        }
        this.hasNetworkSubject.onNext(hasNetwork);
    }

    @Override
    public void onRefresh() {
        updateRefreshing(false);
        presenter.refreshAdapter();
    }
}
