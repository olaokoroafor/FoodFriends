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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.foodfriends.activities.MapActivity;
import com.example.foodfriends.adapters.ExploreAdapter;
import com.example.foodfriends.misc.DistanceRestaurantServer;
import com.example.foodfriends.misc.EndlessRecyclerViewScrollListener;
import com.example.foodfriends.misc.PopularityRestaurantServer;
import com.example.foodfriends.misc.RelevanceRestaurantServer;
import com.example.foodfriends.misc.RestaurantListener;
import com.example.foodfriends.observable_models.RestaurantObservable;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;


public class ExploreFragment extends Fragment implements Observer, AdapterView.OnItemSelectedListener {

    private RecyclerView rvRestaurants;
    private int RELEVANCE_SORT = 0;
    private int DISTANCE_SORT = 1;
    private int POPULARITY_SORT = 2;
    private ImageView ivMap;
    private static final String TAG = "ExploreFragment";
    private ExploreAdapter adapter;
    private List<RestaurantObservable> restaurantList;
    private SwipeRefreshLayout swipeContainer;
    private EndlessRecyclerViewScrollListener scrollListener;
    private RelevanceRestaurantServer relevanceRestaurantServer;
    private PopularityRestaurantServer popularityRestaurantServer;
    private DistanceRestaurantServer distanceRestaurantServer;
    private RestaurantListener restaurantListener;
    private ProgressBar exploreProgressBar;
    private Spinner exploreSpinner;

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
        relevanceRestaurantServer = new RelevanceRestaurantServer(restaurantList, getContext());
        relevanceRestaurantServer.getUser().addObserver(this);

        popularityRestaurantServer = new PopularityRestaurantServer(restaurantList);
        popularityRestaurantServer.getUser().addObserver(this);

        distanceRestaurantServer = new DistanceRestaurantServer(restaurantList);
        distanceRestaurantServer.getUser().addObserver(this);

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
        rvRestaurants = view.findViewById(R.id.rvRestaurants);
        ivMap = view.findViewById(R.id.ivMapIndicator);
        exploreSpinner = view.findViewById(R.id.spinnerExplore);

        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                restaurantList.clear();
                scrollListener.resetState();

                relevanceRestaurantServer.reset();
                distanceRestaurantServer.reset();
                popularityRestaurantServer.reset();
                if(exploreSpinner.getSelectedItemPosition() == RELEVANCE_SORT){
                    relevanceRestaurantServer.findRestaurants(getResources().getString(R.string.yelp_api_key), restaurantListener);
                    swipeContainer.setRefreshing(false);
                }
                else if(exploreSpinner.getSelectedItemPosition() == DISTANCE_SORT){
                    distanceRestaurantServer.findRestaurants(getResources().getString(R.string.yelp_api_key), restaurantListener);
                    swipeContainer.setRefreshing(false);
                }
                else if(exploreSpinner.getSelectedItemPosition() == POPULARITY_SORT){
                    popularityRestaurantServer.findRestaurants(restaurantListener);
                    swipeContainer.setRefreshing(false);
                }
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

        String[] items = new String[]{"Relevance", "Distance", "Popularity"};
        ArrayAdapter<String> spinner_adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_dropdown_item, items);
        exploreSpinner.setAdapter(spinner_adapter);
        spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        exploreSpinner.setAdapter(spinner_adapter);
        exploreSpinner.setOnItemSelectedListener(this);
        exploreSpinner.setSelection(RELEVANCE_SORT);

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
                if(exploreSpinner.getSelectedItemPosition() == RELEVANCE_SORT){
                    relevanceRestaurantServer.findRestaurants(getResources().getString(R.string.yelp_api_key), restaurantListener);
                }
                else if(exploreSpinner.getSelectedItemPosition() == DISTANCE_SORT){
                    distanceRestaurantServer.findRestaurants(getResources().getString(R.string.yelp_api_key), restaurantListener);
                }
                else if(exploreSpinner.getSelectedItemPosition() == POPULARITY_SORT){
                    popularityRestaurantServer.findRestaurants(restaurantListener);
                }
            }
        };
        rvRestaurants.addOnScrollListener(scrollListener);
    }

    @Override
    public void update(Observable o, Object arg) {
        restaurantList.clear();

        relevanceRestaurantServer.reset();
        distanceRestaurantServer.reset();
        popularityRestaurantServer.reset();

        if(exploreSpinner.getSelectedItemPosition() == RELEVANCE_SORT){
            relevanceRestaurantServer.findRestaurants(getResources().getString(R.string.yelp_api_key), restaurantListener);
        }
        else if(exploreSpinner.getSelectedItemPosition() == DISTANCE_SORT){
            distanceRestaurantServer.findRestaurants(getResources().getString(R.string.yelp_api_key), restaurantListener);
        }
        else if(exploreSpinner.getSelectedItemPosition() == POPULARITY_SORT){
            popularityRestaurantServer.findRestaurants(restaurantListener);
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        restaurantList.clear();

        relevanceRestaurantServer.reset();
        distanceRestaurantServer.reset();
        popularityRestaurantServer.reset();

        if(exploreSpinner.getSelectedItemPosition() == RELEVANCE_SORT){
            Log.i(TAG, "RELEVANCE SORT");
            relevanceRestaurantServer.findRestaurants(getResources().getString(R.string.yelp_api_key), restaurantListener);
        }
        else if(exploreSpinner.getSelectedItemPosition() == DISTANCE_SORT){
            Log.i(TAG, "DISTANCE SORT");
            distanceRestaurantServer.findRestaurants(getResources().getString(R.string.yelp_api_key), restaurantListener);
        }
        else if(exploreSpinner.getSelectedItemPosition() == POPULARITY_SORT){
            Log.i(TAG, "POPULARITY SORT");
            popularityRestaurantServer.findRestaurants(restaurantListener);
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}


