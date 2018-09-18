package com.johnyhawkdesigns.a52_sunshine_udacity;

import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;


/**
 * A placeholder fragment containing a simple view.
 */
public class ForecastFragment extends android.support.v4.app.Fragment {

    private static final String TAG = "ForecastFragment";
    public static String APIKey = "2a3d28af75a740af1e2614c2a02d26b2";

    //Creating our Adapter to collect our data and display in our ListView
    private ArrayAdapter<String> mForecastAdapter;

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

            //Create AsyncTask for fetching weather info
            FetchWeatherTask fetchWeatherTask = new FetchWeatherTask();
            fetchWeatherTask.execute("25000"); // Peshawar Postal code = 25000
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

        ArrayList<String> weekForecast = new ArrayList<>(Arrays.asList(data)); //Add dummy data to our ArrayList

        // Now that we have some dummy forecast data, create an ArrayAdapter.
        // The ArrayAdapter will take data from a source (like our dummy forecast) and use it to populate the ListView it's attached to.
        mForecastAdapter = new ArrayAdapter<>(
          getActivity(), // The current context (this activity)
          R.layout.list_item_forecast, // The name of the layout ID.
          R.id.list_item_forecast_textview, // The ID of the textview to populate.
          weekForecast //Our ArrayList fake data
        );

        ListView listView = view.findViewById(R.id.listview_forecast);//Find our list view by id
        listView.setAdapter(mForecastAdapter); // Set adapter for listView



        return view;
    }

    //===================================================================================================================================================//
    //=================================================Background Thread to GET data from OpenWeather.org================================================//
    //===================================================================================================================================================//

    public class FetchWeatherTask extends AsyncTask<String, Void, String>{

        private final String TAG = FetchWeatherTask.class.getSimpleName();


        @Override
        protected String doInBackground(String... params) {

            //=======================================================Read Data from OpenWeather.org using API====================================================//

            // These two need to be declared outside the try/catch so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader bufferedReader = null;

            // Will contain the raw JSON response as a string.
            String forecastJsonStr = null;

            String format = "json";
            String units = "metric";
            int numDays = 7;

            try{
                // Construct the URL for the OpenWeatherMap query. Possible parameters are avaiable at OWM's forecast API page, at
                // http://openweathermap.org/API#forecast
                String FORECAST_BASE_URL = "http://api.openweathermap.org/data/2.5/forecast/daily?q=Peshawar&mode=json&units=metric&cnt=7";

                final String QUERY_PARAM = "q";
                final String FORMAT_PARAM = "mode";
                final String UNITS_PARAM = "units";
                final String DAYS_PARAM = "cnt";
                final String APPID_PARAM = "APPID";

                Uri builtUri = Uri.parse(FORECAST_BASE_URL).buildUpon()
                        .appendQueryParameter(QUERY_PARAM, params[0]) //We have to manually add that
                        .appendQueryParameter(FORMAT_PARAM, format)
                        .appendQueryParameter(UNITS_PARAM, units)
                        .appendQueryParameter(DAYS_PARAM, Integer.toString(numDays))
                        .appendQueryParameter(APPID_PARAM, APIKey )
                        .build();

                //URL url = new URL(FORECAST_BASE_URL.concat(APIKey)); //Old concatenation method
                URL url = new URL(builtUri.toString()); //Make URL out of our Uri

                Log.v(TAG, "Built URL =  " + url); //Show in logs how concatenation with base ulr worked

                // Create the request to OpenWeatherMap, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection(); // open url connection with our url
                urlConnection.setRequestMethod("GET"); // set method as get
                urlConnection.connect(); // setup connection

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer stringBuffer = new StringBuffer();
                if (inputStream == null){
                    //Nothing to do
                    Log.e(TAG, "onCreateView: inputStream == null" );
                    return null;
                }

                bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                //While loop to iterate through all the lines
                while ((line = bufferedReader.readLine()) != null){
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing). But it does make debugging a *lot* easier if you print out the completed buffer for debugging.
                    stringBuffer.append(line + "\n"); //Start new line after reading each line
                }

                if (stringBuffer.length() == 0){
                    // Stream was empty.  No point in parsing.
                    Log.e(TAG, "onCreateView: stringBuffer.length() == 0" );
                    return null;
                }

                //Append all read data from stringBuffer to forecastJsonStr
                forecastJsonStr = stringBuffer.toString();
                Log.d(TAG, "forecastJsonStr: " + forecastJsonStr); //To check output

            }

            catch (IOException e){
                Log.e(TAG, "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attemping to parse it.
                return null;
            }

            finally {
                if (urlConnection != null){
                    urlConnection.disconnect(); //disconnect connection after reading the data
                }
                if (bufferedReader != null){
                    try {
                        bufferedReader.close();
                    } catch (final IOException e) {
                        Log.e(TAG, "Error closing stream", e);
                    }
                }
            }

            return null;
        }

    }


    //===================================================================================================================================================//
    //=================================================Background Thread to GET data from OpenWeather.org================================================//
    //===================================================================================================================================================//


}






















































