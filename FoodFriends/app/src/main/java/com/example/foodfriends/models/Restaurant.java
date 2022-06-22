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
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
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
    public static final String YELP_ID_KEY = "yelp_id";
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

    public String getYelpID(){return getString(YELP_ID_KEY);}
    public void setYelpId(String yelpID){put(YELP_ID_KEY, yelpID);}

    public static Restaurant fromJson(JSONObject jsonObject) throws JSONException {

        try{
            String yelp_id = jsonObject.getString("id");
            ParseQuery<Restaurant> query = ParseQuery.getQuery(Restaurant.class);
            query.whereEqualTo(YELP_ID_KEY, yelp_id);
            if (query.find().size() > 0)
                return null;
            Restaurant r = new Restaurant();
            r.setYelpId(jsonObject.getString("id"));
            r.setName(jsonObject.getString("name"));
            r.setImageUrl(jsonObject.getString("image_url"));
            JSONObject location = jsonObject.getJSONObject("location");
            r.setCity(location.getString("city"));
            r.setState(location.getString("state"));
            r.setZipcode(location.getString("zip_code"));
            JSONObject coordinates = jsonObject.getJSONObject("coordinates");
            r.setLatitude(coordinates.getDouble("latitude"));
            r.setLongitude(coordinates.getDouble("longitude"));
            r.setPrice("price");
            String address = "";
            JSONArray display =  location.getJSONArray("display_address");
            for(int i = 0; i < display.length(); i++){
                address += display.get(i);
                if (i != display.length()-1)
                    address += ", ";
            }
            r.setAddress(address);
            r.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null)
                        Log.i(TAG, "RESTAURANT ADDED DONE");
                    else
                        Log.e(TAG, e.toString());
                }
            });
            return r;
        }
        catch (Exception e){
            return null;
        }
    }

    public static List<Restaurant> fromJsonArray(JSONArray jsonArray) throws JSONException {
        List<Restaurant> restaurants = new ArrayList<>();
        for(int i = 0; i < jsonArray.length(); i++){
            Restaurant restaurant = fromJson(jsonArray.getJSONObject(i));
            if(restaurant != null)
                restaurants.add(fromJson(jsonArray.getJSONObject(i)));
        }
        return restaurants;

    }

    public int getLikes(){
        int likesCount= 0;
        // specify what type of data we want to query - Post.class
        ParseQuery<UserLike> query = ParseQuery.getQuery(UserLike.class);
        // start an asynchronous call for posts
        query.whereEqualTo("restaurant", this);
        try {
            List<UserLike> likes = query.find();
            likesCount = likes.size();
            Log.i(TAG, "List size: " + likesCount);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Log.i(TAG, "likes Count" + likesCount);
        return likesCount;
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
        boolean liked = false;
        ParseQuery<UserLike> query = ParseQuery.getQuery(UserLike.class);
        query.whereEqualTo("restaurant", this);
        query.whereEqualTo("user", ParseUser.getCurrentUser());
        try {
            List<UserLike> likes = query.find();
            if (likes.size() > 0){
                liked = true;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return liked;
    }

    public int getToGos(){
        int toGoCount= 0;
        // specify what type of data we want to query - Post.class
        ParseQuery<UserToGo> query = ParseQuery.getQuery(UserToGo.class);
        // start an asynchronous call for posts
        query.whereEqualTo("restaurant", this);
        try {
            List<UserToGo> togos = query.find();
            toGoCount = togos.size();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return toGoCount;
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
        boolean going = false;
        ParseQuery<UserToGo> query = ParseQuery.getQuery(UserToGo.class);
        query.whereEqualTo("restaurant", this);
        query.whereEqualTo("user", ParseUser.getCurrentUser());
        try {
            List<UserToGo> togos = query.find();
            if (togos.size() > 0){
                going = true;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return going;
    }

}
