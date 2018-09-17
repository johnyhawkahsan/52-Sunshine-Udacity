package com.johnyhawkdesigns.a52_sunshine_udacity;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
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
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class PlaceHolderFragment extends android.support.v4.app.Fragment {

    private static final String TAG = "PlaceHolderFragment";

    //Creating our Adapter to collect our data and display in our ListView
    private ArrayAdapter<String> mForecastAdapter;

    //Constructor
    public PlaceHolderFragment() {
        //Empty constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

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


        //===================================================================================================================================================//
        //=======================================================Read Data from OpenWeather.org using API====================================================//
        //===================================================================================================================================================//

        // These two need to be declared outside the try/catch so that they can be closed in the finally block.
        HttpURLConnection urlConnection = null;
        BufferedReader bufferedReader = null;

        // Will contain the raw JSON response as a string.
        String forecastJsonStr = null;

        try{
            // Construct the URL for the OpenWeatherMap query. Possible parameters are avaiable at OWM's forecast API page, at
            // http://openweathermap.org/API#forecast
            String baseUrl = "http://api.openweathermap.org/data/2.5/forecast/daily?q=Peshawar&mode=json&units=metric&cnt=7";
            String apiKey = "&APPID=" + "2a3d28af75a740af1e2614c2a02d26b2";
            //String apiKey = "&APPID=" + BuildConfig.OPEN_WEATHER_MAP_API_KEY;

            URL url = new URL(baseUrl.concat(apiKey)); //Create url from base url concatenating api key

            Log.d(TAG, "onCreateView: url =  " + url); //Show in logs how concatenation with base ulr worked

            // Create the request to OpenWeatherMap, and open the connection
            urlConnection = (HttpURLConnection) url.openConnection(); // open url connection with our url
            urlConnection.setRequestMethod("GET"); // set method as get
            urlConnection.connect(); // setup connection

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            if (inputStream == null){
                //Nothing to do
                Log.e(TAG, "onCreateView: inputStream == null" );
                return null;
            }

            StringBuffer stringBuffer = new StringBuffer();
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

        }

        catch (IOException e){
            Log.e("PlaceholderFragment", "Error ", e);
            // If the code didn't successfully get the weather data, there's no point in attemping
            // to parse it.
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
                    Log.e("PlaceholderFragment", "Error closing stream", e);
                }
            }
        }

        //===================================================================================================================================================//
        //=====================================================//Read Data from OpenWeather.org using API====================================================//
        //===================================================================================================================================================//








        return view;
    }
}
