package com.example.foodfriends.models;

import android.util.Log;

import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONArray;

import java.util.List;

@ParseClassName("Restaurant")
public class Restaurant extends ParseObject {
    public static final String NAME_KEY = "name";
    public static final String IMAGE_URL_KEY = "imageUrl";
    public static final String CITY_KEY = "city";
    public static final String STATE_KEY = "state";
    public static final String ZIPCODE_KEY = "zipcode";
    public static final String LATITUDE_KEY = "latitude";
    public static final String LONGITUDE_KEY = "longitude";
    public static final String PRICE_KEY = "price";
    public static final String ADDRESS_KEY = "address";
    private static final String TAG = "Restaurant Model";

    public String getName(){
        return getString(NAME_KEY);
    }
    public void setName(String name){
        put(NAME_KEY, name);
    }

    public String getImageUrl(){
        return getString(IMAGE_URL_KEY);
    }
    public void setImageUrl(String imageUrl){
        put(IMAGE_URL_KEY, imageUrl);
    }

    public String getCity(){return getString(CITY_KEY);}
    public void setCity(String city){
        put(CITY_KEY, city);
    }

    public String getState(){
        return getString(STATE_KEY);
    }
    public void setState(String name){
        put(STATE_KEY, name);
    }

    public String getZipcode(){
        return getString(ZIPCODE_KEY);
    }
    public void setZipcode(String zipcode){
        put(ZIPCODE_KEY, zipcode);
    }

    public Double getLatitude(){
        return getDouble(LATITUDE_KEY);
    }
    public void setLatitude(Double latitude){
        put(LATITUDE_KEY, latitude);
    }

    public Double getLongitude(){
        return getDouble(LONGITUDE_KEY);
    }
    public void setLongitude(Double longitude){
        put(LONGITUDE_KEY, longitude);
    }

    public String getPrice(){return getString(PRICE_KEY);}
    public void setPrice(String price){
        put(PRICE_KEY, price);
    }

    public String getAddress(){return getString(ADDRESS_KEY);}
    public void setAddress(String address){
        put(ADDRESS_KEY, address);
    }

    public int getLikes(){
        final int[] likeCount = {0};
        // specify what type of data we want to query - Post.class
        ParseQuery<UserLike> query = ParseQuery.getQuery(UserLike.class);
        // start an asynchronous call for posts
        query.whereEqualTo("restaurant", getObjectId());
        query.findInBackground(new FindCallback<UserLike>() {
            @Override
            public void done(List<UserLike> likes, ParseException e) {
                // check for errors
                if (e != null) {
                    return;
                }
                likeCount[0] = likes.size();

            }
        });
        return likeCount[0];
    }

    public void incrementLikes(){
        UserLike like = new UserLike();
        like.setRestaurant(this);
        like.setUser(ParseUser.getCurrentUser());
        like.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null)
                    Log.i(TAG, e.toString());
            }
        });
    }

    public void decrementLikes(){
        ParseQuery<UserLike> query = ParseQuery.getQuery(UserLike.class);
        query.whereEqualTo("restaurant", this);
        query.whereEqualTo("user", ParseUser.getCurrentUser());
        query.findInBackground(new FindCallback<UserLike>() {
            public void done(List<UserLike> likes, ParseException e) {
                ParseObject.deleteAllInBackground(likes, new DeleteCallback() {
                    @Override
                    public void done(ParseException e) {
                        Log.d(TAG, "deleted successfully");
                    }
                });
            }
        });
    }

    public boolean user_like(){
        final boolean[] liked = {false};
        ParseQuery<UserLike> query = ParseQuery.getQuery(UserLike.class);
        query.whereEqualTo("restaurant", this);
        query.whereEqualTo("user", ParseUser.getCurrentUser());
        query.findInBackground(new FindCallback<UserLike>() {
            public void done(List<UserLike> likes, ParseException e) {
                Log.i(TAG, String.valueOf(likes.size()));
                if(likes.size() > 0){
                    liked[0] =true;
                }
            }
        });
        return liked[0];
    }

    public int getToGos(){
        final int[] toGoCount= {0};
        // specify what type of data we want to query - Post.class
        ParseQuery<UserToGo> query = ParseQuery.getQuery(UserToGo.class);
        // start an asynchronous call for posts
        query.whereEqualTo("restaurant", getObjectId());
        query.findInBackground(new FindCallback<UserToGo>() {
            @Override
            public void done(List<UserToGo> togos, ParseException e) {
                // check for errors
                if (e != null) {
                    Log.i(TAG, e.toString());
                    return;
                }
                toGoCount[0] = togos.size();

            }
        });
        return toGoCount[0];
    }

    public void incrementToGos(){
        UserToGo togo = new UserToGo();
        togo.setRestaurant(this);
        togo.setUser(ParseUser.getCurrentUser());
        togo.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null)
                    Log.i(TAG, e.toString());
            }
        });
    }

    public void decrementToGos(){
        ParseQuery<UserToGo> query = ParseQuery.getQuery(UserToGo.class);
        query.whereEqualTo("restaurant", this);
        query.whereEqualTo("user", ParseUser.getCurrentUser());
        query.findInBackground(new FindCallback<UserToGo>() {
            public void done(List<UserToGo> togos, ParseException e) {
                ParseObject.deleteAllInBackground(togos, new DeleteCallback() {
                    @Override
                    public void done(ParseException e) {
                        Log.d(TAG, "deleted successfully");
                    }
                });
            }
        });
    }

    public boolean user_to_go(){
        final boolean[] going = {false};
        ParseQuery<UserToGo> query = ParseQuery.getQuery(UserToGo.class);
        query.whereEqualTo("restaurant", this);
        query.whereEqualTo("user", ParseUser.getCurrentUser());
        query.findInBackground(new FindCallback<UserToGo>() {
            public void done(List<UserToGo> togos, ParseException e) {
                Log.i(TAG, String.valueOf(togos.size()));
                if(togos.size() > 0){
                    going[0] =true;
                }
            }
        });
        return going[0];
    }

}
