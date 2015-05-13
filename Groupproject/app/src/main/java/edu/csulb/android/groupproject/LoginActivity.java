package edu.csulb.android.groupproject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseUser;
import com.parse.ui.ParseLoginBuilder;

/**
 * CECS 453 : Group Project
 * LoginActivity.java
 * purpose: Login view to log in, register, ask a forgot password
 *
 * @author Geoffrey Heckmann
 */
public class LoginActivity extends Activity {
    /** Number of login requests */
    private static final int LOGIN_REQUEST = 0;

    /** View components */
    private TextView titleTextView;
    private TextView emailTextView;
    private TextView nameTextView;

    /** GPS Tracker */
    private GPSTracker gps;

    /** Current User Parse Object */
    private ParseUser currentUser;

    /**
     * Get the view components and set the listeners
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.loginlayout);

        /** Get the view components */
        titleTextView = (TextView) findViewById(R.id.profile_title);
        emailTextView = (TextView) findViewById(R.id.profile_email);
        nameTextView = (TextView) findViewById(R.id.profile_name);
        titleTextView.setText(R.string.profile_title_logged_in);
    }

    /** Get the user currently logged in if there is one */
    @Override
    protected void onStart() {
        super.onStart();

        currentUser = ParseUser.getCurrentUser();
        if (currentUser != null) {
            showProfileLoggedIn();
        } else {
            showProfileLoggedOut();
        }
    }

    /**
     * Get the Location of the user and redirect him to the map activity
     */
    private void showProfileLoggedIn() {
        /** Create a new GPS tracker object */
        gps = new GPSTracker(LoginActivity.this);

        /** check if GPS enabled */
        if(gps.canGetLocation()){
            /** Save location in Parse */
            currentUser.put("latitude", gps.getLatitude());
            currentUser.put("longitude", gps.getLongitude());
            currentUser.saveInBackground();
         }else{
            /** can't get location
             * GPS or Network is not enabled
             * Ask user to enable GPS/network in
             */
            gps.showSettingsAlert();
        }
        /** Start map activity */
        Intent maps = new Intent(this, MapsActivity.class);
        startActivity(maps);

    }

    /**
     * Show the Login view
     */
    private void showProfileLoggedOut() {
        ParseLoginBuilder loginBuilder = new ParseLoginBuilder(
                LoginActivity.this);
        startActivityForResult(loginBuilder.build(), LOGIN_REQUEST);
    }
}
