package com.johnyhawkdesigns.a52_sunshine_udacity.data;

import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
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
    private Context mContext = InstrumentationRegistry.getTargetContext();
    private SQLiteDatabase db;

    @Before
    public void setUp(){
        System.out.println(TAG + " = settingUp()");
        mContext.deleteDatabase(WeatherDbHelper.DATABASE_NAME); //We delete any previous database, starting out clear
        WeatherDbHelper dbHelper = new WeatherDbHelper(mContext);
        db = dbHelper.getWritableDatabase();
    }

    @After
    public void finish(){
        db.close();
        System.out.println(TAG + " = finish()");
    }





    /* This test uses the database directly to insert and then uses the ContentProvider to read out the data.
       Uncomment this test to see if your location queries are performing correctly.
     */
    @Test
    public void testBasicLocationQueries() {

        ContentValues testValues = TestUtilities.createNorthPoleLocationValues();
        long locationRowId = TestUtilities.insertNorthPoleLocationValues(mContext);
        System.out.println(TAG + ": testBasicLocationQueries() = locationRowId = " + locationRowId);
        Assert.assertTrue("Location row could not be added.", locationRowId != -1);

        // Test the basic content provider query
        Cursor locationCursor = mContext.getContentResolver().query(
                WeatherContract.LocationEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );

        // Make sure we get the correct cursor out of the database
        TestUtilities.validateCursor("testBasicLocationQueries, location query", locationCursor, testValues);

        // Has the NotificationUri been set correctly? --- we can only test this easily against API
        // level 19 or greater because getNotificationUri was added in API level 19.
        if ( Build.VERSION.SDK_INT >= 19 ) {
            Assert.assertEquals("Error: Location Query did not properly set NotificationUri",
                    locationCursor.getNotificationUri(), WeatherContract.LocationEntry.CONTENT_URI);
        }
    }




    /* This test uses the database directly to insert and then uses the ContentProvider to read out the data.
   Uncomment this test to see if the basic weather query functionality given in the ContentProvider is working correctly.
 */
    @Test
    public void testBasicWeatherQuery() {

        long locationRowId = TestUtilities.insertNorthPoleLocationValues(mContext);
        System.out.println(TAG + ": testBasicWeatherQuery() = locationRowId = " + locationRowId);

        // Fantastic.  Now that we have a location, add some weather!
        ContentValues weatherValues = TestUtilities.createWeatherValues(locationRowId);

        long weatherRowId = db.insert(WeatherContract.WeatherEntry.TABLE_NAME, null, weatherValues);
        System.out.println(TAG + ": testBasicWeatherQuery() = weatherRowId = " + weatherRowId);
        Assert.assertTrue("Unable to Insert WeatherEntry into the Database", weatherRowId != -1);

        // Test the basic content provider query
        Cursor weatherCursor = mContext.getContentResolver().query(
                WeatherContract.WeatherEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );

        // Make sure we get the correct cursor out of the database
        TestUtilities.validateCursor("testBasicWeatherQuery", weatherCursor, weatherValues);
    }






    //This method is directly copied from TestDb.java class. There we were testing SQLite database, here we are using it to test ContentResolver.
    @Test
    public void testDeleteDb(){
        //delete all records from DB using database functions only

        //delete all records from DB using Content provider method
        //deleteAllRecordsFromProvider();
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
       This helper function deletes all records from both database tables using the database
       functions only.  This is designed to be used to reset the state of the database until the
       delete functionality is available in the ContentProvider.
     */
    public void deleteAllRecordsFromDB() {
        WeatherDbHelper dbHelper = new WeatherDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        db.delete(WeatherContract.WeatherEntry.TABLE_NAME, null, null);
        db.delete(WeatherContract.LocationEntry.TABLE_NAME, null, null);
        db.close();
    }




    /*
       Testing ContentProvider delete functionality.
       This helper function deletes all records from both database tables using the ContentProvider. It also queries the ContentProvider to make sure that the database has been successfully
       deleted, so it cannot be used until the Query and Delete functions have been written in the ContentProvider.
       Students: Replace the calls to deleteAllRecordsFromDB with this one after you have written the delete functionality in the ContentProvider.
     */
    public void deleteAllRecordsFromProvider(){

        System.out.println(TAG + " : deleteAllRecordsFromProvider()");

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
        Cursor cursor;
        cursor = mContext.getContentResolver().query(
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
