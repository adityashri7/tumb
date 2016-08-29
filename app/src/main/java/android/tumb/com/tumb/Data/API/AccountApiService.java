package android.tumb.com.tumb.Data.API;

import android.os.AsyncTask;

import com.tumblr.jumblr.JumblrClient;
import com.tumblr.jumblr.types.Post;
import com.tumblr.jumblr.types.User;

import java.util.List;

import rx.subjects.SerializedSubject;

/**
 * Created by trust on 8/27/2016.
 * Class to fetch Account info
 */
public class AccountApiService {
    JumblrClient jumblr;
    String oAuthToken;
    String oAuthTokenSecret;
    String consumerKey;
    String consumerSecret;

    public AccountApiService(String consumerKey, String consumerSecret, String token, String tokenSecret){
        this.consumerKey = consumerKey;
        this.consumerSecret = consumerSecret;
        this.oAuthToken = token;
        this.oAuthTokenSecret = tokenSecret;
        jumblr = new JumblrClient(consumerKey, consumerSecret);
        jumblr.setToken(oAuthToken, oAuthTokenSecret);
    }

    public void getUser(UserResponse delegate){
        newUser getNewUser = new newUser(delegate);
        getNewUser.execute();
    }


    public class newUser extends AsyncTask<String, String, User> {

        public UserResponse delegate = null;
        public newUser(UserResponse delegate){
            this.delegate = delegate;
        }

        @Override
        protected User doInBackground(String... params) {
            return jumblr.user();
        }

        @Override
        protected void onPostExecute(User user){
            delegate.processFinish(user);
        }
    }

    public interface UserResponse {
        void processFinish(User output);
    }

}
