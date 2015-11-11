package com.example.zachary.test2;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.SensorManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ImageView;

import com.google.android.glass.touchpad.Gesture;
import com.google.android.glass.touchpad.GestureDetector;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MainActivity extends Activity {
    private static final String TAG = MainActivity.class.getSimpleName();

    private ImageView view;
    private GestureDetector mGestureDetector;

    // Store current sensor data
    Rolling rollingX, rollingY, rollingZ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Register sensor detecting
        SensorManager sensorManager = (SensorManager) this.getSystemService(SENSOR_SERVICE);
        LocationManager locationManger = (LocationManager) this.getSystemService(LOCATION_SERVICE);
        OrientationManager orientationManager = new OrientationManager(sensorManager, locationManger);
        orientationManager.addOnChangedListener(new OrientationManager.OnChangedListener() {
            @Override
            public void onOrientationChanged(OrientationManager orientationManager) {
                rotateImage(orientationManager.getHeading());
            }

            @Override
            public void onLocationChanged(OrientationManager orientationManager) {
                return;
            }

            @Override
            public void onAccuracyChanged(OrientationManager orientationManager) {
                return;
            }
        });
        orientationManager.start();


        // Set-up the default view to be an overview
        view = new ImageView(this);
        setContentView(view);

        loadImage(makeStaticMapsUrl(getDummyPath()));

        // Set-up the Gesture Detector
        mGestureDetector = createGestureDetector(this);

        rollingX = new Rolling(30);
        rollingY = new Rolling(30);
        rollingZ = new Rolling(30);
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
    // Note: "%7C" is used in place of "|"
    private String makeStaticMapsUrl(ArrayList<ArrayList<Double>> points) {
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

        // Add current location to the map and make it the center
        Location lastLocation = getLastLocation(this);
        if (lastLocation != null) {
            builder.append("&markers=");
            builder.append("size:tiny%7C");
            builder.append(lastLocation.getLatitude());
            builder.append(",");
            builder.append(lastLocation.getLongitude());
            builder.append("&center=");
            builder.append(lastLocation.getLatitude());
            builder.append(",");
            builder.append(lastLocation.getLongitude());
        }

        // Set zoom level
        builder.append("&zoom=14");

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

        // Remove text layers
        builder.append("&style=feature:all%7Celement:labels%7Cvisibility:off");

        System.out.println(builder.toString());

        return builder.toString();
    }

    // http://developer.android.com/reference/android/hardware/SensorManager.html#getOrientation(float[], float[])
    // http://developer.android.com/reference/android/hardware/SensorManager.html#getRotationMatrix(float[], float[], float[], float[])
    // https://developers.google.com/glass/develop/gdk/location-sensors
//    private void rotateImage() {
//        float R[] = new float[9];
//        float I[] = new float[9];
//        float orientationValues[] = new float[3];
//
//        if (mGravs != null && mGeoMags != null) {
//            if (sensorManager.getRotationMatrix(R, null, mGravs, mGeoMags)) {
//                sensorManager.getOrientation(R, orientationValues);
//                view.setRotation(new Float(Math.toDegrees(new Double(orientationValues[0]))));
//                System.out.println(Arrays.toString(orientationValues));
//                System.out.println(new Float(Math.toDegrees(new Double(orientationValues[0]))) + "  |  " +  new Float(Math.toDegrees(new Double(orientationValues[1]))) + "  |  " +  new Float(Math.toDegrees(new Double(orientationValues[2]))));
//
//                rollingX.add(-1 * orientationValues[1]);
//                rollingY.add(orientationValues[2]);
//                rollingZ.add(-1 * orientationValues[0]);
//
//                System.out.println("Y: " + rollingY.getAverage() / 3.14159);
//                System.out.println("X: " + rollingX.getAverage() + "  |  Y: " + rollingY.getAverage() + "  |  Z: " + rollingZ.getAverage());
//                System.out.println("mGravs: " + Arrays.toString(mGravs));
//                System.out.println("mGeoMags: " + Arrays.toString(mGeoMags));
//            }
//            if (sensorManager.getRotationMatrix(null, I, mGravs, mGeoMags)) {
//                System.out.println("I: " + sensorManager.getInclination(I));
//            }
//        }
//    }

    private void rotateImage(float angle) {
        view.setRotation(-1 * angle);
        view.setScaleY(2);
        view.setScaleX(2);
    }

    private GestureDetector createGestureDetector(Context context) {
        GestureDetector gestureDetector = new GestureDetector(context);
        //Create a base listener for generic gestures
        gestureDetector.setBaseListener(new GestureDetector.BaseListener() {
            @Override
            public boolean onGesture(Gesture gesture) {
                if (gesture == Gesture.TAP) {

                    Intent myIntent = new Intent(MainActivity.this, NavigationActivity.class);
                    MainActivity.this.startActivity(myIntent);

                    return true;
//                } else if (gesture == Gesture.TWO_TAP) {
//                    // do something on two finger tap
//                    return true;
//                } else if (gesture == Gesture.SWIPE_RIGHT) {
//                    // do something on right (forward) swipe
//                    return true;
//                } else if (gesture == Gesture.SWIPE_LEFT) {
//                    // do something on left (backwards) swipe
//                    return true;
                }
                return false;
            }
        });

//        gestureDetector.setFingerListener(new GestureDetector.FingerListener() {
//            @Override
//            public void onFingerCountChanged(int previousCount, int currentCount) {
//                // do something on finger count changes
//            }
//        });
//
//        gestureDetector.setScrollListener(new GestureDetector.ScrollListener() {
//            @Override
//            public boolean onScroll(float displacement, float delta, float velocity) {
//                // do something on scrolling
//            }
//        });

        return gestureDetector;
    }

    // Enable out GestureDetector to capture gestures
    @Override
    public boolean onGenericMotionEvent(MotionEvent event) {
        if (mGestureDetector != null) {
            return mGestureDetector.onMotionEvent(event);
        }
        return false;
    }

    // Provide dummy data to start testing
    private ArrayList<ArrayList<Double>> getDummyPath() {
        ArrayList<ArrayList<Double>> path = new ArrayList<>();

        path.add(new ArrayList<Double>() {{ add(38.987595); add(-76.941287); }});
        path.add(new ArrayList<Double>() {{ add(38.987658); add(-76.940542); }});
        path.add(new ArrayList<Double>() {{ add(38.984457); add(-76.940107); }});
        path.add(new ArrayList<Double>() {{ add(38.982812); add(-76.939213); }});
        path.add(new ArrayList<Double>() {{ add(38.982315); add(-76.939126); }});
        path.add(new ArrayList<Double>() {{ add(38.982284); add(-76.938809); }});
        path.add(new ArrayList<Double>() {{ add(38.981767); add(-76.938834); }});
        path.add(new ArrayList<Double>() {{ add(38.981772); add(-76.938961); }});
        path.add(new ArrayList<Double>() {{ add(38.980967); add(-76.938921); }});
        path.add(new ArrayList<Double>() {{ add(38.980949); add(-76.938759); }});
        path.add(new ArrayList<Double>() {{ add(38.980490); add(-76.938759); }});

        return path;
    }

    public static Location getLastLocation(Context context) {
        LocationManager manager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.NO_REQUIREMENT);

        List<String> providers = manager.getProviders(criteria, true);
        List<Location> locations = new ArrayList<Location>();

        for (String provider : providers) {
            Location location = manager.getLastKnownLocation(provider);
            if (location != null && location.getAccuracy() != 0.0) {
                    locations.add(location);
            }
        }

        Collections.sort(locations, new Comparator<Location>() {
            @Override
            public int compare(Location location, Location location2) {
                return (int) (location.getAccuracy() - location2.getAccuracy());
            }
        });

        if (locations.size() > 0) {
            return locations.get(0);
        }

        return null;
    }

    public class Rolling {

        private int size;
        private double total = 0d;
        private int index = 0;
        private double samples[];

        public Rolling(int size) {
            this.size = size;
            samples = new double[size];
            for (int i = 0; i < size; i++) samples[i] = 0d;
        }

        public void add(double x) {
            total -= samples[index];
            samples[index] = x;
            total += x;
            if (++index == size) index = 0; // cheaper than modulus
        }

        public double getAverage() {
            return total / size;
        }
    }
}