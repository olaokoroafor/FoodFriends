package com.example.foodfriends.fragments;

import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;

import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.foodfriends.R;

import android.util.Log;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.foodfriends.activities.LogInActivity;
import com.example.foodfriends.activities.MainActivity;
import com.example.foodfriends.activities.MapActivity;
import com.example.foodfriends.adapters.ExploreAdapter;
import com.example.foodfriends.misc.EndlessRecyclerViewScrollListener;
import com.example.foodfriends.misc.RestaurantListener;
import com.example.foodfriends.misc.RestaurantServer;
import com.example.foodfriends.observable_models.RestaurantObservable;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;


public class ExploreFragment extends Fragment implements Observer {

    private RecyclerView rvRestaurants;
    private int REQUEST_CODE = 0;
    private ImageView ivMap;
    private static final String TAG = "ExploreFragment";
    private ExploreAdapter adapter;
    private List<RestaurantObservable> restaurantList;
    private SwipeRefreshLayout swipeContainer;
    private EndlessRecyclerViewScrollListener scrollListener;
    private RestaurantServer restaurantServer;
    private RestaurantListener restaurantListener;
    private ProgressBar exploreProgressBar;

    public ExploreFragment() {
        // Required empty public constructor
    }


    /**
     * Inflates the UI xml for the fragment and declares/instantiates the swipe container
     * */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_explore, container, false);
        restaurantList = new ArrayList<RestaurantObservable>();
        restaurantServer = new RestaurantServer(restaurantList);
        restaurantServer.getUser().addObserver(this);
        restaurantListener = new RestaurantListener(){
            @Override
            public void dataChanged() {
                rvRestaurants.post(new Runnable() {
                    public void run() {
                        adapter.notifyDataSetChanged();
                        rvRestaurants.setVisibility(View.VISIBLE);
                        exploreProgressBar.setVisibility(View.GONE);
                    }
                });
            }
        };
        swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainer);
        exploreProgressBar = view.findViewById(R.id.exploreProgressBar);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                restaurantList.clear();
                scrollListener.resetState();
                restaurantServer.reset();
                restaurantServer.findRestaurants(getResources().getString(R.string.yelp_api_key), restaurantListener);
                swipeContainer.setRefreshing(false);
            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        return view;
    }

    /**
     * Connects the recycler view of restaurants to the adapter
     * Declares/instantiates the endless scroll listener
     * Populates the list of restautants for the adapter to display
     * */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rvRestaurants = view.findViewById(R.id.rvRestaurants);
        ivMap = view.findViewById(R.id.ivMapIndicator);
        ivMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), MapActivity.class);
                startActivity(intent);
            }
        });
        adapter = new ExploreAdapter(getContext(), restaurantList);
        rvRestaurants.setAdapter(adapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());

        rvRestaurants.setLayoutManager(linearLayoutManager);
        scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                restaurantServer.findRestaurants(getResources().getString(R.string.yelp_api_key), restaurantListener);
                Log.i(TAG, String.valueOf(adapter.size()));
            }
        };
        rvRestaurants.addOnScrollListener(scrollListener);

        restaurantServer.findRestaurants(getResources().getString(R.string.yelp_api_key), restaurantListener);
    }

    @Override
    public void update(Observable o, Object arg) {
        restaurantList.clear();
        restaurantServer.reset();
        restaurantServer.findRestaurants(getResources().getString(R.string.yelp_api_key), restaurantListener);
    }
}


