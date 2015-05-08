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
 * Created by ahmed on 08/05/15.
 */
public class ProfileActivity extends Activity {
    private TextView t_name, t_age,t_profession,t_description,t_hobbies;
    private ImageView profile_image,icon;
    private String extra_name;
    private TextView t_title_description;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);

        Parse.initialize(this, "fdgb3DiwkZokytY9wwMjxCZXLXnRiRK69YpPPhtX",
                "Kui6CxsP31Wt54V3P4DciY38k6ODbBPtFMGD6khP");

        t_name = (TextView) findViewById(R.id.name);
        t_age = (TextView) findViewById(R.id.age);
        t_profession = (TextView) findViewById(R.id.profession);
        t_description = (TextView) findViewById(R.id.description);
        t_hobbies = (TextView) findViewById(R.id.hobbies);
        profile_image = (ImageView) findViewById(R.id.imageprofile);
        t_title_description = (TextView) findViewById(R.id.title_description);
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


    public void get_info_user(String extra){
        ParseQuery<ParseUser> query = ParseQuery.getQuery(ParseUser.class);
        query.whereEqualTo("name", extra);
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> parseUsers, ParseException e) {
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
                }else{
                    //something wrong
                    Log.e("Error","Wrong");
                }
            }
        });
    }

    public Bitmap getRoundedShape(Bitmap scaleBitmapImage) {
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
        canvas.drawBitmap(sourceBitmap,
                new Rect(0, 0, sourceBitmap.getWidth(),
                        sourceBitmap.getHeight()),
                new Rect(0, 0, targetWidth, targetHeight), null);
        return targetBitmap;
    }

    private class LoadProfileImage extends AsyncTask<String, Void, Bitmap> {

        public LoadProfileImage() {
        }

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

        protected void onPostExecute(Bitmap result) {
            Bitmap scaledBitmap = getRoundedShape(result);
            //BitmapDescriptor icon = BitmapDescriptorFactory.fromBitmap(scaledBitmap);
            profile_image.setImageBitmap(scaledBitmap);
        }
    }

}
