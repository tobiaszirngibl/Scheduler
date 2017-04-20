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
import android.widget.TextView;
import android.widget.Toast;

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
 * The event feed activity.
 * It contains a listview which shows new events fetched from the server.
 * The user can accept or decline each event by clicking the according button or swiping.
 * On click detailed information according the event and colliding events is shown.
 * If no new events are available an info message is shown instead.
 */

public class EventFeedActivity extends AppCompatActivity implements FetchEventsAsyncInterface, AcceptEventAsyncInterface {

    private AlertDialog permRequestDialog;
    private AlertDialog eventActionDialog;

    private ListView eventFeedListView;
    private TextView noEventsMessage;

    private EventFeedAdapter eventFeedAdapter;

    private ArrayList<CalEvent> eventFeedList = new ArrayList<>();
    private ArrayList<CalEvent> eventDeviceList = new ArrayList<>();

    private String accessToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_feed);
        eventFeedListView = (ListView) findViewById(R.id.list_event_feed);
        noEventsMessage = (TextView) findViewById(R.id.no_events_text);

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
        }
    }

    /**
     * Check the calendar read permission.
     * Request the permission if necessary.
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

    /**
     * Destroy dialogs if the current activity is destroyed
     * e.g flipping device.
     */
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

    /**
     * Start the login activity.
     */
    private void logout() {

        Intent logoutIntent = new Intent(this, LoginActivity.class);
        startActivity(logoutIntent);
    }

    /**
     * Start an asynctask to fetch new events data from the server.
     */
    private void fetchEventFeedData() {
        FetchEventsAsyncTask fetchEventsAsyncTask = new FetchEventsAsyncTask();
        fetchEventsAsyncTask.fetchEventsAsyncInterface = this;
        fetchEventsAsyncTask.execute(Constants.NEW_EVENTS_URL, accessToken);
    }

    /**
     * Save the new events list when the async task finished fetching it from the server.
     * Hide the no events message if new events are available.
     * @param eventFeedList Arraylist containing the fetched calEvents
     */
    @Override
    public void newEventsFetchingSuccess(ArrayList<CalEvent> eventFeedList) {
        this.eventFeedList = eventFeedList;
        if(eventFeedList.size() > 0){
            hideNoEventsMsg();
        }
        setupEventFeedList();
    }

    /**
     * Show an error alert when the event fetching process failed.
     */
    @Override
    public void newEventsFetchingError() {
        Toast toast = Toast.makeText(this, Constants.FETCHING_ERROR_MESSAGE, Toast.LENGTH_SHORT);
        toast.show();
    }

    /**
     * Hide the "no events available" message.
     */
    private void hideNoEventsMsg() {
        noEventsMessage.setVisibility(View.GONE);
    }

    /**
     * Show the "no events available" message.
     */
    private void showNoEventsMsg() {
        noEventsMessage.setVisibility(View.VISIBLE);
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
     * @param position Position of the clicked event
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
     * @param event The calEvent to be accepted
     * @param position The position of the calEvent in the feed
     */
    private void showEventAcceptAlert(final CalEvent event, final int position) {
        //Variables necessary for the acceptEventAsyncTask
        final AcceptEventAsyncInterface acceptEventAsyncInterface = this;
        final Integer eventId = event.getEventId();
        final String[] eventTitle = {event.getEventName()};
        final Context context = this;

        //String containing info according colliding events
        String collEventsString = getCollEventsInfo(event);

        //String array containing the event data and collide info
        String[] eventInfo = {MiscUtility.calEventToInfo(event) + "\n" + collEventsString};

        eventActionDialog = new AlertDialog.Builder(this)
                .setTitle("Accept event?")
                .setMessage(MiscUtility.formatEventInfo(eventTitle, eventInfo))
                .setPositiveButton("Accept", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        AcceptEventAsyncTask acceptEventAsyncTask = new AcceptEventAsyncTask(context);
                        acceptEventAsyncTask.acceptEventAsyncInterface = acceptEventAsyncInterface;
                        acceptEventAsyncTask.execute(accessToken, eventId, position, Constants.EVENT_ANSWER_POSITIVE, eventTitle[0]);
                    }
                })
                .setNegativeButton("Cancel", null)
                .create();
        eventActionDialog.show();
    }

    /**
     * When the positive answer according an event has been sent to the server
     * add the event to the device calendar and remove it from the event feed.
     * @param eventPosition Position of the clicked event
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
     * @param eventPosition Position of the event in the feed
     */
    @Override
    public void eventDeclined(Integer eventPosition) {
        removeEventFromFeed(eventPosition);
    }

    /**
     * Check for time collision between the given event and the existing
     * calendar events. Return a string containing the event titles that collide with
     * the given event.
     * @param event The calEvent object to be checked for collisions
     * @return String containing the titles of the colliding events
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
     * @param position Position of the clicked event in the feed
     */
    public void onEventDeclined(int position){
        CalEvent event = eventFeedList.get(position);

        showEventDeclineAlert(event, position);
    }

    /**
     * Ask the user whether he wants to decline the according event and therefore
     * start an asyncTask to send the negative answer to the server.
     * Cancel the process otherwise.
     * @param event CalEvent to be declined
     * @param position Position of the clicked calEvent in the feed
     */
    private void showEventDeclineAlert(final CalEvent event, final int position) {
        final Context context = this;
        final AcceptEventAsyncInterface acceptEventAsyncInterface = this;
        final Integer eventId = event.getEventId();
        final String eventTitle[] = {event.getEventName()};

        //String array containing the event data
        String[] eventInfo = {MiscUtility.calEventToInfo(event)};

        eventActionDialog = new AlertDialog.Builder(this)
                .setTitle("Decline event?")
                .setMessage(MiscUtility.formatEventInfo(eventTitle, eventInfo))
                .setPositiveButton("Decline", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        AcceptEventAsyncTask acceptEventAsyncTask = new AcceptEventAsyncTask(context);
                        acceptEventAsyncTask.acceptEventAsyncInterface = acceptEventAsyncInterface;
                        acceptEventAsyncTask.execute(accessToken, eventId, position, Constants.EVENT_ANSWER_NEGATIVE, eventTitle[0]);
                    }
                })
                .setNegativeButton("Cancel", null)
                .create();
        eventActionDialog.show();
    }

    /**
     * Remove the event at the given position from the new event feed list
     * when the user has either accepted or declined it.
     * Notify the eventFeed adapter that the eventFeedList changed.
     * Show the "no events available" message if the removed event was the last list element.
     * @param position Position of the event to be removed from the feed
     */
    private void removeEventFromFeed(int position){
        eventFeedList.remove(position);
        eventFeedAdapter.notifyDataSetChanged();

        if(eventFeedList.size() == 0){
            showNoEventsMsg();
        }
    }

}
