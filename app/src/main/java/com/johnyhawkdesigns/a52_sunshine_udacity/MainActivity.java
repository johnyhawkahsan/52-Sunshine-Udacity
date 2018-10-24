package com.johnyhawkdesigns.a52_sunshine_udacity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

//https://home.openweathermap.org/api_keys API
//2a3d28af75a740af1e2614c2a02d26b2

public class MainActivity extends AppCompatActivity implements ForecastFragment.Callback{

    public static String APIKey = "2a3d28af75a740af1e2614c2a02d26b2"; //My openweathermap api key

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String DETAILFRAGMENT_TAG = "DFTAG";

    private boolean mTwoPane;
    private String mLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Sunshine App");
        //NOTE: Ahsan: if it is an ordinary phone, we have activity_main with fragment_forecast . If it is a tablet, we have activity_main(sw600dp) with fragment_forecast + FrameLayout (weather_detail_container) to dynamically add second detail fragment

        mLocation = Utility.getPreferredLocation(this);

/*
        // This is our old method that we used to create new fragment to our FrameLayout in our activity_main.xml Now we have specifically added fragment_forecast
        //Add fragment to our MainActivity
        if (savedInstanceState == null){
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_forecast, new ForecastFragment()) //container_activity_main is the id of activity_main.xml's main FrameLayout.
                    .commit();
        }
*/

        // If this is a tablet
        if (findViewById(R.id.weather_detail_container) != null) {
            Log.d(TAG, "onCreate: This is a tablet and device resolution is more than 600dp");
            // The detail container view will be present only in the large-screen layouts (res/layout-sw600dp).
            // If this view is present, then the activity should be in two-pane mode.
            mTwoPane = true;
            // In two-pane mode, show the detail view in this activity by adding or replacing the detail fragment using a fragment transaction.
            if (savedInstanceState == null) { // Dynamically adding Detail Fragment
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.weather_detail_container, new DetailFragment(), DETAILFRAGMENT_TAG)
                        .commit();
            }
        }
        // If this is a phone
        else {
            Log.d(TAG, "onCreate: This is a phone and device resolution is less than 600dp");
            //getSupportActionBar().setElevation(0f); // This does not work in our Toolbar
            mTwoPane = false;

        }

        ForecastFragment forecastFragment =  ((ForecastFragment)getSupportFragmentManager().findFragmentById(R.id.fragment_forecast));
        forecastFragment.setUseTodayLayout(!mTwoPane);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            //Start Settings Activity
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }

        if (id == R.id.action_map){
            openPreferredLocationInMap();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //=========Method to open our preferred location in Google Maps
    private void openPreferredLocationInMap() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String location = sharedPreferences.getString(
                getString(R.string.pref_location_key),
                getString(R.string.pref_location_default)
        );

        // Using the URI scheme for showing a location found on a map.  This super-handy intent can is detailed in the "Common Intents" page of Android's developer site:
        // http://developer.android.com/guide/components/intents-common.html#Maps
        Uri geoLocation = Uri.parse("geo:0,0?").buildUpon()
                .appendQueryParameter("q", location)
                .build();

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(geoLocation);
        
        if (intent.resolveActivity(getPackageManager()) != null){
            startActivity(intent);
        } else {
            Log.d(TAG, "Couldn't call " + location + ", no receiving apps installed!");
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        String location = Utility.getPreferredLocation( this );
        // update the location in our second pane using the fragment manager
        if (location != null && !location.equals(mLocation)) {
            ForecastFragment forecastFragment = (ForecastFragment)getSupportFragmentManager().findFragmentById(R.id.fragment_forecast);
            if ( null != forecastFragment ) {
                forecastFragment.onLocationChanged();
            }
            DetailFragment detailFragment = (DetailFragment)getSupportFragmentManager().findFragmentByTag(DETAILFRAGMENT_TAG);
            if ( null != detailFragment ) {
                detailFragment.onLocationChanged(location);
            }
            mLocation = location;
        }
    }


    @Override
    public void onItemSelected(Uri contentUri) {
        Log.d(TAG, "onItemSelected: contentUri received from ForecastFragment = " + contentUri);

        if (mTwoPane) {
            // In two-pane mode, show the detail view in this activity by adding or replacing the detail fragment using a fragment transaction.
            Bundle args = new Bundle();
            args.putParcelable(DetailFragment.DETAIL_URI, contentUri);

            DetailFragment fragment = new DetailFragment();
            fragment.setArguments(args);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.weather_detail_container, fragment, DETAILFRAGMENT_TAG)
                    .commit();
        } else {
            Intent intent = new Intent(this, DetailActivity.class)
                    .setData(contentUri);
            startActivity(intent);
        }
    }
}
