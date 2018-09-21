package com.johnyhawkdesigns.a52_sunshine_udacity;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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

        TextView textView = view.findViewById(R.id.detail_text);

        Intent intent = getActivity().getIntent(); //Get intent that launched this activity

        //Check if Intent data is not null and also it contains our extra data
        if (intent != null && intent.hasExtra(Intent.EXTRA_TEXT)){

            String forecast = intent.getStringExtra(Intent.EXTRA_TEXT);
            Log.d(TAG, "onCreateView: forecast data received, " + Intent.EXTRA_TEXT + " = " + forecast);
            textView.setText(forecast);

        }


        return view;
    }
}
