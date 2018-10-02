package com.johnyhawkdesigns.a52_sunshine_udacity.data;

import android.content.UriMatcher;
import android.net.Uri;
import android.support.test.runner.AndroidJUnit4;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class TestUriMatcher {

    private static final String TAG = TestUriMatcher.class.getSimpleName();

    private static final String LOCATION_QUERY = "London, UK";
    private static final String TEST_DATE = "1419033600L";  // December 20th, 2014
    private static final long TEST_LOCATION_ID = 10L;

    // content://com.example.android.sunshine.app/weather"
    private static final Uri TEST_WEATHER_DIR = WeatherContract.WeatherEntry.CONTENT_URI;
    private static final Uri TEST_WEATHER_WITH_LOCATION_DIR = WeatherContract.WeatherEntry.buildWeatherLocation(LOCATION_QUERY);
    private static final Uri TEST_WEATHER_WITH_LOCATION_AND_DATE_DIR = WeatherContract.WeatherEntry.buildWeatherLocationWithDate(LOCATION_QUERY, TEST_DATE);

    // content://com.johnyhawkdesigns.a52_sunshine_udacity/location"
    private static final Uri TEST_LOCATION_DIR = WeatherContract.LocationEntry.CONTENT_URI;

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
        Assert.assertEquals("Error: The WEATHER WITH LOCATION URI was matched incorrectly.", testMatcher.match(TEST_WEATHER_WITH_LOCATION_DIR), WeatherProvider.WEATHER_WITH_LOCATION);
        Assert.assertEquals("Error: The WEATHER WITH LOCATION AND DATE URI was matched incorrectly.", testMatcher.match(TEST_WEATHER_WITH_LOCATION_AND_DATE_DIR), WeatherProvider.WEATHER_WITH_LOCATION_AND_DATE);
        Assert.assertEquals("Error: The LOCATION URI was matched incorrectly.", testMatcher.match(TEST_LOCATION_DIR), WeatherProvider.LOCATION);
    }

}
