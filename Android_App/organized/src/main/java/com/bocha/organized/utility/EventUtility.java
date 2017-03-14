package com.bocha.organized.utility;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.Toast;

import com.bocha.organized.data.Event;
import com.bocha.organized.data.CalEvent;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by oCocha on 31.01.2017.
 */

public class EventUtility {
    private final static String TAG = "EventUtility";

    private static Integer returnEventId = 1;

    private static AlertDialog permRequestDialog;

    public static ArrayList<CalEvent> eventList = new ArrayList<CalEvent>();
    //public static ArrayList<ArrayList> eventList = new ArrayList<ArrayList>();

    private static Context context;

    /**Read all events from the device default calendar and return
     * an arraylist containing all events
     * @param context
     * @return ArrayList An ArrayList containing the events of the device calendar app
     */
    public static ArrayList<CalEvent> readCalendarEvent(Context context) {
        //Clear the eventList if it is not null
        if (eventList != null) {
            eventList.clear();
        }

        Cursor cursor = context.getContentResolver()
                .query(
                        Uri.parse("content://com.android.calendar/events"),
                        new String[]{"calendar_id", "title", "description",
                                "dtstart", "dtend", "eventLocation", "deleted", "_id", "uid2445"}, null,
                        null, null);
        cursor.moveToFirst();

        //Get the number of events returned
        String CNames[] = new String[cursor.getCount()];

        //Save the current event in a single Arraylist
         // format: title, startTime, endTime, description, location, calID
         //
        for (int i = 0; i < CNames.length; i++) {
            //Checks whether the given event is the requested kind of event id and
             // if the event is already marked to be deleted(deleted == 0)
             //
             // 1: event
             // 3: holiday
            if (Integer.parseInt(cursor.getString(0)) == returnEventId && Integer.parseInt(cursor.getString(6)) == 0) {
                CalEvent tempCalEvent = cursorToCalEvent(cursor);

                Log.v(TAG, "TESTTTTTTTTTTT: "+cursor.getString(8));

                //Add the current calEvent to the Arraylist containing all calEvents
                eventList.add(tempCalEvent);
                CNames[i] = cursor.getString(1);
            }
            cursor.moveToNext();
        }
        return eventList;
    }

    /**
     * Create a new event object from a given cursor object
     * @param cursor
     * @return
     */
    private static CalEvent cursorToCalEvent(Cursor cursor) {
        String title = cursor.getString(1);
        String description = cursor.getString(2);
        String location = cursor.getString(5);
        Integer id = Integer.parseInt(cursor.getString(7));
        Date startDate = new Date(Long.parseLong(cursor.getString(3)));
        Date endDate = new Date(Long.parseLong(cursor.getString(4)));
        //Date lastUpdated = cursor.getString(8);
        //Log.v(TAG, "start: "+cursor.getString(2)+" --- end: "+cursor.getString(3)/*+" --- update: "+cursor.getString(8)*/);

        CalEvent calEvent = new CalEvent(title, new Date(), startDate, endDate, location, description/*, notes, town*/, id);
        return calEvent;
    }

