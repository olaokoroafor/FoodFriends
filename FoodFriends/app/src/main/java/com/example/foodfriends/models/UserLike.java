package com.example.foodfriends.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("UserLike")
public class UserLike extends ParseObject {
    private static final String USER_KEY = "user";
    private static final String RESTAURANT_KEY = "restaurant";

    public ParseUser getUser(){
        return getParseUser(USER_KEY);
    }
    public void setUser(ParseUser user){
        put(USER_KEY, user);
    }

    public Restaurant getRestaurant(){
        return (Restaurant) getParseObject(RESTAURANT_KEY);
    }
    public void setRestaurant(Restaurant restaurant){
        put(RESTAURANT_KEY, restaurant);
    }


}

