package com.ase_1617.organizedlib.network;

import android.os.AsyncTask;

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
 * Class extending the AsyncTask class to fetch event data from the organized web server
 */

public class JSONAsyncTask extends AsyncTask<String, Void, Boolean> {
    private final static String TAG = "JSONAsyncTask";

    private ArrayList<CalEvent> eventList = new ArrayList<>();
    public JSONAsyncInterface jsonAsyncInterface = null;

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

    }

    /**
     * Fetch event data from a given url and decode the json result
     * @param urls
     * @return
     */
    @Override
    protected Boolean doInBackground(String... urls) {
        String serverResponse = "Not fetched";
        HttpURLConnection urlConnection = null;
        JSONArray jsonArray = null;

        try {
            //Create the URL
            URL eventURL = new URL(urls[0]);

            //Establish the HTTP connection
            urlConnection = (HttpURLConnection) eventURL.openConnection();

            //Check the http response code
            int responseCode = urlConnection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                serverResponse = readStream(urlConnection.getInputStream());
            }

            //Create a JSON Object from the server response
            try {
                jsonArray = new JSONArray(serverResponse);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            if (jsonArray != null) {
                eventList = JSONUtility.decodeEventData(jsonArray);
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            return true;
        }
    }

    /**
     * Read the input stream and return the String result
     * @param inputStream
     * @return
     */
    private static String readStream(InputStream inputStream) {
        BufferedReader bufferedReader = null;
        StringBuffer response = new StringBuffer();

        try{
            bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String line = "";
            while((line = bufferedReader.readLine()) != null){
                response.append(line);
            }
        }catch(IOException e){
            e.printStackTrace();
        }finally{
            if(bufferedReader != null){
                try{
                    bufferedReader.close();
                }catch(IOException e){
                    e.printStackTrace();
                }
            }
        }
        return response.toString();
    }

    /**
     * Call the given jsonAsyncInterface method when the async task has finished
     * @param result
     */
    protected void onPostExecute(Boolean result) {
        jsonAsyncInterface.newEventsFetched(eventList);
    }
}
