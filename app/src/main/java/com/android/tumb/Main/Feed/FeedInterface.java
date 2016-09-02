package com.android.tumb.Main.Feed;

/**
 * Created by trust on 9/1/2016.
 */
public interface FeedInterface {
    void showNoNetworkSnack(Boolean network);

    void setProgressVisibility(int visible);

    void updateRefreshing(boolean refreshing);
}
