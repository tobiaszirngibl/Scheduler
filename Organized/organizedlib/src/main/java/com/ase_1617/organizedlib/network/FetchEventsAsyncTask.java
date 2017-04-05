package com.ase_1617.organizedlib.network;

import android.os.AsyncTask;
import android.util.Log;

import com.ase_1617.organizedlib.data.CalEvent;
import com.ase_1617.organizedlib.utility.JSONUtility;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Class extending the AsyncTask class to fetch event data
 * from the organized web server.
 */

public class FetchEventsAsyncTask extends AsyncTask<String, Void, Boolean> {
    private final static String TAG = "FetchEventsAsyncTask";

    private ArrayList<CalEvent> eventList = new ArrayList<>();

    public FetchEventsAsyncInterface fetchEventsAsyncInterface = null;

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

    }

    /**
     * Fetch event data from the organized server, decode the json result
     * and save the results in a list of calEvents.
     * @param urls
     * @return
     */
    @Override
    protected Boolean doInBackground(String... urls) {
        String serverResponse;
        String url = urls[0];
        String accessToken = urls[1];

        int responseCode = 0;

        HttpURLConnection connection = null;

        JSONArray jsonArray = null;

        URL server = null;

        //Create an URL object from the created url String
        try {
            server = new URL(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        //Open a connection to the created URL using the granted access token
        //Write the encoded answer into the output stream
        //Save the connection's response code
        try{
            connection = (HttpURLConnection) server.openConnection();
            connection.setRequestProperty("Authorization", "Bearer " + accessToken);

            responseCode = connection.getResponseCode();
        }catch (IOException e) {
            e.printStackTrace();
        }

        //Interpret the server response
        if(responseCode == HttpURLConnection.HTTP_OK) {
            serverResponse = InputStreamInterpreter.interpretInputStream(connection);

            //Create a JSON Object from the server response
            try {
                jsonArray = new JSONArray(serverResponse);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            //Create calEvents from the json array and save it in a list
            if (jsonArray != null) {
                eventList = JSONUtility.decodeEventData(jsonArray);
            }
        }else{
            Log.v(TAG, "Error while fetching event data.");
        }

        if (connection != null) {
            connection.disconnect();
        }
        return true;
    }

    /**
     * Call the given fetchEventsAsyncInterface method when the async task has finished
     * @param result
     */
    protected void onPostExecute(Boolean result) {
        fetchEventsAsyncInterface.newEventsFetched(eventList);
    }
}
