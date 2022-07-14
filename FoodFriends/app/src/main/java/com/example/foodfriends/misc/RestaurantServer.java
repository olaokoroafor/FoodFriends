package com.example.foodfriends.misc;

import android.text.style.AlignmentSpan;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.foodfriends.fragments.ExploreFragment;
import com.example.foodfriends.models.Restaurant;
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
import java.util.List;
import java.util.Observer;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class RestaurantServer {
    public static final String YELP_URL = "https://api.yelp.com/v3/businesses/search";
    private static final String TAG = "Restaurant Server";
    private int max_radius = 40000; //in kilometers
    private boolean parseSource;
    private int offset;
    private List<RestaurantObservable> observed_restaurants;
    private UserObservable user;
    private boolean precise_loc;


    /**
     * Constructor
     */
    public RestaurantServer(List<RestaurantObservable> restaurantList) {
        this.parseSource = true;
        this.offset = 0;
        this.observed_restaurants = restaurantList;
        this.user = new UserObservable(ParseUser.getCurrentUser());
        precise_loc = false;
    }

    /**
     * Resets the instance variables to allow for a new search without offsets and at Parse
     */
    public void reset() {
        this.offset = 0;
        this.parseSource = true;
    }


    public UserObservable getUser() {
        return user;
    }

    public void setUser(UserObservable user) {
        this.user = user;
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
            urlBuilder.addQueryParameter("radius", String.valueOf(max_radius));
        } else {
            urlBuilder.addQueryParameter("location", user.getCity() + "," + user.getState());
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
                            observed_restaurants.addAll(res);
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

    /**
     * Adds restaurant from parse or calls yelpquery to do so depending on parse_source
     */
    public void findRestaurants(String apiKey, @Nullable RestaurantListener listener) {
        Log.i(TAG, String.valueOf(parseSource));
        if (parseSource) {
            ParseQuery<Restaurant> query = ParseQuery.getQuery(Restaurant.class);
            if (user.getCoordinates() != null) {
                query.whereWithinKilometers("location_coordinates", user.getCoordinates(), max_radius);
            } else {
                query.whereEqualTo("city", user.getCity());
                query.whereEqualTo("state", user.getState());
            }

            query.setLimit(20);
            query.setSkip(offset);
            List<RestaurantObservable> observed = new ArrayList<RestaurantObservable>();
            query.findInBackground(new FindCallback<Restaurant>() {
                @Override
                public void done(List<Restaurant> restaurants, ParseException e) {
                    List<RestaurantObservable> observed = new ArrayList<RestaurantObservable>();
                    Log.i(TAG, "Restaurant Size: " + String.valueOf(restaurants.size()));
                    // check for errors
                    if (e != null) {
                        Log.e(TAG, "Issue with getting posts", e);
                        return;
                    }
                    // for debugging purposes let's print every restaurant description to logcat
                    for (Restaurant r : restaurants) {
                        observed.add(new RestaurantObservable(r));
                    }
                    offset += restaurants.size();
                    if (restaurants.size() < 20) {
                        offset = 0;
                        parseSource = false;
                    }
                    if (restaurants.size() == 0) {
                        yelpQuery(apiKey, listener);
                    }

                    observed_restaurants.addAll(observed);
                    listener.dataChanged();
                }
            });

        } else {
            yelpQuery(apiKey, listener);
        }
    }
}


interface Listener{
    void dataChanged();
}

