package com.bocha.calendartest.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.bocha.calendartest.R;
import com.bocha.calendartest.activities.NewEventsActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import com.bocha.organized.data.Event;
import com.bocha.organized.utility.EventUtility;

/**
 * Created by bob on 26.12.16.
 */

public class eventAdapter extends ArrayAdapter {
    private final String TAG = "EventAdapter";

    private ArrayList<Event> events;
    private Context context;

    SimpleDateFormat formatter;

    public eventAdapter(Context context, int eventId, ArrayList<Event> events){
        super(context, eventId, events);
        this.events = events;
        this.context = context;

        formatter = new SimpleDateFormat("dd MMM yyyy HH : mm");
    }

    /*
	 * we are overriding the getView method here - this is what defines how each
	 * list item will look.
	 */
    public View getView(final int position, final View convertView, ViewGroup parent){

        // assign the view we are converting to a local variable
        View v = convertView;

        // first check to see if the view is null. if so, we have to inflate it.
        // to inflate it basically means to render, or show, the view.
        if (v == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.item_new_event, null);
        }

        //Set onClicklListener for the Imagebuttons of the layout
        ImageButton acceptButton = (ImageButton) v.findViewById(R.id.new_event_accept_button);
        acceptButton.setClickable(false);
        acceptButton.setFocusable(false);
        acceptButton.setFocusableInTouchMode(false);
        acceptButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(context instanceof NewEventsActivity){
                    ((NewEventsActivity)context).onEventAccepted(position);
                }
            }
        });

        ImageButton declineButton = (ImageButton) v.findViewById(R.id.new_event_decline_button);
        declineButton.setClickable(false);
        declineButton.setFocusable(false);
        declineButton.setFocusableInTouchMode(false);
        declineButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(context instanceof NewEventsActivity){
                    ((NewEventsActivity)context).onEventDeclined(position);
                }
            }
        });

		/*
		 * Recall that the variable position is sent in as an argument to this method.
		 * The variable simply refers to the position of the current object in the list. (The ArrayAdapter
		 * iterates through the list we sent it)
		 *
		 * Therefore, i refers to the current Item object.
		 */
        Event i = events.get(position);

        if (i != null) {

            // Gather references for the textviews

            TextView titleView = (TextView) v.findViewById(R.id.new_event_title);
            TextView locationView = (TextView) v.findViewById(R.id.new_event_location);
            TextView descView = (TextView) v.findViewById(R.id.new_event_description);
            TextView dateView = (TextView) v.findViewById(R.id.new_event_date);
            TextView timeView = (TextView) v.findViewById(R.id.new_event_time);

            //Get the date and the time data as strings.
            String[] dateData = EventUtility.calculateDate(i.getEventStartDate().getTime(), i.getEventEndDate().getTime());

            // check to see if each individual textview is null.
            // if not, assign some text!
            if (titleView != null){
                titleView.setText(i.getEventName());
            }
            if (locationView != null){
                locationView.setText(i.getEventLocation());
            }
            if (descView != null){
                descView.setText(i.getEventDescription());
            }
            if (dateView != null){
                dateView.setText(dateData[0]);
            }
            if (timeView != null){
                timeView.setText(dateData[1]);
            }
        }

        // the view must be returned to our activity
        return v;

    }

    private String dateToString(Date date) {
        String result = "";
        result += formatter.format(date);

        return result;
    }

}
