package com.example.foodfriends;

import android.app.Application;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

import com.example.foodfriends.models.Friends;
import com.example.foodfriends.models.Restaurant;
import com.example.foodfriends.models.UserLike;
import com.example.foodfriends.models.UserToGo;
import com.parse.Parse;
import com.parse.ParseObject;

public class ParseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // Use for troubleshooting -- remove this line for production
        Parse.setLogLevel(Parse.LOG_LEVEL_DEBUG);
        ParseObject.registerSubclass(Restaurant.class);
        ParseObject.registerSubclass(UserLike.class);
        ParseObject.registerSubclass(UserToGo.class);
        //ParseObject.registerSubclass(User.class);
        ParseObject.registerSubclass(Friends.class);

        // Use for monitoring Parse OkHttp traffic
        // Can be Level.BASIC, Level.HEADERS, or Level.BODY
        // See https://square.github.io/okhttp/3.x/logging-interceptor/ to see the options.
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        builder.networkInterceptors().add(httpLoggingInterceptor);

        // set applicationId, and server server based on the values in the back4app settings.
        // any network interceptors must be added with the Configuration Builder given this syntax
        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId(getString(R.string.back4app_app_id))
                .clientKey(getString(R.string.back4app_client_key))
                .server(getString(R.string.back4app_server_url))
                .build());
    }
}