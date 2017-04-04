package com.ase_1617.organized.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.TimePicker;

import com.ase_1617.organized.LoginActivity;
import com.ase_1617.organized.R;
import com.ase_1617.organizedlib.data.CalEvent;
import com.ase_1617.organizedlib.data.Event;
import com.ase_1617.organizedlib.utility.Constants;
import com.ase_1617.organizedlib.utility.EventUtility;
import com.ase_1617.organizedlib.utility.MiscUtility;
import com.ase_1617.organizedlib.views.ExpandedListView;
import com.roomorama.caldroid.CaldroidFragment;
import com.roomorama.caldroid.CaldroidListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class CalendarActivity extends AppCompatActivity {

    private static final String TAG = "CalendarActivity";

    private SharedPreferences userData;

    private AlertDialog permRequestDialog;
    private AlertDialog infoDialog;

    private ListView myEventListView;

    private ArrayList<CalEvent> deviceEventList;

    private ArrayAdapter<String> myAdapter;

    private boolean undo = false;
    private CaldroidFragment caldroidFragment;

    SimpleDateFormat formatter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);
        myEventListView = (ExpandedListView) findViewById(R.id.list_event_calendar);

        setupCaldroid(savedInstanceState);

        formatter = new SimpleDateFormat("dd MMM yyyy HH : mm");

        fetchDeviceEvents();
        updateUI();
        setupListClickListener();
        if(deviceEventList != null) {
            insertEvents();
        }
    }

    /**
     * Setup the caldroid fragment.
     * @param savedInstanceState
     */
    private void setupCaldroid(Bundle savedInstanceState) {
        caldroidFragment = new CaldroidFragment();

        //If Activity is created after rotation restore the saved state
        if (savedInstanceState != null) {
            caldroidFragment.restoreStatesFromKey(savedInstanceState,
                    "CALDROID_SAVED_STATE");
        }
        //If activity is created from fresh set new caldroid args
        else {
            Bundle args = new Bundle();
            Calendar cal = Calendar.getInstance();
            args.putInt(CaldroidFragment.MONTH, cal.get(Calendar.MONTH) + 1);
            args.putInt(CaldroidFragment.YEAR, cal.get(Calendar.YEAR));
            args.putBoolean(CaldroidFragment.ENABLE_SWIPE, true);
            args.putBoolean(CaldroidFragment.SIX_WEEKS_IN_CALENDAR, true);

            caldroidFragment.setArguments(args);
        }

        //Attach the created caldroid fragment to the activity
        FragmentTransaction t = getSupportFragmentManager().beginTransaction();
        t.replace(R.id.calendar1, caldroidFragment);
        t.commit();

        final Context listenerContext = this;

        // Setup listener
        final CaldroidListener listener = new CaldroidListener() {

            /**
             * On selecting a date in the cladroid fragment check if events are available for
             * the chosen date and show an info alert containing the event data.
             * @param date
             * @param view
             */
            @Override
            public void onSelectDate(Date date, View view) {
                ArrayList<CalEvent> matchingEvents = getMatchingEvents(date);
                if(matchingEvents.size() != 0){
                    showEventsData(matchingEvents);
                }
                updateUI();
            }

            /**
             * On LongClicking a date in the cladroid fragment open the new event dialog,
             * fetch the necessary new event data and add the event to the device calendar.
             * @param date
             * @param view
             */
            @Override
            public void onLongClickDate(final Date date, View view) {
                final View dialogView = View.inflate(listenerContext, R.layout.item_new_event_dialog, null);
                final AlertDialog alertDialog = new AlertDialog.Builder(listenerContext).create();

                //Setup the numberpicker for the event duration
                final NumberPicker hourPicker = (NumberPicker) dialogView.findViewById(R.id.detail_event_hour_picker);
                final NumberPicker minutePicker = (NumberPicker) dialogView.findViewById(R.id.detail_event_minute_picker);
                setupNumberPickers(hourPicker, minutePicker);

                //Fetch the new event data and add the event to the device calendar when clicking the accept button
                dialogView.findViewById(R.id.dialog_accept_event).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        TimePicker startPicker = (TimePicker) dialogView.findViewById(R.id.start_time_picker);
                        EditText titleBox = (EditText) dialogView.findViewById(R.id.dialog_title_edit);
                        EditText locationBox = (EditText) dialogView.findViewById(R.id.dialog_location_edit);
                        EditText descBox = (EditText) dialogView.findViewById(R.id.dialog_desc_edit);

                        int startHour = startPicker.getCurrentHour();
                        int startMinute = startPicker.getCurrentMinute();

                        int durationHour = hourPicker.getValue();
                        int durationMinute = minutePicker.getValue();

                        addEvent(new Date(), MiscUtility.getStartingDate(date, startHour, startMinute), MiscUtility.getDurationMillis(durationHour, durationMinute), String.valueOf(titleBox.getText()), String.valueOf(descBox.getText()), String.valueOf(locationBox.getText()));

                        alertDialog.dismiss();
                    }});

                dialogView.findViewById(R.id.dialog_cancel_event).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        alertDialog.dismiss();
                    }});

                alertDialog.setView(dialogView);
                alertDialog.show();

                updateUI();
            }
        };

        //
        caldroidFragment.setCaldroidListener(listener);

    }

    /**
     * Setup the hour and the minute picker to display the according numbers
     *
     * @param hourPicker
     * @param minutePicker
     */
    private void setupNumberPickers(NumberPicker hourPicker, NumberPicker minutePicker) {
        String[] hours = new String[24];
        for(int i=0; i<hours.length; i++)
            hours[i] = Integer.toString(i);

        hourPicker.setMinValue(1);
        hourPicker.setMaxValue(24);
        hourPicker.setWrapSelectorWheel(true);
        hourPicker.setDisplayedValues(hours);
        hourPicker.setValue(1);

        String[] minutes = new String[60];
        for(int i=0; i<minutes.length; i++)
            minutes[i] = Integer.toString(i);

        minutePicker.setMinValue(1);
        minutePicker.setMaxValue(60);
        minutePicker.setWrapSelectorWheel(true);
        minutePicker.setDisplayedValues(minutes);
        minutePicker.setValue(31);
    }

    /**
     * Add the new event to the device calendar.
     * @param lastUpdate
     * @param date
     * @param length
     * @param eventTitle
     * @param eventDescription
     * @param location
     */
    private void addEvent(Date lastUpdate, Date date, Long length, String eventTitle, String eventDescription, String location) {

        Event event = new Event(eventTitle, lastUpdate, date.getTime(), date.getTime() + length, location, eventDescription);

        Log.v(TAG, "ID: "+EventUtility.addCustomEvent(CalendarActivity.this, event));

        updateUI();
        insertEvents();
    }

    /**
     * Check whether the clicked date contains any events and return the according events as list.
     * @param date
     * @return
     */
    private ArrayList<CalEvent> getMatchingEvents(Date date) {
        Long dateStart = date.getTime();
        Long dateEnd = dateStart + TimeUnit.DAYS.toMillis(1);
        ArrayList<CalEvent> matchingEvents = new ArrayList<>();

        for(int i = 0, j = deviceEventList.size(); i < j; i++){
            Long eventStart = deviceEventList.get(i).getEventStartDate().getTime();
            if(eventStart >= dateStart && eventStart <= dateEnd){
                matchingEvents.add(deviceEventList.get(i));
            }
        }
        return matchingEvents;
    }

    /**
     * Setup two click listeners for the listview element
     * */
    private void setupListClickListener() {
        //Setup a clickListener for the eventlist
        //Show event info on click
        myEventListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                Date startDate = deviceEventList.get(position).getEventStartDate();
                caldroidFragment.moveToDate(startDate);

                showEventData(position);
            }
        });

        //TODO:Update title only for non organized events
        //Setup a longClickListener for the eventlist
        //Edit the title of the event on longclick
        final Context context = this;
        myEventListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                final String eventTitle = myEventListView.getItemAtPosition(position).toString();
                final EditText eventEditText = new EditText(context);
                AlertDialog dialog = new AlertDialog.Builder(context)
                        .setTitle("Update event title")
                        .setMessage("New title")
                        .setView(eventEditText)
                        .setPositiveButton("Accept", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String newTitle = String.valueOf(eventEditText.getText());
                                Integer eventId = EventUtility.getEventIdByTitle(context, eventTitle);
                                Log.v(TAG, "Change title to: "+newTitle);
                                EventUtility.updateEventTitle(context, newTitle, eventId);

                                updateUI();
                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .create();
                dialog.show();
                return true;
            }
        });
    }

    /**Show event data in an info alert.
     *
     * @param position Positon of the event to display in the eventlist
     */
    private void showEventData(int position) {
        CalEvent clickedEvent = deviceEventList.get(position);

        String eventInfo = MiscUtility.calEventToInfo(clickedEvent);

        showInfoAlert("Event", eventInfo);
    }

    /**Show events data in an info alert.
     *
     * @param eventList Arraylist containing the events to display
     */
    private void showEventsData(ArrayList<CalEvent> eventList) {
        CalEvent clickedEvent;
        String eventsInfo = new String();
        for(int i = 0, j = eventList.size(); i < j; i++){
            clickedEvent = eventList.get(i);
            eventsInfo += "" + clickedEvent.getEventName() + "\n";

            String[] dateData = MiscUtility.calculateDate(clickedEvent.getEventStartDate().getTime(), clickedEvent.getEventEndDate().getTime());
            eventsInfo += dateData[0] + "\n";
            eventsInfo += dateData[1] + "\n";

            eventsInfo += "" + clickedEvent.getEventDescription() + "\n" + "\n";
        }
        showInfoAlert("Events", eventsInfo);
    }

    /**
     * Insert the extracted events to the calendar fragment
     * */
    private void insertEvents() {
        Calendar cal;
        Date eventDate;

        for(int i  = 0, j = deviceEventList.size(); i < j; i++){

            cal = Calendar.getInstance();
            cal.add(Calendar.DATE, 7);
            cal.getTime();
            eventDate = deviceEventList.get(i).getEventStartDate();

            if (caldroidFragment != null) {
                ColorDrawable eventDrawable = new ColorDrawable(Color.RED);
                caldroidFragment.setBackgroundDrawableForDate(eventDrawable, eventDate);
                caldroidFragment.refreshView();
            }
        }
    }

    //TODO:Check for remaining events before removing color
    /**
     * Remove the given event from the calendar fragment
     * */
    private void removeCalEvent(Date removeEventStart) {

        if (caldroidFragment != null) {
            ColorDrawable eventDrawable = new ColorDrawable(Color.WHITE);
            caldroidFragment.setBackgroundDrawableForDate(eventDrawable, removeEventStart);
            caldroidFragment.refreshView();
        }
    }

    /**
     * Fetch the device events and update the list showing the events.
     * */
    private void updateUI() {
        fetchDeviceEvents();

        ArrayList<String> taskList = new ArrayList<>();

        if (deviceEventList != null) {
            for (int i = 0, l = deviceEventList.size(); i < l; i++) {
                taskList.add(deviceEventList.get(i).getEventName());
            }
            if (myAdapter == null) {
                myAdapter = new ArrayAdapter<>(this,
                        R.layout.item_event,
                        R.id.event_title,
                        taskList);
                myEventListView.setAdapter(myAdapter);
                Log.v(TAG, "New adapter");
            } else {
                myAdapter.clear();
                myAdapter.addAll(taskList);
                myAdapter.notifyDataSetChanged();
                Log.v(TAG, "NotifyDataSetChanged");
            }
        }
        setupListClickListener();
    }

    /**
     * Check for the read calendar permission.
     * Fetch the events from the device calendar and save them in the deviceEventList.
     */
    private void fetchDeviceEvents(){
        if(permissionGrantedReadCal()){
            //Clear the eventList if necessary
            if(deviceEventList != null){
                deviceEventList.clear();
            }
            //Update the eventList
            deviceEventList = EventUtility.fetchDeviceEvents(this);
        }else{
            Log.v(TAG, "Read events permission not granted");
        }
    }

    /**
     * Delete the according calendar event when the delete button is clicked
     * To delete the event the EventUtility class is used
     * */
    public void deleteEvent(View view) {
        //Get the parent view and parent listview of the clicked button
        //and therefore the index of the button that is used to receive the event data
        View parent = (View) view.getParent();
        ListView listView = (ListView) parent.getParent();
        final int position = listView.getPositionForView(parent);

        //Save the id, the name and the start date of the event and the current context
        final Integer eventId = deviceEventList.get(position).getEventId();
        final Date removeEventDate = deviceEventList.get(position).getEventStartDate();
        final Context context = this;
        String eventTitle = deviceEventList.get(position).getEventName();

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Delete event")
                .setMessage("Delete the event '" + eventTitle + "' with event id: " + eventId + "?")
                .setPositiveButton("Accept", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        EventUtility.deleteEventById(context, eventId);

                        updateUI();
                        removeCalEvent(removeEventDate);
                    }
                })
                .setNegativeButton("Cancel", null)
                .create();
        dialog.show();

        updateUI();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.calendar_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.activity_event_feed:
                Intent newIntent = new Intent(this, EventFeedActivity.class);
                startActivity(newIntent);
                return true;
            case R.id.action_log_out:
                logout();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //TODO:Remove saved login data when logging out
    /**
     * Replace the saved login data with the default values
     * and start the login activity
     * */
    private void logout() {
        //userData = getSharedPreferences(Constants.PREFS_NAME, 0);
        //SharedPreferences.Editor editor = userData.edit();
        //editor.putString("userMail", "Default");
        //editor.putString("userPass", "Default");
        //editor.commit();

        Intent logoutIntent = new Intent(this, LoginActivity.class);
        startActivity(logoutIntent);
    }

    /**
     * Show an alert dialog containing the given info.
     * @param infoText
     */
    public void showInfoAlert(String title, String infoText){
        infoDialog = new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(infoText)
                .create();
        infoDialog.show();
    }

    /**
     * Check whether the app can read in the calendar device app.
     * Request the necessary permisison if necessary.
     * */
    private boolean permissionGrantedReadCal(){
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_CALENDAR)
                != PackageManager.PERMISSION_GRANTED) {

            //Show an explanation if necessary.
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_CALENDAR)) {

                final Activity activity = this;

                //Show an explanation dialog that explains why the app does need the permission.
                permRequestDialog = new AlertDialog.Builder(this)
                        .setTitle("Calendar read permission needed")
                        .setMessage("The app needs the calendar read permission to get the events from the default calendar app.")
                        .setPositiveButton("Accept", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                //Request the permission if the user accepts
                                ActivityCompat.requestPermissions(activity,
                                        new String[]{Manifest.permission.READ_CALENDAR},
                                        1);
                            }
                        })
                        .setNegativeButton("Decline", null)
                        .create();
                permRequestDialog.show();

            } else {

                //If no explanation is needed just request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_CALENDAR},
                        1);
            }
            return false;
        }else{
            return true;
        }
    }

    /**
     * Save current states of the Calendar here
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if (caldroidFragment != null) {
            caldroidFragment.saveStatesToKey(outState, "CALDROID_SAVED_STATE");
        }
    }

    /**
     * Destroy dialogs if the used activity is destroyed
     * e.g flipping device.
     * */
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (permRequestDialog != null) {
            permRequestDialog.dismiss();
            permRequestDialog = null;
        }

        if (infoDialog != null) {
            infoDialog.dismiss();
            infoDialog = null;
        }
    }

}
