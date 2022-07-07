package com.example.foodfriends.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.foodfriends.R;

public class SearchFragment extends Fragment {

    public SearchFragment() {
        // Required empty public constructor
    }


    /**
     * Inflates the UI xml for the fragment
     * */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search, container, false);
    }
}