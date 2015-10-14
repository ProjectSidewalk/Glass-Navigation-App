package com.example.zachary.test2;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.ImageView;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.InputStream;

public class MainActivity extends Activity {

    private static final String TAG = MainActivity.class.getSimpleName();

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

    private ImageView view;
    private boolean overviewMode = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        view = new ImageView(this);
        setContentView(view);

        loadImage(makeStaticMapsUrl(0, 0, 0));
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
                loadImage(makeStaticMapsUrl(0, 0, 0));
            }

            return true;
        }

        return super.onKeyDown(keycode, event);
    }
}