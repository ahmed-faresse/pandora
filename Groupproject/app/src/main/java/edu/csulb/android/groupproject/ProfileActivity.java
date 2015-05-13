package edu.csulb.android.groupproject;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.style.ImageSpan;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;


/**
 * CECS 453 : Group Project
 * ProfileActivity.java
 * purpose: View of the profile of a user
 *
 * @author Ahmed Faresse Ali
 */
public class ProfileActivity extends Activity {
    /** Information of the user */
    private TextView t_name, t_age,t_profession,t_description,t_hobbies;
    private ImageView profile_image;
    private String extra_name;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);

        /** Get the view components */
        t_name = (TextView) findViewById(R.id.name);
        t_age = (TextView) findViewById(R.id.age);
        t_profession = (TextView) findViewById(R.id.profession);
        t_description = (TextView) findViewById(R.id.description);
        t_hobbies = (TextView) findViewById(R.id.hobbies);
        profile_image = (ImageView) findViewById(R.id.imageprofile);

        /** Fill the components with the information of parse */
        if (savedInstanceState == null){
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                extra_name= null;
            } else {
                extra_name= extras.getString("EXTRA");
            }
        } else {
            extra_name= (String) savedInstanceState.getSerializable("EXTRA");
        }
        get_info_user(extra_name);

    }

    /**
     * Fill the components with the information of parse
     * @param extra name of the user to search
     */
    public void get_info_user(String extra){
        /** Parse select request to get the user information */
        ParseQuery<ParseUser> query = ParseQuery.getQuery(ParseUser.class);
        query.whereEqualTo("name", extra);
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> parseUsers, ParseException e) {
                /** Fill the view components with the information of the request */
                if (e == null){
                    ParseObject object = parseUsers.get(0);
                    LoadProfileImage loadProfileImage = new LoadProfileImage();
                    t_name.setText(object.get("name").toString());
                    t_age.setText(object.get("age").toString() + " " + "years old");
                    t_profession.setText(object.get("profession").toString());
                    t_hobbies.setText(object.get("hobbies").toString());
                    t_description.setText(object.get("description").toString());
                    if (object.get("social").toString().compareTo("facebook") == 0) {
                        String url = "https://graph.facebook.com/" + object.get("facebook_id") + "/picture?type=large";
                        loadProfileImage.execute(url);
                    }
                    else if (object.get("social").toString().compareTo("twitter") == 0) {
                        String url = "https://twitter.com/" + object.get("name").toString() + "/profile_image?size=original";
                        loadProfileImage.execute(url);

                    }
                    else {
                        Drawable mydrawable = getResources().getDrawable(R.drawable.bb);
                        profile_image.setImageDrawable(mydrawable);
                    }
                }
                /** If something goes wrong, display an unknown user */
                else{
                    t_name.setText("Unknown");
                    t_age.setText("Unknown");
                    t_profession.setText("Unknown");
                    t_hobbies.setText("Unknown");
                    t_description.setText("Unknown");
                    Drawable mydrawable = getResources().getDrawable(R.drawable.bb);
                    profile_image.setImageDrawable(mydrawable);
                }
            }
        });
    }

    /**
     * Transform a bitmap to a rounded shape one
     * @param scaleBitmapImage the original bitmap
     * @return the Bitmap created
     */
    public Bitmap getRoundedShape(Bitmap scaleBitmapImage) {
        /** Create the bitmap with options */
        int targetWidth = 150;
        int targetHeight = 150;
        Bitmap targetBitmap = Bitmap.createBitmap(targetWidth,
                targetHeight,Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(targetBitmap);
        Path path = new Path();
        path.addCircle(((float) targetWidth - 1) / 2,
                ((float) targetHeight - 1) / 2,
                (Math.min(((float) targetWidth),
                        ((float) targetHeight)) / 2),
                Path.Direction.CCW);

        canvas.clipPath(path);
        Bitmap sourceBitmap = scaleBitmapImage;

        /** Draw the original bitmap rounded result bitmap */
        canvas.drawBitmap(sourceBitmap,
                new Rect(0, 0, sourceBitmap.getWidth(),
                        sourceBitmap.getHeight()),
                new Rect(0, 0, targetWidth, targetHeight), null);
        return targetBitmap;
    }

    /**
     * Class to get asynchronously the image of Twitter or Facebook user and create the marker
     */
    private class LoadProfileImage extends AsyncTask<String, Void, Bitmap> {

        public LoadProfileImage() {
        }

        /** Get the Bitmap of the Facebook/Twitter user profile picture */
        protected Bitmap doInBackground(String... params){
            Bitmap bitmap = null;
            try {
                URL url = new URL(params[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                Bitmap myBitmap = BitmapFactory.decodeStream(input);

                return myBitmap;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return bitmap;
        }

        /** Once the request is finished, display it */
        protected void onPostExecute(Bitmap result) {
            Bitmap scaledBitmap = getRoundedShape(result);
            //BitmapDescriptor icon = BitmapDescriptorFactory.fromBitmap(scaledBitmap);
            profile_image.setImageBitmap(scaledBitmap);
        }
    }

}
