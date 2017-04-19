package com.ase_1617.organizedlib.network;

import android.os.AsyncTask;

import com.ase_1617.organizedlib.data.CalEvent;
import com.ase_1617.organizedlib.utility.JSONUtility;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Class extending the AsyncTask class to fetch event data related to the used account
 * from the organized web server.
 */

public class FetchEventsAsyncTask extends AsyncTask<String, Void, Boolean> {

    private ArrayList<CalEvent> eventList = new ArrayList<>();

    private int fetchingEventsError = 0;

    public FetchEventsAsyncInterface fetchEventsAsyncInterface = null;

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

    }

    /**
     * Fetch event data from the organized server, decode the json result
     * and save the results in a list of calEvents.
     * @param urls Parameters containing
     *             the server url,
     *             the access token
     * @return Boolean value
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
        if(server != null){
            try{
                connection = (HttpURLConnection) server.openConnection();
                connection.setRequestProperty("Authorization", "Bearer " + accessToken);

                responseCode = connection.getResponseCode();
            }catch(IOException e){
                e.printStackTrace();
            }
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

            //Create calEvents from the json array and save them in a list
            if (jsonArray != null) {
                eventList = JSONUtility.decodeEventData(jsonArray);
            }
        }else{
            fetchingEventsError = 1;
        }

        if (connection != null) {
            connection.disconnect();
        }
        return true;
    }

    /**
     * Call the success/error interfcae method when the async task has finished
     * @param result Boolean value
     */
    protected void onPostExecute(Boolean result) {
        if(fetchingEventsError == 0){
            fetchEventsAsyncInterface.newEventsFetchingSuccess(eventList);
        }else{
            fetchEventsAsyncInterface.newEventsFetchingError();
        }
    }
}
