package com.android.tumb.Login;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import com.android.tumb.R;
import com.chyrta.onboarder.OnboarderPage;
import com.tumblr.loglr.Interfaces.LoginListener;
import com.tumblr.loglr.LoginResult;
import com.tumblr.loglr.Loglr;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by trust on 9/1/2016.
 */
public class LoginPresenter {

    private List<OnboarderPage> onboarderPages;
    private LoginViewInterface listener;
    private Loglr loglr;
    private Context context;

    public LoginPresenter(LoginViewInterface viewInterface, Context context) {
        listener = viewInterface;
        this.context = context;
        loglr = Loglr.getInstance();
        String consumerKey = context.getString(R.string.consumer_key);
        String consumerSecret = context.getString(R.string.consumer_secret);
        loglr.setConsumerKey(consumerKey);
        loglr.setConsumerSecretKey(consumerSecret)
                .setLoginListener(new LoginListener() {
                    @Override
                    public void onLoginSuccessful(LoginResult loginResult) {
                        String strOAuthToken = loginResult.getOAuthToken();
                        String strOAuthTokenSecret = loginResult.getOAuthTokenSecret();
                        listener.login(strOAuthToken, strOAuthTokenSecret);
                    }
                })
                .setUrlCallBack("https://github.com/adityashri7/tumb/callback.php");
    }


    public void initializeOnboarders(){
        onboarderPages = new ArrayList<OnboarderPage>();


        OnboarderPage onboarderPage1 = new OnboarderPage(context.getString(R.string.login_screen_1_title), context.getString(R.string.login_screen_1_text), R.drawable.ic_tumblr);
        OnboarderPage onboarderPage2 = new OnboarderPage(context.getString(R.string.login_screen_2_title), context.getString(R.string.login_screen_2_text), R.drawable.ic_offline);
        OnboarderPage onboarderPage3 = new OnboarderPage(context.getString(R.string.login_screen_3_title), context.getString(R.string.login_screen_3_text), R.drawable.ic_rxjava);


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
        listener.setOnboarding(onboarderPages);

    }

    public void login() {
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
}
