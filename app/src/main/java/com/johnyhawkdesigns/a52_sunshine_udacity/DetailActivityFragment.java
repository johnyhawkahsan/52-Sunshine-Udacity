package com.johnyhawkdesigns.a52_sunshine_udacity;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment {

    private static final String TAG = DetailActivityFragment.class.getCanonicalName();


    //Constructor
    public DetailActivityFragment() {
        //Empty constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detail, container, false);

        String forecast = getActivity().getIntent().getStringExtra("item");

        Log.d(TAG, "onCreateView: forecast data received =  " + forecast);


        return view;
    }
}
