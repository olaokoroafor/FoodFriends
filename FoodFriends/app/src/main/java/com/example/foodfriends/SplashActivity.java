package com.example.foodfriends;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        Thread background = new Thread() {
            public void run() {
                try {
                    // Thread will sleep for 5 seconds
                    sleep(1*1000);

                    // After 5 seconds redirect to another intent
                    if (ParseUser.getCurrentUser() != null){
                        Intent i = new Intent(MainActivity.this, HomeActivity.class);
                        startActivity(i);
                    }
                    else{
                        Intent i=new Intent(getBaseContext(), LoginActivity.class);
                        startActivity(i);
                    }

                    //Remove activity
                    finish();
                } catch (Exception e) {
                }
            }
        };
        // start thread
        background.start();
    }
}
