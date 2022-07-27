package com.example.foodfriends.misc;
import android.content.Context;
import com.example.foodfriends.observable_models.RestaurantObservable;
import java.util.List;

public class RestaurantServer {
    private static int RELEVANCE_SORT = 0;
    private static int DISTANCE_SORT = 1;
    private static int POPULARITY_SORT = 2;
    private RelevanceRestaurantServer relevanceRestaurantServer;
    private PopularityRestaurantServer popularityRestaurantServer;
    private DistanceRestaurantServer distanceRestaurantServer;
    private List<RestaurantObservable> restaurants;

    public RestaurantServer(List<RestaurantObservable> restaurants, Context context){
        this.restaurants = restaurants;
        relevanceRestaurantServer = new RelevanceRestaurantServer(restaurants, context);
        popularityRestaurantServer = new PopularityRestaurantServer(restaurants);
        distanceRestaurantServer = new DistanceRestaurantServer(restaurants);
    }

    public void reset(){
        relevanceRestaurantServer.reset();
        distanceRestaurantServer.reset();
        popularityRestaurantServer.reset();
    }

    public void findRestaurants(int selectedItemPosition, String apiKey, RestaurantListener restaurantListener) {
        if(selectedItemPosition == RELEVANCE_SORT){
            relevanceRestaurantServer.findRestaurants(apiKey, restaurantListener);
        }
        else if(selectedItemPosition == DISTANCE_SORT){
            distanceRestaurantServer.findRestaurants(apiKey, restaurantListener);
        }
        else if(selectedItemPosition == POPULARITY_SORT){
            popularityRestaurantServer.findRestaurants(restaurantListener);
        }
    }
}
