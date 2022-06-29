package com.example.foodfriends.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.foodfriends.R;
import com.example.foodfriends.fragments.ExploreFragment;
import com.example.foodfriends.fragments.FindFriendsFragment;
import com.example.foodfriends.fragments.ProfileFragment;
import com.example.foodfriends.fragments.SearchFragment;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.parse.ParseUser;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "Main Activity";

    BottomNavigationView bottomNavigationView;
    FusedLocationProviderClient mFusedLocationClient;
    int PERMISSION_ID = 44;
    private double latitude;
    private double longitude;
    ParseUser user;

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        user = ParseUser.getCurrentUser();

        final FragmentManager fragmentManager = getSupportFragmentManager();

        // define your fragments here
        final Fragment exploreFragment = new ExploreFragment();
        final Fragment searchFragment = new SearchFragment();
        final Fragment profileFragment = new ProfileFragment();


        //Use when we want to get user's location
        /*
         New Handler to start the Menu-Activity
         * and close this Splash-Screen after some seconds.
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mFusedLocationClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                Location location = task.getResult();
                if (location == null) {
                    requestNewLocationData();
                } else {
                    Log.i(TAG, "Getting Location 2");
                    user.put("latitude", location.getLatitude());
                    user.put("longitude", location.getLongitude());
                    user.saveInBackground();

                }
            }
        });
        */

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener(){
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Log.i(TAG, "Menu item selected");
                //Fragment fragment = exploreFragment;
                if (item.getItemId() == R.id.menu_explore) {
                    if (fragmentManager.findFragmentByTag("explore") != null) {
                        //if the fragment exists, show it.
                        fragmentManager.beginTransaction().show(fragmentManager.findFragmentByTag("explore")).commit();
                    } else {
                        //if the fragment does not exist, add it to fragment manager.
                        fragmentManager.beginTransaction().add(R.id.tvPlaceholder, exploreFragment, "explore").commit();
                    }
                    if (fragmentManager.findFragmentByTag("search") != null) {
                        //if the other fragment is visible, hide it.
                        fragmentManager.beginTransaction().hide(fragmentManager.findFragmentByTag("search")).commit();
                    }
                    if (fragmentManager.findFragmentByTag("profile") != null) {
                        //if the other fragment is visible, hide it.
                        fragmentManager.beginTransaction().hide(fragmentManager.findFragmentByTag("profile")).commit();
                    }
                    //Log.i(TAG, "Explore Fragment");
                    //fragment = exploreFragment;
                    //fragmentManager.beginTransaction().addToBackStack(null).show(fragment).commit();
                }
                if (item.getItemId() == R.id.menu_search) {
                    if (fragmentManager.findFragmentByTag("search") != null) {
                        //if the fragment exists, show it.
                        fragmentManager.beginTransaction().show(fragmentManager.findFragmentByTag("search")).commit();
                    } else {
                        //if the fragment does not exist, add it to fragment manager.
                        fragmentManager.beginTransaction().add(R.id.tvPlaceholder, searchFragment, "search").commit();
                    }
                    if (fragmentManager.findFragmentByTag("explore") != null) {
                        //if the other fragment is visible, hide it.
                        fragmentManager.beginTransaction().hide(fragmentManager.findFragmentByTag("explore")).commit();
                    }
                    if (fragmentManager.findFragmentByTag("profile") != null) {
                        //if the other fragment is visible, hide it.
                        fragmentManager.beginTransaction().hide(fragmentManager.findFragmentByTag("profile")).commit();
                    }
                }
                if (item.getItemId() == R.id.menu_profile) {
                    if (fragmentManager.findFragmentByTag("profile") != null) {
                        //if the fragment exists, show it.
                        fragmentManager.beginTransaction().show(fragmentManager.findFragmentByTag("profile")).commit();
                    } else {
                        //if the fragment does not exist, add it to fragment manager.
                        Bundle bundle = new Bundle();
                        bundle.putParcelable("user", ParseUser.getCurrentUser());
                        profileFragment.setArguments(bundle);
                        fragmentManager.beginTransaction().add(R.id.tvPlaceholder, profileFragment, "profile").commit();
                    }
                    if (fragmentManager.findFragmentByTag("search") != null) {
                        //if the other fragment is visible, hide it.
                        fragmentManager.beginTransaction().hide(fragmentManager.findFragmentByTag("search")).commit();
                    }
                    if (fragmentManager.findFragmentByTag("explore") != null) {
                        //if the other fragment is visible, hide it.
                        fragmentManager.beginTransaction().hide(fragmentManager.findFragmentByTag("explore")).commit();
                    }
                }
            return true;
            }
        }
        );
        bottomNavigationView.setSelectedItemId(R.id.menu_explore);

    /*
    @SuppressLint("MissingPermission")
    private void requestNewLocationData() {

        // Initializing LocationRequest
        // object with appropriate methods
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(5);
        mLocationRequest.setFastestInterval(0);
        mLocationRequest.setNumUpdates(1);

        // setting LocationRequest
        // on FusedLocationClient
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
    }

    private LocationCallback mLocationCallback = new LocationCallback() {

        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location mLastLocation = locationResult.getLastLocation();
            Log.i(TAG, "Getting location 1");
            user.put("latitude", mLastLocation.getLatitude());
            user.put("longitude", mLastLocation.getLongitude());
            user.saveInBackground();
        }
    };

    /*
    @SuppressLint("MissingPermission")
    private void getLastLocation() {
        // check if permissions are given
        if (checkPermissions()) {

            // check if location is enabled
            if (isLocationEnabled()) {

                // getting last
                // location from
                // FusedLocationClient
                // object
                mFusedLocationClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        Location location = task.getResult();
                        if (location == null) {
                            requestNewLocationData();
                        } else {
                            Log.i(TAG, "Getting Location 2");
                            user.put("latitude", location.getLatitude());
                            user.put("longitude", location.getLongitude());
                            user.saveInBackground();

                        }
                    }
                });
            } else {
                Toast.makeText(this, "Please turn on" + " your location...", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        } else {
            // if permissions aren't available,
            // request for permissions
            requestPermissions();
        }
    }



    // method to check for permissions
    private boolean checkPermissions() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;

        // If we want background location
        // on Android 10.0 and higher,
        // use:
        // ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    // method to request for permissions
    private void requestPermissions() {
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_ID);
    }

    // method to check
    // if location is enabled
    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    /*
    // If everything is alright then
    @Override
    public void
    onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_ID) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation();
            }
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        if (checkPermissions()) {
            getLastLocation();
        }
    }
    */
}

    public void displayOtherProfileFragment(ParseUser user) {
        Fragment other_profile_fragment = new ProfileFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable("user", user);
        other_profile_fragment.setArguments(bundle);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.tvPlaceholder, other_profile_fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }
}