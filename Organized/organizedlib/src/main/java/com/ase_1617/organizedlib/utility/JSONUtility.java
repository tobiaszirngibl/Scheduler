package com.ase_1617.organizedlib.utility;

import com.ase_1617.organizedlib.data.CalEvent;
import com.ase_1617.organizedlib.data.Event;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;

/**
 * Utility class to decode given json data and convert it to
 * organized calEvent/event objects.
 */

public class JSONUtility {
    //Json tags used in the json objects fetched from the organized server
    private final static String JSON_TAG_NAME = "name";
    private final static String JSON_TAG_CHANGE = "last_changed";
    private final static String JSON_TAG_START_DATE = "dtstart";
    private final static String JSON_TAG_END_DATE = "dtend";
    private final static String JSON_TAG_LOCATION = "location";
    private final static String JSON_TAG_DESCRIPTION = "summary";
    private final static String JSON_TAG_NOTES = "notes";
    private final static String JSON_TAG_TOWN = "town";
    private static final String JSON_TAG_ID = "id";
    private static final String JSON_TAG_WILL_TAKE_PLACE = "will_take_place";
    private static final String JSON_TAG_ANSWER = "own_answer";


    /**
     * Create calEvent objects from JSONArray data and return them in an arrayList
     * @param jsonArray The json data to be converted to calEvents
     * @return The created calEvents in an arrayList
     */
    public static ArrayList<CalEvent> decodeEventData(JSONArray jsonArray) {
        ArrayList<CalEvent> eventList = new ArrayList<>();
        JSONObject jsonObject = null;

        //Try to extract the jsonObjects from the jsonArray and
        //convert them to calEvents
        //Finally save them in an arrayList
        for(int i = 0, j = jsonArray.length(); i < j; i++){
            try{
                jsonObject = (JSONObject)jsonArray.get(i);
            }catch(JSONException e){
                e.printStackTrace();
            }

            //Check whether the event has not been answered by the user yet("own_answer = p")
            //Add it to the arraylist if it has not
            try {
                if (jsonObject != null && jsonObject.getString(JSON_TAG_ANSWER).equals("p")) {
                    eventList.add(convertJSONToCalEvent(jsonObject));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return eventList;
    }

    /**
     * Convert a JSONObject to an calEvent object
     * @param jsonObject The jsonObject which is converted to a calEvent
     * @return The converted calEvent
     */
    private static CalEvent convertJSONToCalEvent(JSONObject jsonObject) {
        Date startDate = null, endDate = null, lastChanged = null;
        String title = null, description = null, notes = "", town = "", location = null;
        Integer id = null;

        try{
            id = jsonObject.getInt(JSON_TAG_ID);

            //CHeck the start/end time format and parse it accordingly
            if(jsonObject.getString((JSON_TAG_START_DATE)).length() > 20){
                startDate = MiscUtility.stringToDate(jsonObject.getString(JSON_TAG_START_DATE), "yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'");
            }else{
                startDate = MiscUtility.stringToDate(jsonObject.getString(JSON_TAG_START_DATE), "yyyy-MM-dd'T'HH:mm:ss'Z'");
            }
            if(jsonObject.getString((JSON_TAG_END_DATE)).
                    length() > 20){
                endDate = MiscUtility.stringToDate(jsonObject.getString(JSON_TAG_END_DATE), "yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'");
            }else{
                endDate = MiscUtility.stringToDate(jsonObject.getString(JSON_TAG_END_DATE), "yyyy-MM-dd'T'HH:mm:ss'Z'");
            }

            lastChanged = new Date();
            title = jsonObject.getString(JSON_TAG_NAME);
            location = jsonObject.getString(JSON_TAG_LOCATION);
            description = jsonObject.getString(JSON_TAG_DESCRIPTION);

        }catch(JSONException e){
            e.printStackTrace();
        }

        return new CalEvent(title, lastChanged, startDate, endDate, location, description, id/*, notes, town*/);
    }

    /**
     * Convert a JSONObject to an event object
     * @param jsonObject The jsonObject which is converted to a event
     * @return The converted event
     */
    private static Event convertJSONToEvent(JSONObject jsonObject) {
        Date startDate = null, endDate = null, lastChanged = null;
        String title = null, description = null, notes = null, town = null, location = null;

        try{

            //CHeck the start/end time format and parse it accordingly
            if(jsonObject.getString((JSON_TAG_START_DATE)).length() > 20){
                startDate = MiscUtility.stringToDate(jsonObject.getString(JSON_TAG_START_DATE), "yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'");
            }else{
                startDate = MiscUtility.stringToDate(jsonObject.getString(JSON_TAG_START_DATE), "yyyy-MM-dd'T'HH:mm:ss'Z'");
            }
            if(jsonObject.getString((JSON_TAG_END_DATE)).
                    length() > 20){
                endDate = MiscUtility.stringToDate(jsonObject.getString(JSON_TAG_END_DATE), "yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'");
            }else{
                endDate = MiscUtility.stringToDate(jsonObject.getString(JSON_TAG_END_DATE), "yyyy-MM-dd'T'HH:mm:ss'Z'");
            }
            //lastChanged = MiscUtility.stringToDate(jsonObject.getString(JSON_TAG_CHANGE), "yyyy-MM-dd'T'HH:mm:ss'Z'");
            lastChanged = new Date();
            title = jsonObject.getString(JSON_TAG_NAME);
            notes = jsonObject.getString(JSON_TAG_NOTES);
            town = jsonObject.getString(JSON_TAG_TOWN);
            location = jsonObject.getString(JSON_TAG_LOCATION);
            description = jsonObject.getString(JSON_TAG_DESCRIPTION);

        }catch(JSONException e){
            e.printStackTrace();
        }

        return new Event(title, lastChanged, startDate, endDate, location, description/*, notes, town*/);
    }
}
