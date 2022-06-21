package com.example.foodfriends.models;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

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


}
