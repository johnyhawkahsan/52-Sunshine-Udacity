package com.johnyhawkdesigns.a52_sunshine_udacity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

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
public class ForecastFragment extends android.support.v4.app.Fragment {

    private static final String TAG = "ForecastFragment";
    public static String APIKey = "2a3d28af75a740af1e2614c2a02d26b2"; //My openweathermap api key

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

            updateWeather();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //We took the code from Refresh button and created a method so we can use it in onStart method as well
    private void updateWeather(){
        //Retrieve location stored in SharedPreferences
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String location = sharedPreferences.getString(getString(R.string.pref_location_key), getString(R.string.pref_location_default)); //The 2nd value is default value, if new value is not found

        //Create AsyncTask for fetching weather info
        FetchWeatherTask fetchWeatherTask = new FetchWeatherTask();
        fetchWeatherTask.execute(location); // Note: We can use City name and Postal code here. Peshawar Zip code = 25000 not working, 94043 = MountainView postal code works. why?? Maybe zip code is different than postal code

    }

    @Override
    public void onStart() {
        super.onStart();
        updateWeather();
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

    //===================================================================================================================================================//
    //=================================================Background Thread to GET data from OpenWeather.org================================================//
    //===================================================================================================================================================//

    public class FetchWeatherTask extends AsyncTask<String, Void, String[]> //params = string, progress = void, result = String[]
    {
        private final String TAG = FetchWeatherTask.class.getSimpleName();
        private String city; //I made this global variable to extract passed city info from params[0]

        /** Prepare the weather high/lows for presentation. Also see if it's unit is Fahrenheit, convert it mathematically to Fahrenheit from Metric  */
        private String formatHighLows(double high, double low, String unitType) {

            //If passed unit is of type Imperial, then convert it mathematically to Fahrenheit
            if (unitType.equals(getString(R.string.pref_units_imperial))){
                high = (high * 1.8) + 32;
                low = (low * 1.8) + 32;
            }
            //If unit type is not Metric and we have already checked for Imperial, then it's an exception that should never happen
            else if (!unitType.equals(getString(R.string.pref_units_metric))){
                Log.d(TAG, "Unit type not found: " + unitType);
            }

            // For presentation, assume the user doesn't care about tenths of a degree.
            long roundedHigh = Math.round(high);
            long roundedLow = Math.round(low);

            String highLowStr = roundedHigh + "/" + roundedLow;
            return highLowStr;
        }

        /**
         * Take the String representing the complete forecast in JSON Format and pull out the data we need to construct the Strings needed for the wireframes.
         *
         * Fortunately parsing is easy:  constructor takes the JSON string and converts it into an Object hierarchy for us.
         */
        private String[] getWeatherDataFromJson(String forecastJsonStr, int numDays)
                throws JSONException {

            // These are the names of the JSON objects that need to be extracted.
            final String OWM_LIST = "list";
            final String OWM_WEATHER = "weather";
            final String OWM_TEMPERATURE = "temp";
            final String OWM_MAX = "max";
            final String OWM_MIN = "min";
            final String OWM_DESCRIPTION = "main";

            JSONObject forecastJson = new JSONObject(forecastJsonStr);
            JSONArray weatherArray = forecastJson.getJSONArray(OWM_LIST);

            // OWM returns daily forecasts based upon the local time of the city that is being asked for, which means that we need to know the GMT offset to translate this data properly.
            // Since this data is also sent in-order and the first day is always the current day, we're going to take advantage of that to get a nice normalized UTC date for all of our weather.
            //Using the Gregorian Calendar Class instead of Time Class to get current date
            Calendar gc = new GregorianCalendar(); //Note: The object gc gets set to the current time at the time of its creation

            String[] resultStrs = new String[numDays]; //String of length numDays = 7

            // Data is fetched in Celsius by default. If user prefers to see in Fahrenheit, convert the values here.
            // We do this rather than fetching in Fahrenheit so that the user can change this option without us having to re-fetch the data once we start storing the values in a database.
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
            String unitType = sharedPreferences.getString( getString(R.string.pref_units_key), getString(R.string.pref_units_metric));

            // Loop through JSON weather array data to extract appropriate String values
            for(int i = 0; i < weatherArray.length(); i++) {
                // For now, using the format "Day, description, hi/low" for the app display
                String day;
                String description;
                String highAndLow;

                // Get the JSON object representing the day
                JSONObject dayForecast = weatherArray.getJSONObject(i); //starting from 0th item in the "list"
                Log.d(TAG, "getting dayForeCast of " + city + " city from weatherArray at position = " + i);

                //Converting the integer value returned by Calendar.DAY_OF_WEEK to a human-readable String
                day = gc.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.ENGLISH);

                //iterating to the next day
                gc.add(Calendar.DAY_OF_WEEK, 1); //to get an integer with correspond to the day of the week. For example: 7 corresponds to Saturday, 1 to Sunday, 2 to Monday and so on.

                // description is in a child array called "weather", which is 1 element long.
                JSONObject weatherObject = dayForecast.getJSONArray(OWM_WEATHER).getJSONObject(0);
                description = weatherObject.getString(OWM_DESCRIPTION); //get "description" from "weather"

                // Temperatures are in a child object called "temp".
                JSONObject temperatureObject = dayForecast.getJSONObject(OWM_TEMPERATURE);
                double high = temperatureObject.getDouble(OWM_MAX);
                double low = temperatureObject.getDouble(OWM_MIN);

                //Pass high and low temp data and also pass unitType, so if it's "Imperial" then convert it to fahrenhiet mathematically
                highAndLow = formatHighLows(high, low, unitType);
                resultStrs[i] = day + " - " + description + " - " + highAndLow;
            }

            //Loop through result string to get each item, so that we can print it to the logs.
            for (String s : resultStrs){
                Log.d(TAG, "getWeatherDataFromJson : Forecast entry for " + city +" : " + s);
            }

            return resultStrs;
        }


        @Override
        protected String[] doInBackground(String... params) { //Void means this method returns nothing, so no need to use postExecute() method

            //=======================================================Read Data from OpenWeather.org using API====================================================//

            // If there's no zip code, there's nothing to look up.  Verify size of params.
            if (params.length == 0){
                Log.d(TAG, "doInBackground params.length == 0");
                return null;
            }

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
                //String FORECAST_BASE_URL = "http://api.openweathermap.org/data/2.5/forecast/daily?q=Peshawar&mode=json&units=metric&cnt=7"; //Old url before Uri parsing, we used for testing
                String FORECAST_BASE_URL = "http://api.openweathermap.org/data/2.5/forecast/daily?";


                final String QUERY_PARAM = "q";
                final String FORMAT_PARAM = "mode";
                final String UNITS_PARAM = "units";
                final String DAYS_PARAM = "cnt";
                final String APPID_PARAM = "APPID";

                city = params[0];

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
                //LogExample Built URL =  http://api.openweathermap.org/data/2.5/forecast/daily?q=Peshawar&mode=json&units=metric&cnt=7&APPID=2a3d28af75a740af1e2614c2a02d26b2

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

            //----Parse this forecastJsonStr string data to Json and get String[]
            try{
                return getWeatherDataFromJson(forecastJsonStr, numDays);
            } catch (JSONException e){
                Log.e(TAG, e.getMessage(), e);
                e.printStackTrace();
            }

            // This will only happen if there was an error getting or parsing the forecast.
            return null;
        }


        @Override
        protected void onPostExecute(String[] result) {

            if (result != null){
                Log.d(TAG, "onPostExecute: result != null");
                mForecastAdapter.clear();
                for (String dayForecastStr : result){
                    mForecastAdapter.add(dayForecastStr);
                    Log.d(TAG, "onPostExecute: adding dayForecastStr to Adapter = " + dayForecastStr);
                }
                // New data is back from the server.  Hooray!
                mForecastAdapter.notifyDataSetChanged();
            }

        }
    }


    //===================================================================================================================================================//
    //=================================================Background Thread to GET data from OpenWeather.org================================================//
    //===================================================================================================================================================//


}






















































