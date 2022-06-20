package com.example.foodfriends.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.example.foodfriends.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.parse.ParseUser;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "Main Activity";
    Button btnLogOut;
    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final FragmentManager fragmentManager = getSupportFragmentManager();

        // define your fragments here
        //final Fragment timelineFragment = new TimelineFragment();
        //final Fragment composeFragment = new ComposeFragment();
        //final Fragment profileFragment = new ProfileFragment();

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        btnLogOut = findViewById(R.id.btnLogOut);
        btnLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseUser.logOut();
                Intent i = new Intent(MainActivity.this, LogInActivity.class);
                // set the new task and clear flags
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
            }
        });
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener(){
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Log.i(TAG, "Menu item selected");
                //Fragment fragment = timelineFragment;
                if (item.getItemId() == R.id.menu_explore){
                    Log.i(TAG, "Explore Fragment");
                    //fragment = timelineFragment;
                }
                if (item.getItemId() == R.id.menu_search){
                    Log.i(TAG, "Search selected");
                    //fragment = composeFragment;
                }
                if (item.getItemId() == R.id.menu_profile){
                    //compose action has been selected
                    //showComposeFragment("");
                    Log.i(TAG, "profile selected");
                    //fragment = profileFragment;
                    /*
                    Bundle bundle = new Bundle();
                    bundle.putParcelable("user", ParseUser.getCurrentUser());
                    fragment.setArguments(bundle);
                    */
                }
                /*
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.tvPlaceholder, fragment);
                transaction.addToBackStack(null).commit();
                */
                return true;
            }
        });
        bottomNavigationView.setSelectedItemId(R.id.menu_explore);
    }
}