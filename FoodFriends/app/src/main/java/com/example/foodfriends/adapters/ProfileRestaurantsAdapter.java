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
import com.example.foodfriends.models.Restaurant;
import com.parse.ParseUser;

import java.util.List;

public class ProfileRestaurantsAdapter extends RecyclerView.Adapter<ProfileRestaurantsAdapter.ViewHolder>{
    private List<Restaurant> restaurants;
    private LayoutInflater mInflater;
    private Context context;



    // data is passed into the constructor
    public ProfileRestaurantsAdapter(Context context, List<Restaurant> rs) {
        this.context = context;
        this.mInflater = LayoutInflater.from(context);
        this.restaurants = rs;
    }

    // inflates the cell layout from xml when needed
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.profile_restaurant, parent, false);

        return new ViewHolder(view);
    }

    // binds the data to the TextView in each cell
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Restaurant restaurant= restaurants.get(position);
        holder.bind(restaurant);
    }

    // total number of cells
    @Override
    public int getItemCount() {
        return restaurants.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView ivResPic;
        private ImageView ivMap;
        private TextView tvResName;
        private TextView tvAddress;

        ViewHolder(View itemView) {
            super(itemView);
            ivResPic = itemView.findViewById(R.id.ivProfileRPic);
            ivMap = itemView.findViewById(R.id.ivProfileMapWidget);
            tvResName = itemView.findViewById(R.id.tvProfileRName);
            tvAddress = itemView.findViewById(R.id.tvProfileRAddress);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            /*

            // gets item position
            int position = getAdapterPosition();
            // make sure the position is valid, i.e. actually exists in the view
            if (position != RecyclerView.NO_POSITION) {
                // get the movie at the position, this won't work if the class is static
                Post post = allPosts.get(position);
                // create intent for the new activity
                Intent intent = new Intent(context, PostDetailActivity.class);
                intent.putExtra(Post.class.getSimpleName(), post);
                context.startActivity(intent);
            }*/

        }

        public void bind(Restaurant restaurant) {
            tvResName.setText(restaurant.getName());
            tvAddress.setText(restaurant.getAddress());
            tvAddress.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ParseUser user = ParseUser.getCurrentUser();
                    //String uri = String.format(Locale.ENGLISH, "http://maps.google.com/maps?saddr=%f,%f&daddr=%f,%f (%s)", user.getDouble("latitude"), user.getDouble("longitude"), restaurant.getLatitude(), restaurant.getLongitude(), restaurant.getName());
                    String uri = "http://maps.google.com/maps?daddr=" + restaurant.getLatitude().toString() + "," + restaurant.getLongitude().toString() + " (" + restaurant.getName() + ")";
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                    intent.setPackage("com.google.android.apps.maps");
                    context.startActivity(intent);
                }
            });
            RequestOptions requestOptions = new RequestOptions();
            requestOptions = requestOptions.transforms(new CenterCrop(), new RoundedCorners(15));
            Glide.with(context).applyDefaultRequestOptions(requestOptions).load(restaurant.getImageUrl()).into(ivResPic);
        }
    }
    // Clean all elements of the recycler
    public void clear() {
        restaurants.clear();
        notifyDataSetChanged();
    }

}
