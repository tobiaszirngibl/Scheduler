package com.ase_1617.organizedlib.network;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
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
import java.util.HashMap;
import java.util.Map;

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
/*
        int responseCode = 0;

        String data = null;
        try {
            //data = URLEncoder.encode( "grant_type", "UTF-8" ) + "=" + URLEncoder.encode(GRANT_TYPE, "UTF-8" );
            data = URLEncoder.encode( "answer", "UTF-8" ) + "=" + URLEncoder.encode(answer, "UTF-8" );
            //data = "{answer: yes}";
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
            connection.setDoOutput( true );
            OutputStreamWriter osw = null;
            osw = new OutputStreamWriter( connection.getOutputStream() );
            osw.write( data );
            osw.flush();
            responseCode = connection.getResponseCode();
            String response = connection.getResponseMessage();
            Log.v(TAG, "Resüonse: "+response);
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
            Log.v(TAG, "response 1: "+response);
        }
        else
        {
            Log.v( "CatalogClient", "Response code innerhalb:" + responseCode );
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
            Log.v( "CatalogClient", "Response 2:" + response );
        }
        Log.v( "CatalogClient", "Response code außerhalb:" + responseCode);

        progressDialog.dismiss();
*/


        //Create a new volley stringrequest, response and error listener
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.v(TAG, "Response: " + response);

                        progressDialog.dismiss();
                        //signupAsyncInterface.onSignUpSuccess(email, password);
                    }
                },
                new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.v(TAG, "Error: " + error);

                        progressDialog.dismiss();
                        //signupAsyncInterface.onSignUpError(error.toString());
                    }
                })
        {
            //Supply the necessary parameters
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put("answer",answer);
                return params;
            }

            @Override
            public Map<String, String> getHeaders() {
                Map<String,String> params = new HashMap<String, String>();
                params.put("Authorization","Bearer " + accessToken);
                return params;
            }
        };

        //Add the stringrequest to the volley requestqueue
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);

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
