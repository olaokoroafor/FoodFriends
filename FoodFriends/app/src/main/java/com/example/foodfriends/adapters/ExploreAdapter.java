package com.example.foodfriends.adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodfriends.R;
import com.example.foodfriends.models.Restaurant;
import com.parse.ParseFile;
import com.parse.ParseUser;

import java.util.List;

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

        /*
        private TextView tvUsername;
        private TextView tvLikeCount;
        private TextView tvCaptionUsername;
        private TextView tvCaption;
        private TextView tvPostedAt;
        private ImageView ivProfilePic;
        private ImageView ivPostPic;
        private ImageView ivLike;
        private ImageView ivComment;
        private TextView tvComments;
        private boolean liked;
        */

        public ViewHolder(@NonNull View itemView){
            super(itemView);
            /*
            tvUsername = itemView.findViewById(R.id.tvTimelineUsername);
            tvLikeCount = itemView.findViewById(R.id.tvTimelineLikes);
            tvCaptionUsername = itemView.findViewById(R.id.tvTimelineCaptionUsername);
            tvCaption = itemView.findViewById(R.id.tvTimelineCaption);
            tvPostedAt = itemView.findViewById(R.id.tvTimelinePostedAt);
            ivProfilePic = itemView.findViewById(R.id.ivTimelinePfp);
            ivPostPic = itemView.findViewById(R.id.ivTimelinePostPic);
            ivLike = itemView.findViewById(R.id.ivTimelineLike);
            ivComment = itemView.findViewById(R.id.ivTimelineComment);
            tvComments = itemView.findViewById(R.id.tvComments);
             */
        }

        public void bind(Restaurant restaurant) {
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
