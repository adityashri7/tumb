package android.tumb.com.tumb;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.f2prateek.rx.preferences.Preference;
import com.tumblr.jumblr.types.User;

/**
 * Created by trust on 8/27/2016.
 */
public class TumbApp extends Application {
    private String token;
    private String tokenSecret;

    @Override public void onCreate() {
        super.onCreate();
        final SharedPreferences prefs = this.getSharedPreferences("keychain", Context.MODE_PRIVATE);
        token = prefs.getString("OAuthToken", null);
        tokenSecret = prefs.getString("OAuthTokenSecret", null);
    }

    public String getToken(){
        final SharedPreferences prefs = this.getSharedPreferences("keychain", Context.MODE_PRIVATE);
        token = prefs.getString("OAuthToken", null);
        return token;
    }

    public String getTokenSecret(){
        final SharedPreferences prefs = this.getSharedPreferences("keychain", Context.MODE_PRIVATE);
        tokenSecret = prefs.getString("OAuthTokenSecret", null);
        return tokenSecret;
    }

    public static TumbApp get(Context context) {
        return (TumbApp) context.getApplicationContext();
    }


    public void logout() {

        SharedPreferences prefs = this.getSharedPreferences("keychain", Context.MODE_PRIVATE);
        token = null;
        tokenSecret = null;

        prefs.edit().putString("OAuthToken", null).apply();
        prefs.edit().putString("OAuthTokenSecret", null).apply();

    }
}
