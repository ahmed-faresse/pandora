package edu.csulb.android.groupproject;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.maps.android.clustering.ClusterItem;
import com.google.maps.android.clustering.ClusterManager;
import com.parse.FindCallback;
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
 * MapsActivity.java
 * purpose: Display maps and users of the application on it
 *
 * @author Geoffrey Heckmann
 */

public class MapsActivity extends FragmentActivity {
    /** Might be null if Google Play services APK is not available */
    private GoogleMap mMap;
    /** Cluster of Markers */
    private ClusterManager<MarkerCluster> mClusterManager;

    /**
     * Set up the map if it's not already done
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        setUpMapIfNeeded();
    }

    /**
     * Set up the map if it's not already done
     */
    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    /**
     * Set up the map if it's not already done
     */
    private void setUpMapIfNeeded() {
        /** Do a null check to confirm that we have not already instantiated the map */
        if (mMap != null)
        return;
            /** Try to obtain the map from the SupportMapFragment */
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            /** Check if we were successful in obtaining the map */
            if (mMap != null) {
                /** Add controls to the map */
                mMap.getUiSettings().setZoomControlsEnabled(true);
                mMap.getUiSettings().setZoomGesturesEnabled(true);

                /** Initialize the Cluster Manager */
                mClusterManager = new ClusterManager<>(getApplicationContext(), mMap);

                /** Set listeners on the map */
                mMap.setOnCameraChangeListener(mClusterManager);
                mMap.setOnMarkerClickListener(mClusterManager);

                /** Add the markers to the map */
                setUpMap();
        }
    }

    /**
     * Transform a picture into a rounded and scaled one
     * @param bitmap the bitmap to transform
     * @param pixels the radius of round image
     * @return The bitmap generated
     */
    private Bitmap getScaledRoundedRectBitmap(Bitmap bitmap, int pixels) {
        Bitmap result = null;
        try {
            /** Create the bitmap with options */
            result = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(),
            Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(result);
            int color = 0xff424242;
            Paint paint = new Paint();
            Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
            RectF rectF = new RectF(rect);
            int roundPx = pixels;
            paint.setAntiAlias(true);
            canvas.drawARGB(0, 0, 0, 0);
            paint.setColor(color);
            canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

            /** Draw the original bitmap rounded and scaled to the result bitmap */
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
            canvas.drawBitmap(bitmap, rect, rect, paint);
        } catch (NullPointerException e) {
        }
        return result;
    }

    /**
     * Add a marker with the information from Parse
     * @param lat latitude
     * @param lon longitude
     * @param name name of the user
     * @param desc description of the user
     * @param id_img image id
     */
    private void AddCustomMarker(double lat, double lon, String name,
                                 String desc, int id_img)
    {
        if (mMap != null) {
            Bitmap bitmap;
            Boolean exists = false;
            /** Create a scaled bitmap */
            bitmap = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
                        getResources(), id_img), 50, 50, false);

            /** Round it */
            Bitmap scaledBitmap = getScaledRoundedRectBitmap(bitmap, 100);
            BitmapDescriptor icon = BitmapDescriptorFactory.fromBitmap(scaledBitmap);

            /** Create a new MarkerItem for the cluster */
            MarkerCluster item = new MarkerCluster(lat, lon, name, desc, icon);

            /** If the marker does not exist, add it to the cluster */
            for (Marker m : mClusterManager.getMarkerCollection().getMarkers())
                if (m.getTitle().compareTo(item.getTitle()) == 0)
                    exists = true;
            if (!exists)
                mClusterManager.addItem(item);
        }
    }


    /**
     *  Add Custom marker with the photo from Facebook or Twitter
     */
    private void AddCustomMarkerTwFB(double lat, double lon, String name,
                                 String desc, String url)
    {
        if (mMap != null) {
             try {
                    /** Get the image asynchronously */
                    new LoadProfileImage(lat, lon, name, desc).execute(url);
                }
             catch (Exception e)
             {
                    e.printStackTrace();
             }
            }
    }

    /**
     * Class to get asynchronously the image of Twitter or Facebook user and create the marker
     */
    private class LoadProfileImage extends AsyncTask<String, Void, Bitmap> {
        /** User information displayed on the marker */
        private double lat;
        private double lon;
        private String name;
        private String desc;

        public LoadProfileImage(double lat, double lon, String name, String desc) {
            this.lat = lat;
            this.lon = lon;
            this.name = name;
            this.desc = desc;
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

        /** Once the request is finished, create the marker the same way as a nomal user */
        protected void onPostExecute(Bitmap result) {
            Bitmap scaledBitmap = getScaledRoundedRectBitmap(result, 100);
            BitmapDescriptor icon = BitmapDescriptorFactory.fromBitmap(scaledBitmap);
            MarkerCluster item = new MarkerCluster(lat, lon, name, desc, icon);
            mClusterManager.addItem(item);
            mClusterManager.cluster();

        }
    }

    /**
     * Place the markers on the maps and set the listeners
     */
    private void setUpMap()
    {
        /** Parse query to get the users */
        final ParseQuery<ParseUser> query = ParseQuery.getQuery(ParseUser.class);
        query.findInBackground(new FindCallback<ParseUser>() {
                                   public void done(List<ParseUser> objects, ParseException e) {
                                       if (objects == null || objects.size() == 0) {
                                           /** No object */
                                           return;
                                       }

                                       /** For each user, get the information and call the functions
                                        * to create a marker and add it to the map
                                        */
                                       for (int i = 0; i < objects.size(); i++) {
                                           ParseObject object = objects.get(i);
                                           try {
                                               double lat = (double)object.get("latitude");
                                               double lng = (double)object.get("longitude");
                                               String name = object.get("name").toString();
                                               String profession = object.get("profession").toString();
                                               /** Facebook User */
                                               if (object.get("social").toString().compareTo("facebook") == 0) {
                                                   String url = "https://graph.facebook.com/" + (String) object.get("facebook_id") + "/picture?type=small";
                                                   AddCustomMarkerTwFB(lat, lng, name,
                                                           profession, url);
                                               }
                                               /** Twitter User */
                                               else if (object.get("social").toString().compareTo("twitter") == 0) {
                                                   String url = "https://twitter.com/" + name + "/profile_image?size=normal";
                                                   AddCustomMarkerTwFB(lat, lng, name,
                                                           profession, url);
                                               }
                                               /** Normal user */
                                               else
                                                   AddCustomMarker(lat, lng, name, profession, R.drawable.geoffrey);
                                           }
                                           catch (Exception ex)
                                           {
                                               ex.printStackTrace();
                                           }
                                       }
                                   }
                               });

        /** Move the camera to California */
        if (mMap != null)
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(33.783768,-118.114336), 10));

        /** Call a function when a marker infoWindow is clicked to access his profile */
        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {

            @Override
            public void onInfoWindowClick(Marker marker) {
                Intent intent = new Intent(getApplicationContext(),ProfileActivity.class);
                String name = marker.getTitle();
                intent.putExtra("EXTRA",name);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId())
        {
            /** Logout Button */
            case R.id.Logout:
                ParseUser.logOut();
                Intent login = new Intent(this, LoginActivity.class);
                startActivity(login);
                finish();
                break;
            /** Account page button */
            case R.id.Account:
                Intent account = new Intent(this, AccountActivity.class);
                startActivity(account);
                break;
        }
        return true;
    }
}
