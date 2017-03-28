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
 * Class extending the AsyncTask class to fetch event data from the organized web server
 */

public class AcceptEventAsyncTask extends AsyncTask<Object, Void, Boolean> {
    private final static String TAG = "AcceptEventAsyncTask";
    private final static String GRANT_TYPE = "password";

    private Integer eventPosition = -1;
    private String answer;

    public AcceptEventAsyncInterface acceptEventAsyncInterface = null;

    Context context;

    ProgressDialog progressDialog;

    public AcceptEventAsyncTask(Context context){
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        Log.v(TAG, "PREEXECUTE");

        progressDialog = new ProgressDialog(context);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Submitting data...");
        progressDialog.show();
    }

    /**
     * Send the event data to the server using HTTP POST.
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

        Log.v(TAG, "url: " + url);

        int responseCode = 0;

        String data = null;
        try {
            //data = URLEncoder.encode( "grant_type", "UTF-8" ) + "=" + URLEncoder.encode(GRANT_TYPE, "UTF-8" );
            data = URLEncoder.encode( "answer", "UTF-8" ) + "=" + URLEncoder.encode(answer, "UTF-8" );
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        Log.v(TAG, "data: "+data);

        URL server = null;
        try {
            server = new URL( url );
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) server.openConnection();
            connection.setRequestProperty("Authorization", "Bearer " + accessToken);
        } catch (IOException e) {
            e.printStackTrace();
        }
        connection.setDoOutput( true );
        OutputStreamWriter osw = null;
        try {
            osw = new OutputStreamWriter( connection.getOutputStream() );
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            osw.write( data );
            osw.flush();
            responseCode = connection.getResponseCode();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if( responseCode == HttpURLConnection.HTTP_OK)
        {

            BufferedReader reader = null;
            try {
                reader = new BufferedReader( new InputStreamReader( connection.getInputStream() ) );
            } catch (IOException e) {
                e.printStackTrace();
            }
            StringBuffer response = new StringBuffer();
            String line = "";
            try {
                while( ( line = reader.readLine() ) != null )
                {
                    response.append( line );
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
            Log.v(TAG, "response: "+response);
        }
        else
        {
            Log.v( "CatalogClient", "Response code:" + responseCode );
            BufferedReader reader = null;
            try {
                reader = new BufferedReader( new InputStreamReader( connection.getInputStream() ) );
            } catch (IOException e) {
                e.printStackTrace();
            }
            StringBuffer response = new StringBuffer();
            String line = "";
            try {
                while( ( line = reader.readLine() ) != null )
                {
                    response.append( line );
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
            Log.v( "CatalogClient", "Response code:" + response );
        }
        Log.v( "CatalogClient", "Response code:" + responseCode );

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
