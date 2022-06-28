package com.example.foodfriends.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

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
import com.example.foodfriends.models.Restaurant;
import com.parse.ParseUser;

public class RestaurantDetailFragment extends Fragment {
    private TextView tvRName;
    private TextView tvLikeCount;
    private TextView tvToGoCount;
    private TextView tvAddress;
    private TextView tvPrice;
    private ImageView ivRPic;
    private ImageView ivLike;
    private ImageView ivToGo;
    private Restaurant restaurant;
    private RecyclerView rvComments;
    private boolean liked;
    private boolean going;
    int num_likes;
    int num_togo;

    public RestaurantDetailFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        restaurant = this.getArguments().getParcelable("restaurant");
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_restaurant_detail, container, false);
    }

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
        num_likes =  restaurant.getLikes();
        tvLikeCount.setText(String.valueOf(num_likes));
        num_togo = restaurant.getToGos();
        tvToGoCount.setText(String.valueOf(num_togo));
        RequestOptions requestOptions = new RequestOptions();
        requestOptions = requestOptions.transforms(new CenterCrop(), new RoundedCorners(15));
        Glide.with(getContext()).applyDefaultRequestOptions(requestOptions).load(restaurant.getImageUrl()).into(ivRPic);
        liked = restaurant.user_like();
        if(liked){
            Glide.with(getContext())
                    .load(R.drawable.ic_baseline_red_heart_24)
                    .into(ivLike);
        }
        else{
            Glide.with(getContext())
                    .load(R.drawable.ic_baseline_heart_24)
                    .into(ivLike);
        }


        going = restaurant.user_to_go();
        if(going){
            Glide.with(getContext())
                    .load(R.drawable.ic_baseline_active_go_24)
                    .into(ivToGo);
        }

        else{
            Glide.with(getContext())
                    .load(R.drawable.ic_baseline_call_missed_outgoing_24)
                    .into(ivToGo);
        }

        ivLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!liked){
                    restaurant.incrementLikes();
                    tvLikeCount.setText(new Integer(num_likes+1).toString());
                    liked = true;
                    num_likes += 1;
                    Glide.with(getContext())
                            .load(R.drawable.ic_baseline_red_heart_24)
                            .into(ivLike);
                }

                else{
                    restaurant.decrementLikes();
                    tvLikeCount.setText(new Integer(num_likes-1).toString());
                    liked = false;
                    num_likes -= 1;
                    Glide.with(getContext())
                            .load(R.drawable.ic_baseline_heart_24)
                            .into(ivLike);
                }
            }
        });

        ivToGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!going){
                    restaurant.incrementToGos();
                    tvToGoCount.setText(new Integer(num_togo+1).toString());
                    going = true;
                    num_togo += 1;
                    Glide.with(getContext())
                            .load(R.drawable.ic_baseline_active_go_24)
                            .into(ivToGo);
                }

                else{
                    restaurant.decrementToGos();
                    tvToGoCount.setText(new Integer(num_togo-1).toString());
                    going = false;
                    num_togo -=1;
                    Glide.with(getContext())
                            .load(R.drawable.ic_baseline_call_missed_outgoing_24)
                            .into(ivToGo);
                }
            }
        });

        tvAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseUser user = ParseUser.getCurrentUser();
                String uri = "http://maps.google.com/maps?daddr=" + restaurant.getLatitude().toString() + "," + restaurant.getLongitude().toString() + " (" + restaurant.getName() + ")";
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                intent.setPackage("com.google.android.apps.maps");
                getContext().startActivity(intent);
            }
        });


    }
}