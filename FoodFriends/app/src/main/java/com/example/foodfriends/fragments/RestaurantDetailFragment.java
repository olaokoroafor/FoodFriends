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
import com.example.foodfriends.misc.CommentFetchHelper;
import com.example.foodfriends.misc.CommentListener;
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
    private CommentFetchHelper commentHelper;

    public RestaurantDetailFragment() {
        // Required empty public constructor
    }

    /**
     * Inflates the UI xml for the fragment, and adds observer to restaurant object
     * */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_restaurant_detail, container, false);
        restaurant = this.getArguments().getParcelable("restaurant");
        restaurant.addObserver(this);
        user = new UserObservable(ParseUser.getCurrentUser());
        comments = new ArrayList<Comment>();
        commentHelper = new CommentFetchHelper(comments);
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
        // Inflate the layout for this fragment
        return view;
    }

    /**
     * Sets the values of the xml elements to restaurant data
     * Also sets the on click listener for necessary objects
     * */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tvPrice.setText("PRICE: " + restaurant.getPrice());
        tvRName.setText(restaurant.getName());
        tvAddress.setText(restaurant.getAddress());
        tvLikeCount.setText(String.valueOf(restaurant.getLikes()));
        tvToGoCount.setText(String.valueOf(restaurant.getTogos()));
        displayResPic(ivRPic);
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
        adapter = new CommentsAdapter(getContext(), comments);

        // recycler view set up: layout manage and the adapter
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        rvComments.setLayoutManager(linearLayoutManager);
        rvComments.setAdapter(adapter);
        ivCommentSubmit.setOnClickListener(this);

        commentHelper.fetchComments(restaurant, new CommentListener(){
            @Override
            public void dataChanged() {
                adapter.notifyDataSetChanged();
            }
        });
    }

    private void displayResPic(ImageView ivRPic) {
        RequestOptions picOptions = new RequestOptions();
        picOptions = picOptions.transforms(new CenterCrop(), new RoundedCorners(15));
        Glide.with(getContext()).applyDefaultRequestOptions(picOptions).load(restaurant.getImageUrl()).into(ivRPic);
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
                commentHelper.addComment(etCommentBody.getText().toString(), user, restaurant, new CommentListener(){
                    @Override
                    public void dataChanged() {
                        adapter.notifyDataSetChanged();
                        etCommentBody.setText("");
                    }
                });
        }

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
}