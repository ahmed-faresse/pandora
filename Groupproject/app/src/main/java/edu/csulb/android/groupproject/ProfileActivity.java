package edu.csulb.android.groupproject;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import com.parse.GetCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by ahmed on 08/05/15.
 */
public class ProfileActivity extends Activity {
    private TextView t_name, t_age,t_profession,t_description,t_hobbies;
    private ImageView profile_image;

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
        get_info_user();

    }


    public void get_info_user(){
        ParseQuery<ParseUser> query = ParseQuery.getQuery(ParseUser.class);
        query.getInBackground(extra_name, new GetCallback<ParseUser>() {
            public void done(ParseUser parseUser, ParseException e) {
                if (e == null){
                    LoadProfileImage loadProfileImage = new LoadProfileImage();
                    t_name.setText(parseUser.get("name").toString());
                    t_age.setText(parseUser.get("age").toString());
                    t_profession.setText(parseUser.get("profession").toString());
                    t_hobbies.setText(parseUser.get("hobbies").toString());
                    t_description.setText(parseUser.get("description").toString());
                    if (parseUser.get("social").toString().compareTo("facebook") == 0) {
                        String url = "https://graph.facebook.com/" + parseUser.get("facebook_id") + "/picture?type=normal";
                        loadProfileImage.execute(url);
                    }
                    else if (parseUser.get("social").toString().compareTo("twitter") == 0) {
                        String url = "https://twitter.com/" + parseUser.get("name").toString() + "/profile_image?size=normal";
                        loadProfileImage.execute(url);

                    }
                    else {
                        profile_image.setImageResource(R.drawable.geoffrey);
                    }
                }else{
                    //something wrong
                    Log.e("Error","Wrong");
                }
            }
        });
    }

    public Bitmap getRoundedShape(Bitmap scaleBitmapImage) {
        int targetWidth = 50;
        int targetHeight = 50;
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
