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
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MapsActivity extends FragmentActivity {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        setUpMapIfNeeded();
        if (mMap != null)
        {
            mMap.getUiSettings().setZoomControlsEnabled(true);
            mMap.getUiSettings().setZoomGesturesEnabled(true);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
  //      setUpMapIfNeeded();
        setUpMap();
    }

    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    private Bitmap getScaledRoundedRectBitmap(Bitmap bitmap, int pixels) {
        Bitmap result = null;
        try {

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

            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
            canvas.drawBitmap(bitmap, rect, rect, paint);
        } catch (NullPointerException e) {
        } catch (OutOfMemoryError o){}
        return result;
    }

    private void AddCustomMarker(double lat, double lon, String name,
                                 String desc, int id_img)
    {
        if (mMap != null) {
            Bitmap bitmap;

            bitmap = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
                        getResources(), id_img), 50, 50, false);

            Bitmap scaledBitmap = getScaledRoundedRectBitmap(bitmap, 100);
            BitmapDescriptor icon = BitmapDescriptorFactory.fromBitmap(scaledBitmap);
            MarkerOptions marker = new MarkerOptions()
                    .position(new LatLng(lat, lon))
                    .title(name)
                    .snippet(desc)
                    .icon(icon);

            mMap.addMarker(marker);
        }
    }


    private void AddCustomMarkerTwFB(double lat, double lon, String name,
                                 String desc, String url)
    {
        if (mMap != null) {
             try {
                    LoadProfileImage Loader = new LoadProfileImage(lat, lon, name, desc);
                    Loader.execute(url);
                }
             catch (Exception e)
             {
                    e.printStackTrace();
             }
            }
    }

    private class LoadProfileImage extends AsyncTask<String, Void, Bitmap> {
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
            Bitmap scaledBitmap = getScaledRoundedRectBitmap(result, 100);
            BitmapDescriptor icon = BitmapDescriptorFactory.fromBitmap(scaledBitmap);
            MarkerOptions marker = new MarkerOptions()
                    .position(new LatLng(lat, lon))
                    .title(name)
                    .snippet(desc)
                    .icon(icon);
            mMap.addMarker(marker);
        }
    }


    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap()
    {
        final ParseQuery<ParseUser> query = ParseQuery.getQuery(ParseUser.class);
//        query.whereEqualTo("owners", ParseUser.getCurrentUser());
        query.findInBackground(new FindCallback<ParseUser>() {
                                   public void done(List<ParseUser> objects, ParseException e) {
                                       if (objects == null || objects.size() == 0) {
                                           return; //no objects
                                       }

                                       for (int i = 0; i < objects.size(); i++) {
                                           ParseObject object = objects.get(i);
                                           try {
                                               double lat = (double)object.get("latitude");
                                               double lng = (double)object.get("longitude");
                                               String name = object.get("name").toString();
                                               if (object.get("social").toString().compareTo("facebook") == 0) {
                                                   String url = "https://graph.facebook.com/" + (String) object.get("facebook_id") + "/picture?type=small";
                                                   AddCustomMarkerTwFB(lat, lng, name,
                                                           "Renverseur de Shaker", url);
                                               }
                                               else if (object.get("social").toString().compareTo("twitter") == 0) {
                                                   String url = "https://twitter.com/" + name + "/profile_image?size=normal";
                                                   AddCustomMarkerTwFB(lat, lng, name,
                                                           "Renverseur de Shaker", url);
                                               }
                                               else
                                                   AddCustomMarker(lat, lng, name, "local", R.drawable.geoffrey);
                                           }
                                           catch (Exception ex)
                                           {
                                               ex.printStackTrace();
                                           }
                                       }
                                   }
                               });

        if (mMap != null)
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(33.783768,-118.114336), 10));

        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {

            @Override
            public void onInfoWindowClick(Marker marker) {
                //Intent intent = new Intent(MainActivity.this,Example.class);
                //startActivity(intent);
                Log.d("artaerteratae", marker.getTitle() + "yuytdyr");
                Toast.makeText(getApplicationContext(), marker.getTitle(), Toast.LENGTH_SHORT);
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
            case R.id.Logout:
                ParseUser.logOut();
                Intent login = new Intent(this, LoginActivity.class);
                startActivity(login);
                finish();
                break;
            case R.id.Account:
                Intent account = new Intent(this, AccountActivity.class);
                startActivity(account);
                break;
        }
        return true;
    }
}
