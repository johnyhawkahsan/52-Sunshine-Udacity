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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;


/**
 * A placeholder fragment containing a simple view.
 */
public class ForecastFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = ForecastFragment.class.getSimpleName();

    //Creating our Adapter to collect our data and display in our ListView
    private ArrayAdapter<String> mForecastAdapter;

    private String mLocation;
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

        // Create some dummy data for the ListView.  Here's a sample weekly forecast
        String[] data = {
                "Mon 6/23 - Sunny - 31/17",
                "Tue 6/24 - Foggy - 21/8",
                "Wed 6/25 - Cloudy - 22/17",
                "Thurs 6/26 - Rainy - 18/11",
                "Fri 6/27 - Foggy - 21/10",
                "Sat 6/28 - TRAPPED IN WEATHERSTATION - 23/18",
                "Sun 6/29 - Sunny - 20/7"
        };
        ArrayList<String> dummyWeekForecast = new ArrayList<>(Arrays.asList(data)); //Add dummy data to our ArrayList


        // The ArrayAdapter will take data from a source (like our dummy forecast) and use it to populate the ListView it's attached to.
        mForecastAdapter = new ArrayAdapter<>(
                getActivity(), // The current context (this activity)
                R.layout.list_item_forecast, // The name of the layout ID.
                R.id.list_item_forecast_textview, // The ID of the textview to populate.
                dummyWeekForecast //Our ArrayList fake data NOTE: If want to remove Fake data, simply replace it with new ArrayList<> to create an empty arrayList, real data will be added from onStart or Refresh button by updateWeather method
        );

        ListView listView = view.findViewById(R.id.listview_forecast);//Find our list view by id
        listView.setAdapter(mForecastAdapter); // Set adapter for listView

        //============Set onItemClickListener for items that are being clicked
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //String forecast = (String) parent.getItemAtPosition(position);    //Both do the same work
                String forecast = mForecastAdapter.getItem(position);               //Above line is also correct
                Toast.makeText(getActivity(), "Position = " + position + ", parent.getItemAtPosition = " + forecast, Toast.LENGTH_LONG).show();
                Log.d(TAG, "Position = " + position + ", parent.getItemAtPosition = " + parent.getItemAtPosition(position));

                //===============Launch Detail Activity Using Intent ====================//
                Intent intent = new Intent(getActivity(), DetailActivity.class);
                intent.putExtra(Intent.EXTRA_TEXT, forecast);     //Udacity video shows this which is also a KeyValue Pair
                startActivity(intent);

            }
        });

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //Loaders are created in onActivityCreated and it's bound to Activity not a fragment
        getLoaderManager().initLoader(FORECAST_LOADER, null, this);

    }

    //We took the code from Refresh button and created a method so we can use it in onStart method as well
    private void updateWeather(){
        //Retrieve location stored in SharedPreferences
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String location = sharedPreferences.getString(getString(R.string.pref_location_key), getString(R.string.pref_location_default)); //The 2nd value is default value, if new value is not found

        //Create AsyncTask for fetching weather info
        FetchWeatherTask fetchWeatherTask = new FetchWeatherTask(getActivity(), mForecastAdapter);
        fetchWeatherTask.execute(location); // Note: We can use City name and Postal code here. Peshawar Zip code = 25000 not working, 94043 = MountainView postal code works. why?? Maybe zip code is different than postal code

    }

    @Override
    public void onStart() {
        super.onStart();
        updateWeather();
    }



    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int i, @Nullable Bundle bundle) {
        String locationSetting = Utility.getPreferredLocation(getActivity());

        // Sort order:  Ascending, by date.
        String sortOrder = WeatherContract.WeatherEntry.COLUMN_DATE + " ASC";
        Uri weatherForLocationUri = WeatherContract.WeatherEntry.buildWeatherLocationWithStartDate(locationSetting, String.valueOf(System.currentTimeMillis()));

        return new CursorLoader(getActivity(),
                weatherForLocationUri,
                FORECAST_COLUMNS,
                null,
                null,
                sortOrder);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor) {
        //mForecastAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        //mForecastAdapter.swapCursor(null);
    }
}






















































