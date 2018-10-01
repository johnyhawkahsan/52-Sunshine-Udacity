package com.johnyhawkdesigns.a52_sunshine_udacity;

import com.johnyhawkdesigns.a52_sunshine_udacity.data.ExampleInstrumentedTest;
import com.johnyhawkdesigns.a52_sunshine_udacity.data.TestDb;
import com.johnyhawkdesigns.a52_sunshine_udacity.data.TestUriMatcher;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

//This is standard TestSuite method by Java. https://www.tutorialspoint.com/junit/junit_suite_test.htm
@RunWith(Suite.class)
@Suite.SuiteClasses({
        ExampleInstrumentedTest.class,
        TestDb.class,
        TestUriMatcher.class,
})

public class FullTestSuite {
    //Empty constructor
}

/**
 //I used this method before, according to Android Documentation but instructions are unclear. https://developer.android.com/reference/junit/framework/TestSuite
 @org.junit.Test
 public static Test suite(){
        TestSuite testSuite = new TestSuite();
        testSuite.addTest(TestSuite.createTest(TestDb.class, "Test"));
        return testSuite;
 }
 */



