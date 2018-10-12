package com.johnyhawkdesigns.a52_sunshine_udacity;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.text.format.Time;
import android.util.Log;

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
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.Vector;


//===================================================================================================================================================//
//=================================================Background Thread to GET data from OpenWeather.org================================================//
//===================================================================================================================================================//

public class FetchWeatherTask extends AsyncTask<String, Void, Void> //params = string, progress = void, result = void, Now result is void, so there would be no onPostExecute
{
    private final String TAG = FetchWeatherTask.class.getSimpleName();

    private final Context mContext;
    private String city; //I made this global variable to extract passed city info from params[0]

    //Constructor
    public FetchWeatherTask(Context context) {
        mContext = context;
    }

    private boolean DEBUG = true;

    /**
     * Helper method to handle insertion of a new location in the weather database.
     *
     * @param locationSetting The location string used to request updates from the server.
     * @param cityName        A human-readable city name, e.g "Mountain View"
     * @param lat             the latitude of the city
     * @param lon             the longitude of the city
     * @return the row ID of the added location.
     */
    long addLocation(String locationSetting, String cityName, double lat, double lon) {
        long locationId;

        // First, check if the location with this city name exists in the db
        Cursor locationCursor = mContext.getContentResolver().query(
                WeatherContract.LocationEntry.CONTENT_URI,
                new String[]{WeatherContract.LocationEntry._ID},
                WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING + " = ?",
                new String[]{locationSetting},
                null);

        if (locationCursor.moveToFirst()) {
            int locationIdIndex = locationCursor.getColumnIndex(WeatherContract.LocationEntry._ID);
            locationId = locationCursor.getLong(locationIdIndex);
        } else {
            // Now that the content provider is set up, inserting rows of data is pretty simple. First create a ContentValues object to hold the data you want to insert.
            ContentValues locationValues = new ContentValues();

            // Then add the data, along with the corresponding name of the data type, so the content provider knows what kind of value is being inserted.
            locationValues.put(WeatherContract.LocationEntry.COLUMN_CITY_NAME, cityName);
            locationValues.put(WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING, locationSetting);
            locationValues.put(WeatherContract.LocationEntry.COLUMN_COORD_LAT, lat);
            locationValues.put(WeatherContract.LocationEntry.COLUMN_COORD_LONG, lon);

            // Finally, insert location data into the database.
            Uri insertedUri = mContext.getContentResolver().insert(
                    WeatherContract.LocationEntry.CONTENT_URI,
                    locationValues
            );

            // The resulting URI contains the ID for the row.  Extract the locationId from the Uri.
            locationId = ContentUris.parseId(insertedUri);
        }

        locationCursor.close();
        // Wait, that worked?  Yes!
        return locationId;
    }


