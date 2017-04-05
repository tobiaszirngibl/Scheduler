package com.ase_1617.organizedlib.network;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.ase_1617.organizedlib.data.CalEvent;
import com.ase_1617.organizedlib.utility.Constants;
import com.ase_1617.organizedlib.utility.JSONUtility;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

/**
 * Class extending the AsyncTask class to send the user's answer
 * according an event to the server.
 */

public class AcceptEventAsyncTask extends AsyncTask<Object, Void, Boolean> {
    private final static String TAG = "AcceptEventAsyncTask";

    private Integer eventPosition = -1;
    private String answer;

    private Context context;

    private ProgressDialog progressDialog;

    public AcceptEventAsyncInterface acceptEventAsyncInterface = null;

    public AcceptEventAsyncTask(Context context){
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        progressDialog = new ProgressDialog(context);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Submitting data...");
        progressDialog.show();
    }

    /**
     * Send the user's answer to the server using HTTP POST.
     * @param urls
     * @return
     */
    @Override
    protected Boolean doInBackground(Object... urls) {
        final String accessToken = (String)urls[0];
        final Integer eventId = (Integer)urls[1];

        eventPosition = (Integer)urls[2];
        answer = (String)urls[3];

        String url = Constants.FEEDBACK_URL_START + eventId + Constants.FEEDBACK_URL_END;
        String data = null;
        String serverResponse;

        int responseCode = 0;

        URL server = null;

        HttpURLConnection connection = null;

        OutputStreamWriter osw;

        //Encode the user's answer
        try{
            data = URLEncoder.encode( "answer", "UTF-8" ) + "=" + URLEncoder.encode(answer, "UTF-8" );
        }catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        //Create an URL object from the created url String
        try{
            server = new URL(url);
        }catch (MalformedURLException e) {
            e.printStackTrace();
        }

        //Open a connection to the created URL using the granted access token
        //Write the encoded answer into the output stream
        //Save the connection's response code
        try{
            connection = (HttpURLConnection) server.openConnection();
            connection.setRequestProperty("Authorization", "Bearer " + accessToken);
            connection.setDoOutput( true );

            osw = new OutputStreamWriter(connection.getOutputStream());
            osw.write(data);
            osw.flush();

            responseCode = connection.getResponseCode();
        }catch (IOException e) {
            e.printStackTrace();
        }

        //Interpret the server response and react to it
        if(responseCode == HttpURLConnection.HTTP_OK){
            serverResponse = InputStreamInterpreter.interpretInputStream(connection);
            Log.v(TAG, "serverResponse: "+serverResponse);
        }
        else{
            serverResponse = InputStreamInterpreter.interpretInputStream(connection);
            Log.v(TAG, "serverResponse: "+serverResponse);
        }

        progressDialog.dismiss();

        return true;
    }

    /**
     * Call the given fetchEventsAsyncInterface method when the async task has finished
     * @param result
     */
    protected void onPostExecute(Boolean result) {
        if(answer.equals("yes")){
            acceptEventAsyncInterface.eventAccepted(eventPosition);
        }else{
            acceptEventAsyncInterface.eventDeclined(eventPosition);
        }
    }
}
