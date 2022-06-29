package com.example.foodfriends.fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.foodfriends.R;
import com.example.foodfriends.activities.LogInActivity;
import com.example.foodfriends.adapters.ProfileRestaurantsAdapter;
import com.example.foodfriends.models.Friends;
import com.example.foodfriends.models.Restaurant;
import com.example.foodfriends.models.User;
import com.example.foodfriends.models.UserLike;
import com.example.foodfriends.models.UserToGo;
import com.google.android.material.tabs.TabLayout;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ProfileFragment extends Fragment {
    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 42;
    private static final int RESULT_OK = 0;
    private String photoFileName = "profile_photo.jpg";
    private File photoFile;
    private RecyclerView rvRestaurants;
    private static final String TAG = "Profile Fragment";
    private ProfileRestaurantsAdapter adapter;
    private List<Restaurant> allRestaurants;
    private ImageView ivPfp;
    private ImageView ivAddPfp;
    private TextView tvUsername;
    private ParseUser currentUser;
    private TabLayout tabLayout;
    private Button btnLogOut;
    private ImageView ivFindFriends;
    private ImageView ivFollow;
    private boolean follows;

    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        currentUser = this.getArguments().getParcelable("user");
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rvRestaurants = view.findViewById(R.id.rvProfilePosts);
        ivPfp = view.findViewById(R.id.ivProfilePfp);
        tvUsername = view.findViewById(R.id.tvprofileUsername);
        ivAddPfp = view.findViewById(R.id.ivAddPfp);
        tabLayout = view.findViewById(R.id.profileTab);
        ivFindFriends = view.findViewById(R.id.ivFindFriends);
        btnLogOut = view.findViewById(R.id.btnLogOut);
        ivFollow = view.findViewById(R.id.ivFollow);


        if (currentUser.getObjectId().equals(ParseUser.getCurrentUser().getObjectId())) {
            currentUser = ParseUser.getCurrentUser();
            ivAddPfp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    launchCamera();
                    currentUser.put("profilePhoto", new ParseFile(photoFile));
                    currentUser.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e != null){
                                Log.e(TAG, "Error saving photo: " + e);
                                Toast.makeText(getContext(), "Error while saving", Toast.LENGTH_SHORT).show();
                            }
                            else{
                                Log.i(TAG, "Photo upload was successful!");
                                RequestOptions requestOptions = new RequestOptions();
                                requestOptions = requestOptions.transforms(new CenterCrop(), new RoundedCorners(100));
                                Glide.with(getContext()).applyDefaultRequestOptions(requestOptions).load(currentUser.getParseFile("profilePhoto").getUrl()).into(ivPfp);
                            }
                        }
                    });
                }
            });

            btnLogOut.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ParseUser.logOut();
                    Intent i = new Intent(getContext(), LogInActivity.class);
                    // set the new task and clear flags
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(i);
                }
            });

            ivFindFriends.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Fragment fragment = new FindFriendsFragment();
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.tvPlaceholder, fragment);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                }
            });
            ivFollow.setVisibility(View.GONE);
        }
        else{
            ivAddPfp.setVisibility(View.GONE);
            btnLogOut.setVisibility(View.GONE);
            ivFindFriends.setVisibility(View.GONE);
            follows = Friends.user_follows(currentUser);
            if (follows){
                Glide.with(getContext())
                        .load(R.drawable.ic_baseline_person_remove_24)
                        .into(ivFollow);
            }
            else{
                Glide.with(getContext())
                        .load(R.drawable.ic_baseline_person_add_24)
                        .into(ivFollow);
            }
            ivFollow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (follows){
                        Friends.unfollow(currentUser);
                        Glide.with(getContext())
                                .load(R.drawable.ic_baseline_person_add_24)
                                .into(ivFollow);
                        follows = false;
                    }
                    else{
                        Friends.follow(currentUser);
                        Glide.with(getContext())
                                .load(R.drawable.ic_baseline_person_remove_24)
                                .into(ivFollow);
                        follows = true;
                    }
                }
            });
        }

        RequestOptions requestOptions = new RequestOptions();
        requestOptions = requestOptions.transforms(new CenterCrop(), new RoundedCorners(90));
        ParseFile pfp = currentUser.getParseFile("profilePhoto");
        if (pfp != null){
            Glide.with(getContext()).applyDefaultRequestOptions(requestOptions).load(pfp.getUrl()).into(ivPfp);
        }
        else{
            Glide.with(this).applyDefaultRequestOptions(requestOptions).load(getResources().getIdentifier("ic_baseline_face_24", "drawable", getActivity().getPackageName())).into(ivPfp);
        }

        tvUsername.setText("@"+currentUser.getUsername());


        allRestaurants = new ArrayList<Restaurant>();
        rvRestaurants.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ProfileRestaurantsAdapter(getContext(), allRestaurants);
        rvRestaurants.setAdapter(adapter);
        int selected_tab = tabLayout.getSelectedTabPosition();
        if(selected_tab == 0){
            Log.i(TAG, "Likes Tab selected");
            queryUserLikes();
        }
        else{
            Log.i(TAG, "To Go tab selected");
            queryUserToGos();
        }
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener(){

            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0){
                    adapter.clear();
                    Log.i(TAG, "Likes Tab selected");
                    queryUserLikes();
                }
                else{
                    adapter.clear();
                    Log.i(TAG, "To Go tab selected");
                    queryUserToGos();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }

    private void queryUserLikes() {
        ParseQuery<UserLike> query = ParseQuery.getQuery(UserLike.class);
        // include data referred by restaurant key
        query.include(UserLike.RESTAURANT_KEY);
        query.whereEqualTo("user", currentUser);
        query.addDescendingOrder("createdAt");

        query.findInBackground(new FindCallback<UserLike>() {
            @Override
            public void done(List<UserLike> likes, ParseException e) {
                // check for errors
                if (e != null) {
                    Log.e(TAG, "Issue with getting posts", e);
                    return;
                }
                List<Restaurant> rs = new ArrayList<Restaurant>();
                for (UserLike like : likes) {
                    rs.add(like.getRestaurant());
                    Log.i(TAG, "Restaurant: " + like.getObjectId());
                }
                // save received posts to list and notify adapter of new data
                allRestaurants.addAll(rs);
                adapter.notifyDataSetChanged();
            }
        });
    }

    private void queryUserToGos() {

        ParseQuery<UserToGo> query = ParseQuery.getQuery(UserToGo.class);
        // include data referred by restaurant key
        query.include(UserToGo.RESTAURANT_KEY);
        query.whereEqualTo("user", currentUser);
        // order posts by creation date (newest first)
        query.addDescendingOrder("createdAt");


        query.findInBackground(new FindCallback<UserToGo>() {
            @Override
            public void done(List<UserToGo> togos, ParseException e) {
                // check for errors
                if (e != null) {
                    Log.e(TAG, "Issue with getting restaurants", e);
                    return;
                }
                List<Restaurant> rs = new ArrayList<Restaurant>();
                for (UserToGo togo : togos) {
                    rs.add(togo.getRestaurant());
                    Log.i(TAG, "Restaurant: " + togo.getObjectId());
                }

                allRestaurants.addAll(rs);
                adapter.notifyDataSetChanged();
            }
        });
    }

    public File getPhotoFileUri(String fileName) {
        // Get safe storage directory for photos
        // Use `getExternalFilesDir` on Context to access package-specific directories.
        // This way, we don't need to request external read/write runtime permissions.
        File mediaStorageDir = new File(getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES), TAG);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()){
            Log.d(TAG, "failed to create directory");
        }

        // Return the file target for the photo based on filename
        return new File(mediaStorageDir.getPath() + File.separator + fileName);
    }

    private void launchCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        photoFile = getPhotoFileUri(photoFileName);

        Uri fileProvider = FileProvider.getUriForFile(getContext(), "com.codepath.fileprovider", photoFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);
        Log.i(TAG, String.valueOf(photoFile));
        Log.i(TAG, String.valueOf(fileProvider));

        if(intent.resolveActivity(getActivity().getPackageManager()) != null){
            startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {

            if (resultCode != RESULT_OK) { // Result was a failure
                Toast.makeText(getContext(), "Picture wasn't taken!", Toast.LENGTH_SHORT).show();
            } else {
                // by this point we have the camera photo on disk
                Bitmap takenImage = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
                // RESIZE BITMAP, see section below
                // Load the taken image into a preview
                ivPfp.setImageBitmap(takenImage);
            }


        }
    }

}


interface ProfileState {
}

class LoadingState implements ProfileState {
}

class CompleteState implements ProfileState {
    public ParseUser user;

}