    /**
     * Take the String representing the complete forecast in JSON Format and pull out the data we need to construct the Strings needed for the wireframes.
     * Fortunately parsing is easy:  constructor takes the JSON string and converts it into an Object hierarchy for us.
     */
    private void getWeatherDataFromJson(String forecastJsonStr,
                                        String locationSetting)
            throws JSONException {

        // Now we have a String representing the complete forecast in JSON Format.Fortunately parsing is easy:  constructor takes the JSON string and converts it into an Object hierarchy for us.
        // These are the names of the JSON objects that need to be extracted.

        // Location information
        final String OWM_CITY = "city";
        final String OWM_CITY_NAME = "name";
        final String OWM_COORD = "coord";

        // Location coordinate
        final String OWM_LATITUDE = "lat";
        final String OWM_LONGITUDE = "lon";

        // Weather information.  Each day's forecast info is an element of the "list" array.
        final String OWM_LIST = "list";

        final String OWM_PRESSURE = "pressure";
        final String OWM_HUMIDITY = "humidity";
        final String OWM_WINDSPEED = "speed";
        final String OWM_WIND_DIRECTION = "deg";

        // All temperatures are children of the "temp" object.
        final String OWM_TEMPERATURE = "temp";
        final String OWM_MAX = "max";
        final String OWM_MIN = "min";

        final String OWM_WEATHER = "weather";
        final String OWM_DESCRIPTION = "main";
        final String OWM_WEATHER_ID = "id";

        try {

            JSONObject forecastJson = new JSONObject(forecastJsonStr); // forecastJsonStr is data provided to this getWeatherDataFromJson() method
            JSONArray weatherArray = forecastJson.getJSONArray(OWM_LIST);

            JSONObject cityJson = forecastJson.getJSONObject(OWM_CITY);
            String cityName = cityJson.getString(OWM_CITY_NAME);

            JSONObject cityCoord = cityJson.getJSONObject(OWM_COORD);
            double cityLatitude = cityCoord.getDouble(OWM_LATITUDE);
            double cityLongitude = cityCoord.getDouble(OWM_LONGITUDE);

            Log.d(TAG, "cityName = " + cityName + ", with coord: " + cityLatitude + " " + cityLongitude);

            long locationId = addLocation(locationSetting, cityName, cityLatitude, cityLongitude);

            // Insert the new weather information into the database
            Vector<ContentValues> cVVector = new Vector<ContentValues>(weatherArray.length());

            // OWM returns daily forecasts based upon the local time of the city that is being asked for, which means that we need to know the GMT offset to translate this data properly.
            // Since this data is also sent in-order and the first day is always the current day, we're going to take advantage of that to get a nice normalized UTC date for all of our weather.
            // Using the Gregorian Calendar Class instead of Time Class to get current date
            // Calendar gc = new GregorianCalendar(); //Note: The object gc gets set to the current time at the time of its creation

            Time dayTime = new Time();
            dayTime.setToNow();

            // we start at the day returned by local time. Otherwise this is a mess.
            int julianStartDay = Time.getJulianDay(System.currentTimeMillis(), dayTime.gmtoff);
            Log.d(TAG, "julianStartDay = " + julianStartDay + ", dayTime.gmtoff = " + dayTime.gmtoff);

            // now we work exclusively in UTC
            dayTime = new Time();

            // Loop through JSON weather array data to extract appropriate String values
            for (int i = 0; i < weatherArray.length(); i++) {
                // These are the values that will be collected.
                String dateTime;
                double pressure;
                int humidity;
                double windSpeed;
                double windDirection;

                double high;
                double low;

                String description;
                int weatherId;

                // Get the JSON object representing the day
                JSONObject dayForecast = weatherArray.getJSONObject(i);

                //Converting the integer value returned by Calendar.DAY_OF_WEEK to a human-readable String
                //dateTime = gc.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.ENGLISH);
                //iterating to the next day
                //gc.add(Calendar.DAY_OF_WEEK, 1); //to get an integer with correspond to the day of the week. For example: 7 corresponds to Saturday, 1 to Sunday, 2 to Monday and so on.

                dateTime = String.valueOf(dayTime.setJulianDay(julianStartDay + i));
                Log.d(TAG, "getWeatherDataFromJson: dateTime = " + dateTime + ", dayTime = " + dayTime + ", julianStartDay = " + julianStartDay);

                pressure = dayForecast.getDouble(OWM_PRESSURE);
                humidity = dayForecast.getInt(OWM_HUMIDITY);
                windSpeed = dayForecast.getDouble(OWM_WINDSPEED);
                windDirection = dayForecast.getDouble(OWM_WIND_DIRECTION);

                // Description is in a child array called "weather", which is 1 element long. That element also contains a weather code.
                JSONObject weatherObject = dayForecast.getJSONArray(OWM_WEATHER).getJSONObject(0);
                description = weatherObject.getString(OWM_DESCRIPTION);
                weatherId = weatherObject.getInt(OWM_WEATHER_ID);

                // Temperatures are in a child object called "temp".  Try not to name variables "temp" when working with temperature. It confuses everybody.
                JSONObject temperatureObject = dayForecast.getJSONObject(OWM_TEMPERATURE);
                high = temperatureObject.getDouble(OWM_MAX);
                low = temperatureObject.getDouble(OWM_MIN);

                ContentValues weatherValues = new ContentValues();

                weatherValues.put(WeatherContract.WeatherEntry.COLUMN_LOC_KEY, locationId);
                weatherValues.put(WeatherContract.WeatherEntry.COLUMN_DATE, dateTime);
                weatherValues.put(WeatherContract.WeatherEntry.COLUMN_HUMIDITY, humidity);
                weatherValues.put(WeatherContract.WeatherEntry.COLUMN_PRESSURE, pressure);
                weatherValues.put(WeatherContract.WeatherEntry.COLUMN_WIND_SPEED, windSpeed);
                weatherValues.put(WeatherContract.WeatherEntry.COLUMN_DEGREES, windDirection);
                weatherValues.put(WeatherContract.WeatherEntry.COLUMN_MAX_TEMP, high);
                weatherValues.put(WeatherContract.WeatherEntry.COLUMN_MIN_TEMP, low);
                weatherValues.put(WeatherContract.WeatherEntry.COLUMN_SHORT_DESC, description);
                weatherValues.put(WeatherContract.WeatherEntry.COLUMN_WEATHER_ID, weatherId);

                cVVector.add(weatherValues);
            }

            int inserted = 0;

            // add to database
            if (cVVector.size() > 0) {
                ContentValues[] cvArray = new ContentValues[cVVector.size()];
                cVVector.toArray(cvArray); //Convert cVVector to an array for bulk insert to work
                inserted = mContext.getContentResolver().bulkInsert(WeatherContract.WeatherEntry.CONTENT_URI, cvArray);
            }

            Log.d(TAG, "FetchWeatherTask Complete. " + inserted + " Inserted");

/*
            //This is not used for now, because below code doesn't exist in FetchWeatherTask

            // Sort order:  Ascending, by date.
            String sortOrder = WeatherContract.WeatherEntry.COLUMN_DATE + " ASC";
            Uri weatherForLocationUri = WeatherContract.WeatherEntry.buildWeatherLocationWithStartDate(locationSetting, String.valueOf(System.currentTimeMillis()));

            // Students: Uncomment the next lines to display what what you stored in the bulkInsert
            Cursor cur = mContext.getContentResolver().query(weatherForLocationUri, null, null, null, sortOrder);

            cVVector = new Vector<ContentValues>(cur.getCount());
            if (cur.moveToFirst()) {
                do {
                    ContentValues cv = new ContentValues();
                    DatabaseUtils.cursorRowToContentValues(cur, cv);
                    cVVector.add(cv);
                } while (cur.moveToNext());
            }

            Log.d(TAG, "FetchWeatherTask Complete. " + cVVector.size() + " Inserted");

            String[] resultStrs = convertContentValuesToUXFormat(cVVector);
*/

        } catch (JSONException e) {
            Log.e(TAG, e.getMessage(), e);
            e.printStackTrace();
        }
    }



