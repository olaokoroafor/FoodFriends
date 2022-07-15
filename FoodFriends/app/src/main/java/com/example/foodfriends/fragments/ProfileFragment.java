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
import android.os.Parcelable;
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
import com.example.foodfriends.activities.MapActivity;
import com.example.foodfriends.activities.SettingsActivity;
import com.example.foodfriends.adapters.ProfileRestaurantsAdapter;
import com.example.foodfriends.models.Friends;
import com.example.foodfriends.models.UserLike;
import com.example.foodfriends.models.UserToGo;
import com.example.foodfriends.observable_models.RestaurantObservable;
import com.example.foodfriends.observable_models.UserObservable;
import com.google.android.material.tabs.TabLayout;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

public class ProfileFragment extends Fragment implements Observer, View.OnClickListener {

    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 42;
    private static final int RESULT_OK = 0;
    private String photoFileName = "profile_photo.jpg";
    private File photoFile;
    private RecyclerView rvRestaurants;
    private static final String TAG = "ProfileFragment";
    private ProfileRestaurantsAdapter adapter;
    private List<RestaurantObservable> allRestaurants;
    private ImageView ivPfp;
    private ImageView ivAddPfp;
    private TextView tvUsername;
    private UserObservable currentUser;
    private UserObservable loggedInUser;
    private TabLayout tabLayout;
    private ImageView ivSettingsIcon;
    private ImageView ivFindFriends;
    private ImageView ivFollow;
    private ImageView ivLock;
    private boolean display_content;
    private boolean follows;

    public ProfileFragment() {
        // Required empty public constructor
    }


    /**
     * Inflates the UI xml for the fragment
     * Receives user object from bundle
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        currentUser = this.getArguments().getParcelable("user");
        currentUser.addObserver(this);
        loggedInUser = new UserObservable(ParseUser.getCurrentUser());
        rvRestaurants = view.findViewById(R.id.rvProfilePosts);
        ivPfp = view.findViewById(R.id.ivProfilePfp);
        tvUsername = view.findViewById(R.id.tvprofileUsername);
        ivAddPfp = view.findViewById(R.id.ivAddPfp);
        tabLayout = view.findViewById(R.id.profileTab);
        ivFindFriends = view.findViewById(R.id.ivFindFriends);
        ivSettingsIcon = view.findViewById(R.id.ivSettingsIcon);
        ivFollow = view.findViewById(R.id.ivFollow);
        ivLock = view.findViewById(R.id.ivLock);

        return view;
    }

    /**
     * Sets the values of the xml elements to restaurant data
     * Also sets the on click listener for necessary objects
     * Connects the recycler view of restaurants to the adapter
     * Populates the list of restaurants for the adapter to display
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (currentUser.getObjectId().equals(ParseUser.getCurrentUser().getObjectId())) {
            displayPersonalProfile();
        } else {
            displayOtherProfile();
        }
        displayProfilePic();
        tvUsername.setText("@" + currentUser.getUsername());
        allRestaurants = new ArrayList<RestaurantObservable>();
        rvRestaurants.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ProfileRestaurantsAdapter(getContext(), allRestaurants);
        rvRestaurants.setAdapter(adapter);
        handleTab();
    }

    private void displayOtherProfile() {
        ivAddPfp.setVisibility(View.GONE);
        ivSettingsIcon.setVisibility(View.GONE);
        ivFindFriends.setVisibility(View.GONE);
        follows = Friends.user_follows(currentUser.getUser());
        if (follows) {
            Glide.with(getContext())
                    .load(R.drawable.ic_baseline_person_remove_24)
                    .into(ivFollow);
        } else {
            Glide.with(getContext())
                    .load(R.drawable.ic_baseline_person_add_24)
                    .into(ivFollow);
        }
        ivFollow.setOnClickListener(this);
        display_content = loggedInUser.display_content(currentUser);
        if (display_content) {
            ivLock.setVisibility(View.GONE);
        } else {
            rvRestaurants.setVisibility(View.GONE);
        }
    }

    private void displayPersonalProfile() {
        ivAddPfp.setOnClickListener(this);
        ivSettingsIcon.setOnClickListener(this);
        ivFindFriends.setOnClickListener(this);
        ivFollow.setVisibility(View.GONE);
        ivLock.setVisibility(View.GONE);
        display_content = true;
    }

    private void handleTab() {
        int selected_tab = tabLayout.getSelectedTabPosition();
        if (selected_tab == 0) {
            if(display_content) {
                queryUserLikes();
            }
        } else {
            if(display_content) {
                queryUserToGos();
            }
        }
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (display_content){
                    if (tab.getPosition() == 0) {
                        adapter.clear();
                        Log.i(TAG, "Likes Tab selected");
                        queryUserLikes();
                    } else {
                        adapter.clear();
                        Log.i(TAG, "To Go tab selected");
                        queryUserToGos();
                    }
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

    private void displayProfilePic() {
        RequestOptions requestOptions = new RequestOptions();
        requestOptions = requestOptions.transforms(new CenterCrop(), new RoundedCorners(90));
        ParseFile pfp = currentUser.getProfilePhoto();
        if (pfp != null) {
            Glide.with(getContext()).applyDefaultRequestOptions(requestOptions).load(pfp.getUrl()).into(ivPfp);
        } else {
            Glide.with(this).applyDefaultRequestOptions(requestOptions).load(getResources().getIdentifier("ic_baseline_face_24", "drawable", getActivity().getPackageName())).into(ivPfp);
        }
    }


    /**
     * Specifies what needs to be done for each UI element click
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.ivAddPfp:
                addPfp();
                break;
            case R.id.ivSettingsIcon:
                toSettingsActivity();
                break;
            case R.id.ivFindFriends:
                goFindFriends();
                break;
            case R.id.ivFollow:
                toggle_follow();
                break;
        }
    }

    private void toSettingsActivity() {
        Intent intent = new Intent(getContext(), SettingsActivity.class);
        Bundle bundle = new Bundle();
        bundle.putParcelable("user", loggedInUser);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    /**
     * Adds profile picture bu launching camera, then sabving this photo to user object
     */
    private void addPfp() {
        launchCamera();
        currentUser.setProfilePhoto(new ParseFile(photoFile));
        currentUser.saveUser();
    }

