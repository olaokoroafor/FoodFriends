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
import com.example.foodfriends.observable_models.FriendObservable;
import com.parse.ParseFile;
import com.parse.ParseUser;

import java.util.List;
import java.util.Observable;
import java.util.Observer;

public class FindFriendsAdapter extends RecyclerView.Adapter<FindFriendsAdapter.ViewHolder>{
    private List<FriendObservable> users;
    private LayoutInflater mInflater;
    private Context context;




    // data is passed into the constructor
    public FindFriendsAdapter(Context context, List<FriendObservable> users) {
        this.context = context;
        this.mInflater = LayoutInflater.from(context);
        this.users = users;
    }

    /**
     * Inflates view of the user search item xml**/
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.search_user_item, parent, false);

        return new ViewHolder(view);
    }

    /**
     * Calls binder for a user at particular position**/
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FriendObservable user = users.get(position);
        holder.bind(user);
    }

    // total number of cells
    @Override
    public int getItemCount() {
        return users.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements Observer, View.OnClickListener{
        private ImageView ivPfp;
        private ImageView ivAddFriend;
        private TextView tvUsername;
        private TextView tvLocation;
        private FriendObservable current_user;

        /**
         * Constructor
         * */
        public ViewHolder(View itemView) {
            super(itemView);
            ivPfp = itemView.findViewById(R.id.ivSearchPfp);
            ivAddFriend = itemView.findViewById(R.id.ivAddFriend);
            tvUsername = itemView.findViewById(R.id.tvSearchUsername);
            tvLocation = itemView.findViewById(R.id.tvSearchLocation);
        }

        /**
         * Binds the xml elements to user data
         * */
        public void bind(FriendObservable user) {
            current_user = user;
            user.addObserver(this);
            tvUsername.setText(user.getUsername());
            tvLocation.setText(user.getCity() + ", " + user.getState());
            RequestOptions requestOptions = new RequestOptions();
            requestOptions = requestOptions.transforms(new CenterCrop(), new RoundedCorners(90));
            ParseFile pfp = user.getProfilePhoto();
            if (pfp != null){
                Glide.with(context).applyDefaultRequestOptions(requestOptions).load(pfp.getUrl()).into(ivPfp);
            }
            else{
                Glide.with(context).applyDefaultRequestOptions(requestOptions).load(context.getResources().getIdentifier("ic_baseline_face_24", "drawable", context.getPackageName())).into(ivPfp);
            }
            ivPfp.setOnClickListener(this);
            if (user.user_follows()){
                Glide.with(context)
                        .load(R.drawable.ic_baseline_person_remove_24)
                        .into(ivAddFriend);
            }
            else{
                Glide.with(context)
                        .load(R.drawable.ic_baseline_person_add_24)
                        .into(ivAddFriend);
            }
            ivAddFriend.setOnClickListener(this);
        }

        /**
         * Specifies what needs to be done for each UI click
         * */
        @Override
        public void onClick(View v) {
            switch(v.getId()){
                case R.id.ivSearchPfp:
                    ((MainActivity) context).displayOtherProfileFragment(current_user.getUser());
                    break;
                case R.id.ivAddFriend:
                    current_user.toggle_follow();
                    break;
            }
        }

        /**
         * Called when restaurant data is updated, re renders follow image view
         * */
        @Override
        public void update(Observable o, Object arg) {
            if (current_user.user_follows()){
                Glide.with(context)
                        .load(R.drawable.ic_baseline_person_add_24)
                        .into(ivAddFriend);
            }
            else{
                Glide.with(context)
                        .load(R.drawable.ic_baseline_person_remove_24)
                        .into(ivAddFriend);
            }
        }
    }
    // Clean all elements of the recycler
    public void clear() {
        users.clear();
        notifyDataSetChanged();
    }

}