    /* The date/time conversion code is going to be moved outside the asynctask later,
     * so for convenience we're breaking it out into its own method now.
     */
    private String getReadableDateString(long time) {
        // Because the API returns a unix timestamp (measured in seconds)
        // it must be converted to milliseconds in order to be converted to valid date.
        Date date = new Date(time);
        SimpleDateFormat format = new SimpleDateFormat("E, MMM d");
        return format.format(date).toString();
    }

    /**
     * Prepare the weather high/lows for presentation. Also see if it's unit is Fahrenheit, convert it mathematically to Fahrenheit from Metric
     */
    private String formatHighLows(double high, double low) {
        // Data is fetched in Celsius by default. If user prefers to see in Fahrenheit, convert the values here.
        // We do this rather than fetching in Fahrenheit so that the user can change this option without us having to re-fetch the data once we start storing the values in a database.
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        String unitType = sharedPreferences.getString(mContext.getString(R.string.pref_units_key), mContext.getString(R.string.pref_units_metric));

        //If passed unit is of type Imperial, then convert it mathematically to Fahrenheit
        if (unitType.equals(mContext.getString(R.string.pref_units_imperial))) {
            high = (high * 1.8) + 32;
            low = (low * 1.8) + 32;
        }
        //If unit type is not Metric and we have already checked for Imperial, then it's an exception that should never happen
        else if (!unitType.equals(mContext.getString(R.string.pref_units_metric))) {
            Log.d(TAG, "Unit type not found: " + unitType);
        }

        // For presentation, assume the user doesn't care about tenths of a degree.
        long roundedHigh = Math.round(high);
        long roundedLow = Math.round(low);

        String highLowStr = roundedHigh + "/" + roundedLow;
        return highLowStr;
    }



    /*
        Students: This code will allow the FetchWeatherTask to continue to return the strings that
        the UX expects so that we can continue to test the application even once we begin using
        the database.
     */
    String[] convertContentValuesToUXFormat(Vector<ContentValues> cvv) {
        // return strings to keep UI functional for now
        String[] resultStrs = new String[cvv.size()];
        for (int i = 0; i < cvv.size(); i++) {
            ContentValues weatherValues = cvv.elementAt(i);
            String highAndLow = formatHighLows(
                    weatherValues.getAsDouble(WeatherContract.WeatherEntry.COLUMN_MAX_TEMP),
                    weatherValues.getAsDouble(WeatherContract.WeatherEntry.COLUMN_MIN_TEMP));
            resultStrs[i] = getReadableDateString(
                    weatherValues.getAsLong(WeatherContract.WeatherEntry.COLUMN_DATE)) +
                    " - " + weatherValues.getAsString(WeatherContract.WeatherEntry.COLUMN_SHORT_DESC) +
                    " - " + highAndLow;
        }
        return resultStrs;
    }




