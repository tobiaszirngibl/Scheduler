package com.bocha.organized.utility;

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
    private final static String jsonTagName = "name";
    private final static String jsonTagStartDate = "date";
    private final static String jsonTagEndDate = "date";
    private final static String jsonTagChange = "last_change";


    /**
     * Create events from the JSONArray data and return them in an arraylist
     * @param jsonArray
     * @return
     */
    public static ArrayList<Event> decodeEventData(JSONArray jsonArray) {
        ArrayList<Event> eventList = new ArrayList<>();
        JSONObject jsonObject = null;
        for(int i = 0, j = jsonArray.length(); i < j; i++){
            try{
                jsonObject = (JSONObject)jsonArray.get(i);
            }catch(JSONException e){
                e.printStackTrace();
            }
            eventList.add(convertJSONToEvent(jsonObject));
        }

        return eventList;
    }

    /**
     * Convert a JSONObject to an event and return it
     * @param jsonObject
     * @return
     */
    private static Event convertJSONToEvent(JSONObject jsonObject) {
        Date startDate = null, endDate = null;
        String title = null, description = "Description";

        try{
            startDate = MiscUtility.stringToDate(jsonObject.getString(jsonTagStartDate));
            endDate = MiscUtility.stringToDate(jsonObject.getString(jsonTagEndDate));
            title = jsonObject.getString(jsonTagName);
        }catch(JSONException e){
            e.printStackTrace();
        }

        Event event = new Event(startDate, endDate, title, description);

        return event;
    }
}
