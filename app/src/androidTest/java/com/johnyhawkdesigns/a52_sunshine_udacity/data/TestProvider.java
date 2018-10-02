package com.johnyhawkdesigns.a52_sunshine_udacity.data;

import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class TestProvider {

    private static final String TAG = TestProvider.class.getSimpleName();
    Context mContext = InstrumentationRegistry.getTargetContext();


    @Before
    public void setUp(){
        System.out.println(TAG + " = settingUp()");
        mContext.deleteDatabase(WeatherDbHelper.DATABASE_NAME); //We delete any previous database, starting out clear
    }

    @After
    public void finish(){
        System.out.println(TAG + " = finish()");
    }

    @Test
    public void test1(){

    }

    /** This test doesn't touch the database.  It verifies that the ContentProvider returns the correct type for each type of URI that it can handle.
        Students: Uncomment this test to verify that your implementation of GetType is functioning correctly.
    */
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


        String testDate = "1419120000L"; // December 21st, 2014
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

    /*
       Testing ContentProvider delete functionality.
       This helper function deletes all records from both database tables using the ContentProvider. It also queries the ContentProvider to make sure that the database has been successfully
       deleted, so it cannot be used until the Query and Delete functions have been written in the ContentProvider.
       Students: Replace the calls to deleteAllRecordsFromDB with this one after you have written the delete functionality in the ContentProvider.
     */
    public void deleteAllRecordsFromProvider(){
        mContext.getContentResolver().delete(
                WeatherContract.WeatherEntry.CONTENT_URI,
                null,
                null
        );
        mContext.getContentResolver().delete(
                WeatherContract.LocationEntry.CONTENT_URI,
                null,
                null
        );

        //After Deleting the WeatherEntry, check against the CONTENT_URI if it exists now. It shouldn't exist and should return cursor.getCount() = 0
        Cursor cursor = mContext.getContentResolver().query(
                WeatherContract.WeatherEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        Assert.assertEquals("Error: Records not deleted from Weather table during delete", 0, cursor.getCount());
        cursor.close();

        //After Deleting the LocationEntry, check against the CONTENT_URI if it exists now. It shouldn't exist and should return cursor.getCount() = 0
        cursor = mContext.getContentResolver().query(
                WeatherContract.LocationEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        Assert.assertEquals("Error: Records not deleted from Location table during delete", 0, cursor.getCount());
        cursor.close();

    }



}
