package com.example.foodfriends.models;

import com.example.foodfriends.misc.RestaurantListener;
import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

@ParseClassName("Comment")
public class Comment extends ParseObject {
    public static final String RESTAURANT_KEY = "restaurant";
    public static final String TEXT_KEY = "text";
    public static final String USER_KEY = "user";
    private static final int SECOND_MILLIS = 1000;
    private static final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
    private static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
    private static final int DAY_MILLIS = 24 * HOUR_MILLIS;

    public Restaurant getRestaurant(){
        return (Restaurant) getParseObject(this.RESTAURANT_KEY);
    }

    public void setRestaurant(Restaurant restaurant){
        put(RESTAURANT_KEY, restaurant);
    }

    public String getText(){
        return getString(TEXT_KEY);
    }

    public void setText(String text){
        put(TEXT_KEY, text);
    }

    public ParseUser getUser(){
        return getParseUser(USER_KEY);
    }

    public void setUser(ParseUser user){
        put(USER_KEY, user);
    }

}
