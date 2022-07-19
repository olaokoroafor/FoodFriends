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
    private static final int PARSE_LIKES = 0;
    private static final int PARSE_TOGOS = 1;
    private static final int PARSE_GENERAL = 2;
    private static final int YELP = 3;
    private int maxRadiuskm = 40; //in kilometers
    private int offset;
    private List<RestaurantObservable> observedRestaurants;
    private Set<String> displayedRestaurants;
    private UserObservable user;
    private int source;
    private List<ParseUser> friends;


    /**
     * Constructor
     */
    public DistanceRestaurantServer(List<RestaurantObservable> restaurantList) {
        this.source = PARSE_LIKES;
        this.offset = 0;
        this.observedRestaurants = restaurantList;
        this.user = new UserObservable(ParseUser.getCurrentUser());
        this.friends = new ArrayList<ParseUser>();
        this.displayedRestaurants = new HashSet<String>();
        populateFriends();
    }


    /**
     * Resets the instance variables to allow for a new search without offsets and at Parse
     */
    public void reset() {
        this.offset = 0;
        this.source = PARSE_LIKES;
        displayedRestaurants.clear();
    }


    public UserObservable getUser() {
        return user;
    }

    public void setUser(UserObservable user) {
        this.user = user;
    }


    private void populateFriends(){
        ParseQuery<Friends> query = ParseQuery.getQuery(Friends.class);
        query.include(Friends.REQUESTED_KEY);
        query.whereEqualTo(Friends.USER_KEY, user.getUser());
        List<Friends> parseFriends = new ArrayList<Friends>();
        try {
            parseFriends = query.find();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        for (Friends friend: parseFriends){
            friends.add(friend.getRequested());
        }
    }

    /**
     * Adds restaurant from parse likes, togos, or general or calls yelpquery to do so depending on source
     */
    public void findRestaurants(String apiKey, @Nullable RestaurantListener listener) {
        if (source == PARSE_LIKES){
            parseLikes(apiKey, listener);
        }
        else if (source == PARSE_TOGOS){
            parseTogos(apiKey, listener);
        }
        else if (source == PARSE_GENERAL){
            parseGeneral(apiKey, listener);
        } else {
            yelpQuery(apiKey, listener);
        }
    }

    /**
     * Adds restaurant liked by the user's foodfriends
     */
    private void parseLikes(String apiKey, RestaurantListener listener) {
        ParseQuery<UserLike> query = ParseQuery.getQuery(UserLike.class);
        final boolean[] updating = {true};
        // include data referred by restaurant key
        query.whereContainedIn("user", friends);
        query.include(UserLike.RESTAURANT_KEY);
        query.include(UserLike.USER_KEY);
        //TO DO MAKE DISTINCT
        query.addDescendingOrder("createdAt");
        query.setLimit(20);
        query.setSkip(offset);

        query.findInBackground(new FindCallback<UserLike>() {
            @Override
            public void done(List<UserLike> likes, ParseException e) {
                // check for errors
                if (e != null) {
                    Log.e(TAG, "Issue with getting Restaurants", e);
                    return;
                }
                for (UserLike like : likes) {
                    String r_id = like.getRestaurant().getYelpID();
                    if(!displayedRestaurants.contains(r_id)){
                        observedRestaurants.add(new RestaurantObservable(like.getRestaurant()));
                        displayedRestaurants.add(r_id);
                    }
                }
                offset += likes.size();
                if (likes.size() < 20) {
                    offset = 0;
                    source = PARSE_TOGOS;
                }
                if (likes.size() == 0) {
                    updating[0] = false;
                    parseTogos(apiKey, listener);
                }

                if (updating[0]){
                    listener.dataChanged();
                }
            }
        });

    }

    /**
     * Adds restaurant the user's foodfriends are planning to go to
     */
    private void parseTogos(String apiKey, RestaurantListener listener) {
        ParseQuery<UserToGo> query = ParseQuery.getQuery(UserToGo.class);
        final boolean[] updating = {true};
        // include data referred by restaurant key
        query.whereContainedIn("user", friends);
        query.include(UserLike.USER_KEY);
        query.include(UserLike.RESTAURANT_KEY);
        //TO DO MAKE DISTINCT
        query.addDescendingOrder("createdAt");
        query.setLimit(20);
        query.setSkip(offset);

        query.findInBackground(new FindCallback<UserToGo>() {
            @Override
            public void done(List<UserToGo> togos, ParseException e) {
                // check for errors
                if (e != null) {
                    Log.e(TAG, "Issue with getting Restaurants", e);
                    return;
                }
                for (UserToGo toGo : togos) {
                    String r_id = toGo.getRestaurant().getYelpID();
                    if(!displayedRestaurants.contains(r_id)){
                        observedRestaurants.add(new RestaurantObservable(toGo.getRestaurant()));
                        displayedRestaurants.add(r_id);
                    }
                }
                offset += togos.size();
                if (togos.size() < 20) {
                    offset = 0;
                    source = PARSE_GENERAL;
                }
                if (togos.size() == 0) {
                    updating[0] = false;
                    parseGeneral(apiKey, listener);
                }

                if (updating[0]){
                    listener.dataChanged();
                }
            }
        });
    }

    private void parseGeneral(String apiKey, RestaurantListener listener) {
        ParseQuery<Restaurant> query = ParseQuery.getQuery(Restaurant.class);
        final boolean[] updating = {true};
        if (user.getCoordinates() != null) {
            query.whereWithinKilometers("location_coordinates", user.getCoordinates(), maxRadiuskm);
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
                // check for errors
                if (e != null) {
                    Log.e(TAG, "Issue with getting posts", e);
                    return;
                }
                // for debugging purposes let's print every restaurant description to logcat
                Log.i(TAG, "Parse general list" + String.valueOf(restaurants.size()));
                for (Restaurant r : restaurants) {
                    if(!displayedRestaurants.contains(r.getYelpID())){
                        observedRestaurants.add(new RestaurantObservable(r));
                        displayedRestaurants.add(r.getYelpID());
                    }
                }

                offset += restaurants.size();
                if (restaurants.size() < 20) {
                    offset = 0;
                    source = YELP;
                }
                if (restaurants.size() == 0) {
                    updating[0] = false;
                    yelpQuery(apiKey, listener);
                }
                if (updating[0]){
                    listener.dataChanged();
                }
            }
        });
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
                            for (RestaurantObservable r: res){
                                if(!displayedRestaurants.contains(r.getYelpId())){
                                    observedRestaurants.add(r);
                                    displayedRestaurants.add(r.getYelpId());
                                }
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
