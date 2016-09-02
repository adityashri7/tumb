package com.android.tumb.Misc;

import com.android.tumb.Data.PostSerializable;
import com.android.tumb.Data.PostWrapper;

/**
 * Created by trust on 8/27/2016.
 */
public class PostWrapperToSerializable {
    PostWrapper wrapper;
    PostSerializable serializable;
    public PostWrapperToSerializable(PostWrapper postWrapper){
        wrapper = postWrapper;
        serializable = new PostSerializable(postWrapper.getBlog(), postWrapper.getBlogAvatar(),
                postWrapper.getNotes(), postWrapper.getSource(), postWrapper.getPhotoUrl(),
                postWrapper.getTags(), postWrapper.getType(), postWrapper.getCaption());
    }

    public PostSerializable getSerializable(){
        return serializable;
    }
}
