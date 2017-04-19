package com.ase_1617.organized.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableStringBuilder;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.TimePicker;

import com.ase_1617.organized.LoginActivity;
import com.ase_1617.organized.R;
import com.ase_1617.organizedlib.data.CalEvent;
import com.ase_1617.organizedlib.data.Event;
import com.ase_1617.organizedlib.utility.EventUtility;
import com.ase_1617.organizedlib.utility.MiscUtility;
import com.ase_1617.organizedlib.views.ExpandedListView;
import com.roomorama.caldroid.CaldroidFragment;
import com.roomorama.caldroid.CaldroidListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * The calendar activity of the organized example app.
 * It contains a calendar fragment and a listview.
 * Both show the device events and offer additional information according the events on click.
 * The user can either delete existing events or create new ones by clicking the according
 * buttons or long clicking a date in the calendar fragment.
 */

public class CalendarActivity extends AppCompatActivity {

    private AlertDialog permRequestDialog;
    private AlertDialog infoDialog;

    private ListView myEventListView;

    private ArrayList<CalEvent> deviceEventList;

    private ArrayAdapter<String> myAdapter;

    private CaldroidFragment caldroidFragment;

    SimpleDateFormat formatter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);
        myEventListView = (ExpandedListView) findViewById(R.id.list_event_calendar);

        setupCaldroid(savedInstanceState);

        formatter = new SimpleDateFormat("dd MMM yyyy HH : mm", Locale.GERMAN);

        fetchDeviceEvents();
        updateUI();
        setupListClickListener();
        if(deviceEventList != null) {
            insertEvents();
        }
    }

    /**
     * Setup the caldroid calendar fragment.
     * @param savedInstanceState Settings of a previous instance
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
             * @param date The clicked date
             * @param view The clicked view
             */
            @Override
            public void onSelectDate(Date date, View view) {
                ArrayList<CalEvent> matchingEvents = getMatchingEvents(date);
                if(matchingEvents.size() != 0){
                    showEventsDate(matchingEvents);
                }
                updateUI();
            }

            /**
             * On LongClicking a date in the caldroid fragment open the new event dialog,
             * fetch the necessary new event data and add the event to the device calendar.
             * @param date The long clicked date
             * @param view The long clicked view
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

        //Set the created listener
        caldroidFragment.setCaldroidListener(listener);

    }

    /**
     * Setup the hour and the minute picker to display the according numbers
     *
     * @param hourPicker The hourPicker to configure
     * @param minutePicker The minutePicker to configure
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
     * Create a new event object from the given parameters and add it to the device calendar.
     * @param lastUpdate The date the event was updated the last time
     * @param date The date of the event
     * @param length The duration of the event
     * @param eventTitle The title of the event
     * @param eventDescription the description of the event
     * @param location The location of the event
     */
    private void addEvent(Date lastUpdate, Date date, Long length, String eventTitle, String eventDescription, String location) {

        Event event = new Event(eventTitle, lastUpdate, date.getTime(), date.getTime() + length, location, eventDescription);

        EventUtility.addCustomEvent(CalendarActivity.this, event);

        updateUI();
        insertEvents();
    }

    /**
     * Check whether the clicked date contains any events and return the according events as list.
     * @param date The clicked date
     * @return Arraylist containing the matching calEvents
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
     * Setup two click listeners for the listview element.
     * Click for information.
     * Longclick for creating a new event.
     */
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

    /**
     * Show event data in an info alert.
     * @param position Positon of the event to display in the eventlist
     */
    private void showEventData(int position) {
        CalEvent clickedEvent = deviceEventList.get(position);

        String[] eventTitle = {clickedEvent.getEventName()};
        String[] eventInfo = {MiscUtility.calEventToInfo(clickedEvent)};

        showInfoAlert(eventTitle, eventInfo);
    }

    /**
     * Show events data in an info alert.
     * @param eventList Arraylist containing the events to display
     */
    private void showEventsDate(ArrayList<CalEvent> eventList) {
        CalEvent clickedEvent;
        String[] eventTitles = new String[eventList.size()];
        String[] eventData = new String[eventList.size()];

        for(int i = 0, j = eventList.size(); i < j; i++){
            clickedEvent = eventList.get(i);
            eventTitles[i] = "" + clickedEvent.getEventName() + "\n";

            String[] dateData = MiscUtility.calculateDate(clickedEvent.getEventStartDate().getTime(), clickedEvent.getEventEndDate().getTime());
            eventData[i] = "" + dateData[0] + "\n" + dateData[1] + "\n" + clickedEvent.getEventDescription() + "\n" + "\n";
        }

        showInfoAlert(eventTitles, eventData);
    }

    /**
     * Insert the device events into the calendar fragment
     */
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

    /**
     * Remove the given event from the calendar fragment
     */
    private void removeCalEvent(Date removeEventStart) {

        if (caldroidFragment != null) {
            ColorDrawable eventDrawable = new ColorDrawable(Color.WHITE);
            caldroidFragment.setBackgroundDrawableForDate(eventDrawable, removeEventStart);
            caldroidFragment.refreshView();
        }
    }

    /**
     * Fetch the device events and update the list showing the events.
     */
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
            } else {
                myAdapter.clear();
                myAdapter.addAll(taskList);
                myAdapter.notifyDataSetChanged();
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
        }
    }

    /**
     * Delete the according calendar event when the delete button is clicked.
     * @param view The clicked view
     */
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
                .setMessage("Do you really want to delete the event\n" + eventTitle + "?")
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
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

    /**
     * Start the login activity
     * */
    private void logout() {

        Intent logoutIntent = new Intent(this, LoginActivity.class);
        startActivity(logoutIntent);
    }

    /**
     * Show an alert dialog containing the given info.
     * @param titles Array containing the event titles
     * @param infoTexts Array containing the event info
     */
    public void showInfoAlert(String[] titles, String[] infoTexts){
        SpannableStringBuilder infoContent = MiscUtility.formatEventInfo(titles, infoTexts);

        infoDialog = new AlertDialog.Builder(this)
                .setMessage(infoContent)
                .create();
        infoDialog.show();
    }

    /**
     * Check whether the app can read in the calendar device app.
     * Request the necessary permisison if necessary.
     * @return Boolean value
     */
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
     * Save current states of the Calendar
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
