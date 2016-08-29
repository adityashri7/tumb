package android.tumb.com.tumb.Misc;

import android.tumb.com.tumb.Data.PostSerializable;
import android.tumb.com.tumb.Data.PostWrapper;

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
