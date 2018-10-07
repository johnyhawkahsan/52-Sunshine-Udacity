package com.johnyhawkdesigns.a52_sunshine_udacity.data;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

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



/*
    // This test uses the database directly to insert and then uses the ContentProvider to read out the data.
    // Uncomment this test to see if your location queries are performing correctly.
    @Test
    public void testBasicLocationQueries() {

        ContentValues testValues = TestUtilities.createNorthPoleLocationValues();

        long locationRowId = TestUtilities.insertNorthPoleLocationValues(mContext); //Simple db method to insert new data and get locationRowId
        //long locationRowId = TestUtilities.insertNorthPoleLocationValuesUsingContentProvider(mContext); //New Content resolver method to insert new data and get locationRowId

        System.out.println(TAG + ": testBasicLocationQueries() = locationRowId = " + locationRowId);
        Assert.assertTrue("Location row could not be added.", locationRowId != -1); // Verify we got a row back.

        // Test the basic content provider query - A cursor is your primary interface to the query results.
        Cursor locationCursor = mContext.getContentResolver().query(
                WeatherContract.LocationEntry.CONTENT_URI,
                null,         // leaving "columns" null just returns all the columns.
                 null,         // cols for "where" clause
                 null,      // values for "where" clause
                 null         // sort order
        );

        // Make sure we get the correct cursor out of the database
        TestUtilities.validateCursor("testBasicLocationQueries, location query", locationCursor, testValues);

        locationCursor.close();

        // Has the NotificationUri been set correctly? --- we can only test this easily against API
        // level 19 or greater because getNotificationUri was added in API level 19.
        if ( Build.VERSION.SDK_INT >= 19 ) {
            Assert.assertEquals("Error: Location Query did not properly set NotificationUri", locationCursor.getNotificationUri(), WeatherContract.LocationEntry.CONTENT_URI);
            System.out.println(TAG + ": locationCursor.getNotificationUri() is expected value, while real object is WeatherContract.LocationEntry.CONTENT_URI ");
        }
    }
*/







/*
    // This test uses the database directly to insert and then uses the ContentProvider to read out the data.
    // Uncomment this test to see if the basic weather query functionality given in the ContentProvider is working correctly.
    @Test
    public void testBasicWeatherQuery() {

        long locationRowId = TestUtilities.insertNorthPoleLocationValues(mContext);
        System.out.println(TAG + ": testBasicWeatherQuery() = locationRowId = " + locationRowId);

        // Fantastic.  Now that we have a location, add some weather!
        ContentValues weatherValues = TestUtilities.createWeatherValues(locationRowId);

        long weatherRowId = db.insert(WeatherContract.WeatherEntry.TABLE_NAME, null, weatherValues);

        System.out.println(TAG + ": testBasicWeatherQuery() = weatherRowId = " + weatherRowId);
        Assert.assertTrue("Unable to Insert WeatherEntry into the Database", weatherRowId != -1);

        db.close();

        // Test the basic content provider query - A cursor is your primary interface to the query results.
        Cursor weatherCursor = mContext.getContentResolver().query(
                WeatherContract.WeatherEntry.CONTENT_URI,
                //WeatherContract.WeatherEntry.buildWeatherLocation("94074"),
                null,         // leaving "columns" null just returns all the columns.
                null,         // cols for "where" clause
                null,      // values for "where" clause
                null         // sort order
        );



        // Make sure we get the correct cursor out of the database
        if (weatherCursor.moveToFirst()){
            TestUtilities.validateCursor("testBasicWeatherQuery error because weatherCursor is null", weatherCursor, weatherValues);
            Assert.assertTrue("Empty cursor returned.", weatherCursor.moveToFirst());
            System.out.println(TAG + ": testBasicWeatherQuery() = weatherCursorgetCount() = " + weatherCursor.getCount());
        }
        else {
            System.out.println(TAG + ": weatherCursor.moveToFirst() == null ");
            //Assert.fail("No Weather data returned!");
        }
    }
*/




