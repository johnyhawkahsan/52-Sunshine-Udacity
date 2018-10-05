package com.johnyhawkdesigns.a52_sunshine_udacity.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashSet;

@RunWith(AndroidJUnit4.class)
public class TestDb{

    private static final String TAG = TestDb.class.getSimpleName();
    private SQLiteDatabase db;
    private Context mContext = InstrumentationRegistry.getTargetContext();
    final HashSet<String> tableNameHashSet = new HashSet<String>();

    //We want to add our Table names in this hash set so we can retrieve easily
    public HashSet<String> getTableNameHashSet() {
        tableNameHashSet.add(WeatherContract.LocationEntry.TABLE_NAME);
        tableNameHashSet.add(WeatherContract.WeatherEntry.TABLE_NAME);
        return tableNameHashSet;
    }

    // First delete the db and then create new Db for testing
    @Before
    public void setUp(){
        deleteTheDatabase();
        db = new WeatherDbHelper(mContext).getWritableDatabase();
        System.out.println(TestDb.TAG + " = setUp()");
    }

    // Close db after testing
    @After
    public void finish(){
        db.close();
        System.out.println(TAG + " = finish(): db.close()");
    }

    // Test to ensure if db is not null
    @Test
    public void testPreConditions(){
        Assert.assertNotNull(db);
        System.out.println(TAG + " = testPreConditions(): checking if db object is not null");
    }

    //Testing creation of Database, if Database is open or not and also if it contains all tables and columns
    @Test
    public void testCreateDb(){
        Assert.assertEquals("Database is not open error",true, db.isOpen());
        System.out.println(TAG + " = testCreateDb(): running Assert.assertEquals method to ensure Database is open");

        // have we created the tables we want?
        Cursor c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);
        Assert.assertTrue("Error: This means that the database has not been created correctly", c.moveToFirst());

        // verify that the tables have been created
        do {
            tableNameHashSet.remove(c.getString(0));
        } while( c.moveToNext() );

        // if this fails, it means that your database doesn't contain both the location entry
        // and weather entry tables
        Assert.assertTrue("Error: Your database was created without both the location entry and weather entry tables", tableNameHashSet.isEmpty());

        // now, do our tables contain the correct columns?
        c = db.rawQuery("PRAGMA table_info(" + WeatherContract.LocationEntry.TABLE_NAME + ")", null);
        Assert.assertTrue("Error: This means that we were unable to query the database for table information.", c.moveToFirst());

        // Build a HashSet of all of the column names we want to look for
        final HashSet<String> locationColumnHashSet = new HashSet<String>();
        locationColumnHashSet.add(WeatherContract.LocationEntry._ID);
        locationColumnHashSet.add(WeatherContract.LocationEntry.COLUMN_CITY_NAME);
        locationColumnHashSet.add(WeatherContract.LocationEntry.COLUMN_COORD_LAT);
        locationColumnHashSet.add(WeatherContract.LocationEntry.COLUMN_COORD_LONG);
        locationColumnHashSet.add(WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING);

        int columnNameIndex = c.getColumnIndex("name");
        do {
            String columnName = c.getString(columnNameIndex);
            System.out.println(TAG + " = testCreateDb(): columnName = " + columnName);
            locationColumnHashSet.remove(columnName);
        } while(c.moveToNext());

