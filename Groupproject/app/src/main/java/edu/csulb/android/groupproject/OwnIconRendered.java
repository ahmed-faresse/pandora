package edu.csulb.android.groupproject;

import android.content.Context;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;


/**
 * CECS 453 : Group Project
 * OwnIconRendered.java
 * purpose: Override the default display of a Cluster marker
 *
 * @author Geoffrey Heckmann
 */
class OwnIconRendered extends DefaultClusterRenderer<MarkerCluster> {

    public OwnIconRendered(Context context, GoogleMap map,
                           ClusterManager<MarkerCluster> clusterManager) {
        super(context, map, clusterManager);
    }

    /**
     * Set the Icon, description and title of the marker
     * @param item the Marker of the cluster
     * @param markerOptions the options of the marker
     */
    @Override
    protected void onBeforeClusterItemRendered(MarkerCluster item, MarkerOptions markerOptions) {
        markerOptions.icon(item.getIcon());
        markerOptions.snippet(item.getDesc());
        markerOptions.title(item.getTitle());
        super.onBeforeClusterItemRendered(item, markerOptions);
    }
}