/*

    @Test
    public void testInsertContentUri() {

        ContentValues testValues = TestUtilities.createNorthPoleLocationValues();
        Uri locationUri = mContext.getContentResolver().insert(WeatherContract.LocationEntry.CONTENT_URI, testValues);
        long locationRowId = ContentUris.parseId(locationUri);
        Assert.assertTrue("Location row could not be added.", locationRowId != -1); // Verify we got a row back.
        System.out.println(TAG + ": testInsertContentUri() = locationUri = " + locationUri + ", locationRowId = " + locationRowId);

        // A cursor is your primary interface to the query results.
        Cursor cursor = mContext.getContentResolver().query(
                WeatherContract.LocationEntry.CONTENT_URI,
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null  // sort order
        );
        TestUtilities.validateCursor("testInsertReadProvider. Error validating LocationEntry.", cursor, testValues);

        // Fantastic.  Now that we have a location, add some weather!
        ContentValues weatherValues = TestUtilities.createWeatherValues(locationRowId);
        Uri weatherInsertUri = mContext.getContentResolver().insert(WeatherContract.WeatherEntry.CONTENT_URI, weatherValues);
        long weatherRowId = ContentUris.parseId(weatherInsertUri);
        System.out.println(TAG + ": testInsertContentUri() = weatherInsertUri = " + weatherInsertUri + ", weatherRowId = " + weatherRowId);
        Assert.assertTrue("Unable to Insert WeatherEntry into the Database", weatherRowId != -1);

        db.close();

        // Test the basic content provider query - A cursor is your primary interface to the query results.
        Cursor weatherCursor = mContext.getContentResolver().query(
                WeatherContract.WeatherEntry.CONTENT_URI,
                //WeatherContract.WeatherEntry.buildWeatherLocation("94074"),
                null,         // leaving "columns" null just returns all the columns.
                null,         // cols for "where" clause
                null,      // values for "where" clause
                null         // sort order
        );



        // Make sure we get the correct cursor out of the database
        if (weatherCursor.moveToFirst()){
            TestUtilities.validateCursor("testBasicWeatherQuery error because weatherCursor is null", weatherCursor, weatherValues);
            Assert.assertTrue("Empty cursor returned.", weatherCursor.moveToFirst());
            System.out.println(TAG + ": testBasicWeatherQuery() = weatherCursorgetCount() = " + weatherCursor.getCount());
        }
        else {
            System.out.println(TAG + ": weatherCursor.moveToFirst() == null ");
            //Assert.fail("No Weather data returned!");
        }

        //After data is written to the database, now we need to test our delete functionality
        deleteAllRecordsFromProvider();
    }
*/







    // This test uses the provider to insert and then update the data. Uncomment this test to see if your update location is functioning correctly.
    @Test
    public void testUpdateLocation() {
        // Create a new map of values, where column names are the keys
        ContentValues values = TestUtilities.createNorthPoleLocationValues();

        Uri locationUri = mContext.getContentResolver().insert(WeatherContract.LocationEntry.CONTENT_URI, values);
        long locationRowId = ContentUris.parseId(locationUri);

        // Verify we got a row back.
        Assert.assertTrue(locationRowId != -1);
        Log.d(TAG, "New row id: " + locationRowId);
        System.out.println(TAG + ": New row id: " + locationRowId);


        ContentValues updatedValues = new ContentValues(values);
        updatedValues.put(WeatherContract.LocationEntry._ID, locationRowId);
        updatedValues.put(WeatherContract.LocationEntry.COLUMN_CITY_NAME, "Santa's Village");

        // Create a cursor with observer to make sure that the content provider is notifying the observers as expected
        Cursor locationCursor = mContext.getContentResolver().query(WeatherContract.LocationEntry.CONTENT_URI, null, null, null, null);

        TestUtilities.TestContentObserver tco = TestUtilities.getTestContentObserver();
        locationCursor.registerContentObserver(tco);

        int count = mContext.getContentResolver().update(
                WeatherContract.LocationEntry.CONTENT_URI, updatedValues, WeatherContract.LocationEntry._ID + "= ?",
                new String[] { Long.toString(locationRowId)});
        Assert.assertEquals(count, 1);
        System.out.println(TAG + ": count: " + count);


        // Test to make sure our observer is called.  If not, we throw an assertion.
        // Students: If your code is failing here, it means that your content provider isn't calling getContext().getContentResolver().notifyChange(uri, null);
        tco.waitForNotificationOrFail();

        locationCursor.unregisterContentObserver(tco);
        locationCursor.close();

        // A cursor is your primary interface to the query results.
        Cursor cursor = mContext.getContentResolver().query(
                WeatherContract.LocationEntry.CONTENT_URI,
                null,   // projection
                WeatherContract.LocationEntry._ID + " = " + locationRowId,
                null,   // Values for the "where" clause
                null    // sort order
        );

        TestUtilities.validateCursor("testUpdateLocation.  Error validating location entry update.", cursor, updatedValues);

        cursor.close();
    }



    //This method is directly copied from TestDb.java class. There we were testing SQLite database, here we are using it to test ContentResolver.
    @Test
    public void testDeleteDb(){
        //delete all records from DB using database functions only
        //deleteAllRecordsFromDB();
        //delete all records from DB using Content provider method
        //deleteAllRecordsFromProvider();
    }

/*
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
*/



