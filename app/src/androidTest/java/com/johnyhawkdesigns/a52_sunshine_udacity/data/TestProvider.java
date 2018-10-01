package com.johnyhawkdesigns.a52_sunshine_udacity.data;

import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
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
    }

    @After
    public void finish(){
        System.out.println(TAG + " = finish()");
    }

    //This function tests that your UriMatcher returns the correct integer value for each of the Uri types that our ContentProvider can handle.
    @Test
    public void testUriMatcher(){

    }

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
        Cursor cursor = mContext.getContentResolver().query(
                WeatherContract.LocationEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        Assert.assertEquals("Error: Records not deleted from Weather table during delete", 0, cursor.getCount());
        cursor.close();

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
