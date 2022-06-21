package com.example.foodfriends.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.foodfriends.R;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.foodfriends.adapters.ExploreAdapter;
import com.example.foodfriends.models.Restaurant;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class ExploreFragment extends Fragment {

        private RecyclerView rvRestaurants;
        private int offset = 0;
        public static final String TAG = "Explore Fragment";
        private ExploreAdapter adapter;
        public List<Restaurant> restaurantList;
        private SwipeRefreshLayout swipeContainer;
        public static final String YELP_URL = "https://api.yelp.com/v3/businesses/search";
        public ExploreFragment() {
            // Required empty public constructor
        }


        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragment_explore, container, false);
            // Inflate the layout for this fragment
            swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainer);
            // Setup refresh listener which triggers new data loading
            swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    // Your code to refresh the list here.
                    // Make sure you call swipeContainer.setRefreshing(false)
                    // once the network request has completed successfully.
                    fetchTimelineAsync();
                }
            });
            // Configure the refreshing colors
            swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                    android.R.color.holo_green_light,
                    android.R.color.holo_orange_light,
                    android.R.color.holo_red_light);

            return view;
        }

        public void fetchTimelineAsync() {
            // Send the network request to fetch the updated data
            // `client` here is an instance of Android Async HTTP
            // getHomeTimeline is an example endpoint.
            adapter.clear();
            //should change this later on for getting restaurants from yelp API instead of from Parse
            yelpQuery();
            swipeContainer.setRefreshing(false);
        }

        @Override
        public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
            rvRestaurants = view.findViewById(R.id.rvRestaurants);

            restaurantList = new ArrayList<Restaurant>();
            adapter = new ExploreAdapter(getContext(), restaurantList);
            rvRestaurants.setAdapter(adapter);
            rvRestaurants.setLayoutManager(new LinearLayoutManager(getContext()));

            queryRestaurants();
            yelpQuery();
            //yelpAsyncQuery();
        }
/*
    private void yelpAsyncQuery() {
        String url = YELP_URL;
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("q", "android");
        params.put("rsz", "8");
        client.get(url, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                // Root JSON in response is an dictionary i.e { "data : [ ... ] }
                // Handle resulting parsed JSON response here
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String res, Throwable t) {
                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
            }
        });
    }
*/
    private void yelpQuery() {
        // Use OkHttpClient singleton
        OkHttpClient client = new OkHttpClient();
        HttpUrl.Builder urlBuilder = HttpUrl.parse(YELP_URL).newBuilder();
        urlBuilder.addQueryParameter("term", "restaurant");
        urlBuilder.addQueryParameter("limit", "20");
        urlBuilder.addQueryParameter("offset", String.valueOf(offset));
        urlBuilder.addQueryParameter("location", ParseUser.getCurrentUser().getString("city"));
        String url = urlBuilder.build().toString();
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer " + R.string.yelp_api_key)
                .addHeader("Accept", "application/json")
                .build();

        // Get a handler that can be used to post to the main thread


        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, final Response response){
                if (response.isSuccessful()) {
                    String responseData = response.body().toString();
                    //JSONObject json = new JSONObject(responseData);
                   // offset += new JSONArray().length();
                } else {
                    Log.i(TAG, response.toString());
                    //Toast.makeText(getActivity(), "Connection failed", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void queryRestaurants() {
            // specify what type of data we want to query - Post.class
            ParseQuery<Restaurant> query = ParseQuery.getQuery(Restaurant.class);
            // start an asynchronous call for posts
            query.findInBackground(new FindCallback<Restaurant>() {
                @Override
                public void done(List<Restaurant> restaurants, ParseException e) {
                    // check for errors
                    if (e != null) {
                        Log.e(TAG, "Issue with getting posts", e);
                        return;
                    }
                    // for debugging purposes let's print every post description to logcat
                    for (Restaurant r : restaurants) {
                        Log.i(TAG, "Restaurant: " + r.getName());
                    }
                    // save received posts to list and notify adapter of new data
                    restaurantList.addAll(restaurants);
                    adapter.notifyDataSetChanged();
                }
            });
        }


}