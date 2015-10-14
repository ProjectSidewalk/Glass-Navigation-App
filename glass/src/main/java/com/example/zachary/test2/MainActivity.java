package com.example.zachary.test2;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.KeyEvent;
import android.widget.ImageView;

import com.google.android.glass.touchpad.GestureDetector;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {
    private static final String TAG = MainActivity.class.getSimpleName();

    private ImageView view;
    private GestureDetector mGestureDetector;
    private boolean overviewMode = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        view = new ImageView(this);
        setContentView(view);

        loadImage(makeStaticMapsUrl(getDummyPath()));
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

    @Override
    public boolean onKeyDown(int keycode, KeyEvent event) {
        if (keycode == KeyEvent.KEYCODE_DPAD_CENTER) {
            // user tapped touchpad, do something
            System.out.println("Pressed!!!");

            if (overviewMode) {
                loadImage("http://www.drodd.com/images12/arrow-clip-art36.png");
                overviewMode = false;
            } else {
                loadImage(makeStaticMapsUrl(getDummyPath()));
            }

            return true;
        }

        return super.onKeyDown(keycode, event);
    }

    // Formats a Google static maps URL
    private static String makeStaticMapsUrl(List<Pair<Double, Double>> points) {
        StringBuilder builder = new StringBuilder();

        builder.append("http://maps.googleapis.com/maps/api/staticmap");
        builder.append("?size=320x180");
        builder.append("&scale=2");
        builder.append("&maptype=roadmap");
        builder.append("&format=png");

        // Add the markers to the map
//        builder.append("&markers=");
//        for (int i = 0; i < points.size(); i++) {
//            Pair<Double, Double> point = points.get(i);
//            builder.append(point.first + "," + point.second);
//            if (i != (points.size() - 1)) {
//                // Add the '|' character
//                builder.append("%7C");
//            }
//        }

        // Add the path to the map
        builder.append("&path=color:0x0000ff%7Cweight:5%7C");
        for (int i = 0; i < points.size(); i++) {
            Pair<Double, Double> point = points.get(i);
            builder.append(point.first + "," + point.second);
            if (i != (points.size() - 1)) {
                // Add the '|' character
                builder.append("%7C");
            }
        }

        System.out.println(builder.toString());

        return builder.toString();
    }

    // Provide dummy data to start testing
    private List<Pair<Double, Double>> getDummyPath() {
        ArrayList<Pair<Double, Double>> path = new ArrayList<>();

        path.add(new Pair<>(38.987595, -76.941287));
        path.add(new Pair<>(38.987658, -76.940542));
        path.add(new Pair<>(38.984457, -76.940107));
        path.add(new Pair<>(38.982812, -76.939213));
        path.add(new Pair<>(38.982315, -76.939126));
        path.add(new Pair<>(38.982284, -76.938809));
        path.add(new Pair<>(38.981767, -76.938834));
        path.add(new Pair<>(38.981772, -76.938961));
        path.add(new Pair<>(38.980967, -76.938921));
        path.add(new Pair<>(38.980949, -76.938759));
        path.add(new Pair<>(38.980490, -76.938759));

        return path;
    }
}