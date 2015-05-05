package edu.csulb.android.groupproject;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.facebook.FacebookSdk;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.Parse;
import com.parse.ParseObject;

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

        // Enable Local Datastore.
        Parse.enableLocalDatastore(this);

        Parse.initialize(this, "fdgb3DiwkZokytY9wwMjxCZXLXnRiRK69YpPPhtX",
                "Kui6CxsP31Wt54V3P4DciY38k6ODbBPtFMGD6khP");

        ParseObject testObject = new ParseObject("TestObject");
        testObject.put("testouille", "tousteille");
        testObject.put("testouille2", "tousteille2  ");
        testObject.saveInBackground();

        FacebookSdk.sdkInitialize(getApplicationContext());
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
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

    private Bitmap getScaledRoundedRectBitmap(int img_id, int pixels) {
        Bitmap result = null;
        try {
            Bitmap bitmap = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
                            getResources(), img_id), 50, 50, false);
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
// return bitmap;
        } catch (OutOfMemoryError o){}
        return result;
    }

    private void AddCustomMarker(double lat, double lon, String name,
                                 String desc, int id_img)
    {
        if (mMap != null) {
            Bitmap scaledBitmap = getScaledRoundedRectBitmap(id_img, 100);

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
        AddCustomMarker(33.783768,-118.114336, "Ahmed", "Renverseur de Shaker", R.drawable.ahmed);
        AddCustomMarker(33.883768,-118.215336, "Geoffrey", "PDG", R.drawable.geoffrey);

        if (mMap != null)
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(33.783768,-118.114336), 10));
    }
}
