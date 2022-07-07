package com.example.foodfriends.observable_models;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.Log;

import com.example.foodfriends.models.Friends;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.Observable;

public class FriendObservable extends Observable{
    private ParseUser user;
    private String objectId;
    private String username;
    private String name;
    private ParseFile profilePhoto;
    private String city;
    private String state;
    private boolean follows;
    private static final String USERNAME_KEY = "username";
    private static final String NAME_KEY = "name";
    private static final String PROFILE_PHOTO_KEY = "profilePhoto";
    private static final String CITY_KEY = "city";
    private static final String STATE_KEY = "state";
    private static final String TAG = "USER OBSERVABLE MODEL";

    public FriendObservable() {
        this.user = new ParseUser();
        this.objectId = user.getObjectId();
    }

    public FriendObservable(ParseUser user) {
        this.user = user;
        this.objectId = user.getObjectId();
        this.username = user.getString(USERNAME_KEY);
        this.name = user.getString(NAME_KEY);
        this.profilePhoto = user.getParseFile(PROFILE_PHOTO_KEY);
        this.city = user.getString(CITY_KEY);
        this.state = user.getString(STATE_KEY);
        this.follows = Friends.user_follows(user);
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


    public String getName() {
        return name;
    }


    public ParseFile getProfilePhoto() {
        return profilePhoto;
    }

    public String getCity() {
        return city;
    }


    public String getState() {
        return state;
    }

    public void triggerObserver() {
        setChanged();
        notifyObservers();
    }

    /**
     * Checks to see if logged in user follows this friend
     * */
    public boolean user_follows() {
        return follows;
    }

    /**
     * Makes logged in user follow this friend
     * */
    public void toggle_follow() {
        if (this.follows) {
            Friends.follow(this.user);
            follows = false;
        } else {
            Friends.unfollow(this.user);
            follows = true;
        }
        setChanged();
        notifyObservers();

    }

}



