package com.example.foodfriends.misc;

import android.util.Log;

import androidx.annotation.Nullable;

import com.example.foodfriends.models.Friends;
import com.example.foodfriends.models.Restaurant;
import com.example.foodfriends.models.UserLike;
import com.example.foodfriends.models.UserToGo;
import com.example.foodfriends.observable_models.RestaurantObservable;
import com.example.foodfriends.observable_models.UserObservable;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class DistanceRestaurantServer {
    public static final String YELP_URL = "https://api.yelp.com/v3/businesses/search";
    private static final String TAG = "Restaurant Server";
    private int maxRadiuskm = 40; //in kilometers
    private int offset;
    private List<RestaurantObservable> observedRestaurants;
    private UserObservable user;



    /**
     * Constructor
     */
    public DistanceRestaurantServer(List<RestaurantObservable> restaurantList) {
        this.offset = 0;
        this.observedRestaurants = restaurantList;
        this.user = new UserObservable(ParseUser.getCurrentUser());
    }


    /**
     * Resets the instance variables to allow for a new search without offsets and at Parse
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
     * Adds restaurant from parse likes, togos, or general or calls yelpquery to do so depending on source
     */
    public void findRestaurants(String apiKey, @Nullable RestaurantListener listener) {
        yelpQuery(apiKey, listener);
    }


    /**
     * Adds restaurants returned from Yelp Query to the list that the explore adapter uses
     */
    public void yelpQuery(String apiKey,  @Nullable RestaurantListener listener) {
        // Use OkHttpClient singleton
        OkHttpClient client = new OkHttpClient();
        HttpUrl.Builder urlBuilder = HttpUrl.parse(YELP_URL).newBuilder();
        urlBuilder.addQueryParameter("term", "restaurant");
        urlBuilder.addQueryParameter("offset", String.valueOf(offset));
        urlBuilder.addQueryParameter("limit", "20");
        if (user.getCoordinates() != null) {
            urlBuilder.addQueryParameter("latitude", String.valueOf(user.getCoordinates().getLatitude()));
            urlBuilder.addQueryParameter("longitude", String.valueOf(user.getCoordinates().getLongitude()));
            urlBuilder.addQueryParameter("radius", String.valueOf(maxRadiuskm*1000));
            urlBuilder.addQueryParameter("sort_by", "distance");
        } else {
            urlBuilder.addQueryParameter("location", user.getCity() + "," + user.getState());
            urlBuilder.addQueryParameter("sort_by", "distance");
        }
        String url = urlBuilder.build().toString();
        Log.i(TAG, "URL: " + url);
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer " + apiKey)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, final Response response) {
                if (response.isSuccessful()) {
                    String responseData = null;
                    try {
                        responseData = response.body().string();
                        JSONObject json = new JSONObject(responseData);
                        JSONArray jsonArray = json.getJSONArray("businesses");
                        List<RestaurantObservable> res = RestaurantObservable.fromJsonArray(jsonArray);
                        offset += 20;
                        if (res.size() == 0) {
                            yelpQuery(apiKey, listener);
                        } else {
                            for (RestaurantObservable r: res){
                                observedRestaurants.add(r);
                            }

                            Log.i(TAG, "Size: " + String.valueOf(observedRestaurants.size()));
                            listener.dataChanged();

                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    Log.i(TAG, response.toString());
                }
            }
        });
    }
}
