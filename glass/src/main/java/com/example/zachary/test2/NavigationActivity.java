package com.example.zachary.test2;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.glass.widget.CardBuilder;
import com.google.android.glass.widget.CardScrollAdapter;
import com.google.android.glass.widget.CardScrollView;

import java.util.ArrayList;
import java.util.List;

public class NavigationActivity extends Activity {
    private List<CardBuilder> mCards;
    private CardScrollView mCardScrollView;
    private ExampleCardScrollAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        createCards();

        mCardScrollView = new CardScrollView(this);
        mAdapter = new ExampleCardScrollAdapter();
        mCardScrollView.setAdapter(mAdapter);
        mCardScrollView.activate();
        setContentView(mCardScrollView);
    }

    private void createCards() {
        mCards = new ArrayList<CardBuilder>();

//        mCards.add(new CardBuilder(this, CardBuilder.Layout.TEXT)
//                .setText("This card has a footer.")
//                .setFootnote("I'm the footer!"));

        mCards.add(new CardBuilder(this, CardBuilder.Layout.COLUMNS)
                .setText("Turn Right on Adam Street")
                .addImage(R.drawable.turn_right));

        mCards.add(new CardBuilder(this, CardBuilder.Layout.COLUMNS)
                .setText("Turn Left on Bland Street")
                .addImage(R.drawable.turn_left));
//
//        mCards.add(new CardBuilder(this, CardBuilder.Layout.COLUMNS)
//                .setText("This card has a mosaic of puppies.")
//                .setFootnote("Aren't they precious?")
//                .addImage(R.drawable.puppy_small_1);
//        .addImage(R.drawable.puppy_small_2);
//        .addImage(R.drawable.puppy_small_3));
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
}