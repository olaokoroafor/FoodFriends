package com.example.foodfriends.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

    public ExploreAdapter(Context context, List<RestaurantObservable> restaurants) {
        this.context = context;
        this.restaurants = restaurants;
    }

    @NonNull
    @Override

    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_restaurant, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        RestaurantObservable restaurant = restaurants.get(position);
        holder.bind(restaurant);

    }

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


        public ViewHolder(@NonNull View itemView){
            super(itemView);
            tvRName = itemView.findViewById(R.id.tvExploreName);
            tvLikeCount = itemView.findViewById(R.id.tvExploreLikeCount);
            tvToGoCount = itemView.findViewById(R.id.tvExploreToGoCount);
            tvAddress = itemView.findViewById(R.id.tvExploreAddress);
            ivRPic = itemView.findViewById(R.id.ivExplorePic);
            ivLike = itemView.findViewById(R.id.ivExploreLike);
            ivToGo = itemView.findViewById(R.id.ivExploreToGo);
        }

        public void bind(RestaurantObservable restaurant) {
            restaurantObservable = restaurant;
            restaurant.addObserver(this);
            tvRName.setText(restaurant.getName());
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

        @Override
        public void onClick(View v) {
            switch(v.getId()){

                case R.id.ivExploreLike:
                    restaurantObservable.toggleLike();
                    break;
                case R.id.ivExploreToGo:
                    restaurantObservable.toggleTogo();
                    break;
                case R.id.tvExploreAddress:
                    go_to_gmaps();
                    break;
                case R.id.ivExplorePic:
                    ((MainActivity) context).displayRestaurantDetailFragment(restaurantObservable);
                    break;
            }

        }

        private void go_to_gmaps() {
            ParseUser user = ParseUser.getCurrentUser();
            String uri = "http://maps.google.com/maps?daddr=" + restaurantObservable.getLatitude().toString() + "," + restaurantObservable.getLongitude().toString() + " (" + restaurantObservable.getName() + ")";
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
            intent.setPackage("com.google.android.apps.maps");
            context.startActivity(intent);
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
