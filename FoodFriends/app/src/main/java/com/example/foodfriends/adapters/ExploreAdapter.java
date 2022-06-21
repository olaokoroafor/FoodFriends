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
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.foodfriends.R;
import com.example.foodfriends.models.Restaurant;
import com.parse.ParseFile;
import com.parse.ParseUser;

import java.util.List;
import java.util.Locale;

public class ExploreAdapter extends RecyclerView.Adapter <ExploreAdapter.ViewHolder>{
    private Context context;
    private List<Restaurant> restaurants;

    public ExploreAdapter(Context context, List<Restaurant> restaurants) {
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
        Restaurant restaurant = restaurants.get(position);
        holder.bind(restaurant);

    }

    @Override
    public int getItemCount() {
        return restaurants.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{


        private TextView tvRName;
        private TextView tvLikeCount;
        private TextView tvToGoCount;
        private TextView tvAddress;
        private ImageView ivRPic;
        private ImageView ivLike;
        private ImageView ivToGo;
        private boolean liked;
        private boolean going;


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

        public void bind(Restaurant restaurant) {

            tvRName.setText(restaurant.getName());
            tvAddress.setText(restaurant.getAddress());
            int num_likes = restaurant.getLikes();
            tvLikeCount.setText(String.valueOf(num_likes));
            int num_togo = restaurant.getToGos();
            tvToGoCount.setText(String.valueOf(num_togo));
            RequestOptions requestOptions = new RequestOptions();
            requestOptions = requestOptions.transforms(new CenterCrop(), new RoundedCorners(15));
            Glide.with(context).applyDefaultRequestOptions(requestOptions).load(restaurant.getImageUrl()).into(ivRPic);
            liked = restaurant.user_like();
            if(liked){
                Glide.with(context)
                        .load(R.drawable.ic_baseline_red_heart_24)
                        .into(ivLike);
            }
            else{
                Glide.with(context)
                        .load(R.drawable.ic_baseline_heart_24)
                        .into(ivLike);
            }


            going = restaurant.user_to_go();
            if(going){
                Glide.with(context)
                        .load(R.drawable.ic_baseline_active_go_24)
                        .into(ivToGo);
            }

            else{
                Glide.with(context)
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
                        Glide.with(context)
                                .load(R.drawable.ic_baseline_red_heart_24)
                                .into(ivLike);
                    }

                    else{
                        restaurant.decrementLikes();
                        tvLikeCount.setText(new Integer(num_likes-1).toString());
                        liked = false;
                        Glide.with(context)
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
                        Glide.with(context)
                                .load(R.drawable.ic_baseline_active_go_24)
                                .into(ivToGo);
                    }

                    else{
                        restaurant.decrementToGos();
                        tvToGoCount.setText(new Integer(num_togo-1).toString());
                        going = false;
                        Glide.with(context)
                                .load(R.drawable.ic_baseline_call_missed_outgoing_24)
                                .into(ivToGo);
                    }
                }
            });

            tvAddress.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ParseUser user = ParseUser.getCurrentUser();
                    //String uri = String.format(Locale.ENGLISH, "http://maps.google.com/maps?saddr=%f,%f&daddr=%f,%f (%s)", user.getDouble("latitude"), user.getDouble("longitude"), restaurant.getLatitude(), restaurant.getLongitude(), restaurant.getName());
                    String uri = "http://maps.google.com/maps?daddr=" + restaurant.getLatitude().toString() + "," + restaurant.getLatitude().toString() + " (" + restaurant.getName() + ")";
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                    intent.setPackage("com.google.android.apps.maps");
                    context.startActivity(intent);
                }
            });
            /*
            ivPostPic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, PostDetailActivity.class);
                    intent.putExtra(Post.class.getSimpleName(), post);
                    context.startActivity(intent);
                }
            });

            //bind data to view elements
            //tvCaption.setText(post.getDescription());
            //tvUsername.setText(post.getUser().getUsername());
            //tvCaptionUsername.setText(post.getUser().getUsername());
            /*
            tvComments.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, CommentsActivity.class);
                    intent.putExtra(Post.class.getSimpleName(), post);
                    context.startActivity(intent);
                    Log.i("Timeline Adapter", post.getObjectId());
                }
            });


            tvPostedAt.setText(Post.getRelativeTimeAgo(post.getCreatedAt().toString()));
            RequestOptions requestOptions = new RequestOptions();
            requestOptions = requestOptions.transforms(new CenterCrop());
            Glide.with(context).applyDefaultRequestOptions(requestOptions).load(post.getImage().getUrl()).into(ivPostPic);
            ivPostPic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, PostDetailActivity.class);
                    intent.putExtra(Post.class.getSimpleName(), post);
                    context.startActivity(intent);
                }
            });
            requestOptions = requestOptions.transforms(new CenterCrop(), new RoundedCorners(65));
            ParseFile pfp_url = post.getUser().getParseFile("profileImage");
            if (pfp_url != null){
                Glide.with(context).applyDefaultRequestOptions(requestOptions).load(pfp_url.getUrl()).into(ivProfilePic);
            }
            else{
                Glide.with(context).applyDefaultRequestOptions(requestOptions).load(context.getResources().getIdentifier("ic_baseline_face_24", "drawable", context.getPackageName())).into(ivProfilePic);
            }
            ivComment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, CommentsActivity.class);
                    intent.putExtra(Post.class.getSimpleName(), post);
                    context.startActivity(intent);
                    Log.i("Timeline Adapter", post.getObjectId());
                }
            });
            View.OnClickListener to_profile = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((HomeActivity) context).displayProfileFragment(post.getUser());
                }
            };
            ivProfilePic.setOnClickListener(to_profile);
            tvUsername.setOnClickListener(to_profile);
            tvCaptionUsername.setOnClickListener(to_profile);

            List<String> likeArray = post.getLikes();
            Integer likes;
            if (likeArray != null){
                likes = likeArray.size();
            }
            else{
                likes = 0;
            }
            tvLikeCount.setText( likes + " likes");

            if (likeArray.contains(ParseUser.getCurrentUser().getObjectId())){
                liked = true;
                Glide.with(context)
                        .load(R.drawable.ufi_heart_active)
                        .into(ivLike);
            }
            else{
                liked = false;
                Glide.with(context)
                        .load(R.drawable.ufi_heart)
                        .into(ivLike);
            }

            ivLike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!liked){
                        post.setLikes(ParseUser.getCurrentUser(), true);
                        post.saveInBackground();
                        tvLikeCount.setText(new Integer(likes+1).toString() + " likes");
                        liked = true;
                        Glide.with(context)
                                .load(R.drawable.ufi_heart_active)
                                .into(ivLike);
                    }

                    else{
                        post.setLikes(ParseUser.getCurrentUser(), false);
                        post.saveInBackground();
                        tvLikeCount.setText(new Integer(likes-1).toString() + " likes");
                        liked = false;
                        Glide.with(context)
                                .load(R.drawable.ufi_heart)
                                .into(ivLike);
                    }
                }
            });
            */


        }

    }

    // Clean all elements of the recycler
    public void clear() {
        restaurants.clear();
        notifyDataSetChanged();
    }

    // Add a list of items -- change to type used
    public void addAll(List<Restaurant> list) {
        restaurants.addAll(list);
        notifyDataSetChanged();
    }
}
