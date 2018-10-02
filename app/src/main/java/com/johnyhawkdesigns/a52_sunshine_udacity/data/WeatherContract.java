package com.johnyhawkdesigns.a52_sunshine_udacity.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

public class WeatherContract {

    // The "Content authority" is a name for the entire content provider, similar to the relationship between a domain name and its website. A convenient string to use for the content authority is the package name for the app, which is guaranteed to be unique on the device.
    public static final String CONTENT_AUTHORITY = "com.johnyhawkdesigns.a52_sunshine_udacity";
    // Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact the content provider.
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    // Possible paths (appended to base content URI for possible URI's) For instance, content://com.johnyhawkdesigns.a52_sunshine_udacity/weather/ is a valid path for looking at weather data.
    // content://com.johnyhawkdesigns.a52_sunshine_udacity/givemeroot/ will fail, as the ContentProvider hasn't been given any information on what to do with "givemeroot".
    public static final String PATH_WEATHER = "weather";
    public static final String PATH_LOCATION = "location";





    //Note: I replaced the UDACITY method with this one: https://stackoverflow.com/questions/39788033/time-getjulianday-deprecated-which-code-would-get-the-work-done-instead?noredirect=1&lq=1
    // To make it easy to query for the exact date, we normalize all dates that go into the database to the start of the the Julian day at UTC.
    public static long normalizeDate(long startDate){
        // normalize the start date to the beginning of the (UTC) day
        GregorianCalendar date = (GregorianCalendar) GregorianCalendar.getInstance(TimeZone.getTimeZone("UTC"));
        date.setTime(new Date(startDate));
        date.set(Calendar.HOUR_OF_DAY, 0);
        date.set(Calendar.MINUTE, 0);
        date.set(Calendar.SECOND, 0);
        date.set(Calendar.MILLISECOND, 0);

        //transform your calendar to a long in the way you prefer
        return date.getTimeInMillis();
    }






    /*===================================================Inner class that defines the contents of the location table========================================== */
    public static final class LocationEntry implements BaseColumns{

        //Build content uri to get path location
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_LOCATION).build();                                   /** content://com.johnyhawkdesigns.a52_sunshine_udacity/location/*/
        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_LOCATION;         // vnd.android.cursor.dir/com.johnyhawkdesigns.a52_sunshine_udacity/location
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_LOCATION;   // vnd.android.cursor.item/com.johnyhawkdesigns.a52_sunshine_udacity/location



        public static final String TABLE_NAME = "location";

        // The location setting string is what will be sent to openweathermap as the location query.
        public static final String COLUMN_LOCATION_SETTING = "location_setting";

        // Human readable location string, provided by the API.  Because for styling, "Mountain View" is more recognizable than 94043.
        public static final String COLUMN_CITY_NAME = "city_name";

        // In order to uniquely pinpoint the location on the map when we launch the map intent, we store the latitude and longitude as returned by openweathermap.
        public static final String COLUMN_COORD_LAT = "coord_lat";
        public static final String COLUMN_COORD_LONG = "coord_long";

        /**
         * This method will build location Uri when we pass id. This method is built within WeatherContract's inline class LocationEntry
         * @param id long = id
         * @return Uri = ContentUris.withAppendedId(CONTENT_URI, id)
         */
        public static Uri buildLocationUri(long id){
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }









    /*===================================================Inner class that defines the contents of the weather table======================================== */

    public static final class WeatherEntry implements BaseColumns {

        //Build content uri to get weather entry
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_WEATHER).build();                                   // content://com.johnyhawkdesigns.a52_sunshine_udacity/weather/
        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_WEATHER;         // vnd.android.cursor.dir/com.johnyhawkdesigns.a52_sunshine_udacity/weather
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_WEATHER;   // vnd.android.cursor.item/com.johnyhawkdesigns.a52_sunshine_udacity/weather

        public static final String TABLE_NAME = "weather";

        // Column with the foreign key into the location table.
        public static final String COLUMN_LOC_KEY = "location_id";

        // Date, stored as Text with format yyyy-MMM-dd
        public static final String COLUMN_DATETEXT = "date"; //We're using Date text for now, I think later it will be changed
        // Date, stored as long in milliseconds since the epoch
        public static final String COLUMN_DATE = "date";

        // Weather id as returned by API, to identify the icon to be used
        public static final String COLUMN_WEATHER_ID = "weather_id";

        // Short description and long description of the weather, as provided by API. e.g "clear" vs "sky is clear".
        public static final String COLUMN_SHORT_DESC = "short_desc";

        // Min and max temperatures for the day (stored as floats)
        public static final String COLUMN_MIN_TEMP = "min";
        public static final String COLUMN_MAX_TEMP = "max";

        // Humidity is stored as a float representing percentage
        public static final String COLUMN_HUMIDITY = "humidity";

        // Humidity is stored as a float representing percentage
        public static final String COLUMN_PRESSURE = "pressure";

        // Windspeed is stored as a float representing windspeed  mph
        public static final String COLUMN_WIND_SPEED = "wind";

        // Degrees are meteorological degrees (e.g, 0 is north, 180 is south).  Stored as floats.
        public static final String COLUMN_DEGREES = "degrees";

        /**
         * This method will build weather Uri when we pass id. This method is built within WeatherContract's inline class LocationEntry
         * @param id long = id
         * @return Uri = ContentUris.withAppendedId(CONTENT_URI, id)
         */
        public static Uri buildWeatherUri(long id){
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildWeatherLocation(String locationSetting){
            return CONTENT_URI.buildUpon().appendPath(locationSetting).build();
        }

        public static Uri buildWeatherLocationWithStartDate(String locationSetting, String startDate){
            //long normalizedDate = normalizeDate(startDate);
            return CONTENT_URI.buildUpon().appendPath(locationSetting).appendQueryParameter(COLUMN_DATETEXT, startDate).build();
        }

        public static Uri buildWeatherLocationWithDate(String locationSetting, String date ){
            return CONTENT_URI.buildUpon().appendPath(locationSetting).appendPath(date).build();
        }

        public static String getLocationSettingFromUri(Uri uri){
            return uri.getPathSegments().get(1);
        }

        public static long getDateFromUri(Uri uri){
            return  Long.parseLong(uri.getPathSegments().get(2));
        }

        public static long getStartDateFromUri(Uri uri){
            String dateString = uri.getQueryParameter(COLUMN_DATE);
            if (null != dateString && dateString.length() >0 )
                return Long.parseLong(dateString);
            else
                return 0;
        }


    }

}
