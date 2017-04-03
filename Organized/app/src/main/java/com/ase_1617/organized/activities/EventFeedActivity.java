package com.ase_1617.organized.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.ase_1617.organized.LoginActivity;
import com.ase_1617.organized.R;
import com.ase_1617.organized.adapter.EventFeedAdapter;
import com.ase_1617.organizedlib.data.CalEvent;
import com.ase_1617.organizedlib.listener.SwipeListener;
import com.ase_1617.organizedlib.network.AcceptEventAsyncInterface;
import com.ase_1617.organizedlib.network.AcceptEventAsyncTask;
import com.ase_1617.organizedlib.network.FetchEventsAsyncInterface;
import com.ase_1617.organizedlib.network.FetchEventsAsyncTask;
import com.ase_1617.organizedlib.utility.Constants;
import com.ase_1617.organizedlib.utility.EventUtility;
import com.ase_1617.organizedlib.utility.MiscUtility;

import java.util.ArrayList;

/**
 * The main app activity.
 * THe app shows new events fetched from the server and the user can accept or decline them.
 */

public class EventFeedActivity extends AppCompatActivity implements FetchEventsAsyncInterface, AcceptEventAsyncInterface {

    private static final String TAG = "Event Feed";
    public static final String PREFS_NAME = "LoginPrefs";

    private SharedPreferences userData;

    private AlertDialog permRequestDialog;
    private AlertDialog eventActionDialog;

    private ListView eventFeedListView;

    private EventFeedAdapter eventFeedAdapter;

    private ArrayList<CalEvent> eventFeedList = new ArrayList<>();
    private ArrayList<CalEvent> eventDeviceList = new ArrayList<>();

