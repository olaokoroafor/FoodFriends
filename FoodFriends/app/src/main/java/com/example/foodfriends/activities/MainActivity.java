package com.example.foodfriends.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.example.foodfriends.R;
import com.example.foodfriends.fragments.ExploreFragment;
import com.example.foodfriends.fragments.ProfileFragment;
import com.example.foodfriends.fragments.RestaurantDetailFragment;
import com.example.foodfriends.fragments.SearchFragment;
import com.example.foodfriends.observable_models.RestaurantObservable;
import com.example.foodfriends.observable_models.UserObservable;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.parse.ParseUser;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "Main Activity";

    BottomNavigationView bottomNavigationView;
    private String fragment_tag;
    ParseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        user = ParseUser.getCurrentUser();

        final FragmentManager fragmentManager = getSupportFragmentManager();
        final Fragment exploreFragment = new ExploreFragment();
        final Fragment searchFragment = new SearchFragment();
        final Fragment profileFragment = new ProfileFragment();

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        NavigationBarView.OnItemSelectedListener reloaded_listener = new NavigationBarView.OnItemSelectedListener(){
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Log.i(TAG, "Menu item selected");
                Fragment fragment = exploreFragment;
                if (item.getItemId() == R.id.menu_explore){
                    fragment = exploreFragment;
                    fragment_tag = "explore";
                }
                if (item.getItemId() == R.id.menu_search){
                    fragment = searchFragment;
                    fragment_tag = "search";
                }
                if (item.getItemId() == R.id.menu_profile){
                    fragment = profileFragment;
                    Bundle bundle = new Bundle();
                    bundle.putParcelable("user", new UserObservable(ParseUser.getCurrentUser()));
                    fragment.setArguments(bundle);
                    fragment_tag = "profile";
                }
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.tvPlaceholder, fragment, fragment_tag);
                transaction.addToBackStack(null).commit();
                return true;
            }
        };

        bottomNavigationView.setOnItemSelectedListener(reloaded_listener);
        bottomNavigationView.setSelectedItemId(R.id.menu_explore);
}

/**
 * Displays profile fragment for user passed in
 * Done here because can not make this change from Adapter**/
    public void displayOtherProfileFragment(ParseUser user) {
        Fragment other_profile_fragment = new ProfileFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable("user", new UserObservable(user));
        other_profile_fragment.setArguments(bundle);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.tvPlaceholder, other_profile_fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    /**
     * Displays restaurant detail fragment for user passed in
     * Done here because can not make this change from Adapter
     * @param restaurant**/
    public void displayRestaurantDetailFragment(RestaurantObservable restaurant) {
        Fragment detail_fragment = new RestaurantDetailFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable("restaurant", restaurant);
        detail_fragment.setArguments(bundle);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.tvPlaceholder, detail_fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

}