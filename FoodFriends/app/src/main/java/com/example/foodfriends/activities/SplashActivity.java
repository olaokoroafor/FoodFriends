package com.example.foodfriends.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.example.foodfriends.R;
import com.parse.ParseUser;

public class SplashActivity extends AppCompatActivity {

    private static final String TAG = "SplashActivity";
    /** Duration of wait **/
    private final int SPLASH_DISPLAY_LENGTH = 1000;

        /** Called when the activity is first created. */
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_splash);

            /* New Handler to start the Menu-Activity
             * and close this Splash-Screen after some seconds.*/
            new Handler().postDelayed(new Runnable(){
                @Override
                public void run() {
                    /* Create an Intent that will start the Menu-Activity. */
                    if (ParseUser.getCurrentUser() != null){
                        Intent i = new Intent(SplashActivity.this, MainActivity.class);
                        startActivity(i);
                    }
                    else{
                        Log.i(TAG, "intent");
                        Intent i = new Intent(SplashActivity.this, LogInActivity.class);
                        startActivity(i);
                    }

                    Log.i(TAG, "remove activity");
                    SplashActivity.this.finish();
                }
            }, SPLASH_DISPLAY_LENGTH);
        }
}
