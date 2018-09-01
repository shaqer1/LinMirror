package com.shafay.linmirror;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import static com.shafay.linmirror.NotificationListener.mAuth;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        if(mAuth!= null && mAuth.getCurrentUser() != null){
            Intent i = new Intent(SplashActivity.this, DashboardActivity.class);
            startActivity(i);
        }else {
            Intent i = new Intent(SplashActivity.this, LoginActivity.class);
            startActivity(i);
        }
    }
}
