package com.example.zachary.test2;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.glass.touchpad.Gesture;
import com.google.android.glass.touchpad.GestureDetector;
import com.google.android.glass.widget.CardBuilder;
import com.google.android.glass.widget.CardScrollAdapter;
import com.google.android.glass.widget.CardScrollView;

import java.util.ArrayList;
import java.util.List;

public class NavigationActivity extends Activity {
    private List<CardBuilder> mCards;
    private CardScrollView mCardScrollView;
    private ExampleCardScrollAdapter mAdapter;
    private GestureDetector mGestureDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        createCards();

        mCardScrollView = new CardScrollView(this);
        mAdapter = new ExampleCardScrollAdapter();
        mCardScrollView.setAdapter(mAdapter);
        mCardScrollView.activate();
        setContentView(mCardScrollView);

        // Set-up the Gesture Detector
        mGestureDetector = createGestureDetector(this);
    }

    private void createCards() {
        mCards = new ArrayList<CardBuilder>();

        mCards.add(new CardBuilder(this, CardBuilder.Layout.COLUMNS)
                .setText("Turn Right on Adam Street")
                .addImage(R.drawable.turn_right));

        mCards.add(new CardBuilder(this, CardBuilder.Layout.COLUMNS)
                .setText("Turn Left on Bland Street")
                .addImage(R.drawable.turn_left));

        mCards.add(new CardBuilder(this, CardBuilder.Layout.COLUMNS)
                .setText("Turn Left on Charlie Street")
                .addImage(R.drawable.turn_left));
    }

    private class ExampleCardScrollAdapter extends CardScrollAdapter {

        @Override
        public int getPosition(Object item) {
            return mCards.indexOf(item);
        }

        @Override
        public int getCount() {
            return mCards.size();
        }

        @Override
        public Object getItem(int position) {
            return mCards.get(position);
        }

        @Override
        public int getViewTypeCount() {
            return CardBuilder.getViewTypeCount();
        }

        @Override
        public int getItemViewType(int position){
            return mCards.get(position).getItemViewType();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            return mCards.get(position).getView(convertView, parent);
        }
    }

    private GestureDetector createGestureDetector(Context context) {
        GestureDetector gestureDetector = new GestureDetector(context);

        //Create a base listener for generic gestures
        gestureDetector.setBaseListener(new GestureDetector.BaseListener() {
            @Override
            public boolean onGesture(Gesture gesture) {
                if (gesture == Gesture.TAP) {
                    Intent myIntent = new Intent(NavigationActivity.this, IntersectionActivity.class);

                    int currCard = new Long(mCardScrollView.getSelectedItemId()).intValue();
                    int endCard = new Long(mCardScrollView.getSelectedItemId()).intValue() + 3;
                    myIntent.putExtra("path", new ArrayList<ArrayList<Double>> (getDummyPath().subList(currCard, endCard)));

                    NavigationActivity.this.startActivity(myIntent);

                    return true;
                }

                return false;
            }
        });

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
}