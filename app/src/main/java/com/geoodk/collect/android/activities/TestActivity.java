package com.geoodk.collect.android.activities;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;

import com.geoodk.collect.android.R;
import com.geoodk.collect.android.spatial.GeoRender;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.android.gms.maps.model.TileProvider;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Created by RRSCW on 10-08-2017.
 */

public class TestActivity extends android.support.v4.app.FragmentActivity{
    /*	GOAL:
	 *  Display a WMS overlay from OSGEO on top of a google base map.
	 *  (The data is a white map with state boundaries.)
	 *
	 *  Create a debugging Maps API Key and add it to the manifest.
	 *
	 */
    private GoogleMap mMap;
    private GeoRender geoRender;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wms_test);
        setUpMapIfNeeded();
    }

    private void setUpMapIfNeeded() {
        if (mMap == null) {
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(22, 78), 3));
                mMap.addPolygon(new PolygonOptions()
                        .add(new LatLng(-85, 0), new LatLng(-85, 180), new LatLng(85, 180), new LatLng(85,0), new LatLng(-85, 0))
                        .fillColor(Color.LTGRAY).strokeColor(Color.LTGRAY));
                mMap.addPolygon(new PolygonOptions()
                        .add(new LatLng(-85, -185), new LatLng(-85, 0), new LatLng(85, 0), new LatLng(85,-185), new LatLng(-85, -185))
                        .fillColor(Color.LTGRAY).strokeColor(Color.LTGRAY));
                setUpMap();
            } else {
                Log.e("WMSDEMO", "Map was null!");
            }
        }
    }

    private void setUpMap() {
        TileProvider wmsTileProvider = TileProviderFactory.getOsgeoWmsTileProvider();
        mMap.addTileOverlay(new TileOverlayOptions().tileProvider(wmsTileProvider));
        drawMarkers();
        //mMap.addMarker(new MarkerOptions().position(new LatLng(22.7253, 75.8655)).title("Indore"));
        // Because the demo WMS layer we are using is just a white background map, switch the base layer
        // to satellite so we can see the WMS overlay.
        // mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
    }

    private void drawMarkers(){
        geoRender = new GeoRender(this.getApplicationContext(),mMap);
    }
}
