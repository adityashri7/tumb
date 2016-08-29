package android.tumb.com.tumb.Login;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import android.os.Bundle;
import android.tumb.com.tumb.Main.MainActivity;
import android.tumb.com.tumb.R;
import android.view.View;
import android.widget.Toast;

import com.chyrta.onboarder.OnboarderActivity;
import com.chyrta.onboarder.OnboarderPage;
import com.tumblr.loglr.Interfaces.LoginListener;
import com.tumblr.loglr.LoginResult;
import com.tumblr.loglr.Loglr;

import java.util.ArrayList;
import java.util.List;

/**
 * A login screen that offers login via Tumblr
 */
public class LoginActivity extends OnboarderActivity {

    // UI references.
    private View mProgressView;

    String consumerKey;
    String consumerSecret;
    private Loglr loglr;
    private Context context;
    private List<OnboarderPage> onboarderPages;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        consumerKey = getString(R.string.consumer_key);
        consumerSecret = getString(R.string.consumer_secret);

        context = this;


        loglr = Loglr.getInstance();
        loglr.setConsumerKey(consumerKey);
        loglr.setConsumerSecretKey(consumerSecret)
                .setLoginListener(new LoginListener() {
                    @Override
                    public void onLoginSuccessful(LoginResult loginResult) {
                        String strOAuthToken = loginResult.getOAuthToken();
                        String strOAuthTokenSecret = loginResult.getOAuthTokenSecret();
                        setUser(strOAuthToken, strOAuthTokenSecret);
                    }
                })
                .setUrlCallBack("https://github.com/adityashri7/tumb/callback.php");


        onboarderPages = new ArrayList<OnboarderPage>();


        OnboarderPage onboarderPage1 = new OnboarderPage(getString(R.string.login_screen_1_title), getString(R.string.login_screen_1_text), R.drawable.ic_tumblr);
        OnboarderPage onboarderPage2 = new OnboarderPage(getString(R.string.login_screen_2_title), getString(R.string.login_screen_2_text), R.drawable.ic_offline);
        OnboarderPage onboarderPage3 = new OnboarderPage(getString(R.string.login_screen_3_title), getString(R.string.login_screen_3_text), R.drawable.ic_rxjava);


        onboarderPage1.setTitleColor(R.color.white);
        onboarderPage1.setDescriptionColor(R.color.white);
        onboarderPage2.setTitleColor(R.color.white);
        onboarderPage2.setDescriptionColor(R.color.white);
        onboarderPage3.setTitleColor(R.color.white);
        onboarderPage3.setDescriptionColor(R.color.white);


        onboarderPage1.setBackgroundColor(R.color.colorPrimary);
        onboarderPage2.setBackgroundColor(R.color.colorPrimaryDark);
        onboarderPage3.setBackgroundColor(R.color.colorAccent);


        onboarderPages.add(onboarderPage1);
        onboarderPages.add(onboarderPage2);
        onboarderPages.add(onboarderPage3);


        setOnboardPagesReady(onboarderPages);
        shouldDarkenButtonsLayout(true);
        setFinishButtonTitle("LOGIN");

    }

    @Override
    public void onFinishButtonPressed() {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        final Boolean hasNetwork = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        if (hasNetwork) {
            loglr.initiateInActivity(context);
        }
        else{
            Toast.makeText(context.getApplicationContext(), "Please check your network connection.", Toast.LENGTH_LONG).show();
        }

    }

    private void setUser(String strOAuthToken, String strOAuthTokenSecret) {
        SharedPreferences prefs = getSharedPreferences("keychain", MODE_PRIVATE);
        prefs.edit().putString("OAuthToken", strOAuthToken).apply();
        prefs.edit().putString("OAuthTokenSecret", strOAuthTokenSecret).apply();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

}

