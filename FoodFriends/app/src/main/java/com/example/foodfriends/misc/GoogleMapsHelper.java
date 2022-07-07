package com.example.foodfriends.misc;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.example.foodfriends.observable_models.RestaurantObservable;
import com.parse.ParseUser;

import okhttp3.HttpUrl;

public class GoogleMapsHelper {
    private static final String GMAPS_URL =  "http://maps.google.com/maps";
    private RestaurantObservable restaurantObservable;
    private Context context;

    public GoogleMapsHelper(RestaurantObservable restaurant, Context context){
        this.restaurantObservable = restaurant;
        this.context = context;
    }

    /**
     * Directs user to google maps with restaurant preplaced in map
     * */
    public void goToGmaps() {
        HttpUrl.Builder urlBuilder = HttpUrl.parse(GMAPS_URL).newBuilder();
        String destination = String.valueOf(restaurantObservable.getCoordinates().getLatitude()) + "," + String.valueOf(restaurantObservable.getCoordinates().getLongitude()) + " (" + restaurantObservable.getName() + ")";
        urlBuilder.addQueryParameter("daddr", destination);
        String uri = urlBuilder.build().toString();
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        intent.setPackage("com.google.android.apps.maps");
        context.startActivity(intent);
    }
}
