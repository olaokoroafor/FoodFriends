package com.example.foodfriends.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.foodfriends.R;
import com.example.foodfriends.activities.MainActivity;
import com.example.foodfriends.adapters.CommentsAdapter;
import com.example.foodfriends.misc.GoogleMapsHelper;
import com.example.foodfriends.models.Comment;
import com.example.foodfriends.models.Restaurant;
import com.example.foodfriends.observable_models.RestaurantObservable;
import com.example.foodfriends.observable_models.UserObservable;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

public class RestaurantDetailFragment extends Fragment implements Observer, View.OnClickListener{
    private static final String TAG = "Restaurant Detail Fragment";
    private TextView tvRName;
    private TextView tvLikeCount;
    private TextView tvToGoCount;
    private TextView tvAddress;
    private TextView tvPrice;
    private ImageView ivRPic;
    private ImageView ivLike;
    private ImageView ivToGo;
    private RestaurantObservable restaurant;
    private UserObservable user;
    private ImageView ivCommentSubmit;
    private EditText etCommentBody;
    private RecyclerView rvComments;
    private List<Comment> comments;
    private CommentsAdapter adapter;

    public RestaurantDetailFragment() {
        // Required empty public constructor
    }

    /**
     * Inflates the UI xml for the fragment, and adds observer to restaurant object
     * */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        restaurant = this.getArguments().getParcelable("restaurant");
        restaurant.addObserver(this);
        user = new UserObservable(ParseUser.getCurrentUser());
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_restaurant_detail, container, false);
    }

    /**
     * Sets the values of the xml elements to restaurant data
     * Also sets the on click listener for necessary objects
     * */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tvRName = view.findViewById(R.id.tvDetailName);
        tvLikeCount = view.findViewById(R.id.tvDetailLikeCount);
        tvToGoCount = view.findViewById(R.id.tvDetailToGoCount);
        tvAddress = view.findViewById(R.id.tvDetailAddress);
        ivRPic = view.findViewById(R.id.ivDetailPic);
        ivLike = view.findViewById(R.id.ivDetailLike);
        ivToGo = view.findViewById(R.id.ivDetailToGo);
        tvPrice = view.findViewById(R.id.tvDetailPrice);
        rvComments = view.findViewById(R.id.rvComments);
        etCommentBody = view.findViewById(R.id.etCommentBody);
        ivCommentSubmit = view.findViewById(R.id.ivCommentSubmit);

        tvPrice.setText("PRICE: " + restaurant.getPrice());
        tvRName.setText(restaurant.getName());
        tvAddress.setText(restaurant.getAddress());
        tvLikeCount.setText(String.valueOf(restaurant.getLikes()));
        tvToGoCount.setText(String.valueOf(restaurant.getTogos()));
        RequestOptions rPicOptions = new RequestOptions();
        rPicOptions = rPicOptions.transforms(new CenterCrop(), new RoundedCorners(15));
        Glide.with(getContext()).applyDefaultRequestOptions(rPicOptions).load(restaurant.getImageUrl()).into(ivRPic);
        if(restaurant.isLiked()){
            Glide.with(getContext())
                    .load(R.drawable.ic_baseline_red_heart_24)
                    .into(ivLike);
        }
        else{
            Glide.with(getContext())
                    .load(R.drawable.ic_baseline_heart_24)
                    .into(ivLike);
        }
        if(restaurant.isGoing()){
            Glide.with(getContext())
                    .load(R.drawable.ic_baseline_active_go_24)
                    .into(ivToGo);
        }
        else{
            Glide.with(getContext())
                    .load(R.drawable.ic_baseline_call_missed_outgoing_24)
                    .into(ivToGo);
        }
        ivLike.setOnClickListener(this);
        ivToGo.setOnClickListener(this);
        tvAddress.setOnClickListener(this);
        comments = new ArrayList<Comment>();
        adapter = new CommentsAdapter(getContext(), comments);

        // recycler view set up: layout manage and the adapter
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        rvComments.setLayoutManager(linearLayoutManager);
        rvComments.setAdapter(adapter);
        ivCommentSubmit.setOnClickListener(this);

        queryComments();
    }

    /**
     * Specifies what needs to be done for each UI element click
     * */
    @Override
    public void onClick(View v) {
        switch(v.getId()){

            case R.id.ivDetailLike:
                restaurant.toggleLike();
                break;
            case R.id.ivDetailToGo:
                restaurant.toggleTogo();
                break;
            case R.id.tvDetailAddress:
                GoogleMapsHelper helper = new GoogleMapsHelper(restaurant, getContext());
                helper.goToGmaps();
                break;
            case R.id.ivCommentSubmit:
                add_comment();
        }

    }

    private void add_comment() {
        Comment new_comment = new Comment();
        new_comment.setText(etCommentBody.getText().toString());
        new_comment.setUser(user.getUser());
        new_comment.setRestaurant(restaurant.getRestaurant());
        new_comment.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null){
                    Log.e(TAG, "Error saving post: " + e);
                    //Toast.makeText(this, "Error while saving", Toast.LENGTH_SHORT).show();
                }
                else{
                    Log.i(TAG, "Post save was successful!");
                    int len = comments.size();
                    comments.add(new_comment);
                    adapter.notifyDataSetChanged();
                    etCommentBody.setText("");
                }
            }
        });
    }

    /**
     * Called when restaurant data is updated, re renders likes/togos/ text views and image views
     * */
    @Override
    public void update(Observable o, Object arg) {
        tvLikeCount.setText(String.valueOf(restaurant.getLikes()));
        tvToGoCount.setText(String.valueOf(restaurant.getTogos()));
        if(restaurant.isLiked()){
            Glide.with(getContext())
                    .load(R.drawable.ic_baseline_red_heart_24)
                    .into(ivLike);
        }
        else{
            Glide.with(getContext())
                    .load(R.drawable.ic_baseline_heart_24)
                    .into(ivLike);
        }
        if(restaurant.isGoing()){
            Glide.with(getContext())
                    .load(R.drawable.ic_baseline_active_go_24)
                    .into(ivToGo);
        }
        else{
            Glide.with(getContext())
                    .load(R.drawable.ic_baseline_call_missed_outgoing_24)
                    .into(ivToGo);
        }
    }

    /**
     * Adds all the restaurant's comments to the list
     * */
    private void queryComments() {
        // specify what type of data we want to query - Post.class
        ParseQuery<Comment> query = ParseQuery.getQuery(Comment.class);

        query.whereEqualTo("restaurant", restaurant.getRestaurant());
        // include data referred by user key
        query.include(Comment.USER_KEY);
        query.include(Comment.RESTAURANT_KEY);
        // order posts by creation date (newest first)
        query.addAscendingOrder("createdAt");
        // start an asynchronous call for posts
        query.findInBackground(new FindCallback<Comment>() {
            @Override
            public void done(List<Comment> db_comments, ParseException e) {
                // check for errors
                if (e != null) {
                    Log.e(TAG, "Issue with getting comments", e);
                    return;
                }

                // for debugging purposes let's print every post description to logcat
                for (Comment comment : db_comments) {
                    Log.i(TAG, "Comment: " + comment.getText() + ", username: " + comment.getUser().getUsername());
                }

                // save received posts to list and notify adapter of new data
                comments.addAll(db_comments);
                adapter.notifyDataSetChanged();
            }
        });
    }
}