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

    public long insertLocation(){
        long test = 0;
        return test;
    }

    //Database insert operation test
    @Test
    public void testInsertReadDb(){

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

    //Method to test normalize date function in WeatherContract.class
    @Test
    public void testNormalizeDateFunction(){
        long startDateTest = 20180928;
        long normalizedDate = WeatherContract.normalizeDate(startDateTest);
        System.out.println(TestDb.TAG + " = normalizedDate =  " + normalizedDate);
    }




}
