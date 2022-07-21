package com.example.foodfriends.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;

import com.example.foodfriends.R;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.foodfriends.fragments.RestaurantDetailFragment;
import com.example.foodfriends.misc.DistanceRestaurantServer;
import com.example.foodfriends.misc.GeoCoder;
import com.example.foodfriends.misc.RestaurantListener;
import com.example.foodfriends.models.Restaurant;
import com.example.foodfriends.observable_models.RestaurantObservable;
import com.example.foodfriends.observable_models.UserObservable;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback{
    private List<RestaurantObservable> restaurantList;
    private UserObservable user;
    private int maxRadiuskm = 40; //in kilometers
    private String TAG = "MapActivity";
    private DistanceRestaurantServer distanceRestaurantServer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null){
            setContentView(R.layout.activity_map);
            user = new UserObservable(ParseUser.getCurrentUser());
            restaurantList = new ArrayList<RestaurantObservable>();
            distanceRestaurantServer = new DistanceRestaurantServer(restaurantList);
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
        }
    }

    /**
     * Manipulates the map when it's available.
     * The API invokes this callback when the map is ready to be used.
     * Added markers for all the restaurants and made them clickable to go to restaurant detal screen
     * If Google Play services is not installed on the device, the user receives a prompt to install
     * Play services inside the SupportMapFragment. The API invokes this method after the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        parseGeneral();
        //distanceRestaurantServer.findRestaurants(getResources().getString(R.string.yelp_api_key), new RestaurantListener(){});
        for (int i = 0; i < restaurantList.size(); i++) {
            LatLng restaurant_coordinates = new LatLng(restaurantList.get(i).getCoordinates().getLatitude(), restaurantList.get(i).getCoordinates().getLongitude());
            Marker marker = googleMap.addMarker(new MarkerOptions()
                    .position(restaurant_coordinates)
                    .title(restaurantList.get(i).getName()));
            marker.setTag(i);
        }

        float zoomLevel = 12.0f;
        if (user.getCoordinates() != null)
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(user.getCoordinates().getLatitude(), user.getCoordinates().getLongitude()), zoomLevel));

        googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                int position = (int)(marker.getTag());
                RestaurantObservable restaurantObservable = restaurantList.get(position);
                displayRestaurantDetailFragment(restaurantObservable);
                return false;
            }
        });
    }

    private void parseGeneral() {
        ParseQuery<Restaurant> query = ParseQuery.getQuery(Restaurant.class);
        if (user.getCoordinates() != null) {
            query.whereWithinKilometers("location_coordinates", user.getCoordinates(), maxRadiuskm);
        } else {
            LatLng coordinates = GeoCoder.getLocationFromAddress(this, user.getCity() + ", " + user.getState());
            ParseGeoPoint geoPoint = new ParseGeoPoint(coordinates.latitude, coordinates.longitude);
            user.setCoordinates(geoPoint);
            query.whereWithinKilometers("location_coordinates", geoPoint, maxRadiuskm);

        }
        query.setLimit(20);
        List<RestaurantObservable> observed = new ArrayList<RestaurantObservable>();
        try {
            List<Restaurant> parseRestaurants = query.find();
            // for debugging purposes let's print every restaurant description to logcat
            for (Restaurant r : parseRestaurants) {
                restaurantList.add(new RestaurantObservable(r));
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public void displayRestaurantDetailFragment(RestaurantObservable restaurant) {
        Fragment detail_fragment = new RestaurantDetailFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable("restaurant", restaurant);
        detail_fragment.setArguments(bundle);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.map, detail_fragment);
        transaction.addToBackStack(null).commit();
    }

}