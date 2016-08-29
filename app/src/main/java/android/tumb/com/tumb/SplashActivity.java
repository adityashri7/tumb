package android.tumb.com.tumb;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.tumb.com.tumb.Login.LoginActivity;
import android.tumb.com.tumb.Main.MainActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        TumbApp app = TumbApp.get(this);

        Intent intent;
        if (app.getToken() != null && app.getTokenSecret() != null){
            intent = new Intent(SplashActivity.this, MainActivity.class);
        } else {
            intent = new Intent(SplashActivity.this, LoginActivity.class);
        }

        startActivity(intent);
        finish();

    }


    @Override protected void onDestroy() {
        super.onDestroy();
    }

}