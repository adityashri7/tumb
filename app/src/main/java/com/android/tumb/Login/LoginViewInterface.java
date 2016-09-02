package com.android.tumb.Login;

import com.chyrta.onboarder.OnboarderPage;

import java.util.List;

/**
 * Created by trust on 9/1/2016.
 */
public interface LoginViewInterface {
    void login(String token, String tokenSecret);
    void setOnboarding(List<OnboarderPage> pages);

}
