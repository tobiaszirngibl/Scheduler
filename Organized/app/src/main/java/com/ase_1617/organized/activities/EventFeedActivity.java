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

    private AlertDialog permRequestDialog;
    private AlertDialog eventActionDialog;

    private ListView eventFeedListView;

    private EventFeedAdapter eventFeedAdapter;

    private ArrayList<CalEvent> eventFeedList = new ArrayList<>();
    private ArrayList<CalEvent> eventDeviceList = new ArrayList<>();

    private String accessToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_feed);
        eventFeedListView = (ListView) findViewById(R.id.list_event_feed);

        getAccessToken();
        fetchEventFeedData();
        readEvents();
        setupNewEventsSwipeListener();
    }

    /**
     * Get the Oauth accessToken of this session.
     * It is saved in the shared preferences when logging in successfully.
     */
    private void getAccessToken() {
        SharedPreferences userData;
        userData = getSharedPreferences(Constants.PREFS_NAME, 0);
        accessToken = userData.getString("accessToken", "accessToken");
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

    /**
     * Setup a swipeListener on the newEventsList.
     * Swipe right: accept event.
     * Swipe left: decline event.
     * */
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
     * Check for the read calendar permission.
     * Fetch the device calendar events and save them in a list.
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

    /**
     * Check the calendar read permission.
     * Request the permission if necessary.
     *
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
                        .setTitle("Missing permission")
                        .setMessage("Organized needs permission to read calendar events.")
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

                //If no explanation needed just request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_CALENDAR},
                        1);
            }
            return false;
        }else{
            return true;
        }
    }

    //TODO:Any permission granted reaction needed??
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            Log.v(TAG,"Permission: "+permissions[0]+ " was "+grantResults[0] + "PERM"+Manifest.permission.READ_CALENDAR);
            //resume tasks needing this permission
            if(permissions[0].equals(Manifest.permission.READ_CALENDAR)){
                Log.v(TAG, "Permission result read calendar success.");
                //setupNewEventsList();
            }
        }
    }

    /**
     * Destroy dialogs if the current activity is destroyed
     * e.g flipping device.
     * */
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

    //TODO:Delete saved login when logging out
    /**
     * Replace the saved login data with the default values and.
     * Start the login activity.
     * */
    private void logout() {
        //editor.putString("userMail", "Default");
        //editor.putString("userPass", "Default");
        //editor.commit();

        Intent logoutIntent = new Intent(this, LoginActivity.class);
        startActivity(logoutIntent);
    }

    /**
     * Try to fetch new events data from the server.
     * */
    private void fetchEventFeedData() {
        FetchEventsAsyncTask fetchEventsAsyncTask = new FetchEventsAsyncTask();
        fetchEventsAsyncTask.fetchEventsAsyncInterface = this;
        fetchEventsAsyncTask.execute(Constants.NEW_EVENTS_URL, accessToken);
    }

    /**
     * Save the new events list when the async task finished fetching it from the server.
     * @param eventFeedList
     */
    @Override
    public void newEventsFetched(ArrayList<CalEvent> eventFeedList) {
        this.eventFeedList = eventFeedList;
        setupEventFeedList();
    }

    /**
     * Create or update the EventFeedAdapter whether its already created or not.
     */
    private void setupEventFeedList() {
        if (eventFeedAdapter == null) {
            eventFeedAdapter = new EventFeedAdapter(this,
                    R.layout.item_event_feed,
                    eventFeedList);
            eventFeedListView.setAdapter(eventFeedAdapter);
        } else {
            eventFeedAdapter.clear();
            eventFeedAdapter.addAll(eventFeedList);
            eventFeedAdapter.notifyDataSetChanged();
        }
    }

    /**
     * If the accept imageButton of an eventFeed element is clicked.
     * Show an event accept alert containing event details and options to
     * finally accept the event or cancel the process.
     * @param position
     */
    public void onEventAccepted(int position){
        CalEvent event = eventFeedList.get(position);

        showEventAcceptAlert(event, position);
    }

    /**
     * Ask the user whether he wants to accept the according event and therefore
     * start an asyncTask to send the positive answer to the server.
     * Cancel the process otherwise.
     * Furthermore show info according colliding events.
     * @param event
     * @param position
     */
    private void showEventAcceptAlert(final CalEvent event, final int position) {
        //Variables necessary for the acceptEventAsyncTask
        final AcceptEventAsyncInterface acceptEventAsyncInterface = this;
        final Integer eventId = event.getEventId();
        final Context context = this;

        //String containing info according colliding events
        String collEventsString = getCollEventsInfo(event);

        eventActionDialog = new AlertDialog.Builder(this)
                .setTitle("Accept event?")
                .setMessage(MiscUtility.calEventToInfo(event) + "\n" + collEventsString)
                .setPositiveButton("Accept", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        AcceptEventAsyncTask acceptEventAsyncTask = new AcceptEventAsyncTask(context);
                        acceptEventAsyncTask.acceptEventAsyncInterface = acceptEventAsyncInterface;
                        acceptEventAsyncTask.execute(accessToken, eventId, position, Constants.EVENT_ANSWER_POSITIVE);
                    }
                })
                .setNegativeButton("Cancel", null)
                .create();
        eventActionDialog.show();
    }

    /**
     * When the positive answer according an event has been sent to the server
     * add the event to the device calendar and remove it from the event feed.
     * @param eventPosition
     */
    @Override
    public void eventAccepted(Integer eventPosition) {
        CalEvent event = eventFeedList.get(eventPosition);

        EventUtility.addOrganizedEvent(this, event);

        removeEventFromFeed(eventPosition);
    }

    /**
     * When the negative answer according an event has been sent to the server
     * remove the event from the event feed.
     * @param eventPosition
     */
    @Override
    public void eventDeclined(Integer eventPosition) {
        removeEventFromFeed(eventPosition);
    }

    /**
     * Check for time collision between the given event and the existing
     * calendar events. Return a string containing the event titles that collide with
     * the given event.
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
     * If the decline imageButton of an eventFeed element is clicked.
     * Show an event decline alert containing event details and options to
     * finally decline the event or cancel the process.
     * @param position
     */
    public void onEventDeclined(int position){
        CalEvent event = eventFeedList.get(position);

        showEventDeclineAlert(event, position);
    }

    /**
     * Ask the user whether he wants to decline the according event and therefore
     * start an asyncTask to send the negative answer to the server.
     * Cancel the process otherwise.
     * @param event
     * @param position
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
                        AcceptEventAsyncTask acceptEventAsyncTask = new AcceptEventAsyncTask(context);
                        acceptEventAsyncTask.acceptEventAsyncInterface = acceptEventAsyncInterface;
                        acceptEventAsyncTask.execute(accessToken, eventId, position, Constants.EVENT_ANSWER_NEGATIVE);
                    }
                })
                .setNegativeButton("Back", null)
                .create();
        eventActionDialog.show();
    }

    /**
     * Remove the event at the given position from the new event feed list
     * when the user has either accepted or declined it.
     * Notify the eventFeed adapter that the eventFeedList changed.
     */
    private void removeEventFromFeed(int position){
        eventFeedList.remove(position);
        eventFeedAdapter.notifyDataSetChanged();
    }

}