    /**
     * Takes user to the find friends fragment
     */
    private void goFindFriends() {
        Fragment fragment = new FindFriendsFragment();
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.tvPlaceholder, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    /**
     * Either follows or unfollows someone based on original state of user relationship
     */
    private void toggle_follow() {
        if (Friends.user_follows(currentUser.getUser())) {
            Friends.unfollow(currentUser.getUser());
            follows = false;
            display_content = loggedInUser.display_content(currentUser);
        } else {
            Friends.follow(currentUser.getUser());
            follows = true;
            display_content = loggedInUser.display_content(currentUser);
        }
        currentUser.triggerObserver();
    }


    /**
     * Adds all the restaurants that the user has liked to adapter list
     */
    private void queryUserLikes() {
        ParseQuery<UserLike> query = ParseQuery.getQuery(UserLike.class);
        // include data referred by restaurant key
        query.include(UserLike.RESTAURANT_KEY);
        query.whereEqualTo("user", currentUser.getUser());
        query.addDescendingOrder("createdAt");

        query.findInBackground(new FindCallback<UserLike>() {
            @Override
            public void done(List<UserLike> likes, ParseException e) {
                // check for errors
                if (e != null) {
                    Log.e(TAG, "Issue with getting posts", e);
                    return;
                }
                List<RestaurantObservable> rs = new ArrayList<RestaurantObservable>();
                for (UserLike like : likes) {
                    rs.add(new RestaurantObservable(like.getRestaurant()));
                }
                allRestaurants.addAll(rs);
                adapter.notifyDataSetChanged();
            }
        });
    }

    /**
     * Adds all the restaurants that the user wants to go to to adapter list
     */
    private void queryUserToGos() {

        ParseQuery<UserToGo> query = ParseQuery.getQuery(UserToGo.class);
        // include data referred by restaurant key
        query.include(UserToGo.RESTAURANT_KEY);
        query.whereEqualTo("user", currentUser.getUser());
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
                List<RestaurantObservable> rs = new ArrayList<RestaurantObservable>();
                for (UserToGo togo : togos) {
                    rs.add(new RestaurantObservable(togo.getRestaurant()));
                }

                allRestaurants.addAll(rs);
                adapter.notifyDataSetChanged();
            }
        });
    }

    /**
     * Gets filetarget for the photo based on file name
     */
    public File getPhotoFileUri(String fileName) {
        File mediaStorageDir = new File(getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES), TAG);

        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()) {
            Log.d(TAG, "failed to create directory");
        }
        return new File(mediaStorageDir.getPath() + File.separator + fileName);
    }

    /**
     * Launches Camera for user and allows them to take pictures
     */
    private void launchCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        photoFile = getPhotoFileUri(photoFileName);

        Uri fileProvider = FileProvider.getUriForFile(getContext(), "com.codepath.fileprovider", photoFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);
        Log.i(TAG, String.valueOf(photoFile));
        Log.i(TAG, String.valueOf(fileProvider));

        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
        }
    }

    /**
     * Checks to see if picture has been taken when camera finishes
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {

            if (resultCode != RESULT_OK) { // Result was a failure
                Toast.makeText(getContext(), "Picture wasn't taken!" + String.valueOf(resultCode), Toast.LENGTH_SHORT).show();
            } else {
                // by this point we have the camera photo on disk
                Bitmap takenImage = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
                // RESIZE BITMAP, see section below
                // Load the taken image into a preview
                ivPfp.setImageBitmap(takenImage);
            }


        }
    }

    /**
     * Called when user data is updated, re renders user's profile photo
     */
    @Override
    public void update(Observable o, Object arg) {
        RequestOptions requestOptions = new RequestOptions();
        requestOptions = requestOptions.transforms(new CenterCrop(), new RoundedCorners(100));
        ParseFile pfp = currentUser.getProfilePhoto();
        if (pfp != null) {
            Glide.with(getContext()).applyDefaultRequestOptions(requestOptions).load(pfp.getUrl()).into(ivPfp);
        } else {
            Glide.with(this).applyDefaultRequestOptions(requestOptions).load(getResources().getIdentifier("ic_baseline_face_24", "drawable", getActivity().getPackageName())).into(ivPfp);
        }
        if (follows) {
            Glide.with(getContext())
                    .load(R.drawable.ic_baseline_person_remove_24)
                    .into(ivFollow);
        } else {
            Glide.with(getContext())
                    .load(R.drawable.ic_baseline_person_add_24)
                    .into(ivFollow);
        }
        Log.i(TAG, "DISPLAY CONTENT: " + String.valueOf(display_content));
        if(display_content){
            rvRestaurants.setVisibility(View.VISIBLE);
            ivLock.setVisibility(View.GONE);
            int selected_tab = tabLayout.getSelectedTabPosition();
            allRestaurants.clear();
            if (selected_tab == 0) {
                queryUserLikes();
            } else {
                queryUserToGos();
            }
        }
        else{
            rvRestaurants.setVisibility(View.GONE);
            ivLock.setVisibility(View.VISIBLE);
        }


    }
}
