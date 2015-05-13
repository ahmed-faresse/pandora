package edu.csulb.android.groupproject;

import com.parse.Parse;
import android.app.Application;

/**
 * CECS 453 : Group Project
 * App.java
 * purpose: Initialize the Parse library for all the activity
 *
 * @author Geoffrey Heckmann
 */

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        /** Enable the storage of information on the phone */
        Parse.enableLocalDatastore(this);
        /** Initialize the Parse library with the APP ID and APP KEY */
        Parse.initialize(this, "fdgb3DiwkZokytY9wwMjxCZXLXnRiRK69YpPPhtX",
                "Kui6CxsP31Wt54V3P4DciY38k6ODbBPtFMGD6khP");
    }
}