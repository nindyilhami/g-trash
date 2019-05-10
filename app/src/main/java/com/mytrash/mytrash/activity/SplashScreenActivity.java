package com.mytrash.mytrash.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import com.mytrash.mytrash.R;

public class SplashScreenActivity extends AppCompatActivity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splashscreen);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                Intent login=new Intent(SplashScreenActivity.this, LoginActivity.class);
                startActivity(login);
                finish();
            }
        },2000);
    }
}
