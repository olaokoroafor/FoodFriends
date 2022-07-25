package com.example.foodfriends.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.foodfriends.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.parse.ParseUser;

public class SplashActivity extends AppCompatActivity {
    private ImageView ivSplashPic;
    private static final String TAG = "SplashActivity";
    /** Duration of wait **/
    private final int SPLASH_DISPLAY_LENGTH = 1000;

        /** Called when the activity is first created. */
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_splash);
            ivSplashPic = findViewById(R.id.ivSplashPic);

            new Handler().postDelayed(new Runnable(){
                @Override
                public void run() {
                    if (ParseUser.getCurrentUser() != null){
                        Intent i = new Intent(SplashActivity.this, MainActivity.class);
                        startActivity(i);
                    }
                    else{
                        Intent i = new Intent(SplashActivity.this, LogInActivity.class);
                        startActivity(i);
                    }

                    SplashActivity.this.finish();
                }
            }, SPLASH_DISPLAY_LENGTH);
        }

}
