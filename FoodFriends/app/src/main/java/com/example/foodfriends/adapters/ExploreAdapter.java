package com.example.foodfriends.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.foodfriends.R;
import com.example.foodfriends.activities.MainActivity;
import com.example.foodfriends.fragments.RestaurantDetailFragment;
import com.example.foodfriends.misc.GoogleMapsHelper;
import com.example.foodfriends.models.Restaurant;
import com.example.foodfriends.observable_models.RestaurantObservable;
import com.parse.ParseFile;
import com.parse.ParseUser;

import java.util.List;
import java.util.Locale;
import java.util.Observable;
import java.util.Observer;

public class ExploreAdapter extends RecyclerView.Adapter <ExploreAdapter.ViewHolder>{
    private Context context;
    private List<RestaurantObservable> restaurants;
    Animation pulse;

    public ExploreAdapter(Context context, List<RestaurantObservable> restaurants) {
        this.context = context;
        this.restaurants = restaurants;
    }

    @NonNull
    @Override

    /**
     * Inflates view of the item restaurant xml**/
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_restaurant, parent, false);

        return new ViewHolder(view);
    }

    /**
     * Calls binder for a restaurant at particular position**/
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        RestaurantObservable restaurant = restaurants.get(position);
        holder.bind(restaurant);

    }

    /**
     * Returns length of restaurant list**/
    @Override
    public int getItemCount() {
        return restaurants.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements Observer, View.OnClickListener{


        private TextView tvRName;
        private TextView tvLikeCount;
        private TextView tvToGoCount;
        private TextView tvAddress;
        private ImageView ivRPic;
        private ImageView ivLike;
        private ImageView ivToGo;
        private RestaurantObservable restaurantObservable;


        /**
         * Constructor
         * */
        public ViewHolder(@NonNull View itemView){
            super(itemView);
            tvRName = itemView.findViewById(R.id.tvExploreName);
            tvLikeCount = itemView.findViewById(R.id.tvExploreLikeCount);
            tvToGoCount = itemView.findViewById(R.id.tvExploreToGoCount);
            tvAddress = itemView.findViewById(R.id.tvExploreAddress);
            ivRPic = itemView.findViewById(R.id.ivExplorePic);
            ivLike = itemView.findViewById(R.id.ivExploreLike);
            ivToGo = itemView.findViewById(R.id.ivExploreToGo);
            pulse = AnimationUtils.loadAnimation(context, R.anim.pulse);
        }

        /**
         * Binds the xml elements to restaurant data
         * */
        public void bind(RestaurantObservable restaurant) {
            restaurantObservable = restaurant;
            restaurant.addObserver(this);
            tvRName.setText(restaurant.getName());
            tvRName.setOnTouchListener(new View.OnTouchListener() {
                private GestureDetector gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                    @Override
                    public boolean onDoubleTap(MotionEvent e) {
                        Log.d("TEST", "onDoubleTap");
                        restaurantObservable.toggleLike();
                        ivLike.startAnimation(pulse);
                        return super.onDoubleTap(e);
                    }
                });

                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    Log.d("TEST", "Raw event: " + event.getAction() + ", (" + event.getRawX() + ", " + event.getRawY() + ")");
                    gestureDetector.onTouchEvent(event);
                    return true;
                }
            });
            tvAddress.setText(restaurant.getAddress());
            tvLikeCount.setText(String.valueOf(restaurant.getLikes()));
            tvToGoCount.setText(String.valueOf(restaurant.getTogos()));
            ivLike.setOnClickListener(this);
            ivToGo.setOnClickListener(this);
            tvAddress.setOnClickListener(this);
            ivRPic.setOnClickListener(this);
            RequestOptions requestOptions = new RequestOptions();
            requestOptions = requestOptions.transforms(new CenterCrop(), new RoundedCorners(15));
            Glide.with(context).applyDefaultRequestOptions(requestOptions).load(restaurant.getImageUrl()).into(ivRPic);
            if(restaurant.isLiked()){
                Glide.with(context)
                        .load(R.drawable.ic_baseline_red_heart_24)
                        .into(ivLike);
            }
            else{
                Glide.with(context)
                        .load(R.drawable.ic_baseline_heart_24)
                        .into(ivLike);
            }
            if(restaurant.isGoing()){
                Glide.with(context)
                        .load(R.drawable.ic_baseline_active_go_24)
                        .into(ivToGo);
            }
            else{
                Glide.with(context)
                        .load(R.drawable.ic_baseline_call_missed_outgoing_24)
                        .into(ivToGo);
            }
        }


        /**
         * Called when restaurant data is updated, re renders likes/togos/ text views and image views
         * */
        @Override
        public void update(Observable o, Object arg) {
            tvLikeCount.setText(String.valueOf(restaurantObservable.getLikes()));
            tvToGoCount.setText(String.valueOf(restaurantObservable.getTogos()));
            if(restaurantObservable.isLiked()){
                Glide.with(context)
                        .load(R.drawable.ic_baseline_red_heart_24)
                        .into(ivLike);
            }
            else{
                Glide.with(context)
                        .load(R.drawable.ic_baseline_heart_24)
                        .into(ivLike);
            }
            if(restaurantObservable.isGoing()){
                Glide.with(context)
                        .load(R.drawable.ic_baseline_active_go_24)
                        .into(ivToGo);
            }
            else{
                Glide.with(context)
                        .load(R.drawable.ic_baseline_call_missed_outgoing_24)
                        .into(ivToGo);
            }
        }

        /**
         * Specifies what needs to be done for each UI element click
         * */
        @Override
        public void onClick(View v) {
            switch(v.getId()){

                case R.id.ivExploreLike:
                    restaurantObservable.toggleLike();
                    ivLike.startAnimation(pulse);
                    break;
                case R.id.ivExploreToGo:
                    restaurantObservable.toggleTogo();
                    ivToGo.startAnimation(pulse);
                    break;
                case R.id.tvExploreAddress:
                    GoogleMapsHelper helper = new GoogleMapsHelper(restaurantObservable, context);
                    helper.goToGmaps();
                    break;
                case R.id.ivExplorePic:
                    ((MainActivity) context).displayRestaurantDetailFragment(restaurantObservable);
                    break;
            }

        }
    }

    public void clear() {
        restaurants.clear();
        notifyDataSetChanged();
    }

    public void addAll(List<RestaurantObservable> list) {
        restaurants.addAll(list);
        notifyDataSetChanged();
    }
    public int size(){
        return restaurants.size();
    }
}