        // if this fails, it means that your database doesn't contain all of the required location entry columns
        Assert.assertTrue("Error: The database doesn't contain all of the required location entry columns", locationColumnHashSet.isEmpty());

    }





    @Test
    public void testLocationTable(){
        insertLocation();
    }



    /** Students:  Here is where you will build code to test that we can insert and query the database. You'll want to look in TestUtilities where you can use the "createWeatherValues" function.
        You can also make use of the validateCurrentRecord function from within TestUtilities.
     */
    @Test
    public void testWeatherTable(){
        // First insert the location, and then use the locationRowId to insert the weather. Make sure to cover as many failure cases as you can.
        long locationRowId = insertLocation();

        // Make sure we have a valid row ID.
        Assert.assertFalse("Error: Location Not Inserted Correctly", locationRowId == -1);

        // First step: Get reference to writable database - We already did this in @Before setup() method
        // Second Step (Weather): Create weather values
        ContentValues weatherValues = TestUtilities.createWeatherValues(locationRowId); //We pass lcationRowId to this function because it puts this value into WeatherTable's column COLUMN_LOC_KEY

        // Third Step (Weather): Insert ContentValues into database and get a row ID back
        long weatherRowId = db.insert(WeatherContract.WeatherEntry.TABLE_NAME, null, weatherValues);
        Assert.assertTrue(weatherRowId != -1);
        System.out.println(TAG + " = testWeatherTable(): weatherRowId = " + weatherRowId);


        // Fourth Step: Query the database and receive a Cursor back. A cursor is your primary interface to the query results.
        Cursor weatherCursor = db.query(
                WeatherContract.WeatherEntry.TABLE_NAME,  // Table to Query
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null, // columns to group by
                null, // columns to filter by row groups
                null  // sort order
        );
        // Move the cursor to the first valid database row and check to see if we have any rows
        Assert.assertTrue( "Error: No Records returned from location query", weatherCursor.moveToFirst() );

        // Fifth Step: Validate the location Query
        TestUtilities.validateCurrentRecord("testInsertReadDb weatherEntry failed to validate",weatherCursor, weatherValues);
        // Move the cursor to demonstrate that there is only one record in the database
        Assert.assertFalse( "Error: More than one record returned from weather query", weatherCursor.moveToNext() );

        // Sixth Step: Close cursor and database
        weatherCursor.close();
    }




    public long insertLocation(){

        // First step: Get reference to writable database - We already did this in @Before setup() method
        // Second Step: Create ContentValues of what you want to insert
        ContentValues testValues = TestUtilities.createNorthPoleLocationValues();

        // Third Step: Insert ContentValues into database and get a row ID back
        long locationRowId;
        locationRowId = db.insert(WeatherContract.LocationEntry.TABLE_NAME, null, testValues); //Put content values into "location" table

        //Verify if we got row back
        Assert.assertTrue(locationRowId != -1);
        System.out.println(TAG + " = insertLocation(): locationRowId = " + locationRowId);

        // Data's inserted. IN THEORY. Now pull some out to stare at it and verify it made the round trip. Specify which columns you want.
        // Fourth Step: Query the database and receive a Cursor back. A cursor is your primary interface to the query results.
        Cursor cursor = db.query(
                WeatherContract.LocationEntry.TABLE_NAME,  // Table to Query
                null, // all columns
                null, // columns for the "where" clause
                null, // values for the "where" clause
                null, // columns to group by
                null, // columns to filter by row groups
                null // sort order
        );
        // Move the cursor to a valid database row and check to see if we got any records back from the query
        Assert.assertTrue( "Error: No Records returned from location query", cursor.moveToFirst() );

        // Fifth Step: Validate data in resulting Cursor with the original ContentValues (you can use the validateCurrentRecord function in TestUtilities to validate the query if you like)
        TestUtilities.validateCurrentRecord("Error: Location Query Validation Failed", cursor, testValues);
        // Move the cursor to demonstrate that there is only one record in the database
        Assert.assertFalse( "Error: More than one record returned from location query", cursor.moveToNext());

        // Sixth Step: Close Cursor and Database
        cursor.close();
        return locationRowId;
    }




    // Since we want each test to start with a clean slate
    private void deleteTheDatabase() {
        mContext.deleteDatabase(WeatherDbHelper.DATABASE_NAME);
    }




    //Method to test normalize date function in WeatherContract.class
    @Test
    public void testNormalizeDateFunction(){
        long startDateTest = 20180928;
        long normalizedDate = WeatherContract.normalizeDate(startDateTest);
        System.out.println(TestDb.TAG + " = normalizedDate =  " + normalizedDate);
    }


    /***
     * We used this method before we had TestUtilities and short methods there to test Weather and Location Table.
     * We had to test location first by adding location and using it's locationRowId within Weather, therefore it was nested.
     * //Database insert operation test on Weather table and Location Table
     @Test
     public void testWeatherAndLocationTable(){

     // Test data we're going to insert into the DB to see if it works.
     ContentValues values = TestUtilities.createNorthPoleLocationValues();

     long locationRowId;
     locationRowId = db.insert(WeatherContract.LocationEntry.TABLE_NAME, null, values); //Put content values into "location" table

     //Verify if we got row back
     Assert.assertTrue(locationRowId != -1);
     System.out.println(TestDb.TAG + " = testInsertReadDb(): trying to test Inserting. locationRowId = " + locationRowId);

     // Data's inserted.  IN THEORY.  Now pull some out to stare at it and verify it made the round trip. Specify which columns you want.
     String[] columns = {
     WeatherContract.LocationEntry._ID,
     WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING,
     WeatherContract.LocationEntry.COLUMN_CITY_NAME,
     WeatherContract.LocationEntry.COLUMN_COORD_LAT,
     WeatherContract.LocationEntry.COLUMN_COORD_LONG
     };

     // A cursor is your primary interface to the query results.
     Cursor cursorLoc = db.query(
     WeatherContract.LocationEntry.TABLE_NAME,  // Table to Query
     columns, //String[] columns
     null, // Columns for the "where" clause
     null, // Values for the "where" clause
     null, // columns to group by
     null, // columns to filter by row groups
     null // sort order
     );

     // If possible, move to the first row of the query results.
     if (cursorLoc.moveToFirst()) {
     // Get the value in each column by finding the appropriate column index.
     int locationIndex = cursorLoc.getColumnIndex(WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING);
     String location = cursorLoc.getString(locationIndex);
     int nameIndex = cursorLoc.getColumnIndex((WeatherContract.LocationEntry.COLUMN_CITY_NAME));
     String name = cursorLoc.getString(nameIndex);
     int latIndex = cursorLoc.getColumnIndex((WeatherContract.LocationEntry.COLUMN_COORD_LAT));
     double latitude = cursorLoc.getDouble(latIndex);
     int longIndex = cursorLoc.getColumnIndex((WeatherContract.LocationEntry.COLUMN_COORD_LONG));
     double longitude = cursorLoc.getDouble(longIndex);
     // Hooray, data was returned!  Assert that it's the right data, and that the database creation code is working as intended.
     Assert.assertEquals("North Pole", name);
     Assert.assertEquals("99705", location);
     //Assert.assertEquals(testLatitude, latitude); //Method deprecated, we should use delta. Error returned = Use assertEquals(expected, actual, delta) to compare floating-point numbers
     Assert.assertEquals(64.772, latitude, 0.02); //Check that floating point numbers are equal within a certain tolerance
     Assert.assertEquals(147.355, longitude, 0.02);
     System.out.println("Returned Location data:  testCityName = " + name + ", testLocationSetting = " + location + ", latitude = " + latitude + ", longitude = " + longitude );


     // ===================================================Testing Weather Data=============================================================//
     ContentValues weatherValues = TestUtilities.createWeatherValues(locationRowId); //Use method inside TestUtilities to create Weather Content Values

     long weatherRowId;
     weatherRowId = db.insert(WeatherContract.WeatherEntry.TABLE_NAME, null, weatherValues); //Put content values into "weather" table
     Assert.assertTrue(weatherRowId != -1);
     System.out.println(TestDb.TAG + " = weatherRowId = " + weatherRowId);




     Cursor cursorWeather = db.query(
     WeatherContract.WeatherEntry.TABLE_NAME,  // Table to Query
     null, // leaving "columns" null just returns all the columns
     null, // Columns for the "where" clause
     null, // Values for the "where" clause
     null, // columns to group by
     null, // columns to filter by row groups
     null // sort order
     );


     if (cursorWeather.moveToFirst()){
     int dateIndex = cursorWeather.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_DATETEXT);
     String date = cursorWeather.getString(dateIndex);

     int degreeIndex = cursorWeather.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_DEGREES);
     String degree = cursorWeather.getString(degreeIndex);

     int humidIndex = cursorWeather.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_HUMIDITY);
     String humidity = cursorWeather.getString(humidIndex);

     int pressureIndex = cursorWeather.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_PRESSURE);
     String pressure = cursorWeather.getString(pressureIndex);

     int maxIndex = cursorWeather.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_MAX_TEMP);
     double max = cursorWeather.getDouble(maxIndex);

     int minIndex = cursorWeather.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_MIN_TEMP);
     double min = cursorWeather.getDouble(minIndex);

     int descIndex = cursorWeather.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_SHORT_DESC);
     String desc = cursorWeather.getString(descIndex);

     int windIndex = cursorWeather.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_WIND_SPEED);
     double windSpeed = cursorWeather.getDouble(windIndex);

     int weatherIdIndex = cursorWeather.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_WEATHER_ID);
     int weather_Id = cursorWeather.getInt(weatherIdIndex);

     System.out.println("Returned Weather data:  date = " + date + ", degree = " + degree + ", humidity = " + humidity + ", \n pressure = " + pressure +
     ", max = " + max + ", min = " + min + ", desc = " + desc + ", windSpeed = " + windSpeed + ", weather_Id = " + weather_Id);


     } else {
     Assert.fail("No weather data returned");
     }

     // ===================================================//Testing Weather Data=============================================================//




     } else {
     // That's weird, it works on MY machine...
     Assert.fail("No values returned :(");
     }



     }
     */





}
