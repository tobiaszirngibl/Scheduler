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
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

/**
 * Class extending the AsyncTask class to fetch event data from the organized web server
 */

public class AcceptEventAsyncTask extends AsyncTask<Object, Void, Boolean> {
    private final static String TAG = "AcceptEventAsyncTask";
    private Integer eventPosition = -1;

    public AcceptEventAsyncInterface acceptEventAsyncInterface = null;

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        Log.v(TAG, "PREEXECUTE");
    }

    /**
     * Send the event data to the server using HTTP POST.
     * @param urls
     * @return
     */
    @Override
    protected Boolean doInBackground(Object... urls) {
        String serverResponse = "Not fetched";
        String url = (String)urls[0];
        Integer eventId = (Integer)urls[1];
        eventPosition = (Integer)(urls[2]);
        HttpURLConnection urlConnection = null;

        Log.v(TAG, "DOIN");

        try {
            //Create the URL
            URL eventURL = new URL(url);

            //Establish the HTTP connection
            urlConnection = (HttpURLConnection) eventURL.openConnection();

            //Set the request mode to post
            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Key","Value");
            urlConnection.setDoOutput(true);

            //Gather the POST data
            String data = URLEncoder.encode("eventId", "UTF-8")
                    + "=" + URLEncoder.encode(Integer.toString(eventId), "UTF-8");

            data += "&" + URLEncoder.encode("userName", "UTF-8") + "="
                    + URLEncoder.encode("BeispielNutzer", "UTF-8");

            //Send the POST data request
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(urlConnection.getOutputStream());
            outputStreamWriter.write( data );
            outputStreamWriter.flush();

            //Check the http response code
            int responseCode = urlConnection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                serverResponse = readStream(urlConnection.getInputStream());
                Log.v(TAG, "Server response: "+serverResponse);
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
     * Call the given fetchEventsAsyncInterface method when the async task has finished
     * @param result
     */
    protected void onPostExecute(Boolean result) {
        acceptEventAsyncInterface.eventAccepted(eventPosition);
    }
}
