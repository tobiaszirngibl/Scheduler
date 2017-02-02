package com.bocha.organized.network;

import android.util.Log;

import com.bocha.organized.data.Event;
import com.bocha.organized.utility.JSONUtility;
import com.bocha.organized.utility.MiscUtility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by bob on 02.02.17.
 */

public class NetworkHelper {
    private final static String TAG = "NetWorkHelper";

    /**
     * Try to fetch the new events data from the server and save the result
     * @param newEventsUrl
     * @return
     */
    public static ArrayList<Event> fetchEventData(final String newEventsUrl) {
        ArrayList<Event> newEventList = new ArrayList<>();

                new Thread() {
            @Override
            public void run() {
                //your code here

                String serverResponse = "Not fetched";
                HttpURLConnection urlConnection = null;
                JSONArray jsonArray= null;

                try{
                    //Create the URL
                    URL eventURL = new URL(newEventsUrl);

                    //Establish the HTTP connection
                    urlConnection = (HttpURLConnection) eventURL.openConnection();

                    //Check the http response code
                    int responseCode = urlConnection.getResponseCode();
                    if(responseCode == HttpURLConnection.HTTP_OK){
                        serverResponse = readStream(urlConnection.getInputStream());
                    }

                    //Create a JSON Object from the server response
                    try{
                        jsonArray = new JSONArray(serverResponse);
                    }catch(JSONException e){
                        e.printStackTrace();
                    }

                    if(jsonArray != null){
                        ArrayList newEventList = JSONUtility.decodeEventData(jsonArray);
                    }
                }catch(MalformedURLException e){
                    e.printStackTrace();
                }catch(IOException e){
                    e.printStackTrace();
                }finally{
                    if(urlConnection != null){
                        urlConnection.disconnect();
                    }
                }
            }
        }.start();
        return newEventList;
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
}
