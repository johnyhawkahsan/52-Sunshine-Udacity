package com.johnyhawkdesigns.a52_sunshine_udacity;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

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

    @Test
    public void testCreateDb(){

        //InstrumentationRegistry.getContext().deleteDatabase(WeatherDbHelper.class.) //Finally InstrumentationRegistry is found to get context, but deleting database is not working
        Assert.assertEquals("Database is not open error",true, db.isOpen());
        System.out.println(TestDb.TAG + " = testCreateDb(): running Assert.assertEquals");
    }

}