    @Override
    protected Void doInBackground(String... params) { //Void means this method returns nothing, so no need to use postExecute() method

        //=======================================================Read Data from OpenWeather.org using API====================================================//

        // If there's no zip code, there's nothing to look up.  Verify size of params.
        if (params.length == 0) {
            Log.d(TAG, "doInBackground params.length == 0");
            return null;
        }

        String locationQuery = params[0]; // I also assigned params[0] to global variable city

        // These two need to be declared outside the try/catch so that they can be closed in the finally block.
        HttpURLConnection urlConnection = null;
        BufferedReader bufferedReader = null;

        // Will contain the raw JSON response as a string.
        String forecastJsonStr = null;

        String format = "json";
        String units = "metric";
        int numDays = 14;

        try {
            // Construct the URL for the OpenWeatherMap query. Possible parameters are avaiable at OWM's forecast API page, at
            // http://openweathermap.org/API#forecast
            // String FORECAST_BASE_URL = "http://api.openweathermap.org/data/2.5/forecast/daily?q=Peshawar&mode=json&units=metric&cnt=7"; //Old url before Uri parsing, we used for testing
            String FORECAST_BASE_URL = "http://api.openweathermap.org/data/2.5/forecast/daily?";

            final String QUERY_PARAM = "q";
            final String FORMAT_PARAM = "mode";
            final String UNITS_PARAM = "units";
            final String DAYS_PARAM = "cnt";
            final String APPID_PARAM = "APPID";

            city = params[0];


            Uri builtUri = Uri.parse(FORECAST_BASE_URL).buildUpon()
                    .appendQueryParameter(QUERY_PARAM, locationQuery) //We have to manually add that
                    .appendQueryParameter(FORMAT_PARAM, format)
                    .appendQueryParameter(UNITS_PARAM, units)
                    .appendQueryParameter(DAYS_PARAM, Integer.toString(numDays))
                    .appendQueryParameter(APPID_PARAM, MainActivity.APIKey)
                    .build();

            //URL url = new URL(FORECAST_BASE_URL.concat(APIKey)); //Old concatenation method
            URL url = new URL(builtUri.toString()); //Make URL out of our Uri

            Log.d(TAG, "Built URL =  " + url); //Show in logs how concatenation with base ulr worked
            //LogExample Built URL =  http://api.openweathermap.org/data/2.5/forecast/daily?q=Peshawar&mode=json&units=metric&cnt=7&APPID=2a3d28af75a740af1e2614c2a02d26b2

            // Create the request to OpenWeatherMap, and open the connection
            urlConnection = (HttpURLConnection) url.openConnection(); // open url connection with our url
            urlConnection.setRequestMethod("GET"); // set method as get
            urlConnection.connect(); // setup connection

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer stringBuffer = new StringBuffer();
            if (inputStream == null) {
                //Nothing to do
                Log.e(TAG, "onCreateView: inputStream == null");
                return null;
            }

            bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            //While loop to iterate through all the lines
            while ((line = bufferedReader.readLine()) != null) {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing). But it does make debugging a *lot* easier if you print out the completed buffer for debugging.
                stringBuffer.append(line + "\n"); //Start new line after reading each line
            }

            if (stringBuffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                Log.e(TAG, "onCreateView: stringBuffer.length() == 0");
                return null;
            }

            //Append all read data from stringBuffer to forecastJsonStr
            forecastJsonStr = stringBuffer.toString();
            Log.d(TAG, "forecastJsonStr: " + forecastJsonStr); //To check output

        } catch (IOException e) {
            Log.e(TAG, "Error ", e);
            // If the code didn't successfully get the weather data, there's no point in attemping to parse it.
            return null;
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect(); //disconnect connection after reading the data
            }
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (final IOException e) {
                    Log.e(TAG, "Error closing stream", e);
                }
            }
        }

        //----Parse this forecastJsonStr string data to Json and get String[]
        try {
            getWeatherDataFromJson(forecastJsonStr, city); // I used city instead of locationQuery. both are the same things.
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage(), e);
            e.printStackTrace();
        }

        // This will only happen if there was an error getting or parsing the forecast.
        return null;
    }


/*
    //After
    @Override
    protected void onPostExecute(String[] result) {

        if (result != null) {
            Log.d(TAG, "onPostExecute: result != null");
            mForecastAdapter.clear();
            for (String dayForecastStr : result) {
                mForecastAdapter.add(dayForecastStr);
                Log.d(TAG, "onPostExecute: adding dayForecastStr to Adapter = " + dayForecastStr);
            }
            // New data is back from the server.  Hooray!
            mForecastAdapter.notifyDataSetChanged();
        }
    }*/


}


//===================================================================================================================================================//
//=================================================Background Thread to GET data from OpenWeather.org================================================//
//===================================================================================================================================================//


