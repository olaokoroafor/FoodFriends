package com.example.foodfriends.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.foodfriends.R;
import android.util.Log;
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

import java.util.ArrayList;
import java.util.List;


public class ExploreFragment extends Fragment {

        private RecyclerView rvRestaurants;
        public static final String TAG = "Explore Fragment";
        private ExploreAdapter adapter;
        public List<Restaurant> restaurantList;
        private SwipeRefreshLayout swipeContainer;
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
                    fetchTimelineAsync(0);
                }
            });
            // Configure the refreshing colors
            swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                    android.R.color.holo_green_light,
                    android.R.color.holo_orange_light,
                    android.R.color.holo_red_light);

            return view;
        }

        public void fetchTimelineAsync(int page) {
            // Send the network request to fetch the updated data
            // `client` here is an instance of Android Async HTTP
            // getHomeTimeline is an example endpoint.
            adapter.clear();
            //should change this later on for getting restaurants from yelp API instead of from Parse
            queryRestaurants();
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