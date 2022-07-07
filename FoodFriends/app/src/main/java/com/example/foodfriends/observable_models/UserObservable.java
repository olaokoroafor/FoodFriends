package com.example.foodfriends.observable_models;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.Log;

import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

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
    private Double latitude;
    private Double longitude;
    private static final String USERNAME_KEY = "username";
    private static final String NAME_KEY = "name";
    private static final String PROFILE_PHOTO_KEY = "profilePhoto";
    private static final String CITY_KEY = "city";
    private static final String STATE_KEY = "state";
    private static final String LATITUDE_KEY = "latitude";
    private static final String LONGITUDE_KEY = "longitude";
    private static final String TAG = "USER OBSERVABLE MODEL";

    public UserObservable(){
        this.user = new ParseUser();
        this.objectId = user.getObjectId();
    }

    public UserObservable(ParseUser user){
        this.user = user;
        this.objectId = user.getObjectId();
        this.username = user.getString(USERNAME_KEY);
        this.name = user.getString(NAME_KEY);
        this.profilePhoto = user.getParseFile(PROFILE_PHOTO_KEY);
        this.city = user.getString(CITY_KEY);
        this.state = user.getString(STATE_KEY);
        this.latitude = user.getDouble(LATITUDE_KEY);
        this.longitude = user.getDouble(LONGITUDE_KEY);
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
    public void save_user(){
        this.user.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null){
                    Log.e(TAG, "Error saving photo: " + e);
                }
                else{
                    Log.i(TAG, "Photo upload was successful!");
                }
            }
        });
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
    }
}
