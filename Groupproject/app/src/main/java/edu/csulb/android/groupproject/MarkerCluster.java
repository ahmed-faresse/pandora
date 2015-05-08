package edu.csulb.android.groupproject;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

/**
 * Created by Geoffrey on 5/7/15.
 */
public class MarkerCluster implements ClusterItem {
    private final LatLng mPosition;
    private final String mTitle;
    private final String mDesc;
    private final BitmapDescriptor mIcon;

    public MarkerCluster(double lat, double lng, String title, String desc,  BitmapDescriptor icon) {
        mPosition = new LatLng(lat, lng);
        mTitle = title;
        mDesc = desc;
        mIcon = icon;
    }

    @Override
    public LatLng getPosition() {
        return mPosition;
    }

    public String getTitle()
    {
        return mTitle;
    }

    public String getDesc()
    {
        return mDesc;
    }

    public BitmapDescriptor getIcon()
    {
        return mIcon;
    }
}