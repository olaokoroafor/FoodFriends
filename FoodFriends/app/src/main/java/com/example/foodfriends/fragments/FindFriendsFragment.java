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

import com.example.foodfriends.R;
import com.example.foodfriends.adapters.FindFriendsAdapter;
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
    private List<ParseUser> allUsers;

    public FindFriendsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_find_friends, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        searchFriends = view.findViewById(R.id.searchFriends);
        rvUsers = view.findViewById(R.id.rvUsers);

        searchFriends.setIconified(false);
        // perform set on query text listener event
        searchFriends.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                adapter.clear();
                queryUsers(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });


        allUsers= new ArrayList<ParseUser>();
        rvUsers.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new FindFriendsAdapter(getContext(), allUsers);
        rvUsers.setAdapter(adapter);
    }

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
                allUsers.addAll(users);
                adapter.notifyDataSetChanged();
            }
        });
    }
}