package com.revolut.moneytransferapi.controller;

import cucumber.api.junit.Cucumber;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;

import com.revolut.moneytransferapi.App;
import com.revolut.moneytransferapi.dao.AbstractDaoIntegrationTest;

import spark.Spark;

@RunWith(Cucumber.class)
public class APIAcceptanceTest {

    @BeforeClass
    public static void start() {
        AbstractDaoIntegrationTest.init();

        String[] args = {};
        App.main(args);
    }

    @AfterClass
    public static void stop() {
        Spark.stop();
        AbstractDaoIntegrationTest.tearDown();
    }
}
