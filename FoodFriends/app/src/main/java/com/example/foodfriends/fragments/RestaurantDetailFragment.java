package com.example.foodfriends.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.foodfriends.R;
import com.example.foodfriends.activities.MainActivity;
import com.example.foodfriends.models.Restaurant;
import com.example.foodfriends.observable_models.RestaurantObservable;
import com.parse.ParseUser;

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
    private RecyclerView rvComments;

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

        tvPrice.setText("PRICE: " + restaurant.getPrice());
        tvRName.setText(restaurant.getName());
        tvAddress.setText(restaurant.getAddress());
        tvLikeCount.setText(String.valueOf(restaurant.getLikes()));
        tvToGoCount.setText(String.valueOf(restaurant.getTogos()));
        RequestOptions requestOptions = new RequestOptions();
        requestOptions = requestOptions.transforms(new CenterCrop(), new RoundedCorners(15));
        Glide.with(getContext()).applyDefaultRequestOptions(requestOptions).load(restaurant.getImageUrl()).into(ivRPic);
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
                go_to_gmaps();
                break;
        }

    }

    /**
     * Directs user to google maps with restaurant preplaced in map
     * */
    private void go_to_gmaps() {
        String uri = "http://maps.google.com/maps?daddr=" + String.valueOf(restaurant.getCoordinates().getLatitude()) + "," + String.valueOf(restaurant.getCoordinates().getLongitude()) + " (" + restaurant.getName() + ")";
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        intent.setPackage("com.google.android.apps.maps");
        getActivity().startActivity(intent);
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