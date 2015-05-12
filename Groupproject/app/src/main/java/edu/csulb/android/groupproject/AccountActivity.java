package edu.csulb.android.groupproject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.w3c.dom.Text;

import java.util.List;


/**
 * Created by ahmed on 07/05/15.
 */
public class AccountActivity extends Activity {
    private TextView t_localisation,t_name,t_age,t_profession,t_hobbies,t_description;
    private Button btnsave, btnback;

    private ParseUser currentUser;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.accountactivity );

        Parse.initialize(this, "fdgb3DiwkZokytY9wwMjxCZXLXnRiRK69YpPPhtX",
                "Kui6CxsP31Wt54V3P4DciY38k6ODbBPtFMGD6khP");

        t_name = (TextView) findViewById(R.id.name);
        t_age = (TextView) findViewById(R.id.age);
        t_profession = (TextView) findViewById(R.id.profession);
        t_hobbies = (TextView) findViewById(R.id.hobbies);
        t_description = (TextView) findViewById(R.id.description);

        get_info_user();


        btnback = (Button)findViewById(R.id.backmap);
        btnback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent map = new Intent(v.getContext(), MapsActivity.class);
                startActivity(map);
                finish();
            }
        });
        btnsave = (Button) findViewById(R.id.btnsave);
        btnsave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fill_account(v);
            }
        });
    }


    public void fill_account(View v){
        currentUser = ParseUser.getCurrentUser();

        Log.e("Ahmed:", t_age.getText().toString());
        currentUser.put("name", t_name.getText().toString());
        currentUser.put("age",t_age.getText().toString());
        currentUser.put("profession",t_profession.getText().toString());
        currentUser.put("hobbies",t_hobbies.getText().toString());
        currentUser.put("description", t_description.getText().toString());
        currentUser.saveInBackground();
        Toast.makeText(this,"Save success",Toast.LENGTH_LONG).show();
    }

    public void get_info_user(){
        Log.e("ObjectId", ParseUser.getCurrentUser().getObjectId());
        ParseQuery<ParseUser> query = ParseQuery.getQuery(ParseUser.class);
        query.getInBackground(ParseUser.getCurrentUser().getObjectId(), new GetCallback<ParseUser>() {
            public void done(ParseUser parseUser, ParseException e) {
                if (e == null){
                    t_name.setText(parseUser.get("name").toString());
                    t_age.setText(parseUser.get("age").toString());
                    t_profession.setText(parseUser.get("profession").toString());
                    t_hobbies.setText(parseUser.get("hobbies").toString());
                    t_description.setText(parseUser.get("description").toString());
                }else{
                    //something wrong
                    Log.e("Error","Wrong");
                }
            }
        });
    }

}
