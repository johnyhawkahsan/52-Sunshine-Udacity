package com.johnyhawkdesigns.a52_sunshine_udacity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.johnyhawkdesigns.a52_sunshine_udacity.data.WeatherContract;


import java.util.ArrayList;
import java.util.Arrays;



/**
 * A placeholder fragment containing a simple view.
 */
public class ForecastFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = ForecastFragment.class.getSimpleName();

    //Creating our Adapter to collect our data and display in our ListView
    //private ArrayAdapter<String> mForecastAdapter; // Now we don't need this ArrayAdapter<String>. Instead, we use our newly created
    private ForecastAdapter mForecastAdapter;

    private ListView mListView;
    private int mPosition = ListView.INVALID_POSITION; // we first initialize this to 0 or invalid position
    private boolean mUseTodayLayout;

    private static final String SELECTED_KEY = "selected_position";

    private static final int FORECAST_LOADER = 0;

    // For the forecast view we're showing only a small subset of the stored data. Specify the columns we need.
    private static final String[] FORECAST_COLUMNS = {
            // In this case the id needs to be fully qualified with a table name, since the content provider joins the location & weather tables in the background
            // (both have an _id column). On the one hand, that's annoying.  On the other, you can search the weather table
            // using the location set by the user, which is only in the Location table. So the convenience is worth it.
            WeatherContract.WeatherEntry.TABLE_NAME + "." + WeatherContract.WeatherEntry._ID,
            WeatherContract.WeatherEntry.COLUMN_DATE,
            WeatherContract.WeatherEntry.COLUMN_SHORT_DESC,
            WeatherContract.WeatherEntry.COLUMN_MAX_TEMP,
            WeatherContract.WeatherEntry.COLUMN_MIN_TEMP,
            WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING,
            WeatherContract.WeatherEntry.COLUMN_WEATHER_ID,
            WeatherContract.LocationEntry.COLUMN_COORD_LAT,
            WeatherContract.LocationEntry.COLUMN_COORD_LONG
    };

    // These indices are tied to FORECAST_COLUMNS. If FORECAST_COLUMNS changes, these must change.
    static final int COL_WEATHER_ID = 0;
    static final int COL_WEATHER_DATE = 1;
    static final int COL_WEATHER_DESC = 2;
    static final int COL_WEATHER_MAX_TEMP = 3;
    static final int COL_WEATHER_MIN_TEMP = 4;
    static final int COL_LOCATION_SETTING = 5;
    static final int COL_WEATHER_CONDITION_ID = 6;
    static final int COL_COORD_LAT = 7;
    static final int COL_COORD_LONG = 8;


    /**
     * A callback interface that all activities containing this fragment must
     * implement. This mechanism allows activities to be notified of item
     * selections.
     */
    public interface Callback {
        /**
         * DetailFragmentCallback for when an item has been selected.
         */
        public void onItemSelected(Uri dateUri);
    }


    //Constructor
    public ForecastFragment() {
        //Empty constructor
    }

    //onCreate method for setting options menu, otherwise, onCreateView is sufficient
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Add this line in order for this fragment to handle menu events
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.forecastfragment_menu, menu);
    }

    // Handle action bar item clicks here. The action bar will automatically handle clicks on the Home/Up button, so long as you specify a parent activity in AndroidManifest.xml.
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_refresh){
            updateWeather();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_main, container, false);

        // The ArrayAdapter will take data from a source (like our dummy forecast) and use it to populate the ListView it's attached to.
        mForecastAdapter = new ForecastAdapter(getActivity(), null, 0);

        mListView = view.findViewById(R.id.listview_forecast);//Find our list view by id
        mListView.setAdapter(mForecastAdapter); // Set adapter for listView

        //======================================Set onItemClickListener for items that are being clicked===========================================//
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                // CursorAdapter returns a cursor at the correct position for getItem(), or null if it cannot seek to that position.
                Cursor cursor = (Cursor) parent.getItemAtPosition(position);
 /*
                if (cursor != null) {
                    String locationSetting = Utility.getPreferredLocation(getActivity());
                    long weatherDate = cursor.getLong(COL_WEATHER_DATE);
                    Uri weatherLocationWithDateUri = WeatherContract.WeatherEntry.buildWeatherLocationWithDate(locationSetting, weatherDate);
                    //===============Launch Detail Activity Using Intent ====================//
                    Intent intent = new Intent(getActivity(), DetailActivity.class)
                            .setData(weatherLocationWithDateUri); //Send this data in Intent Extra
                    Log.d(TAG, "onItemClick: buildWeatherLocationWithDate(locationSetting = " + locationSetting + ", weatherDate = " + weatherDate + "), uri = " + weatherLocationWithDateUri );
                    startActivity(intent);
                }
*/

                if (cursor != null) {
                    String locationSetting = Utility.getPreferredLocation(getActivity());
                    long weatherDate = cursor.getLong(COL_WEATHER_DATE);
                    Uri weatherLocationWithDateUri = WeatherContract.WeatherEntry.buildWeatherLocationWithDate(locationSetting, weatherDate);
                    Log.d(TAG, "onItemClick: buildWeatherLocationWithDate(locationSetting = " + locationSetting + ", weatherDate = " + weatherDate + "), uri = " + weatherLocationWithDateUri );
                    ((Callback) getActivity())
                            .onItemSelected(weatherLocationWithDateUri); // Using onItemSelected interface, propagate this uri
                }
                mPosition = position;

            }
        });

        // NOTE: In tablet, when we rotated the device, it lost the selected item position in the list. This is to restore it.
        // If there's instance state, mine it for useful information. The end-goal here is that the user never knows that turning their device sideways
        // does crazy lifecycle related things.  It should feel like some stuff stretched out, or magically appeared to take advantage of room,
        // but data or place in the app was never actually *lost*.
        if (savedInstanceState != null && savedInstanceState.containsKey(SELECTED_KEY)) {
            // The listView probably hasn't even been populated yet. Actually perform the swapout in onLoadFinished.
            mPosition = savedInstanceState.getInt(SELECTED_KEY);
        }

        mForecastAdapter.setUseTodayLayout(mUseTodayLayout);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //Loaders are created in onActivityCreated and it's bound to Activity not a fragment
        getLoaderManager().initLoader(FORECAST_LOADER, null, this);
    }

    // since we read the location when we create the loader, all we need to do is restart things
    void onLocationChanged( ) {
        updateWeather();
        getLoaderManager().restartLoader(FORECAST_LOADER, null, this);
    }

    //We took the code from Refresh button and created a method so we can use it in onStart method as well
    public void updateWeather(){
        //Create AsyncTask for fetching weather info
        FetchWeatherTask fetchWeatherTask = new FetchWeatherTask(getActivity());
        String location = Utility.getPreferredLocation(getActivity()); //Retrieve saved location from SharedPreferences
        fetchWeatherTask.execute(location); // Note: We can use City name and Postal code here. Peshawar Zip code = 25000 not working, 94043 = MountainView postal code works. why?? Maybe zip code is different than postal code
    }

    // When tablets rotate, the currently selected list item needs to be saved. When no item is selected, mPosition will be set to Listview.INVALID_POSITION, so check for that before storing.
    @Override
    public void onSaveInstanceState(Bundle outState) {

        if (mPosition != ListView.INVALID_POSITION) {
            outState.putInt(SELECTED_KEY, mPosition);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.i(TAG, "onStart: updateWeather()");
        updateWeather(); //If we comment this out, we need to press refresh button to update the data.
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i(TAG, "onStart: onLocationChanged()");
        onLocationChanged();
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int i, @Nullable Bundle bundle) {
        // This is called when a new Loader needs to be created.  This fragment only uses one loader, so we don't care about checking the id.
        // To only show current and future dates, filter the query to return weather only for dates after or including today.

        // Sort order:  Ascending, by date.
        String sortOrder = WeatherContract.WeatherEntry.COLUMN_DATE + " ASC";

        String locationSetting = Utility.getPreferredLocation(getActivity());
        Uri weatherForLocationUri = WeatherContract.WeatherEntry.buildWeatherLocationWithStartDate(locationSetting, System.currentTimeMillis());

        return new CursorLoader(
                getActivity(),
                weatherForLocationUri,
                FORECAST_COLUMNS,
                null,
                null,
                sortOrder);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor) {
        mForecastAdapter.swapCursor(cursor);
        if (mPosition != ListView.INVALID_POSITION) {
            // If we don't need to restart the loader, and there's a desired position to restore to, do so now.
            mListView.smoothScrollToPosition(mPosition);
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        mForecastAdapter.swapCursor(null);
    }

    public void setUseTodayLayout(boolean useTodayLayout) {
        mUseTodayLayout = useTodayLayout;
        if (mForecastAdapter != null) {
            mForecastAdapter.setUseTodayLayout(mUseTodayLayout);
        }
    }


}






















































