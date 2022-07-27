package com.example.foodfriends.misc;

import android.text.style.AlignmentSpan;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.foodfriends.fragments.ExploreFragment;
import com.example.foodfriends.fragments.ProfileFragment;
import com.example.foodfriends.models.Friends;
import com.example.foodfriends.models.Restaurant;
import com.example.foodfriends.models.UserLike;
import com.example.foodfriends.models.UserToGo;
import com.example.foodfriends.observable_models.RestaurantObservable;
import com.example.foodfriends.observable_models.UserObservable;
import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Observer;
import java.util.Set;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class PopularityRestaurantServer {
    private static final String TAG = "Restaurant Server";
    private int offset;
    private List<RestaurantObservable> observedRestaurants;
    private UserObservable user;


    /**
     * Constructor
     */
    public PopularityRestaurantServer(List<RestaurantObservable> restaurantList) {
        this.offset = 0;
        this.observedRestaurants = restaurantList;
        this.user = new UserObservable(ParseUser.getCurrentUser());
    }


    /**
     * Resets the instance variables to allow for a new search without offset
     */
    public void reset() {
        this.offset = 0;
    }


    public UserObservable getUser() {
        return user;
    }

    public void setUser(UserObservable user) {
        this.user = user;
    }


    /**
     * Adds restaurant from Parse with the most likes to the list
     */
    public void findRestaurants(@Nullable RestaurantListener listener) {
        final boolean[] updating = {true};
        ParseQuery<Restaurant> query = ParseQuery.getQuery(Restaurant.class);
        query.setLimit(20);
        query.setSkip(offset);
        query.orderByDescending("likes");
        query.findInBackground(new FindCallback<Restaurant>() {
            @Override
            public void done(List<Restaurant> restaurants, ParseException e) {
                // check for errors
                if (e != null) {
                    Log.e(TAG, "Issue with getting posts", e);
                    return;
                }

                for (Restaurant r : restaurants) {
                    observedRestaurants.add(new RestaurantObservable(r));
                }

                offset += restaurants.size();
                listener.dataChanged();
            }
        });
    }
}

