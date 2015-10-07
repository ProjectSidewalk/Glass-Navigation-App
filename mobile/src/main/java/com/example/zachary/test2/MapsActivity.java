package com.example.zachary.test2;

import android.graphics.Color;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        setUpMapIfNeeded();
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

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {
        List<LatLng> path = getDummyPath();

        PolylineOptions options = new PolylineOptions();

        options.color(Color.parseColor("#CC0000FF"));
        options.width(5);
        options.visible(true);

        for(LatLng point : path) {
            // Add the point to be drawn on the route
            options.add(point);

            // Add the point as a marker to know that the route has a direction step here
            mMap.addMarker(new MarkerOptions().position(point).title("Marker"));
        }

        mMap.addPolyline(options);

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(38.987595, -76.941287), 14.0f));
    }

    // Provide dummy data to start testing
    private List<LatLng> getDummyPath() {
        ArrayList<LatLng> path = new ArrayList<>();

        path.add(new LatLng(38.987595, -76.941287));
        path.add(new LatLng(38.987658, -76.940542));
        path.add(new LatLng(38.984457, -76.940107));
        path.add(new LatLng(38.982812, -76.939213));
        path.add(new LatLng(38.982315, -76.939126));
        path.add(new LatLng(38.982284, -76.938809));
        path.add(new LatLng(38.981767, -76.938834));
        path.add(new LatLng(38.981772, -76.938961));
        path.add(new LatLng(38.980967, -76.938921));
        path.add(new LatLng(38.980949, -76.938759));
        path.add(new LatLng(38.980490, -76.938759));

        return path;
    }
}
