package com.example.foodfriends.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.example.foodfriends.R;
import com.example.foodfriends.adapters.FindFriendsAdapter;
import com.example.foodfriends.observable_models.FriendObservable;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class FindFriendsFragment extends Fragment {
    private static final String TAG = "FindFriendsFragment";
    private SearchView searchFriends;
    private RecyclerView rvUsers;
    private FindFriendsAdapter adapter;
    private List<FriendObservable> allUsers;
    private ProgressBar searchProgressBar;

    public FindFriendsFragment() {
        // Required empty public constructor
    }

    /**
     * Inflates the UI xml for the fragment
     * */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_find_friends, container, false);
    }

    /**
     * Connects the recycler view of users to the adapter
     * Populates the list of users for the adapter to display
     * */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        searchFriends = view.findViewById(R.id.searchFriends);
        rvUsers = view.findViewById(R.id.rvUsers);
        searchProgressBar = view.findViewById(R.id.pbSearchFriends);
        searchProgressBar.setVisibility(View.GONE);

        searchFriends.setIconified(false);
        // perform set on query text listener event
        searchFriends.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchProgressBar.setVisibility(View.VISIBLE);
                adapter.clear();
                queryUsers(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        allUsers= new ArrayList<FriendObservable>();
        rvUsers.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new FindFriendsAdapter(getContext(), allUsers);
        rvUsers.setAdapter(adapter);
    }

    /**
     * Searches Parse Database for users with username starting with submitted query
     * */
    private void queryUsers(String search_term) {
        ParseQuery<ParseUser> query = ParseQuery.getQuery(ParseUser.class);
        query.whereStartsWith("username", search_term);
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> users, ParseException e) {
                // check for errors
                if (e != null) {
                    Log.e(TAG, "Issue with getting users", e);
                    return;
                }
                allUsers.clear();
                for (ParseUser user: users){
                    allUsers.add(new FriendObservable(user));
                }
                adapter.notifyDataSetChanged();
                rvUsers.setVisibility(View.VISIBLE);
                searchProgressBar.setVisibility(View.GONE);
            }
        });
    }
}