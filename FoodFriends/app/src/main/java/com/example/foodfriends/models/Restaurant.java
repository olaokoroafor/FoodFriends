package com.example.foodfriends.models;

import android.database.CursorIndexOutOfBoundsException;
import android.util.Log;

import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
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
    private static final String NAME_KEY = "name";
    private static final String IMAGE_URL_KEY = "imageUrl";
    private static final String CITY_KEY = "city";
    private static final String STATE_KEY = "state";
    private static final String ZIPCODE_KEY = "zipcode";
    private static final String PRICE_KEY = "price";
    private static final String ADDRESS_KEY = "address";
    private static final String YELP_ID_KEY = "yelp_id";
    private static final String LOCATION_COORDINATES_KEY = "location_coordinates";
    private static final String LIKES_KEY = "likes";
    private static final String TOGOS_KEY = "togos";
    private static final String TAG = "Restaurant Model";

    public String getName() {
        return getString(NAME_KEY);
    }

    public void setName(String name) {
        put(NAME_KEY, name);
    }

    public String getImageUrl() {
        return getString(IMAGE_URL_KEY);
    }

    public void setImageUrl(String imageUrl) {
        put(IMAGE_URL_KEY, imageUrl);
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

    public String getZipcode() {
        return getString(ZIPCODE_KEY);
    }

    public void setZipcode(String zipcode) {
        put(ZIPCODE_KEY, zipcode);
    }

    public String getPrice() {
        return getString(PRICE_KEY);
    }

    public void setPrice(String price) {
        put(PRICE_KEY, price);
    }

    public String getAddress() {
        return getString(ADDRESS_KEY);
    }

    public void setAddress(String address) {
        put(ADDRESS_KEY, address);
    }

    public String getYelpID() {
        return getString(YELP_ID_KEY);
    }

    public void setYelpId(String yelpID) {
        put(YELP_ID_KEY, yelpID);
    }

    public ParseGeoPoint getCoordinates() {
        return getParseGeoPoint(LOCATION_COORDINATES_KEY);
    }

    public void setCoordinates(ParseGeoPoint coordinates) {
        put(LOCATION_COORDINATES_KEY, coordinates);
    }

    /**
     * Returns the number of likes for this particular restaurant
     * */
    public int getLikes() {return getInt(LIKES_KEY);}

    /**
     * Sets the number of likes for this particular restaurant
     * */
    public void setLikes(int likes) {
        put(LIKES_KEY,likes);

    }

    /**
     * Adds a like entry for the logged in user and restaurant into Userlike table
     * */
    public void incrementLikes(int likes) {
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
        setLikes(likes+1);
    }

    /**
     * Removes a like entry for the logged in user and restaurant into Userlike table
     * */
    public void decrementLikes(int likes) {
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
        setLikes(likes-1);
    }

    /**
     * Checks to see if logged in user likes restaurant
     * */
    public boolean userLike() {
        boolean liked = false;
        ParseQuery<UserLike> query = ParseQuery.getQuery(UserLike.class);
        query.whereEqualTo("restaurant", this);
        query.whereEqualTo("user", ParseUser.getCurrentUser());
        try {
            List<UserLike> likes = query.find();
            if (likes.size() > 0) {
                liked = true;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return liked;
    }

    /**
     * Returns the number of togos for this particular restaurant
     * */
    public int getToGos() {return getInt(TOGOS_KEY);}

    /**
     * Sets the number of togos for this particular restaurant
     * */
    public void setToGos(int togos) {
        put(TOGOS_KEY, togos);

    }

    /**
     * Adds a togo entry for the logged in user and restaurant into UserTogo table
     * */
    public void incrementToGos(int togos) {
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
        setToGos(togos+1);
    }

    /**
     * Removes a togo entry for the logged in user and restaurant into UserTogo table
     * */
    public void decrementToGos(int togos) {
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
        setToGos(togos-1);
    }

    /**
     * Checks to see if logged in user wants to go to this restaurant
     * */
    public boolean userToGo() {
        boolean going = false;
        ParseQuery<UserToGo> query = ParseQuery.getQuery(UserToGo.class);
        query.whereEqualTo("restaurant", this);
        query.whereEqualTo("user", ParseUser.getCurrentUser());
        try {
            List<UserToGo> togos = query.find();
            if (togos.size() > 0) {
                going = true;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return going;
    }

}
