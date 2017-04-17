package com.ase_1617.organized.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.ase_1617.organized.R;
import com.ase_1617.organized.activities.EventFeedActivity;
import com.ase_1617.organizedlib.data.CalEvent;
import com.ase_1617.organizedlib.data.Event;
import com.ase_1617.organizedlib.utility.MiscUtility;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

/**
 * An extended ArrayAdapter to show the new events as event feed
 */

public class EventFeedAdapter extends ArrayAdapter {
    private final String TAG = "EventFeedAdapter";

    private ArrayList<CalEvent> events;
    private Context context;

    private SimpleDateFormat formatter;

    public EventFeedAdapter(Context context, int eventId, ArrayList<CalEvent> events){
        super(context, eventId, events);
        this.events = events;
        this.context = context;

        formatter = new SimpleDateFormat("dd MMM yyyy HH : mm", Locale.GERMAN);
    }

    /**
     * Overriden getView method from the ArrayAdapter.
     * Declares the look of each event feed element.
     * @param position
     * @param convertView
     * @param parent
     * @return
     */
    public View getView(final int position, final View convertView, ViewGroup parent){

        View v = convertView;

        if (v == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.item_event_feed, null);
        }

        //Set onClickListener for the acceptButton
        ImageButton acceptButton = (ImageButton) v.findViewById(R.id.new_event_accept_button);
        acceptButton.setClickable(false);
        acceptButton.setFocusable(false);
        acceptButton.setFocusableInTouchMode(false);
        acceptButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(context instanceof EventFeedActivity){
                    ((EventFeedActivity)context).onEventAccepted(position);
                }
            }
        });

        //Set onClickListener for the declineButton
        ImageButton declineButton = (ImageButton) v.findViewById(R.id.new_event_decline_button);
        declineButton.setClickable(false);
        declineButton.setFocusable(false);
        declineButton.setFocusableInTouchMode(false);
        declineButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(context instanceof EventFeedActivity){
                    ((EventFeedActivity)context).onEventDeclined(position);
                }
            }
        });

        Event i = events.get(position);

        if (i != null) {

            // Gather references for the textviews

            TextView titleView = (TextView) v.findViewById(R.id.new_event_title);
            TextView locationView = (TextView) v.findViewById(R.id.new_event_location);
            TextView descView = (TextView) v.findViewById(R.id.new_event_description);
            TextView dateView = (TextView) v.findViewById(R.id.new_event_date);
            TextView timeView = (TextView) v.findViewById(R.id.new_event_time);

            //Get the date and the time data as strings
            String[] dateData = MiscUtility.calculateDate(i.getEventStartDate().getTime(), i.getEventEndDate().getTime());

            // Check to see if each individual textview is null
            // If not assign the according data
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

        return v;

    }

}
