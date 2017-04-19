package com.ase_1617.organizedlib.utility;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;

import com.ase_1617.organizedlib.data.CalEvent;
import com.ase_1617.organizedlib.data.Event;
import com.ase_1617.organizedlib.permission.permissionCheck;

import java.util.ArrayList;
import java.util.Date;

/**
 * Utitlity class containing methods to interact with the device calendar.
 * - Read existing events from the device calendar
 * - Save a new event in the device calendar
 * - Update the title of an existing event
 * - Get the titles of events that collide with an specific event
 * - Get the id of an event with an specific title
 * - Get the starting date of an event with a specific id in milliseconds
 * - Delete an event with a specific id
 */

public class EventUtility {

    private static Integer returnEventId = 1;
    private static ArrayList<CalEvent> eventList = new ArrayList<>();

    /**
     * Read all events from the device default calendar and return
     * an arraylist containing all events in calEvent format.
     * @param context Context from which the function is called
     * @return ArrayList containing the events of the device calendar app
     */
    public static ArrayList<CalEvent> fetchDeviceEvents(Context context) {
        Uri eventUri;

        //Clear the eventList if it is not null
        if (eventList != null) {
            eventList.clear();
        }

        //Create the base for the eventUri according to the used device sdk
        if(android.os.Build.VERSION.SDK_INT <= 7) {
            eventUri = Uri.parse("content://calendar/events");
        }else{
            eventUri = Uri.parse("content://com.android.calendar/events");
        }

        //Read the events from the device
        Cursor cursor = context.getContentResolver()
                .query(
                        eventUri,
                        new String[]{"calendar_id", "title", "description",
                                "dtstart", "dtend", "eventLocation", "deleted", "_id", "uid2445"}, null,
                        null, null);

        if(cursor != null){
            cursor.moveToFirst();

            //Get the number of events returned
            int eventCount = cursor.getCount();

            //Save all cursors as calEvents in the eventList
            for (int i = 0; i < eventCount; i++) {
                //Check whether the given event has the requested kind of event id and
                //if the event is already marked to be deleted(deleted == 1)
                if (Integer.parseInt(cursor.getString(0)) == returnEventId && Integer.parseInt(cursor.getString(6)) == 0) {
                    CalEvent tempCalEvent = cursorToCalEvent(cursor);
                    eventList.add(tempCalEvent);
                }
                cursor.moveToNext();
            }
        }

        return eventList;
    }

    /**
     * Create a new calEvent object from a given cursor object
     * @param cursor A cursor object containing all necessary event data
     * @return An calEvent object created from the cursor object
     */
    private static CalEvent cursorToCalEvent(Cursor cursor) {
        String title = cursor.getString(1);
        String description = cursor.getString(2);
        String location = cursor.getString(5);
        Integer id = Integer.parseInt(cursor.getString(7));
        Date startDate = new Date(Long.parseLong(cursor.getString(3)));
        Date endDate = new Date(Long.parseLong(cursor.getString(4)));
        //Date lastUpdated = cursor.getString(8);

        return new CalEvent(title, new Date(), startDate, endDate, location, description/*, notes, town*/, id);
    }

    /**
     * Add a custom calendar event to the default calendar app
     * @param activity The activity from which the function is called
     * @param event The event to be added to the device calendar
     * @return The event id of the created event
     */
    public static Integer addCustomEvent(final Activity activity, Event event) {

        //Save the event data in a final variable
        final ContentValues eventValues = extractEventData(event);

        Uri uri = null;

        int eventId = -1;

        final ContentResolver cr = activity.getContentResolver();

        //Check whether the permission to write to the device calendar is granted
        //Request it if necessary and insert the event
        if(permissionCheck.permissionGrantedWriteCal(activity)) {
            if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
                return (-1);
            }
            uri = cr.insert(CalendarContract.Events.CONTENT_URI, eventValues);
        }