    private String newEventsUrl = Constants.SERVER_URL_BASE + ":8000/api/appointment/";
    private String accessToken;

    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_feed);
        eventFeedListView = (ListView) findViewById(R.id.list_event_feed);
        userData = getSharedPreferences(PREFS_NAME, 0);
        accessToken = userData.getString("accessToken", "accessToken");
        editor = userData.edit();

        fetchEventFeedData();
        readEvents();
        setupNewEventsSwipeListener();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.event_feed_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.activity_calendar:
                Intent calIntent = new Intent(this, CalendarActivity.class);
                startActivity(calIntent);
                return true;
            case R.id.action_log_out:
                logout();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        readEvents();
    }

    /**Setup a swipeListener on the newEventsList */
    private void setupNewEventsSwipeListener() {

        final SwipeListener swipeListener = new SwipeListener();
        eventFeedListView.setOnTouchListener(swipeListener);

        eventFeedListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                if (swipeListener.swipeDetected()) {
                    if (swipeListener.getAction() == SwipeListener.Action.LR) {
                        onEventAccepted(position);
                    } else if(swipeListener.getAction() == SwipeListener.Action.RL) {
                        onEventDeclined(position);
                    }
                }
            }
        });


    }

    /**
     * Fetch the device calendar events and save them in a list
     * */
    private void readEvents(){
        if(permissionGrantedReadCal()){
            //Clear the eventList if necessary
            if(eventDeviceList != null){
                eventDeviceList.clear();
            }
            //Update the eventList
            eventDeviceList = EventUtility.fetchDeviceEvents(this);
        }else{
            Log.v(TAG, "Read events permission not granted");
        }
    }

    /**Check whether the app can read in the calendar device app
     * Request the necessary permisison if not*/
    private boolean permissionGrantedReadCal(){
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_CALENDAR)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_CALENDAR)) {

                final Activity activity = this;

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

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_CALENDAR},
                        1);
            }
            return false;
        }else{
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults.length > 0 && grantResults[0]== PackageManager.PERMISSION_GRANTED){
            Log.v(TAG,"Permission: "+permissions[0]+ " was "+grantResults[0] + "PERM"+Manifest.permission.READ_CALENDAR);
            //resume tasks needing this permission
            if(permissions[0].equals(Manifest.permission.READ_CALENDAR)){
                Log.v(TAG, "Permission result read calendar success.");
                //setupNewEventsList();
            }
        }
    }

    /**Destroy permission request dialogs if the used activity is destroyed
     * e.g flipping device*/
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (permRequestDialog != null) {
            permRequestDialog.dismiss();
            permRequestDialog = null;
        }
        if (eventActionDialog != null) {
            eventActionDialog.dismiss();
            eventActionDialog = null;
        }
    }

    /**Replace the saved login data with the default values
     * and start the login activity*/
    private void logout() {
        editor.putString("userMail", "Default");
        editor.putString("userPass", "Default");

        // Commit the edits
        editor.commit();

        Intent logoutIntent = new Intent(this, LoginActivity.class);
        startActivity(logoutIntent);
    }

    /**Try to fetch the new events data from the server and save the result*/
    private void fetchEventFeedData() {
        FetchEventsAsyncTask fetchEventsAsyncTask = new FetchEventsAsyncTask();
        fetchEventsAsyncTask.fetchEventsAsyncInterface = this;
        fetchEventsAsyncTask.execute(newEventsUrl, accessToken);
    }

    /**
     * Override AsynTaskInterface method to
     * save the new events list when the async task finished fetching it from the server
     * @param eventFeedList
     */
    @Override
    public void newEventsFetched(ArrayList<CalEvent> eventFeedList) {
        this.eventFeedList = eventFeedList;
        setupEventFeedList();
    }

    /**
     * Create or update the EventFeedAdapter whether its already created or not
     */
    private void setupEventFeedList() {
        if (eventFeedAdapter == null) {
            eventFeedAdapter = new EventFeedAdapter(this,
                    R.layout.item_event_feed,
                    eventFeedList);
            eventFeedListView.setAdapter(eventFeedAdapter);
            Log.v(TAG, "New adapter");
        } else {
            eventFeedAdapter.clear();
            eventFeedAdapter.addAll(eventFeedList);
            eventFeedAdapter.notifyDataSetChanged();
            Log.v(TAG, "NotifyDataSetChanged");
        }
    }

    /**
     * If the accept imageButton of a listElement is clicked
     * start the detailEventActivity and provide the event data in the intent
     * @param position
     */
    public void onEventAccepted(int position){
        CalEvent event = (CalEvent)eventFeedList.get(position);

        showEventAcceptAlert(event, position);
    }

    /**
     * Ask the user whether he wants to accept the according event and therefore
     * create a new device event for it. Furthermore show info according colliding
     * calendar events if available.
     * @param event
     */
    private void showEventAcceptAlert(final CalEvent event, final int position) {

        final Activity activity = this;

        final AcceptEventAsyncInterface acceptEventAsyncInterface = this;

        String collEventsString = getCollEventsInfo(event);
        final Integer eventId = event.getEventId();

        final Context context = this;

        eventActionDialog = new AlertDialog.Builder(this)
                .setTitle("Accept event?")
                .setMessage(MiscUtility.calEventToInfo(event) + "\n" + collEventsString)
                .setPositiveButton("Accept", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        AcceptEventAsyncTask acceptEventAsyncTask = new AcceptEventAsyncTask(context);
                        acceptEventAsyncTask.acceptEventAsyncInterface = acceptEventAsyncInterface;
                        acceptEventAsyncTask.execute(accessToken, eventId, position, "yes");
                    }
                })
                .setNegativeButton("Back", null)
                .create();
        eventActionDialog.show();
    }

    @Override
    public void eventAccepted(Integer eventPosition) {
        CalEvent event = (CalEvent)eventFeedList.get(eventPosition);

        EventUtility.addOrganizedEvent(this, event);

        removeEventFromFeed(eventPosition);
    }

    @Override
    public void eventDeclined(Integer eventPosition) {
        CalEvent event = (CalEvent)eventFeedList.get(eventPosition);

        removeEventFromFeed(eventPosition);
    }

    /**
     * Check for time collision between the event to be created and the existing
     * calendar events. Return a string containing the event titles to collide with
     * the new event.
     * @param event
     * @return
     */
    private String getCollEventsInfo(CalEvent event) {
        ArrayList<String> collidingEvents = EventUtility.checkEventCollision(event.getEventStartDate().getTime(), event.getEventEndDate().getTime());
        String collEventsString = "";

        if(collidingEvents.size() > 0) {

            collEventsString = "Collision with:" + "\n";

            for (int i = 0; i < collidingEvents.size(); i++) {
                collEventsString += " - " + collidingEvents.get(i) + "\n";
            }
        }

        return collEventsString;
    }

    /**
     * If the decline button of a listElement is clicked
     * delete the event from the list and send a response to the server
     * TODO:Event Löschen; Antwort an Server
     * @param position
     */
    public void onEventDeclined(int position){
        CalEvent event = eventFeedList.get(position);

        showEventDeclineAlert(event, position);
    }

    /**
     * Ask the user whether he wants to accept the according event and therefore
     * create a new device event for it. Furthermore show info according colliding
     * calendar events if available.
     * @param event
     */
    private void showEventDeclineAlert(final CalEvent event, final int position) {

        final Context context = this;
        final AcceptEventAsyncInterface acceptEventAsyncInterface = this;
        final Integer eventId = event.getEventId();

        eventActionDialog = new AlertDialog.Builder(this)
                .setTitle("Decline event?")
                .setMessage(MiscUtility.calEventToInfo(event))
                .setPositiveButton("Decline", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.v(TAG, "Event declined: " + event.getEventName());

                        AcceptEventAsyncTask acceptEventAsyncTask = new AcceptEventAsyncTask(context);
                        acceptEventAsyncTask.acceptEventAsyncInterface = acceptEventAsyncInterface;
                        acceptEventAsyncTask.execute(accessToken, eventId, position, "no");
                    }
                })
                .setNegativeButton("Back", null)
                .create();
        eventActionDialog.show();
    }

    /**
     * Remove the event at the given position from the new event feed list
     * when the user has either accepted or declined it.
     * Notify the eventFeed adapter the eventFeedList changed.
     */
    private void removeEventFromFeed(int position){
        eventFeedList.remove(position);
        eventFeedAdapter.notifyDataSetChanged();
    }

}
