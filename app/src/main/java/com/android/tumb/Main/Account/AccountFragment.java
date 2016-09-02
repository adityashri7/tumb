package com.android.tumb.Main.Account;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import com.android.tumb.Data.API.AccountApiService;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.tumb.R;
import com.tumblr.jumblr.types.User;

public class AccountFragment extends Fragment implements AccountApiService.UserResponse {

    private View view;
    private AccountInterface listener;
    private TextView logoutButton;
    private TextView likesCount;
    private TextView followingCount;
    private AccountApiService accountService;
    private User user;


    public void attachAccountService(AccountApiService accountApiService){
        this.accountService = accountApiService;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view =  inflater.inflate(R.layout.fragment_account, container, false);
        logoutButton = (TextView) view.findViewById(R.id.logout_button);
        likesCount = (TextView) view.findViewById(R.id.user_like_count);
        followingCount = (TextView) view.findViewById(R.id.user_follow_count);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.logoutAction();
            }
        });


        ConnectivityManager cm = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        Boolean hasNetwork = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        if (hasNetwork){
            accountService.getUser(this);
        }

        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        listener = (AccountInterface) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (listener!= null) {
            listener = null;
        }
    }

    @Override
    public void processFinish(User user) {
        this.user = user;
        likesCount.setText(""+user.getLikeCount());
        followingCount.setText(""+user.getFollowingCount());
    }

    public interface AccountInterface{
        void logoutAction();
    }

}
