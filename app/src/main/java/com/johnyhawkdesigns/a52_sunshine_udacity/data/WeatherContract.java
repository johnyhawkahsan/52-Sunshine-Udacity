package com.johnyhawkdesigns.a52_sunshine_udacity.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;
import android.text.format.Time;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

public class WeatherContract {

    private static final String TAG = WeatherContract.class.getSimpleName();

    // The "Content authority" is a name for the entire content provider, similar to the relationship between a domain name and its website. A convenient string to use for the content authority is the package name for the app, which is guaranteed to be unique on the device.
    public static final String CONTENT_AUTHORITY = "com.johnyhawkdesigns.a52_sunshine_udacity";
    // Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact the content provider.
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    // Possible paths (appended to base content URI for possible URI's) For instance, content://com.johnyhawkdesigns.a52_sunshine_udacity/weather/ is a valid path for looking at weather data.
    // content://com.johnyhawkdesigns.a52_sunshine_udacity/givemeroot/ will fail, as the ContentProvider hasn't been given any information on what to do with "givemeroot".
    public static final String PATH_WEATHER = "weather";
    public static final String PATH_LOCATION = "location";





    // Note: I replaced the UDACITY method with this one: https://stackoverflow.com/questions/39788033/time-getjulianday-deprecated-which-code-would-get-the-work-done-instead?noredirect=1&lq=1
    // These 2 methods are testing using 2 different tests and the results returned were the same.
    public static long normalizeDate(long startDate){
        // normalize the start date to the beginning of the (UTC) day
        GregorianCalendar date = (GregorianCalendar) GregorianCalendar.getInstance(TimeZone.getTimeZone("UTC"));
        date.setTime(new Date(startDate));
        date.set(Calendar.HOUR_OF_DAY, 0);
        date.set(Calendar.MINUTE, 0);
        date.set(Calendar.SECOND, 0);
        date.set(Calendar.MILLISECOND, 0);

        //transform your calendar to a long in the way you prefer
        System.out.println(TAG + " : normalizeDate( startDate = " + startDate + ") return date.getTimeInMillis() = " + date.getTimeInMillis());
        return date.getTimeInMillis();
    }

    //=============================Udacity Deprecated Method to Normalize Date - After Testing it's confirmed that both return the same resutl=============================//

    // To make it easy to query for the exact date, we normalize all dates that go into the database to the start of the the Julian day at UTC.
    public static long normalizeDateUdacity(long startDate) {
        Time time = new Time(); // normalize the start date to the beginning of the (UTC) day
        time.set(startDate);
        int julianDay = Time.getJulianDay(startDate, time.gmtoff);
        System.out.println(TAG + " : normalizeDateUdacity( startDate =" + startDate + ") return time.setJulianDay(julianDay) = " + time.setJulianDay(julianDay));
        return time.setJulianDay(julianDay);
    }






    /*===================================================Inner class that defines the contents of the location table========================================== */
    public static final class LocationEntry implements BaseColumns{

        //Build content uri to get path location
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_LOCATION).build();                                   /** content://com.johnyhawkdesigns.a52_sunshine_udacity/location/ */
        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_LOCATION;         /** vnd.android.cursor.dir/com.johnyhawkdesigns.a52_sunshine_udacity/location */
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_LOCATION;   /** vnd.android.cursor.item/com.johnyhawkdesigns.a52_sunshine_udacity/location */


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
            Log.d(TAG, "buildLocationUri: return ContentUris.withAppendedId(CONTENT_URI, id) = " + ContentUris.withAppendedId(CONTENT_URI, id));
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }









    /*===================================================Inner class that defines the contents of the weather table======================================== */

    public static final class WeatherEntry implements BaseColumns {

        //Build content uri to get weather entry
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_WEATHER).build();                                   /** content://com.johnyhawkdesigns.a52_sunshine_udacity/weather/ */
        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_WEATHER;         /** vnd.android.cursor.dir/com.johnyhawkdesigns.a52_sunshine_udacity/weather */
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_WEATHER;   /** vnd.android.cursor.item/com.johnyhawkdesigns.a52_sunshine_udacity/weather */

        public static final String TABLE_NAME = "weather";

        // Column with the foreign key into the location table.
        public static final String COLUMN_LOC_KEY = "location_id";

        // Date, stored as Text with format yyyy-MMM-dd
        //public static final String COLUMN_DATETEXT = "date"; //We're using Date text for now, I think later it will be changed

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
            Log.d(TAG, "buildWeatherUri: return ContentUris.withAppendedId(CONTENT_URI, id) = " + ContentUris.withAppendedId(CONTENT_URI, id));
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildWeatherLocation(String locationSetting){
            Log.d(TAG, "buildWeatherLocation:return CONTENT_URI.buildUpon().appendPath(locationSetting).build() = " + CONTENT_URI.buildUpon().appendPath(locationSetting).build());
            return CONTENT_URI.buildUpon().appendPath(locationSetting).build();
        }

        public static Uri buildWeatherLocationWithStartDate(String locationSetting, long startDate){
            long normalizedDate = normalizeDate(startDate);
            Log.d(TAG, "buildWeatherLocationWithStartDate: return CONTENT_URI.buildUpon().appendPath(locationSetting).appendQueryParameter(COLUMN_DATE, Long.toString(normalizedDate)).build() = " +  CONTENT_URI.buildUpon().appendPath(locationSetting).appendQueryParameter(COLUMN_DATE, Long.toString(normalizedDate)).build());
            return CONTENT_URI.buildUpon().appendPath(locationSetting).appendQueryParameter(COLUMN_DATE, Long.toString(normalizedDate)).build();
        }

        public static Uri buildWeatherLocationWithDate(String locationSetting, long date ){
            Log.d(TAG, "buildWeatherLocationWithStartDate: return CONTENT_URI.buildUpon().appendPath(locationSetting).appendPath(Long.toString(date)).build() = " +  CONTENT_URI.buildUpon().appendPath(locationSetting).appendPath(Long.toString(date)).build());
            return CONTENT_URI.buildUpon().appendPath(locationSetting).appendPath(Long.toString(date)).build();
        }

        public static String getLocationSettingFromUri(Uri uri){
            Log.d(TAG, "getLocationSettingFromUri: return uri.getPathSegments().get(1) = " + uri.getPathSegments().get(1));
            return uri.getPathSegments().get(1);
        }

        public static long getDateFromUri(Uri uri){
            Log.d(TAG, "getDateFromUri: return Long.parseLong(uri.getPathSegments().get(2)) = " + Long.parseLong(uri.getPathSegments().get(2)));
            return Long.parseLong(uri.getPathSegments().get(2));
        }

        public static long getStartDateFromUri(Uri uri){
            String dateString = uri.getQueryParameter(COLUMN_DATE);
            if (null != dateString && dateString.length() >0 ){
                Log.d(TAG, "getStartDateFromUri: return Long.parseLong(dateString) = " + Long.parseLong(dateString) );
                return Long.parseLong(dateString);
            }
            else
                return 0;
        }

    }


    public static final String DATE_FORMAT = "yyyyMMdd";


    /**
     * Converts Date class to a string representation, used for easy comparison and database lookup.
     * @param date The input date
     * @return a DB-friendly representation of the date, using the format defined in DATE_FORMAT.
     */
    public static String getDbDateString(Date date){
        // Because the API returns a unix timestamp (measured in seconds),
        // it must be converted to milliseconds in order to be converted to valid date.
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
        return sdf.format(date);
    }

}
