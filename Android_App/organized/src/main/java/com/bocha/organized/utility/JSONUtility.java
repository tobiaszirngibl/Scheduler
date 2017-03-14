package com.bocha.organized.utility;

import android.util.Log;

import com.bocha.organized.data.CalEvent;
import com.bocha.organized.data.Event;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by bob on 02.02.17.
 */

public class JSONUtility {
    private final static String TAG = "JSONUtility";
    private final static String JSON_TAG_NAME = "name";
    private final static String JSON_TAG_CHANGE = "last_change";
    private final static String JSON_TAG_START_DATE = "date_begin";
    private final static String JSON_TAG_END_DATE = "date_end";
    private final static String JSON_TAG_LOCATION = "location";
    private final static String JSON_TAG_DESCRIPTION = "description";
    private final static String JSON_TAG_NOTES = "notes";
    private final static String JSON_TAG_TOWN = "town";
    private static final String JSON_TAG_ID = "id";


    /**
     * Create events from the JSONArray data and return them in an arraylist
     * @param jsonArray
     * @return
     */
    public static ArrayList<CalEvent> decodeEventData(JSONArray jsonArray) {
        ArrayList<CalEvent> eventList = new ArrayList<>();
        JSONObject jsonObject = null;
        for(int i = 0, j = jsonArray.length(); i < j; i++){
            try{
                jsonObject = (JSONObject)jsonArray.get(i);
            }catch(JSONException e){
                e.printStackTrace();
            }
            eventList.add(convertJSONToCalEvent(jsonObject));
        }

        return eventList;
    }

    /**
     * Convert a JSONObject to an event and return it
     * @param jsonObject
     * @return
     */
    private static CalEvent convertJSONToCalEvent(JSONObject jsonObject) {
        Date startDate = null, endDate = null, lastChanged = null;
        String title = null, description = null, notes = null, town = null, location = null;
        Integer id = null;

        try{
            id = jsonObject.getInt(JSON_TAG_ID);
            startDate = MiscUtility.stringToDate(jsonObject.getString(JSON_TAG_START_DATE), "yyyy-MM-dd'T'HH:mm:ss'Z'");
            endDate = MiscUtility.stringToDate(jsonObject.getString(JSON_TAG_END_DATE), "yyyy-MM-dd'T'HH:mm:ss'Z'");
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

        CalEvent event = new CalEvent(title, lastChanged, startDate, endDate, location, description, id/*, notes, town*/);

        return event;
    }

    /**
     * Convert a JSONObject to an event and return it
     * @param jsonObject
     * @return
     */
    private static Event convertJSONToEvent(JSONObject jsonObject) {
        Date startDate = null, endDate = null, lastChanged = null;
        String title = null, description = null, notes = null, town = null, location = null;

        try{
            startDate = MiscUtility.stringToDate(jsonObject.getString(JSON_TAG_START_DATE), "yyyy-MM-dd'T'HH:mm:ss'Z'");
            endDate = MiscUtility.stringToDate(jsonObject.getString(JSON_TAG_END_DATE), "yyyy-MM-dd'T'HH:mm:ss'Z'");
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

        Event event = new Event(title, lastChanged, startDate, endDate, location, description/*, notes, town*/);

        return event;
    }
}
