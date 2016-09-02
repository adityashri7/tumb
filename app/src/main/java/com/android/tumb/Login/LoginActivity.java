package com.android.tumb.Login;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import com.android.tumb.Main.MainActivity;
import com.chyrta.onboarder.OnboarderActivity;
import com.chyrta.onboarder.OnboarderPage;
import java.util.List;

import butterknife.ButterKnife;

/**
 * A login screen that offers login via Tumblr
 */
public class LoginActivity extends OnboarderActivity implements LoginViewInterface{

    private LoginPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);

        presenter = new LoginPresenter(this, this);
        presenter.initializeOnboarders();
    }

    @Override
    public void onFinishButtonPressed() {
        presenter.login();

    }

    @Override
    public void login(String strOAuthToken, String strOAuthTokenSecret) {
        SharedPreferences prefs = getSharedPreferences("keychain", MODE_PRIVATE);
        prefs.edit().putString("OAuthToken", strOAuthToken).apply();
        prefs.edit().putString("OAuthTokenSecret", strOAuthTokenSecret).apply();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void setOnboarding(List<OnboarderPage> pages) {
        setOnboardPagesReady(pages);
        shouldDarkenButtonsLayout(true);
        setFinishButtonTitle("LOGIN");
    }
}

