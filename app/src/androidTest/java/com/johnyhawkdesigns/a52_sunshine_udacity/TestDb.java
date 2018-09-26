package com.johnyhawkdesigns.a52_sunshine_udacity;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import com.johnyhawkdesigns.a52_sunshine_udacity.data.WeatherContract;
import com.johnyhawkdesigns.a52_sunshine_udacity.data.WeatherDbHelper;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class TestDb{

    private static final String TAG = TestDb.class.getSimpleName();
    private SQLiteDatabase db;
    Context mContext = InstrumentationRegistry.getTargetContext();

    @Before
    public void setUp(){
        mContext.deleteDatabase(WeatherDbHelper.DATABASE_NAME); //We delete any previous database, starting out clear
        db = new WeatherDbHelper(mContext).getWritableDatabase();
        System.out.println(TestDb.TAG + " = setUp()");

    }

    @After
    public void finish(){
        db.close();
        System.out.println(TestDb.TAG + " = finish(): db.close()");

    }

    @Test
    public void testPreConditions(){
        Assert.assertNotNull(db);
        System.out.println(TestDb.TAG + " = testPreConditions(): checking if db object is not null");

    }

    //Testing creation of Database, if Database is open or not
    @Test
    public void testCreateDb(){
        Assert.assertEquals("Database is not open error",true, db.isOpen());
        System.out.println(TestDb.TAG + " = testCreateDb(): running Assert.assertEquals method to ensure Database is open");
    }

    //Database insert operation test
    @Test
    public void testInsertReadDb(){

        // Test data we're going to insert into the DB to see if it works.
        String testCityName  = "North Pole";
        String testLocationSetting = "99705";
        double testLatitude = 64.772;
        double testLongitude = 147.355;

        ContentValues values = new ContentValues();
        values.put(WeatherContract.LocationEntry.COLUMN_CITY_NAME, testCityName );
        values.put(WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING, testLocationSetting);
        values.put(WeatherContract.LocationEntry.COLUMN_COORD_LAT, testLatitude);
        values.put(WeatherContract.LocationEntry.COLUMN_COORD_LONG, testLongitude);

        long locationRowId;
        locationRowId = db.insert(WeatherContract.LocationEntry.TABLE_NAME, null, values); //Put content values into "location" table

        //Verify if we got row back
        Assert.assertTrue(locationRowId != -1);
        System.out.println(TestDb.TAG + " = testInsertReadDb(): trying to test Inserting. locationRowId = " + locationRowId);



        // Data's inserted.  IN THEORY.  Now pull some out to stare at it and verify it made the round trip.
        // Specify which columns you want.
        String[] columns = {
                WeatherContract.LocationEntry._ID,
                WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING,
                WeatherContract.LocationEntry.COLUMN_CITY_NAME,
                WeatherContract.LocationEntry.COLUMN_COORD_LAT,
                WeatherContract.LocationEntry.COLUMN_COORD_LONG
        };

        // A cursor is your primary interface to the query results.
        Cursor cursor = db.query(
                WeatherContract.LocationEntry.TABLE_NAME,  // Table to Query
                columns, //String[] columns
                null, // Columns for the "where" clause
                null, // Values for the "where" clause
                null, // columns to group by
                null, // columns to filter by row groups
                null // sort order
        );

        // If possible, move to the first row of the query results.
        if (cursor.moveToFirst()) {
            // Get the value in each column by finding the appropriate column index.
            int locationIndex = cursor.getColumnIndex(WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING);
            String location = cursor.getString(locationIndex);
            int nameIndex = cursor.getColumnIndex((WeatherContract.LocationEntry.COLUMN_CITY_NAME));
            String name = cursor.getString(nameIndex);
            int latIndex = cursor.getColumnIndex((WeatherContract.LocationEntry.COLUMN_COORD_LAT));
            double latitude = cursor.getDouble(latIndex);
            int longIndex = cursor.getColumnIndex((WeatherContract.LocationEntry.COLUMN_COORD_LONG));
            double longitude = cursor.getDouble(longIndex);
            // Hooray, data was returned!  Assert that it's the right data, and that the database creation code is working as intended.
            Assert.assertEquals(testCityName, name);
            Assert.assertEquals(testLocationSetting, location);
            //Assert.assertEquals(testLatitude, latitude); //Method deprecated, we should use delta. Error returned = Use assertEquals(expected, actual, delta) to compare floating-point numbers
            Assert.assertEquals(testLatitude, latitude, 0.02); //Check that floating point numbers are equal within a certain tolerance
            Assert.assertEquals(testLongitude, longitude, 0.02);
            System.out.println("Returned Location data:  testCityName = " + name + ", testLocationSetting = " + location + ", latitude = " + latitude + ", longitude = " + longitude );

        } else {
            // That's weird, it works on MY machine...
            Assert.fail("No values returned :(");
        }


        // ===================================================Testing Weather Data=============================================================//
        ContentValues weatherValues = new ContentValues();
        weatherValues.put(WeatherContract.WeatherEntry.COLUMN_LOC_KEY, locationRowId);
        weatherValues.put(WeatherContract.WeatherEntry.COLUMN_DATETEXT, "20141205");
        weatherValues.put(WeatherContract.WeatherEntry.COLUMN_DEGREES, 1.1);
        weatherValues.put(WeatherContract.WeatherEntry.COLUMN_HUMIDITY, 1.2);
        weatherValues.put(WeatherContract.WeatherEntry.COLUMN_PRESSURE, 1.3);
        weatherValues.put(WeatherContract.WeatherEntry.COLUMN_MAX_TEMP, 75);
        weatherValues.put(WeatherContract.WeatherEntry.COLUMN_MIN_TEMP, 65);
        weatherValues.put(WeatherContract.WeatherEntry.COLUMN_SHORT_DESC, "Asteroids");
        weatherValues.put(WeatherContract.WeatherEntry.COLUMN_WIND_SPEED, 5.5);
        weatherValues.put(WeatherContract.WeatherEntry.COLUMN_WEATHER_ID, 321);

        long weatherRowId;
        weatherRowId = db.insert(WeatherContract.WeatherEntry.TABLE_NAME, null, weatherValues); //Put content values into "weather" table
        Assert.assertTrue(weatherRowId != -1);
        System.out.println(TestDb.TAG + " = weatherRowId = " + weatherRowId);


        Cursor weatherCursor = db.query(
                WeatherContract.WeatherEntry.TABLE_NAME,  // Table to Query
                null, // leaving "columns" null just returns all the columns
                null, // Columns for the "where" clause
                null, // Values for the "where" clause
                null, // columns to group by
                null, // columns to filter by row groups
                null // sort order
        );

        if (weatherCursor.moveToFirst()){
            int dateIndex = cursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_DATETEXT);
            String date = cursor.getString(dateIndex);

            int degreeIndex = cursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_DEGREES);
            String degree = cursor.getString(degreeIndex);

            int humidIndex = cursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_HUMIDITY);
            String humidity = cursor.getString(humidIndex);

            int pressureIndex = cursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_PRESSURE);
            String pressure = cursor.getString(pressureIndex);

            int maxIndex = cursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_MAX_TEMP);
            double max = cursor.getDouble(maxIndex);

            int minIndex = cursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_MIN_TEMP);
            double min = cursor.getDouble(minIndex);

            int descIndex = cursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_SHORT_DESC);
            String desc = cursor.getString(descIndex);

            int windIndex = cursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_WIND_SPEED);
            double windSpeed = cursor.getDouble(windIndex);

            int weatherIdIndex = cursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_WEATHER_ID);
            int weather_Id = cursor.getInt(weatherIdIndex);

            System.out.println("Returned Weather data:  date = " + date + ", degree = " + degree + ", humidity = " + humidity + ", \n pressure = " + pressure +
                    ", max = " + max + ", min = " + min + ", desc = " + desc + ", windSpeed = " + windSpeed + ", weather_Id = " + weather_Id);


        } else {
            Assert.fail("No weather data returned");
        }

    }




}
