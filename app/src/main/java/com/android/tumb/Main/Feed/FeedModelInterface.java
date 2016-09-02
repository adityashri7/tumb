package com.android.tumb.Main.Feed;

import com.android.tumb.Data.PostWrapper;

import java.util.List;

/**
 * Created by trust on 9/2/2016.
 */
public interface FeedModelInterface {
    void updateAdapter(List<PostWrapper> wrapperList);
}
