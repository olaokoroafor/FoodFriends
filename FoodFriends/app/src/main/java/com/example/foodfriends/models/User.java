package com.example.foodfriends.models;

import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

@ParseClassName("User")
public class User extends ParseUser{
    private static final String USERNAME_KEY = "username";
    private static final String NAME_KEY = "name";
    private static final String PROFILE_PHOTO_KEY = "profilePhoto";
    private static final String CITY_KEY = "city";
    private static final String STATE_KEY = "state";
    private static final String LATITUDE_KEY = "latitude";
    private static final String LONGITUDE_KEY = "longitude";
    private static final String TAG = "USER MODEL";

    public String getUsername() {
        return getString(USERNAME_KEY);
    }

    public void setUserame(String username) {
        put(USERNAME_KEY, username);
    }

    public String getName() {
        return getString(NAME_KEY);
    }

    public void setName(String name) {
        put(NAME_KEY, name);
    }

    public ParseFile getProfilePhoto() {
        return getParseFile(PROFILE_PHOTO_KEY);
    }

    public void getProfilePhoto(ParseFile profile_photo) {
        put(PROFILE_PHOTO_KEY, profile_photo);
    }

    public String getCity() {
        return getString(CITY_KEY);
    }

    public void setCity(String city) {
        put(CITY_KEY, city);
    }

    public String getState() {
        return getString(STATE_KEY);
    }

    public void setState(String name) {
        put(STATE_KEY, name);
    }
    public Double getLatitude() {
        return getDouble(LATITUDE_KEY);
    }

    public void setLatitude(Double latitude) {
        put(LATITUDE_KEY, latitude);
    }

    public Double getLongitude() {
        return getDouble(LONGITUDE_KEY);
    }

    public void setLongitude(Double longitude) {
        put(LONGITUDE_KEY, longitude);
    }
}