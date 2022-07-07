package com.example.foodfriends.misc;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.example.foodfriends.observable_models.RestaurantObservable;

public class GoogleMapsHelper {
    RestaurantObservable restaurantObservable;
    Context context;

    public GoogleMapsHelper(RestaurantObservable restaurant, Context context){
        this.restaurantObservable = restaurant;
        this.context = context;
    }

    /**
     * Directs user to google maps with restaurant preplaced in map
     * */
    public void goToGmaps() {
        String uri = "http://maps.google.com/maps?daddr=" + restaurantObservable.getLatitude().toString() + "," + restaurantObservable.getLongitude().toString() + " (" + restaurantObservable.getName() + ")";
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        intent.setPackage("com.google.android.apps.maps");
        context.startActivity(intent);
    }
}
