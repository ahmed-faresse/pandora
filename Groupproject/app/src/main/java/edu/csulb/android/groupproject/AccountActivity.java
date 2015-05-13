package edu.csulb.android.groupproject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;


/**
 * CECS 453 : Group Project
 * AccountActivity.java
 * purpose: View of the profile of the current user. He has the ability to change the fields
 *
 * @author Ahmed Faresse Ali
 */
public class AccountActivity extends Activity {
    /** The information of the user */
    private TextView t_name,t_age,t_profession,t_hobbies,t_description;
    /** Button to save the information and go back to the map */
    private Button btnsave, btnback;

    /** The Current user Parse Object */
    private ParseUser currentUser;

    /**
     * OnCreate method. Get the view components and set the callbacks
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.accountactivity);

        /** Get the view components */
        t_name = (TextView) findViewById(R.id.name);
        t_age = (TextView) findViewById(R.id.age);
        t_profession = (TextView) findViewById(R.id.profession);
        t_hobbies = (TextView) findViewById(R.id.hobbies);
        t_description = (TextView) findViewById(R.id.description);

        /** Get the user information */
        get_info_user();


        /** Set the button to return to the map activity */
        btnback = (Button)findViewById(R.id.backmap);
        btnback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent map = new Intent(v.getContext(), MapsActivity.class);
                startActivity(map);
                finish();
            }
        });

        /** Set the button to save the change made by the user */
        btnsave = (Button) findViewById(R.id.btnsave);
        btnsave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fill_account(v);
            }
        });
    }


    /** Save the change in the parse cloud */
    public void fill_account(View v){
        currentUser = ParseUser.getCurrentUser();
        currentUser.put("name", t_name.getText().toString());
        currentUser.put("age",t_age.getText().toString());
        currentUser.put("profession",t_profession.getText().toString());
        currentUser.put("hobbies",t_hobbies.getText().toString());
        currentUser.put("description", t_description.getText().toString());
        currentUser.saveInBackground();
        Toast.makeText(this,"Save success",Toast.LENGTH_LONG).show();
    }

    /** Get the information of the user add fill the view components with it */
    public void get_info_user(){
        /** Select query in parse */
        ParseQuery<ParseUser> query = ParseQuery.getQuery(ParseUser.class);
        query.getInBackground(ParseUser.getCurrentUser().getObjectId(), new GetCallback<ParseUser>() {
            public void done(ParseUser parseUser, ParseException e) {
                /** Information found */
                if (e == null){
                    t_name.setText(parseUser.get("name").toString());
                    t_age.setText(parseUser.get("age").toString());
                    t_profession.setText(parseUser.get("profession").toString());
                    t_hobbies.setText(parseUser.get("hobbies").toString());
                    t_description.setText(parseUser.get("description").toString());
                }
                /** Information not found */
                else{
                    t_name.setText(parseUser.get("Unknown").toString());
                    t_age.setText(parseUser.get("Unknown").toString());
                    t_profession.setText(parseUser.get("Unknown").toString());
                    t_hobbies.setText(parseUser.get("Unknown").toString());
                    t_description.setText(parseUser.get("Unknown").toString());
                }
            }
        });
    }

}
