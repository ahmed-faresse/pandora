package edu.csulb.android.groupproject;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;


/**
 * CECS 453 : Group Project
 * MarkerCluster.java
 * purpose: Create a custom marker for the Google maps marker Cluster
 *
 * @author Geoffrey Heckmann
 */
public class MarkerCluster implements ClusterItem
{
    /** Information of the marker */
    private final LatLng mPosition;
    private final String mTitle;
    private final String mDesc;
    private final BitmapDescriptor mIcon;

    /**
     * Constructor
     * @param lat latitude
     * @param lng longitude
     * @param title title of the infowindow
     * @param desc content of the infowindow
     * @param icon image of the marker
      */
    public MarkerCluster(double lat, double lng, String title, String desc,  BitmapDescriptor icon) {
        mPosition = new LatLng(lat, lng);
        mTitle = title;
        mDesc = desc;
        mIcon = icon;
    }

    /**
     * Position accessor
     * @return the position of the marker
     */
    @Override
    public LatLng getPosition() {
        return mPosition;
    }

     /**
      * Title accessor
      * @return the title of the marker
      */
    public String getTitle()
    {
        return mTitle;
    }

    /**
     * Description accessor
     * @return the description of the marker
     */
    public String getDesc()
    {
        return mDesc;
    }

    /**
     * Icon accessor
     * @return the icon of the marker
     */
    public BitmapDescriptor getIcon()
    {
        return mIcon;
    }
}