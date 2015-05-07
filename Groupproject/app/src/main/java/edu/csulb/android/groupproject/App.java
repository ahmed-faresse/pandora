package edu.csulb.android.groupproject;

import com.parse.Parse;
import android.app.Application;

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        Parse.enableLocalDatastore(this);
        Parse.initialize(this, "fdgb3DiwkZokytY9wwMjxCZXLXnRiRK69YpPPhtX",
                "Kui6CxsP31Wt54V3P4DciY38k6ODbBPtFMGD6khP");
    }
}