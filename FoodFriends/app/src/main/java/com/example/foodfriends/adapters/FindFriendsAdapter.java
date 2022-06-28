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
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.foodfriends.R;
import com.example.foodfriends.activities.MainActivity;
import com.example.foodfriends.fragments.FindFriendsFragment;
import com.example.foodfriends.models.Friends;
import com.example.foodfriends.models.Restaurant;
import com.parse.ParseFile;
import com.parse.ParseUser;

import java.util.List;

public class FindFriendsAdapter extends RecyclerView.Adapter<FindFriendsAdapter.ViewHolder>{
    private List<ParseUser> users;
    private LayoutInflater mInflater;
    private Context context;



    // data is passed into the constructor
    public FindFriendsAdapter(Context context, List<ParseUser> users) {
        this.context = context;
        this.mInflater = LayoutInflater.from(context);
        this.users = users;
    }

    // inflates the cell layout from xml when needed
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.search_user_item, parent, false);

        return new ViewHolder(view);
    }

    // binds the data to the Views in each cell
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ParseUser user = users.get(position);
        holder.bind(user);
    }

    // total number of cells
    @Override
    public int getItemCount() {
        return users.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder{
        private ImageView ivPfp;
        private ImageView ivAddFriend;
        private TextView tvUsername;
        private TextView tvLocation;
        private boolean follows = false;

        ViewHolder(View itemView) {
            super(itemView);
            ivPfp = itemView.findViewById(R.id.ivSearchPfp);
            ivAddFriend = itemView.findViewById(R.id.ivAddFriend);
            tvUsername = itemView.findViewById(R.id.tvSearchUsername);
            tvLocation = itemView.findViewById(R.id.tvSearchLocation);
        }

        public void bind(ParseUser user) {
            tvUsername.setText(user.getUsername());
            tvLocation.setText(user.getString("city") + ", " + user.getString("state"));
            RequestOptions requestOptions = new RequestOptions();
            requestOptions = requestOptions.transforms(new CenterCrop(), new RoundedCorners(90));
            ParseFile pfp = user.getParseFile("profilePhoto");
            if (pfp != null){
                Glide.with(context).applyDefaultRequestOptions(requestOptions).load(pfp.getUrl()).into(ivPfp);
            }
            else{
                Glide.with(context).applyDefaultRequestOptions(requestOptions).load(context.getResources().getIdentifier("ic_baseline_face_24", "drawable", context.getPackageName())).into(ivPfp);
            }
            ivPfp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((MainActivity) context).displayOtherProfileFragment(user);
                }
            });
            follows = Friends.user_follows(user);
            if (follows){
                Glide.with(context)
                        .load(R.drawable.ic_baseline_person_remove_24)
                        .into(ivAddFriend);
            }
            else{
                Glide.with(context)
                        .load(R.drawable.ic_baseline_person_add_24)
                        .into(ivAddFriend);
            }
            ivAddFriend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (follows){
                        Friends.unfollow(user);
                        Glide.with(context)
                                .load(R.drawable.ic_baseline_person_add_24)
                                .into(ivAddFriend);
                        follows=false;
                    }
                    else{
                        Friends.follow(user);
                        Glide.with(context)
                                .load(R.drawable.ic_baseline_person_remove_24)
                                .into(ivAddFriend);
                        follows=true;
                    }
                }
            });
        }
    }
    // Clean all elements of the recycler
    public void clear() {
        users.clear();
        notifyDataSetChanged();
    }

}