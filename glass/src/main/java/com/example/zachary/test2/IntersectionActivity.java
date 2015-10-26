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
import java.util.ArrayList;

public class IntersectionActivity extends Activity {
    private static final String TAG = MainActivity.class.getSimpleName();

    private ImageView view;
    private ArrayList<ArrayList<Double>> path;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set-up the default view to be an overview
        view = new ImageView(this);
        setContentView(view);

        path = (ArrayList<ArrayList<Double>>) getIntent().getSerializableExtra("path");

        loadImage(makeStaticMapsUrl(path));
    }

    /** Load the map asynchronously and populate the ImageView when it's loaded. */
    private void loadImage(String url) {
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
                    view.setImageBitmap(bitmap);
                }
            }
        }.execute(url);
    }

    // Formats a Google static maps URL
    private static String makeStaticMapsUrl(ArrayList<ArrayList<Double>> points) {
        StringBuilder builder = new StringBuilder();

        builder.append("http://maps.googleapis.com/maps/api/staticmap");
        builder.append("?size=320x180");
        builder.append("&scale=2");
        builder.append("&maptype=roadmap");
        builder.append("&format=png");

        // Add the markers to the map
//        builder.append("&markers=");
//        for (int i = 0; i < points.size(); i++) {
//            ArrayList<Double> point = points.get(i);
//            builder.append(point.get(0) + "," + point.get(1));
//            if (i != (points.size() - 1)) {
//                // Add the '|' character
//                builder.append("%7C");
//            }
//        }

        // Add the path to the map
        builder.append("&path=color:0x0000ff%7Cweight:5%7C");
        for (int i = 0; i < points.size(); i++) {
            ArrayList<Double> point = points.get(i);
            builder.append(point.get(0) + "," + point.get(1));
            if (i != (points.size() - 1)) {
                // Add the '|' character
                builder.append("%7C");
            }
        }

        return builder.toString();
    }
}