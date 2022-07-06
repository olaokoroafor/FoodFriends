package com.example.foodfriends.models;

import android.util.Log;

import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.List;

@ParseClassName("Friends")
public class Friends extends ParseObject {
    public static final String USER_KEY = "user";
    public static final String REQUESTED_KEY = "requested";
    private static final String TAG = "FRIENDS MODEL";

    public ParseUser getUser(){
        return getParseUser(USER_KEY);
    }
    public void setUser(ParseUser user){
        put(USER_KEY, user);
    }

    public ParseUser getRequested(){
        return getParseUser(REQUESTED_KEY);
    }
    public void setRequested(ParseUser user){
        put(REQUESTED_KEY, user);
    }

    /**
     * Checks to see if user passed in is followed by user logged in
     * */
    public static boolean user_follows(ParseUser requested) {
        ParseUser user = ParseUser.getCurrentUser();
        boolean follows = false;
        ParseQuery<Friends> query = ParseQuery.getQuery(Friends.class);
        query.whereEqualTo(Friends.USER_KEY, user);
        query.whereEqualTo(Friends.REQUESTED_KEY, requested);
        try {
            List<Friends> requests = query.find();
            if (requests.size() > 0) {
                follows = true;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return follows;
    }

    /**
     * Adds a following entry into Friends table for logged in user and requested
     * */
    public static void follow(ParseUser requested) {
        Friends friend = new Friends();
        friend.setUser(ParseUser.getCurrentUser());
        friend.setRequested(requested);
        friend.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null)
                    Log.i(TAG, e.toString());
            }
        });
    }

    /**
     * Removes a following entry into Friends table for logged in user and requested
     * */
    public static void unfollow(ParseUser requested) {
        ParseQuery<Friends> query = ParseQuery.getQuery(Friends.class);
        query.whereEqualTo(USER_KEY, ParseUser.getCurrentUser());
        query.whereEqualTo(REQUESTED_KEY, requested);
        query.findInBackground(new FindCallback<Friends>() {
            public void done(List<Friends> friends, ParseException e) {
                ParseObject.deleteAllInBackground(friends, new DeleteCallback() {
                    @Override
                    public void done(ParseException e) {
                        Log.d(TAG, "deleted successfully");
                    }
                });
            }
        });
    }
}