    public static String getDate(long milliSeconds) {
        SimpleDateFormat formatter = new SimpleDateFormat(
                "dd/MM/yyyy HH:mm:ss a");
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());
    }

    /**Add an calendar event to the default calendar app
     * @param activity
     * @param event
     * */
    public static Integer addEvent(final Activity activity, Event event) {

        /**Save the event data in a final variable*/
        final ContentValues eventValues = extractEventData(event);

        Uri uri = null;
        int eventId = -1;

        Log.v(TAG, "eventValues: " + eventValues);

        final ContentResolver cr = activity.getContentResolver();

        if (permissionGrantedWriteCal(activity)) {
            if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
                return (-1);
            }
            uri = cr.insert(CalendarContract.Events.CONTENT_URI, eventValues);
            Log.v(TAG, "URI: "+uri);
        }

        /**Save the id of the created event*/
        String projection[] = {"_id"};
        Cursor cursor = cr.query(uri, null, null, null,
                null);
        if (cursor.moveToFirst()) {
            String calID;
            int idCol = cursor.getColumnIndex(projection[0]);
            do {
                calID = cursor.getString(idCol);
                eventId = Integer.parseInt(calID);
            }while(cursor.moveToNext());
            cursor.close();
        }
        return eventId;
    }

    /**Update the eventlist*//*
    public static void updateEventList() {
        //Clear the eventList if it is not null
        if(eventList != null){
            eventList.clear();
        }

        Cursor cursor = context.getContentResolver()
                .query(
                        Uri.parse("content://com.android.calendar/events"),
                        new String[]{"calendar_id", "title", "description",
                                "dtstart", "dtend", "eventLocation"}, null,
                        null, null);
        cursor.moveToFirst();

        //Get the number of events returned
        String CNames[] = new String[cursor.getCount()];

        //Save the current event in a single Arraylist
         * format: title, startTime, endTime, description, location, calID
         //
        for (int i = 0; i < CNames.length; i++) {
            //Checks whether the given event is the wanted kind of event id
             * 1: event
             // 3: holiday
            if(Integer.parseInt(cursor.getString(0)) == returnEventId){
                ArrayList<String> tempEvenList = new ArrayList<String>();
                tempEvenList.add(cursor.getString(1));
                tempEvenList.add(cursor.getString(3));
                tempEvenList.add(cursor.getString(4));
                tempEvenList.add(cursor.getString(2));
                tempEvenList.add(cursor.getString(5));
                tempEvenList.add(cursor.getString(0));

                //Add the current event to the Arraylist containing all events
                eventList.add(tempEvenList);
                CNames[i] = cursor.getString(1);
            }
            cursor.moveToNext();
        }
    }*/

    private static ContentValues extractEventData(Event event) {
        /**Gather the data which is necessary to create a new event*/
        long calID = 1;
        long startMillis = event.getEventStartDate().getTime();
        long endMillis = event.getEventEndDate().getTime();
        long lastChanged = event.getEventLastChanged().getTime();

        String eventName = event.getEventName();
        if(eventName.equals("")){
            eventName= "Title";
        }

        String eventLocation = event.getEventLocation();
        if(eventName.equals("")){
            eventName= "Location";
        }

        String eventDescription = event.getEventDescription();
        if(eventDescription.equals("")){
            eventDescription = "Description";
        }

        /**Create new ContentValues containing the event data*/
        ContentValues values = new ContentValues();
        values.put(CalendarContract.Events.DTSTART, startMillis);
        values.put(CalendarContract.Events.DTEND, endMillis);
        values.put(CalendarContract.Events.TITLE, eventName);
        values.put(CalendarContract.Events.EVENT_LOCATION, eventLocation);
        values.put(CalendarContract.Events.DESCRIPTION, eventDescription);
        values.put(CalendarContract.Events.CALENDAR_ID, calID);
        values.put(CalendarContract.Events.EVENT_TIMEZONE, "Europe/Berlin");
        values.put("uid2445", "1@organized");

        return values;
    }

    /**Check whether the new event collides with old events
     * Ask the user if he wants to create the new event if collisions occur*/
    public static ArrayList<String> checkEventCollision(long startMillis, long endMillis) {
        ArrayList<String> collidingEvents = new ArrayList<>();

        for(int i = 0, l = eventList.size(); i < l; i++){
            if((eventList.get(i).getEventStartDate().getTime() >= startMillis) && ((eventList.get(i).getEventStartDate().getTime()) <= endMillis) ||
                    ((eventList.get(i).getEventEndDate().getTime() >= startMillis) && (eventList.get(i).getEventEndDate().getTime() <= endMillis))){
                collidingEvents.add(eventList.get(i).getEventName());
                Log.v(TAG, "Event collision: " + eventList.get(i).getEventName());
            }
        }
        return collidingEvents;
    }

    /**Check whether the app can write in the calendar device app
     * Request the necessary permisison if not*/
    private static boolean permissionGrantedWriteCal(Activity activity){
        if (ContextCompat.checkSelfPermission(activity,
                Manifest.permission.WRITE_CALENDAR)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
                    Manifest.permission.WRITE_CALENDAR)) {

                final Activity finalActivity = activity;

                permRequestDialog = new AlertDialog.Builder(finalActivity)
                        .setTitle("Calendar read permission needed")
                        .setMessage("The app needs the calendar read permission to get the events from the default calendar app.")
                        .setPositiveButton("Accept", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                //Request the permission if the user accepts
                                ActivityCompat.requestPermissions(finalActivity,
                                        new String[]{Manifest.permission.WRITE_CALENDAR},
                                        1);
                            }
                        })
                        .setNegativeButton("Decline", null)
                        .create();
                permRequestDialog.show();

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(activity,
                        new String[]{Manifest.permission.WRITE_CALENDAR},
                        1);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
            return false;
        }else{
            return true;
        }
    }

    /**Get the eventID for an event with a given eventtitle*/
    public static int getEventIdByTitle(Context newContext, String eventtitle) {
        context = newContext;
        Uri eventUri;

        /**Create the base for the eventUri according to the used device sdk*/
        if (android.os.Build.VERSION.SDK_INT <= 7) {
            eventUri = Uri.parse("content://calendar/events");
        } else {
            eventUri = Uri.parse("content://com.android.calendar/events");
        }

        int result = 0;
        String projection[] = {"_id", "title"};
        Cursor cursor = context.getContentResolver().query(eventUri, null, null, null,
                null);

        /**Check all results whether they contain the given eventTitle
         * Return the according eventId if a match occurs*/
        if (cursor.moveToFirst()) {
            String calName;
            String calID;
            int nameCol = cursor.getColumnIndex(projection[1]);
            int idCol = cursor.getColumnIndex(projection[0]);
            do {
                calName = cursor.getString(nameCol);
                calID = cursor.getString(idCol);
                if (calName != null && calName.contains(eventtitle)) {
                    result = Integer.parseInt(calID);
                }
            } while (cursor.moveToNext());
            cursor.close();
        }
        Log.v(TAG, "Event: "+eventtitle+" got Id: "+result);
        return result;
    }

    /**Get the start date for an event with a given event id*/
    public static Long getEventStartById(Context newContext, Integer eventId) {
        context = newContext;
        Uri eventUri;

        /**Create the base for the eventUri according to the used device sdk*/
        if (android.os.Build.VERSION.SDK_INT <= 7) {
            eventUri = Uri.parse("content://calendar/events");
        } else {
            eventUri = Uri.parse("content://com.android.calendar/events");
        }

        Long result = 0l;
        String projection[] = {"_id", "dtstart"};
        Cursor cursor = context.getContentResolver().query(eventUri, null, null, null,
                null);

        /**Check all results whether they contain the given eventTitle
         * Return the according eventId if a match occurs*/
        if (cursor.moveToFirst()) {
            String calStart;
            String calID;
            int startCol = cursor.getColumnIndex(projection[1]);
            int idCol = cursor.getColumnIndex(projection[0]);
            do {
                calStart = cursor.getString(startCol);
                calID = cursor.getString(idCol);
                if (calID != null && Integer.parseInt(calID) == eventId) {
                    result = Long.parseLong(calStart);
                }
            } while (cursor.moveToNext());
            cursor.close();
        }
        Log.v(TAG, "Event: "+eventId+" got start time: "+result);
        return result;
    }

    /**Delete a calendar event of the default device calendar app
     * and show a toast afterwards
     * @param newContext
     * @param eventId The Id of the event
     */
    public static void deleteEventById(Context newContext, Integer eventId) {
        context = newContext;
        Log.v(TAG, "Delete event: "+eventId);
        ContentResolver cr = context.getContentResolver();
        Uri deleteUri = null;
        deleteUri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, eventId);
        int rows = cr.delete(deleteUri, null, null);
        Log.i(TAG, "Rows deleted: " + rows);
        Toast toast = Toast.makeText(context, "Event " + eventId + "deleted.", Toast.LENGTH_SHORT);
        toast.show();
    }

    /**Update the eventTitle of an event of the default device calendar app
     *
     * @param newContext
     * @param eventTitle The new event title
     * @param eventId The Id of the event
     */
    public static void updateEventTitle(Context newContext, String eventTitle, Integer eventId) {
        context = newContext;

        ContentResolver cr = context.getContentResolver();
        ContentValues values = new ContentValues();
        Uri updateUri = null;
        // The new title for the event
        values.put(CalendarContract.Events.TITLE, eventTitle);
        Log.v(TAG, "New title: " + eventTitle + " ID: " +eventId);
        updateUri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, eventId);
        int rows = cr.update(updateUri, values, null, null);
        Log.v(TAG, "Rows updated: " + rows);
    }

    /**Set the type of events that should be returned in the readCalendarEvent function
     *  0:
     *  1: event/Termin
     *  2: birthday
     *  3: Feiertag
     *
     * @param i type of the event to be returned
     */
    private void setReturnEventId(Integer i){
        returnEventId = i;
    }


    /**Convert a start and an end date in milliseconds to
     * a string array containing a date and a time string
     * format: dd/MM/yyyy
     * format: hh:mm
     *
     * @param startMillis starting date in milliseconds
     * @param endMillis ending date in milliseconds
     * @return string array
     */
    public static String[] calculateDate(Long startMillis, Long endMillis) {
        String[] dateData = new String[2];
        String startString = millisToDate(startMillis);
        String endString = millisToDate(endMillis);
        String date1 = startString.split(" ")[0];
        String date2 = endString.split(" ")[0];
        String time1 = startString.split(" ")[1];
        String time2 = endString.split(" ")[1];
        String dateString;
        String timeString;


        if(date1.equals(date2)){
            dateString = date1;
        }else{
            dateString = date1 + " - " + date2;
        }

        timeString = time1 + " - " +time2;

        dateData[0] = dateString;
        dateData[1] = timeString;

        return dateData;
    }

    /**Convert a milliseconds date to a String date
     * format: dd/MM/yyyy hh:mm a
     *
     * @param milliSeconds date in milliseconds
     * @return date in String
     */
    private static String millisToDate(Long milliSeconds) {
        SimpleDateFormat formatter = new SimpleDateFormat(
                "dd/MM/yyyy HH:mm a");
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());
    }
}
