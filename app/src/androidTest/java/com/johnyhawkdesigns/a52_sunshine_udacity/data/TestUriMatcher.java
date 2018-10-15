package com.johnyhawkdesigns.a52_sunshine_udacity.data;

import android.content.Context;
import android.content.UriMatcher;
import android.net.Uri;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class TestUriMatcher {

    private static final String TAG = TestUriMatcher.class.getSimpleName();
    private Context mContext = InstrumentationRegistry.getTargetContext();

    private static final String LOCATION_QUERY = "London, UK";
    private static final long TEST_DATE = 1419033600;  // December 20th, 2014

    // content://com.example.android.sunshine.app/weather"
    private static final Uri TEST_WEATHER_DIR = WeatherContract.WeatherEntry.CONTENT_URI;
    private static final Uri TEST_WEATHER_WITH_LOCATION_DIR = WeatherContract.WeatherEntry.buildWeatherLocation(LOCATION_QUERY);
    private static final Uri TEST_WEATHER_WITH_LOCATION_AND_DATE_DIR = WeatherContract.WeatherEntry.buildWeatherLocationWithDate(LOCATION_QUERY, TEST_DATE);

    // content://com.johnyhawkdesigns.a52_sunshine_udacity/location"
    private static final Uri TEST_LOCATION_DIR = WeatherContract.LocationEntry.CONTENT_URI;
    private static final Uri TEST_LOCATION_ID = WeatherContract.LocationEntry.buildLocationUri(10);

    /**
        Students: This function tests that your UriMatcher returns the correct integer value
        for each of the Uri types that our ContentProvider can handle.  Uncomment this when you are ready to test your UriMatcher.
     */

    @Before
    public void setUp(){
        System.out.println(TAG + " = settingUp()");
    }

    @After
    public void finish(){
        System.out.println(TAG + " = finish()");
    }

    //This function tests that your UriMatcher returns the correct integer value for each of the Uri types that our ContentProvider can handle.
    @Test
    public void testUriMatcher(){
        UriMatcher testMatcher = WeatherProvider.buildUriMatcher();
        Assert.assertEquals("Error: The WEATHER URI was matched incorrectly.", testMatcher.match(TEST_WEATHER_DIR), WeatherProvider.WEATHER);
        System.out.println(TAG + " : testMatcher.match(TEST_WEATHER_DIR) = " + testMatcher.match(TEST_WEATHER_DIR) + ", WeatherProvider.WEATHER = " + WeatherProvider.WEATHER);

        Assert.assertEquals("Error: The WEATHER WITH LOCATION URI was matched incorrectly.", testMatcher.match(TEST_WEATHER_WITH_LOCATION_DIR), WeatherProvider.WEATHER_WITH_LOCATION);
        System.out.println(TAG + " : testMatcher.match(TEST_WEATHER_WITH_LOCATION_DIR) = " + testMatcher.match(TEST_WEATHER_WITH_LOCATION_DIR) + ", WeatherProvider.WEATHER_WITH_LOCATION = " + WeatherProvider.WEATHER_WITH_LOCATION);

        Assert.assertEquals("Error: The WEATHER WITH LOCATION AND DATE URI was matched incorrectly.", testMatcher.match(TEST_WEATHER_WITH_LOCATION_AND_DATE_DIR), WeatherProvider.WEATHER_WITH_LOCATION_AND_DATE);
        System.out.println(TAG + " : testMatcher.match(TEST_WEATHER_WITH_LOCATION_AND_DATE_DIR) = " + testMatcher.match(TEST_WEATHER_WITH_LOCATION_AND_DATE_DIR) + ", WeatherProvider.WEATHER_WITH_LOCATION_AND_DATE = " + WeatherProvider.WEATHER_WITH_LOCATION_AND_DATE);

        Assert.assertEquals("Error: The LOCATION URI was matched incorrectly.", testMatcher.match(TEST_LOCATION_DIR), WeatherProvider.LOCATION);
        System.out.println(TAG + " : testMatcher.match(TEST_LOCATION_DIR) = " + testMatcher.match(TEST_LOCATION_DIR) + ", WeatherProvider.LOCATION = " + WeatherProvider.LOCATION);

        Assert.assertEquals("Error: The LOCATION WITH LOCATION ID URI was matched incorrectly.", testMatcher.match(TEST_LOCATION_ID), WeatherProvider.LOCATION_ID);
        System.out.println(TAG + " : testMatcher.match(TEST_LOCATION_ID) = " + testMatcher.match(TEST_LOCATION_ID) + ", WeatherProvider.LOCATION_ID = " + WeatherProvider.LOCATION_ID);

    }


    // This test doesn't touch the database.  It verifies that the ContentProvider returns the correct type for each type of URI that it can handle.
    // Students: Uncomment this test to verify that your implementation of GetType is functioning correctly.
    @Test
    public void testGetType(){
        // content://com.example.android.sunshine.app/weather/
        String type = mContext.getContentResolver().getType(WeatherContract.WeatherEntry.CONTENT_URI);
        // vnd.android.cursor.dir/com.johnyhawkdesigns.a52_sunshine_udacity/weather
        Assert.assertEquals("Error: the WeatherEntry CONTENT_URI should return WeatherEntry.CONTENT_TYPE", WeatherContract.WeatherEntry.CONTENT_TYPE, type);
        System.out.println(TAG + " : type = " + type + ", WeatherContract.WeatherEntry.CONTENT_TYPE = " + WeatherContract.WeatherEntry.CONTENT_TYPE);

        String testLocation = "94074";
        // content://com.example.android.sunshine.app/weather/94074
        type = mContext.getContentResolver().getType(WeatherContract.WeatherEntry.buildWeatherLocation(testLocation));
        // vnd.android.cursor.dir/com.example.android.sunshine.app/weather
        Assert.assertEquals("Error: the WeatherEntry CONTENT_URI with location should return WeatherEntry.CONTENT_TYPE", WeatherContract.WeatherEntry.CONTENT_TYPE, type);
        System.out.println(TAG + " : type = " + type + ", WeatherContract.WeatherEntry.buildWeatherLocation(testLocation) = " + WeatherContract.WeatherEntry.CONTENT_TYPE);


        Long testDate = 1419120000L; // December 21st, 2014
        // content://com.example.android.sunshine.app/weather/94074/20140612
        type = mContext.getContentResolver().getType(WeatherContract.WeatherEntry.buildWeatherLocationWithDate(testLocation, testDate));
        // vnd.android.cursor.item/com.example.android.sunshine.app/weather/1419120000
        Assert.assertEquals("Error: the WeatherEntry CONTENT_URI with location and date should return WeatherEntry.CONTENT_ITEM_TYPE", WeatherContract.WeatherEntry.CONTENT_ITEM_TYPE, type);
        System.out.println(TAG + " : type = " + type + ", WeatherContract.WeatherEntry.CONTENT_ITEM_TYPE = " + WeatherContract.WeatherEntry.CONTENT_ITEM_TYPE);


        // content://com.example.android.sunshine.app/location/
        type = mContext.getContentResolver().getType(WeatherContract.LocationEntry.CONTENT_URI);
        // vnd.android.cursor.dir/com.example.android.sunshine.app/location
        Assert.assertEquals("Error: the LocationEntry CONTENT_URI should return LocationEntry.CONTENT_TYPE", WeatherContract.LocationEntry.CONTENT_TYPE, type);
        System.out.println(TAG + " : type = " + type + ", WeatherContract.WeatherEntry.CONTENT_TYPE = " + WeatherContract.WeatherEntry.CONTENT_TYPE);

    }

}