        //Save the id of the created event
        //Not used here but can be used to update or delete the event
        String projection[] = {"_id"};
        if(uri != null) {
            Cursor cursor = cr.query(uri, null, null, null,
                    null);
            if(cursor != null) {
                if (cursor.moveToFirst()) {
                    String calID;
                    int idCol = cursor.getColumnIndex(projection[0]);
                    do {
                        calID = cursor.getString(idCol);
                        eventId = Integer.parseInt(calID);
                    } while (cursor.moveToNext());
                    cursor.close();
                }
            }
        }
        return eventId;
    }

    /**
     * Add an organized calendar event to the default calendar app
     * @param activity The activity from which the function is called
     * @param event The calEvent to be added to the device calendar
     * */
    public static Integer addOrganizedEvent(final Activity activity, CalEvent event) {

        //Save the event data in a final variable
        final ContentValues eventValues = extractEventData(event);

        Uri uri = null;
        int eventId = -1;

        final ContentResolver cr = activity.getContentResolver();

        //Check whether the permission to write to the device calendar is granted
        //Request it if necessary and insert the calEvent
        if (permissionCheck.permissionGrantedWriteCal(activity)) {
            if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
                return (-1);
            }
            uri = cr.insert(CalendarContract.Events.CONTENT_URI, eventValues);
        }

        //Save the id of the created event
        //Not used here but can be used to update or delete the event
        String projection[] = {"_id"};
        if(uri != null) {
            Cursor cursor = cr.query(uri, null, null, null,
                    null);
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    String calID;
                    int idCol = cursor.getColumnIndex(projection[0]);
                    do {
                        calID = cursor.getString(idCol);
                        eventId = Integer.parseInt(calID);
                    } while (cursor.moveToNext());
                    cursor.close();
                }
            }
        }
        return eventId;
    }

    /**
     * Extract the event data from a given CalEvent object and create a contentValues object
     * @param event The event from which the data shall be extracted
     * @return The created contentValues object
     */
    private static ContentValues extractEventData(CalEvent event) {
        long calID = 1;
        long startMillis = event.getEventStartDate().getTime();
        long endMillis = event.getEventEndDate().getTime();

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

        String eventId = ""+event.getEventId();

        //Create new ContentValues containing the event data
        ContentValues values = new ContentValues();
        values.put(CalendarContract.Events.DTSTART, startMillis);
        values.put(CalendarContract.Events.DTEND, endMillis);
        values.put(CalendarContract.Events.TITLE, eventName);
        values.put(CalendarContract.Events.EVENT_LOCATION, eventLocation);
        values.put(CalendarContract.Events.DESCRIPTION, eventDescription);
        values.put(CalendarContract.Events.CALENDAR_ID, calID);
        values.put(CalendarContract.Events.EVENT_TIMEZONE, "Europe/Berlin");
        values.put("uid2445", eventId);

        return values;
    }

    /**
     * Extract the event data from a given event object and create a contentValues object
     * @param event The event from which the data shall be extracted
     * @return The created contentValues object
     */
    private static ContentValues extractEventData(Event event) {
        long calID = 1;
        long startMillis = event.getEventStartDate().getTime();
        long endMillis = event.getEventEndDate().getTime();

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

        //Create new ContentValues containing the event data
        ContentValues values = new ContentValues();
        values.put(CalendarContract.Events.DTSTART, startMillis);
        values.put(CalendarContract.Events.DTEND, endMillis);
        values.put(CalendarContract.Events.TITLE, eventName);
        values.put(CalendarContract.Events.EVENT_LOCATION, eventLocation);
        values.put(CalendarContract.Events.DESCRIPTION, eventDescription);
        values.put(CalendarContract.Events.CALENDAR_ID, calID);
        values.put(CalendarContract.Events.EVENT_TIMEZONE, "Europe/Berlin");

        return values;
    }

    /**
     * Check whether the new event collides with existing events. Save the names of
     * all colliding events in an arrayList.
     * @param startMillis The start time of the event to be checked for collisions in milliseconds
     * @param endMillis The end time of the event to be checked for collisions in milliseconds
     * @return An arrayList containing the names of all colliding events
     */
    public static ArrayList<String> checkEventCollision(long startMillis, long endMillis) {
        ArrayList<String> collidingEvents = new ArrayList<>();

        for(int i = 0, l = eventList.size(); i < l; i++){
            if((eventList.get(i).getEventStartDate().getTime() >= startMillis) && ((eventList.get(i).getEventStartDate().getTime()) <= endMillis) ||
                    ((eventList.get(i).getEventEndDate().getTime() >= startMillis) && (eventList.get(i).getEventEndDate().getTime() <= endMillis))){
                collidingEvents.add(eventList.get(i).getEventName());
            }
        }
        return collidingEvents;
    }

    /**
     * Get the eventID for an event with a given event title
     * @param newContext The context from which the function is called
     * @param eventtitle The title of the event of which the event id is needed
     * @return The id of the event with the given title
     */
    public static int getEventIdByTitle(Context newContext, String eventtitle) {
        Uri eventUri;

        //Create the base for the eventUri according to the used device sdk
        if (android.os.Build.VERSION.SDK_INT <= 7) {
            eventUri = Uri.parse("content://calendar/events");
        } else {
            eventUri = Uri.parse("content://com.android.calendar/events");
        }

        //Create a query searching for all event ids and titles
        int result = 0;
        String projection[] = {"_id", "title"};
        Cursor cursor = newContext.getContentResolver().query(eventUri, null, null, null,
                null);

        //Check whether the cursor object is not null
        //Check all results whether they contain the given eventTitle
        //Return the according eventId if a match occurs
        if(cursor != null){
            if(cursor.moveToFirst()){
                String calName;
                String calID;
                int nameCol = cursor.getColumnIndex(projection[1]);
                int idCol = cursor.getColumnIndex(projection[0]);
                do{
                    calName = cursor.getString(nameCol);
                    calID = cursor.getString(idCol);
                    if(calName != null && calName.contains(eventtitle)) {
                        result = Integer.parseInt(calID);
                    }
                }while(cursor.moveToNext());
                cursor.close();
            }
        }
        return result;
    }

    /**
     * Get the start date for an event with a given event id
     * @param newContext The context from which the function is called
     * @param eventId The id of the event of which the start date is needed
     * @return The starting date of the event with the given event id
     */
    public static Long getEventStartById(Context newContext, Integer eventId) {
        Uri eventUri;

        //Create the base for the eventUri according to the used device sdk
        if(android.os.Build.VERSION.SDK_INT <= 7) {
            eventUri = Uri.parse("content://calendar/events");
        }else{
            eventUri = Uri.parse("content://com.android.calendar/events");
        }

        //Create a query searching for all event ids and start dates
        Long result = 0L;
        String projection[] = {"_id", "dtstart"};
        Cursor cursor = newContext.getContentResolver().query(eventUri, null, null, null,
                null);

        //Check whether the cursor object is not null
        //Check all results whether they contain the given event id
        //Return the according eventId if a match occurs
        if(cursor != null) {
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
        }
        return result;
    }

    /**
     * Delete a calendar event with a given event id from the default device calendar app
     * and show a toast afterwards.
     * @param newContext The context from which the function is called
     * @param eventId The id of the event to be deleted
     */
    public static void deleteEventById(Context newContext, Integer eventId) {
        ContentResolver cr = newContext.getContentResolver();
        Uri deleteUri;

        //Delete the event with the given event id
        deleteUri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, eventId);
        cr.delete(deleteUri, null, null);

        //Show a toast when the event is deleted
        Toast toast = Toast.makeText(newContext, "Event " + eventId + "deleted.", Toast.LENGTH_SHORT);
        toast.show();
    }

    /**
     * Update the eventTitle of an event of the default device calendar app
     * with a given event id.
     * @param newContext The context from which the function is called
     * @param eventTitle The new event title
     * @param eventId The Id of the event to be updated
     */
    public static void updateEventTitle(Context newContext, String eventTitle, Integer eventId) {
        ContentResolver cr = newContext.getContentResolver();
        ContentValues values = new ContentValues();
        Uri updateUri;

        //ContentValues containing the new event title
        values.put(CalendarContract.Events.TITLE, eventTitle);

        //Update the event with the specified id
        updateUri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, eventId);
        cr.update(updateUri, values, null, null);

        //Show a toast when the title is updated
        Toast toast = Toast.makeText(newContext, "Eventitle of: " + eventId + " updated to: " + eventTitle, Toast.LENGTH_SHORT);
        toast.show();
    }

    /**
     * Set the type of events that should be returned in the readCalendarEvent function
     *  0:
     *  1: event/Termin
     *  2: birthday
     *  3: Feiertag
     * @param i type of the event to be returned
     */
    private void setReturnEventId(Integer i){
        returnEventId = i;
    }

}
