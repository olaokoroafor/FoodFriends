package com.example.foodfriends.observable_models;

import android.os.Parcel;
import android.os.Parcelable;

import com.example.foodfriends.models.Restaurant;
import com.parse.ParseException;
import com.parse.ParseQuery;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.zip.CheckedInputStream;

public class RestaurantObservable extends Observable implements Parcelable{
    private Restaurant restaurant;
    private String objectId;
    private String yelp_id;
    private String name;
    private String imageUrl;
    private String price;
    private String zipcode;
    private String city;
    private String state;
    private String address;
    private Double latitude;
    private Double longitude;
    private int likes;
    private int togos;
    private boolean liked;
    private boolean going;
    private static final String TAG = "RESTAURANT OBSERVABLE MODEL";


    public RestaurantObservable(){
        this.restaurant = new Restaurant();
        this.objectId = restaurant.getObjectId();
    }

    /**
     * Turns a restaurant object into a restaurantobservable object
     * */
    public RestaurantObservable(Restaurant restaurant){
        this.restaurant = restaurant;
        this.objectId = restaurant.getObjectId();
        this.yelp_id = restaurant.getYelpID();
        this.name = restaurant.getName();
        this.imageUrl = restaurant.getImageUrl();
        this.price = restaurant.getPrice();
        this.zipcode = restaurant.getZipcode();
        this.city = restaurant.getCity();
        this.state = restaurant.getState();
        this.address = restaurant.getAddress();
        this.latitude = restaurant.getLatitude();
        this.longitude = restaurant.getLongitude();
        this.likes = restaurant.getLikes();
        this.togos = restaurant.getToGos();
        this.liked = restaurant.user_like();
        this.going = restaurant.user_to_go();
    }

    /**
     * Parcelable
     * */
    protected RestaurantObservable(Parcel in) {
        restaurant = in.readParcelable(Restaurant.class.getClassLoader());
        objectId = in.readString();
        yelp_id = in.readString();
        name = in.readString();
        imageUrl = in.readString();
        price = in.readString();
        zipcode = in.readString();
        city = in.readString();
        state = in.readString();
        address = in.readString();
        if (in.readByte() == 0) {
            latitude = null;
        } else {
            latitude = in.readDouble();
        }
        if (in.readByte() == 0) {
            longitude = null;
        } else {
            longitude = in.readDouble();
        }
        likes = in.readInt();
        togos = in.readInt();
        liked = in.readByte() != 0;
        going = in.readByte() != 0;
    }

    /**
     * Parcelable
     * */
    public static final Creator<RestaurantObservable> CREATOR = new Creator<RestaurantObservable>() {
        @Override
        public RestaurantObservable createFromParcel(Parcel in) {
            return new RestaurantObservable(in);
        }

        @Override
        public RestaurantObservable[] newArray(int size) {
            return new RestaurantObservable[size];
        }
    };

    public Restaurant getRestaurant() {
        return restaurant;
    }

    public void setRestaurant(Restaurant restaurant) {
        this.restaurant = restaurant;
    }

    public String getObjectId() {
        return objectId;
    }

    public String getYelpId() {
        return yelp_id;
    }

    public void setYelpId(String yelp_id) {
        this.yelp_id = yelp_id;
        this.restaurant.setYelpId(yelp_id);
        setChanged();
        notifyObservers();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        this.restaurant.setName(name);
        setChanged();
        notifyObservers();
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
        this.restaurant.setImageUrl(imageUrl);
        setChanged();
        notifyObservers();
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
        this.restaurant.setPrice(price);
        setChanged();
        notifyObservers();
    }

    public String getZipcode() {
        return zipcode;
    }

    public void setZipcode(String zipcode) {
        this.zipcode = zipcode;
        this.restaurant.setZipcode(zipcode);
        setChanged();
        notifyObservers();
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
        this.restaurant.setCity(city);
        setChanged();
        notifyObservers();
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
        this.restaurant.setState(state);
        setChanged();
        notifyObservers();
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
        this.restaurant.setAddress(address);
        setChanged();
        notifyObservers();
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
        this.restaurant.setLatitude(latitude);
        setChanged();
        notifyObservers();
    }

    public Double getLongitude() {
        return longitude;
    }

    public void save_restaurant(){
        this.restaurant.saveInBackground();
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
        this.restaurant.setLongitude(longitude);
        setChanged();
        notifyObservers();
    }

    public int getLikes() {
        return likes;
    }

    public int getTogos() {
        return togos;
    }

    public boolean isLiked() {
        return liked;
    }

    public boolean isGoing() {
        return going;
    }


    /**
     * Either likes or unlikes restuarant depending on if user already liked the restaurant
     * */
    public void toggleLike(){
        if (isLiked()){
            restaurant.decrementLikes();
            likes -= 1;
            liked = false;
        }
        else{
            restaurant.incrementLikes();
            likes += 1;
            liked = true;
        }
        setChanged();
        notifyObservers();
    }

    /**
     * Either togos or un togos restuarant depending on if user already liked the restaurant
     * */
    public void toggleTogo(){
        if (isGoing()){
            restaurant.decrementToGos();
            togos -= 1;
            going = false;
        }
        else{
            restaurant.incrementToGos();
            togos += 1;
            going = true;
        }
        setChanged();
        notifyObservers();
    }

    /**
     * Parses json object into a restaurant observable object
     * */
    public static RestaurantObservable fromJson(JSONObject jsonObject) throws JSONException {

        try {
            String yelp_id = jsonObject.getString("id");
            ParseQuery<Restaurant> query = ParseQuery.getQuery(Restaurant.class);
            query.whereEqualTo("yelp_id", yelp_id);
            if(query.find().size() > 0)
                return null;
            RestaurantObservable r = new RestaurantObservable();
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
            r.setPrice(jsonObject.getString("price"));
            String address = "";
            JSONArray display = location.getJSONArray("display_address");
            for (int i = 0; i < display.length(); i++) {
                address += display.get(i);
                if (i != display.length() - 1)
                    address += ", ";
            }
            r.setAddress(address);
            r.save_restaurant();
            return r;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Parses json array into a list of restaurant observable objects
     * */
    public static List<RestaurantObservable> fromJsonArray(JSONArray jsonArray) throws JSONException {
        List<RestaurantObservable> restaurants = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            RestaurantObservable restaurant = fromJson(jsonArray.getJSONObject(i));
            if(restaurant != null)
                restaurants.add(restaurant);
        }
        return restaurants;

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
        dest.writeParcelable(restaurant, flags);
        dest.writeString(objectId);
        dest.writeString(yelp_id);
        dest.writeString(name);
        dest.writeString(imageUrl);
        dest.writeString(price);
        dest.writeString(zipcode);
        dest.writeString(city);
        dest.writeString(state);
        dest.writeString(address);
        if (latitude == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeDouble(latitude);
        }
        if (longitude == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeDouble(longitude);
        }
        dest.writeInt(likes);
        dest.writeInt(togos);
        dest.writeByte((byte) (liked ? 1 : 0));
        dest.writeByte((byte) (going ? 1 : 0));
    }
}
