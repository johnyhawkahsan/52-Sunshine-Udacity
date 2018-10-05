package com.johnyhawkdesigns.a52_sunshine_udacity.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.test.runner.AndroidJUnit4;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Map;
import java.util.Set;


/*  Students: These are functions and some test data to make it easier to test your database and Content Provider.
    Note that you'll want your WeatherContract class to exactly match the one in our solution to use these as-given.
*/
@RunWith(AndroidJUnit4.class)
public class TestUtilities {

    private static final String TAG = TestUtilities.class.getSimpleName();
    static final String TEST_LOCATION = "99705";
    static final long TEST_DATE = 1419033600L;  // December 20th, 2014
    static final long TEST_DATETEXT = 1419033600L;  // December 20th, 2014


    @Before
    public void setUp(){
        System.out.println(TAG + " = setingUp()");
    }

    @After
    public void finish(){
        System.out.println(TAG + " = finish()");
    }


    //Create Weather Values and use in TestDb. Location Row id is passed here "COLUMN_LOC_KEY" is foreign key
    public static ContentValues createWeatherValues(long locationRowId){
        System.out.println(TAG + " = createWeatherValues(locationRowId =" + locationRowId + ")");
        ContentValues weatherValues = new ContentValues();
        weatherValues.put(WeatherContract.WeatherEntry.COLUMN_LOC_KEY, locationRowId);
        weatherValues.put(WeatherContract.WeatherEntry.COLUMN_DATETEXT, TEST_DATETEXT);
        weatherValues.put(WeatherContract.WeatherEntry.COLUMN_DEGREES, 1.1);
        weatherValues.put(WeatherContract.WeatherEntry.COLUMN_HUMIDITY, 1.2);
        weatherValues.put(WeatherContract.WeatherEntry.COLUMN_PRESSURE, 1.3);
        weatherValues.put(WeatherContract.WeatherEntry.COLUMN_MAX_TEMP, 75);
        weatherValues.put(WeatherContract.WeatherEntry.COLUMN_MIN_TEMP, 65);
        weatherValues.put(WeatherContract.WeatherEntry.COLUMN_SHORT_DESC, "Asteroids");
        weatherValues.put(WeatherContract.WeatherEntry.COLUMN_WIND_SPEED, 5.5);
        weatherValues.put(WeatherContract.WeatherEntry.COLUMN_WEATHER_ID, 321);
        return weatherValues;
    }

    public static ContentValues createNorthPoleLocationValues(){
        System.out.println(TAG + " = createNorthPoleLocationValues()");
        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING, TEST_LOCATION);
        values.put(WeatherContract.LocationEntry.COLUMN_CITY_NAME, "North Pole" );
        values.put(WeatherContract.LocationEntry.COLUMN_COORD_LAT, 64.7488);
        values.put(WeatherContract.LocationEntry.COLUMN_COORD_LONG, -147.353);
        return values;
    }

    static void validateCursor(String error, Cursor valueCursor, ContentValues expectedValues) {
        System.out.println(TAG + " = validateCursor()");
        Assert.assertTrue("Empty cursor returned. " + error, valueCursor.moveToFirst());
        validateCurrentRecord(error, valueCursor, expectedValues);
        valueCursor.close();
    }



    //This method checks all our expectedValues against record returned by Cursor.
    public static void validateCurrentRecord(String error, Cursor valueCursor, ContentValues expectedValues){

        System.out.println(TAG + " = validateCurrentRecord()");

        Set<Map.Entry<String, Object>> valueSet = expectedValues.valueSet();

        for (Map.Entry<String, Object> entry : valueSet){
            String columnName = entry.getKey();
            int idx = valueCursor.getColumnIndex(columnName);
            Assert.assertFalse("Column '" + columnName + "' not found. " + error, idx == -1);
            System.out.println(TAG + " : columnName = " + columnName + ", associated idx = " + idx);

            String expectedValue = entry.getValue().toString();
            Assert.assertEquals("Value '" + entry.getValue().toString() + "' did not match the expected value '" + expectedValue + "'. " + error, expectedValue, valueCursor.getString(idx));
            System.out.println(TAG + " : expectedValue = " + expectedValue);

        }
    }



    /*
        Students: You can uncomment this function once you have finished creating the LocationEntry part of the WeatherContract as well as the WeatherDbHelper.
     */
    static long insertNorthPoleLocationValues(Context context) {
        System.out.println(TAG + " = insertNorthPoleLocationValues()");
        // insert our test records into the database
        WeatherDbHelper dbHelper = new WeatherDbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues testValues = TestUtilities.createNorthPoleLocationValues();

        long locationRowId;
        locationRowId = db.insert(WeatherContract.LocationEntry.TABLE_NAME, null, testValues);

        // Verify we got a row back.
        Assert.assertTrue("Error: Failure to insert North Pole Location Values", locationRowId != -1);

        return locationRowId;
    }

}
