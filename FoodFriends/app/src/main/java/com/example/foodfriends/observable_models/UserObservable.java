package com.example.foodfriends.observable_models;

import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;

import com.parse.ParseFile;
import com.parse.ParseUser;

import java.util.Observable;

public class UserObservable extends Observable {
    private ParseUser user;
    private String objectId;
    private String username;
    private String password;
    private String name;
    private ParseFile profilePhoto;
    private String city;
    private String state;
    private Double latitude;
    private Double longitude;
    private static final String TAG = "USER OBSERVABLE MODEL";

    public UserObservable(){
        this.user = new ParseUser();
        this.objectId = user.getObjectId();
    }

    public ParseUser getUser() {
        return user;
    }

    public void setUser(ParseUser user) {
        this.user = user;
    }

    public String getObjectId() {
        return objectId;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
        user.put("password", password);
        setChanged();
        notifyObservers();
    }

    public void setUsername(String username) {
        this.username = username;
        this.user.put("username", username);
        setChanged();
        notifyObservers();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        this.user.put("name", name);
        setChanged();
        notifyObservers();
    }

    public ParseFile getProfilePhoto() {
        return profilePhoto;
    }

    public void setProfilePhoto(ParseFile profilePhoto) {
        this.profilePhoto = profilePhoto;
        this.user.put("profilePhoto", profilePhoto);
        setChanged();
        notifyObservers();
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
        this.user.put("city", city);
        setChanged();
        notifyObservers();
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
        this.user.put("state", state);
        setChanged();
        notifyObservers();
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
        this.user.put("latitude", latitude);
        setChanged();
        notifyObservers();
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
        this.user.put("longitude", longitude);
        setChanged();
        notifyObservers();

    }

    public void save_user(){
        this.user.saveInBackground();
    }



    public int isValid() {

        if(TextUtils.isEmpty(getUsername()))
            return  0;
        else if(TextUtils.isEmpty(getPassword()))
            return 1;
        else if(getPassword().length()<=5)
            return 2;
        else if(TextUtils.isEmpty(getName()))
            return 3;
        else
            return -1;
    }
}
