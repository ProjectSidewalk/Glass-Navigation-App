package com.example.zachary.test2;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.InputStream;

public class MainActivity extends Activity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private static final String STATIC_MAP_URL_TEMPLATE =
            "https://maps.googleapis.com/maps/api/staticmap"
            + "?center=%.5f,%.5f"
            + "&zoom=%d"
            + "&sensor=true"
            + "&size=640x360"
            + "&scale=1";
//            + "&style=element:geometry%%7Cinvert_lightness:true"
//            + "&style=feature:landscape.natural.terrain%%7Celement:geometry%%7Cvisibility:on"
//            + "&style=feature:landscape%%7Celement:geometry.fill%%7Ccolor:0x303030"
//            + "&style=feature:poi%%7Celement:geometry.fill%%7Ccolor:0x404040"
//            + "&style=feature:poi.park%%7Celement:geometry.fill%%7Ccolor:0x0a330a"
//            + "&style=feature:water%%7Celement:geometry%%7Ccolor:0x00003a"
//            + "&style=feature:transit%%7Celement:geometry%%7Cvisibility:on%%7Ccolor:0x101010"
//            + "&style=feature:road%%7Celement:geometry.stroke%%7Cvisibility:on"
//            + "&style=feature:road.local%%7Celement:geometry.fill%%7Ccolor:0x606060"
//            + "&style=feature:road.arterial%%7Celement:geometry.fill%%7Ccolor:0x888888";

    /** Formats a Google static maps URL for the specified location and zoom level. */
    private static String makeStaticMapsUrl(double latitude, double longitude, int zoom) {
//        return String.format(STATIC_MAP_URL_TEMPLATE, latitude, longitude, zoom);
        return "http://maps.googleapis.com/maps/api/staticmap"
                + "?size=320x180"
                + "&scale=2"
                + "&maptype=roadmap"
                + "&format=png"
                // Maybe use a URL encoder for this part?
                + "&markers=38.987595,-76.941287%7C38.987658,-76.940542%7C38.984457,-76.940107"
                + "&path=color:0x0000ff%7Cweight:5%7C38.987595,-76.941287%7C38.987658,-76.940542%7C38.984457,-76.940107";
    }

    private ImageView mMapView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mMapView = new ImageView(this);
        setContentView(mMapView);

        loadMap(38.987595, -76.941287, 14);
    }

    /** Load the map asynchronously and populate the ImageView when it's loaded. */
    private void loadMap(double latitude, double longitude, int zoom) {
        String url = makeStaticMapsUrl(latitude, longitude, zoom);
        new AsyncTask<String, Void, Bitmap>() {
            @Override
            protected Bitmap doInBackground(String... urls) {
                try {
                    HttpResponse response = new DefaultHttpClient().execute(new HttpGet(urls[0]));
                    InputStream is = response.getEntity().getContent();
                    return BitmapFactory.decodeStream(is);
                } catch (Exception e) {
                    Log.e(TAG, "Failed to load image", e);
                    return null;
                }
            }

            @Override
            protected void onPostExecute(Bitmap bitmap) {
                if (bitmap != null) {
                    mMapView.setImageBitmap(bitmap);
                }
            }
        }.execute(url);
    }
}