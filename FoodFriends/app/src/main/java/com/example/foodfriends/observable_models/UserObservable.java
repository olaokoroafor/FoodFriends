package com.example.foodfriends.observable_models;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.Log;

import com.example.foodfriends.models.Friends;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.List;
import java.util.Observable;

public class UserObservable extends Observable implements Parcelable {
    private ParseUser user;
    private String objectId;
    private String username;
    private String password;
    private String name;
    private ParseFile profilePhoto;
    private String city;
    private String state;
    private ParseGeoPoint coordinates;
    private Boolean privateAccount;
    private static final String USERNAME_KEY = "username";
    private static final String PASSWORD_KEY = "password";
    private static final String NAME_KEY = "name";
    private static final String PROFILE_PHOTO_KEY = "profilePhoto";
    private static final String CITY_KEY = "city";
    private static final String STATE_KEY = "state";
    private static final String LOCATION_COORDINATES_KEY = "location_coordinates";
    private static final String PRIVATE_KEY = "private";
    private static final String TAG = "USER OBSERVABLE MODEL";

    public UserObservable(){
        this.user = new ParseUser();
        this.objectId = user.getObjectId();
        this.privateAccount = false;
    }

    public UserObservable(ParseUser user){
        this.user = user;
        this.objectId = user.getObjectId();
        this.username = user.getString(USERNAME_KEY);
        this.name = user.getString(NAME_KEY);
        this.profilePhoto = user.getParseFile(PROFILE_PHOTO_KEY);
        this.city = user.getString(CITY_KEY);
        this.state = user.getString(STATE_KEY);
        this.coordinates = user.getParseGeoPoint(LOCATION_COORDINATES_KEY);
        this.privateAccount = user.getBoolean(PRIVATE_KEY);
    }


    /**
     * Parcelable
     * */
    protected UserObservable(Parcel in) {
        user = in.readParcelable(ParseUser.class.getClassLoader());
        objectId = in.readString();
        username = in.readString();
        password = in.readString();
        name = in.readString();
        profilePhoto = in.readParcelable(ParseFile.class.getClassLoader());
        city = in.readString();
        state = in.readString();
        coordinates = in.readParcelable(ParseGeoPoint.class.getClassLoader());
        privateAccount = in.readBoolean();
    }

    /**
     * Parcelable
     * */
    public static final Creator<UserObservable> CREATOR = new Creator<UserObservable>() {
        @Override
        public UserObservable createFromParcel(Parcel in) {
            return new UserObservable(in);
        }

        @Override
        public UserObservable[] newArray(int size) {
            return new UserObservable[size];
        }
    };

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

    public void setPassword(String password) {
        this.password = password;
        this.user.put(PASSWORD_KEY, password);
        setChanged();
        notifyObservers();
    }

    public String getPassword() {
        return password;
    }

    public void setUsername(String username) {
        this.username = username;
        this.user.put(USERNAME_KEY, username);
        setChanged();
        notifyObservers();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        this.user.put(NAME_KEY, name);
        setChanged();
        notifyObservers();
    }

    public ParseFile getProfilePhoto() {
        return profilePhoto;
    }

    public void setProfilePhoto(ParseFile profilePhoto) {
        this.profilePhoto = profilePhoto;
        this.user.put(PROFILE_PHOTO_KEY, profilePhoto);
        setChanged();
        notifyObservers();
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
        this.user.put(CITY_KEY, city);
        setChanged();
        notifyObservers();
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
        this.user.put(STATE_KEY, state);
        setChanged();
        notifyObservers();
    }

    public ParseGeoPoint getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(ParseGeoPoint coordinates) {
        this.coordinates = coordinates;
        this.user.put(LOCATION_COORDINATES_KEY, coordinates);
        setChanged();
        notifyObservers();
    }

    /**
     * Triggers update function on observer
     * */
    public void triggerObserver() {
        setChanged();
        notifyObservers();
    }

    /**
     * Saves user with said attributes to database
     * */
    public void saveUser(){
        this.user.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null){
                    Log.e(TAG, "Error saving user: " + e);
                }
                else{
                    Log.i(TAG, "User save swas successful!");
                }
            }
        });
    }


    public Boolean getPrivateAccount() {
        return privateAccount;
    }

    public void setPrivateAccount(Boolean privateAccount) {
        this.privateAccount = privateAccount;
        this.user.put(PRIVATE_KEY, privateAccount);
        setChanged();
        notifyObservers();
    }

    public boolean displayContent(UserObservable friend){
        boolean follows = false;
        boolean followed = false;
        ParseQuery<Friends> query1 = ParseQuery.getQuery(Friends.class);
        query1.whereEqualTo(Friends.USER_KEY, user);
        query1.whereEqualTo(Friends.REQUESTED_KEY, friend.getUser());
        try {
            List<Friends> requests = query1.find();
            if (requests.size() > 0) {
                follows = true;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        ParseQuery<Friends> query2 = ParseQuery.getQuery(Friends.class);
        query2.whereEqualTo(Friends.USER_KEY, friend.getUser());
        query2.whereEqualTo(Friends.REQUESTED_KEY, user);
        try {
            List<Friends> requests = query2.find();
            if (requests.size() > 0) {
                followed = true;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (friend.getPrivateAccount()){
            return follows & followed;
        }
        else{
            return true;
        }
    }

    /**
     * Does validation on user object fields
     * */
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

    /**
     * Parcelable
     * */
    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * Parcelable
     * */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(user, flags);
        dest.writeString(objectId);
        dest.writeString(username);
        dest.writeString(password);
        dest.writeString(name);
        dest.writeParcelable(profilePhoto, flags);
        dest.writeString(city);
        dest.writeString(state);
        dest.writeParcelable(coordinates, flags);
        dest.writeBoolean(privateAccount);
    }

}