/*

    // Make sure we can still delete after adding/updating stuff
    // Student: Uncomment this test after you have completed writing the insert functionality in your provider.
    // It relies on insertions with testInsertReadProvider, so insert and query functionality must also be complete before this test can be used.
    @Test
    public void testInsertReadProvider() {
        ContentValues testValues = TestUtilities.createNorthPoleLocationValues();

        // Register a content observer for our insert.  This time, directly with the content resolver
        TestUtilities.TestContentObserver tco = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(WeatherContract.LocationEntry.CONTENT_URI, true, tco);
        Uri locationUri = mContext.getContentResolver().insert(WeatherContract.LocationEntry.CONTENT_URI, testValues);
        System.out.println(TAG + " :testInsertReadProvider(): locationUri = " + locationUri);


        // Did our content observer get called?  Students:  If this fails, your insert location isn't calling getContext().getContentResolver().notifyChange(uri, null);
        tco.waitForNotificationOrFail();
        mContext.getContentResolver().unregisterContentObserver(tco);

        long locationRowId = ContentUris.parseId(locationUri);
        System.out.println(TAG + " :testInsertReadProvider(): locationRowId = " + locationRowId);


        // Verify we got a row back.
        Assert.assertTrue(locationRowId != -1);

        // Data's inserted.  IN THEORY.  Now pull some out to stare at it and verify it made the round trip.
        Cursor cursor = mContext.getContentResolver().query(
                WeatherContract.LocationEntry.CONTENT_URI,
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null  // sort order
        );

        TestUtilities.validateCursor("testInsertReadProvider. Error validating LocationEntry.", cursor, testValues);

        // Fantastic.  Now that we have a location, add some weather!
        ContentValues weatherValues = TestUtilities.createWeatherValues(locationRowId);
        // The TestContentObserver is a one-shot class
        tco = TestUtilities.getTestContentObserver();

        mContext.getContentResolver().registerContentObserver(WeatherContract.WeatherEntry.CONTENT_URI, true, tco);

        Uri weatherInsertUri = mContext.getContentResolver().insert(WeatherContract.WeatherEntry.CONTENT_URI, weatherValues);
        Assert.assertTrue(weatherInsertUri != null);

        // Did our content observer get called?  Students:  If this fails, your insert weather  in your ContentProvider isn't calling
        // getContext().getContentResolver().notifyChange(uri, null);
        tco.waitForNotificationOrFail();
        mContext.getContentResolver().unregisterContentObserver(tco);

        // A cursor is your primary interface to the query results.
        Cursor weatherCursor = mContext.getContentResolver().query(
                WeatherContract.WeatherEntry.CONTENT_URI,  // Table to Query
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null // columns to group by
        );

        TestUtilities.validateCursor("testInsertReadProvider. Error validating WeatherEntry insert.", weatherCursor, weatherValues);

        // Add the location values in with the weather data so that we can make sure that the join worked and we actually get all the values back
        weatherValues.putAll(testValues);

        // Get the joined Weather and Location data
        weatherCursor = mContext.getContentResolver().query(
                WeatherContract.WeatherEntry.buildWeatherLocation(TestUtilities.TEST_LOCATION),
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null  // sort order
        );
        TestUtilities.validateCursor("testInsertReadProvider.  Error validating joined Weather and Location Data.", weatherCursor, weatherValues);

        // Get the joined Weather and Location data with a start date
        weatherCursor = mContext.getContentResolver().query(
                WeatherContract.WeatherEntry.buildWeatherLocationWithStartDate(TestUtilities.TEST_LOCATION, TestUtilities.TEST_DATETEXT),
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null  // sort order
        );
        TestUtilities.validateCursor("testInsertReadProvider.  Error validating joined Weather and Location Data with start date.", weatherCursor, weatherValues);

        // Get the joined Weather data for a specific date
        weatherCursor = mContext.getContentResolver().query(
                WeatherContract.WeatherEntry.buildWeatherLocationWithDate(TestUtilities.TEST_LOCATION, TestUtilities.TEST_DATETEXT),
                null,
                null,
                null,
                null
        );
        TestUtilities.validateCursor("testInsertReadProvider.  Error validating joined Weather and Location data for a specific date.", weatherCursor, weatherValues);
    }
*/








    // This helper function deletes all records from both database tables using the database functions only.
    public void deleteAllRecordsFromDB() {
        WeatherDbHelper dbHelper = new WeatherDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        db.delete(WeatherContract.WeatherEntry.TABLE_NAME, null, null);
        System.out.println(TAG + " : deleteAllRecordsFromDB() = deleting Weather Table");
        db.delete(WeatherContract.LocationEntry.TABLE_NAME, null, null);
        System.out.println(TAG + " : deleteAllRecordsFromDB() = deleting Location Table");
        db.close();
    }




    // This helper function deletes all records from both database tables using the ContentProvider. It also queries the ContentProvider to make sure that the database has been successfully
    // deleted, so it cannot be used until the Query and Delete functions have been written in the ContentProvider.
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
        System.out.println(TAG + " : deleteAllRecordsFromProvider() , WeatherEntry record after deleting db = " + cursor.getCount());
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
        System.out.println(TAG + " : deleteAllRecordsFromProvider() , LocationEntry record after deleting db = " + cursor.getCount());
        cursor.close();

    }

}
