package edu.csulb.android.groupproject;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseFacebookUtils;
import com.parse.ParseTwitterUtils;

/**
 * CECS 453 : Group Project
 * Login.java
 * purpose: Initialize the Login with Parse Library, Twitter and Facebook
 *
 * @author Geoffrey Heckmann
 */
public class Login extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        Parse.initialize(this);

        Parse.setLogLevel(Parse.LOG_LEVEL_DEBUG);

        ParseFacebookUtils.initialize(this);

        ParseTwitterUtils.initialize(getString(R.string.twitter_consumer_key),
                getString(R.string.twitter_consumer_secret));
    }
}
