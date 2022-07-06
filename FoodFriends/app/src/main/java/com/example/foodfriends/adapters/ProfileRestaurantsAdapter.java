package com.example.foodfriends.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.foodfriends.R;
import com.example.foodfriends.activities.MainActivity;
import com.example.foodfriends.models.Restaurant;
import com.example.foodfriends.observable_models.RestaurantObservable;
import com.parse.ParseUser;

import java.util.List;

public class ProfileRestaurantsAdapter extends RecyclerView.Adapter<ProfileRestaurantsAdapter.ViewHolder>{
    private List<RestaurantObservable> restaurants;
    private LayoutInflater mInflater;
    private Context context;



    // data is passed into the constructor
    public ProfileRestaurantsAdapter(Context context, List<RestaurantObservable> rs) {
        this.context = context;
        this.mInflater = LayoutInflater.from(context);
        this.restaurants = rs;
    }

    /**
     * Inflates view of the item restaurant xml when necessary**/
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.profile_restaurant, parent, false);

        return new ViewHolder(view);
    }

    /**
     * Calls binder for a restaurant at particular position**/
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        RestaurantObservable restaurant= restaurants.get(position);
        holder.bind(restaurant);
    }

    // total number of cells
    @Override
    public int getItemCount() {
        return restaurants.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private ImageView ivResPic;
        private ImageView ivMap;
        private TextView tvResName;
        private TextView tvAddress;
        private RestaurantObservable restaurantObservable;

        /**
         * Constructor
         * */
        public ViewHolder(View itemView) {
            super(itemView);
            ivResPic = itemView.findViewById(R.id.ivProfileRPic);
            ivMap = itemView.findViewById(R.id.ivProfileMapWidget);
            tvResName = itemView.findViewById(R.id.tvProfileRName);
            tvAddress = itemView.findViewById(R.id.tvProfileRAddress);
        }

        /**
         * Binds the xml elements to restaurant data
         * */
        public void bind(RestaurantObservable restaurant) {
            restaurantObservable = restaurant;
            tvResName.setText(restaurant.getName());
            tvAddress.setText(restaurant.getAddress());
            tvAddress.setOnClickListener(this);
            RequestOptions requestOptions = new RequestOptions();
            requestOptions = requestOptions.transforms(new CenterCrop(), new RoundedCorners(15));
            Glide.with(context).applyDefaultRequestOptions(requestOptions).load(restaurant.getImageUrl()).into(ivResPic);
            ivResPic.setOnClickListener(this);
        }

        /**
         * Specifies what needs to be done for each UI element click
         * */
        @Override
        public void onClick(View v) {
            switch(v.getId()){
                case R.id.tvProfileRAddress:
                    go_to_gmaps();
                    break;
                case R.id.ivProfileRPic:
                    ((MainActivity) context).displayRestaurantDetailFragment(restaurantObservable);
                    break;
            }

        }

        /**
         * Directs user to google maps with restaurant preplaced in map
         * */
        private void go_to_gmaps() {
            String uri = "http://maps.google.com/maps?daddr=" + restaurantObservable.getLatitude().toString() + "," + restaurantObservable.getLongitude().toString() + " (" + restaurantObservable.getName() + ")";
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
            intent.setPackage("com.google.android.apps.maps");
            context.startActivity(intent);
        }
    }
    // Clean all elements of the recycler
    public void clear() {
        restaurants.clear();
        notifyDataSetChanged();
    }

}
