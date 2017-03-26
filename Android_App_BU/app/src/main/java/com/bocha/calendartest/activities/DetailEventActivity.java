package com.bocha.calendartest.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.bocha.calendartest.R;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import com.bocha.organized.data.Event;
import com.bocha.organized.utility.EventUtility;

/**
 * Created by bob on 04.01.17.
 */

public class DetailEventActivity extends AppCompatActivity {
    private static final String TAG = "DetailEventActivity";
    public static final String PREFS_NAME = "LoginPrefs";

    private TextView titleTextView;
    private TextView startTextView;
    private TextView endTextView;
    private TextView descTextView;
    private TextView collideTextView;

    private Button acceptButton;
    private Button denyButton;

    private String eventTitle;
    private String eventDescription;
    private Long eventStart;
    private Long eventEnd;
    private Date eventLastUpdate = new Date();
    private String eventLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        titleTextView = (TextView) findViewById(R.id.detail_event_title);
        startTextView = (TextView) findViewById(R.id.detail_event_start);
        endTextView = (TextView) findViewById(R.id.detail_event_end);
        descTextView = (TextView) findViewById(R.id.detail_event_description);
        collideTextView = (TextView) findViewById(R.id.detail_event_colliding);

        acceptButton = (Button) findViewById(R.id.detail_event_allow_button);
        denyButton = (Button) findViewById(R.id.detail_event_deny_button);

        getIntentData();
        setIntentData();
        checkForEventCollision();
        setListener();

    }

    /**
     * Check whether the showed event collides with existing events
     * and show the names of the colliding events in the according textView
     */
    private void checkForEventCollision() {
        ArrayList<String> collidingEvents = EventUtility.checkEventCollision(eventStart, eventEnd);

        if(collidingEvents.size() != 0){
            collideTextView.setTextColor(Color.RED);
            collideTextView.setText(getCollEventsString(collidingEvents));
        }
    }

    private String getCollEventsString(ArrayList<String> collidingEvents) {
        String collEventsString = "";

        for(int i = 0, j = collidingEvents.size(); i < j; i++){
            collEventsString += "  - " + collidingEvents.get(i) + "\n";
        }

        return collEventsString;
    }

    private void setListener() {
        acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addEvent();
            }
        });

        denyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void addEvent() {
        Event event = new Event(eventTitle, eventLastUpdate, eventStart, eventEnd, eventLocation, eventDescription);
        Log.v(TAG, "Add event");
        EventUtility.addEvent(DetailEventActivity.this, event);

        finish();
    }

    private void setIntentData() {
        titleTextView.setText(eventTitle);

        String[] dateData = EventUtility.calculateDate(eventStart, eventEnd);
        startTextView.setText(dateData[0]);
        endTextView.setText(dateData[1]);
        descTextView.setText(eventDescription);
    }

    private void getIntentData() {
        Intent detailIntent = getIntent();
        eventTitle = detailIntent.getStringExtra("eventTitle");
        eventLocation = detailIntent.getStringExtra("eventLocation");
        eventLastUpdate.setTime(detailIntent.getLongExtra("eventLastUpdate", 1L));
        eventStart = detailIntent.getLongExtra("eventStart", 1L);
        eventEnd = detailIntent.getLongExtra("eventEnd", 1L);
        eventDescription = detailIntent.getStringExtra("eventDesc");
    }
}
