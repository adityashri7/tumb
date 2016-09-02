package com.android.tumb.Main;

import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import com.android.tumb.Data.API.AccountApiService;
import com.android.tumb.Data.API.DashboardService;
import com.android.tumb.Login.LoginActivity;
import com.android.tumb.Main.Account.AccountFragment;
import com.android.tumb.Main.Feed.FeedFragment;
import com.android.tumb.Misc.InternetReceiver;
import com.android.tumb.R;
import com.android.tumb.TumbApp;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity implements AccountFragment.AccountInterface{

    String consumerKey;
    String consumerSecret;
    Toolbar toolbar;
    ViewPager pager;
    Drawable drawableHome;
    Drawable drawableUser;
    FeedFragment f0;
    InternetReceiver receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        consumerKey = getString(R.string.consumer_key);
        consumerSecret = getString(R.string.consumer_secret);

        TumbApp app = TumbApp.get(this);
        AccountApiService accountApiService = new AccountApiService(consumerKey, consumerSecret, app.getToken(), app.getTokenSecret());
        DashboardService dashboardService = new DashboardService(consumerKey, consumerSecret, app.getToken(), app.getTokenSecret());

        pager = (ViewPager) findViewById(R.id.main_pager);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.getMenu().add("Home")
        .setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_ALWAYS)
        .setIcon(R.drawable.ic_home)
        .setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                pager.setCurrentItem(0);
                return true;
            }
        });

        toolbar.getMenu().add("User")
                .setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_ALWAYS)
                .setIcon(R.drawable.ic_user)
                .setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        pager.setCurrentItem(1);
                        return true;
                    }
                });

        drawableHome = toolbar.getMenu().getItem(1).getIcon();
        drawableUser = toolbar.getMenu().getItem(0).getIcon();
        drawableHome.mutate();
        drawableHome.setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_ATOP);
        drawableUser.mutate();
        drawableUser.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);

        f0 = new FeedFragment();
        f0.attachDashBoardService(dashboardService);
        final AccountFragment f1 = new AccountFragment();
        f1.attachAccountService(accountApiService);


        pager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                switch (position) {
                    case 0: {
                        return f0;
                    }
                    case 1: {
                        return f1;
                    }
                    default:{
                        return f0;
                    }
                }
            }

            @Override
            public int getCount() {
                return 2;
            }
        });

        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) { }
            @Override
            public void onPageSelected(int position) {
                setToolbarItemActive(position);
            }
            @Override
            public void onPageScrollStateChanged(int state) {}
        });

        //Internet Receiver
        receiver = null;
        receiver = new InternetReceiver();
        receiver.setMainActivityHandler(this);
        IntentFilter intent = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(receiver,intent);

    }

    public void setToolbarItemActive(int pos){
        switch (pos){
            case 1:
                drawableHome.mutate();
                drawableHome.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
                drawableUser.mutate();
                drawableUser.setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_ATOP);
                break;
            case 0:
                drawableHome.mutate();
                drawableHome.setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_ATOP);
                drawableUser.mutate();
                drawableUser.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
        }

    }

    @Override
    public void onDestroy(){
        if (receiver!= null){
            unregisterReceiver(receiver);
            receiver = null;
        }
        super.onDestroy();
    }

    public void networkChange(Boolean hasNetwork) {
        f0.networkChange(hasNetwork);
    }

    @Override
    public void logoutAction() {
        TumbApp.get(this).logout();
        Intent loginIntent = new Intent(this, LoginActivity.class);
        startActivity(loginIntent);
        finish();
    }
}
