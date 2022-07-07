package com.example.foodfriends.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.foodfriends.R;
import com.example.foodfriends.adapters.FindFriendsAdapter;
import com.example.foodfriends.adapters.SearchRestaurantsAdapter;
import com.example.foodfriends.models.Restaurant;
import com.example.foodfriends.observable_models.FriendObservable;
import com.example.foodfriends.observable_models.RestaurantObservable;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends Fragment {

    private static final String TAG = "SearchFragment";
    private SearchView searchRestaurants;
    private RecyclerView rvRestaurants;
    private SearchRestaurantsAdapter adapter;
    private List<RestaurantObservable> allRestaurants;

    public SearchFragment() {
        // Required empty public constructor
    }

    /**
     * Inflates the UI xml for the fragment
     * */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    /**
     * Connects the recycler view of restuarants to the adapter
     * Populates the list of restaurants for the adapter to display
     * */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        searchRestaurants = view.findViewById(R.id.searchRestaurants);
        rvRestaurants = view.findViewById(R.id.rvSearchRestaurants);

        searchRestaurants.setIconified(false);
        // perform set on query text listener event
        searchRestaurants.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                adapter.clear();
                queryRestaurants(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        allRestaurants= new ArrayList<RestaurantObservable>();
        rvRestaurants.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new SearchRestaurantsAdapter(getContext(), allRestaurants);
        rvRestaurants.setAdapter(adapter);
    }

    /**
     * Searches Parse Database for restaurants with name starting with submitted query
     * */
    private void queryRestaurants(String search_term) {
        ParseQuery<Restaurant> query = ParseQuery.getQuery(Restaurant.class);
        query.whereMatches("name", search_term, "i");
        query.setLimit(20);
        query.findInBackground(new FindCallback<Restaurant>() {
            @Override
            public void done(List<Restaurant> restaurants, ParseException e) {
                // check for errors
                if (e != null) {
                    Log.e(TAG, "Issue with getting restaurants", e);
                    return;
                }
                allRestaurants.clear();
                for (Restaurant r: restaurants){
                    Log.i(TAG, r.getName());
                    allRestaurants.add(new RestaurantObservable(r));
                }
                adapter.notifyDataSetChanged();
            }
        });
    }
}