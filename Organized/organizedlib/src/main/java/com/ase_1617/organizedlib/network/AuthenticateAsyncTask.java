package com.ase_1617.organizedlib.network;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Class extending the AsyncTask class to fetch event data from the organized web server
 */

public class AuthenticateAsyncTask extends AsyncTask<Object, Void, Boolean> {
    private final static String TAG = "AuthenticateAsyncTask";
    private final static String GRANT_TYPE = "password";
    private final static String ACCESS_TOKEN_TAG = "access_token";
    private static final String AUTH_ERROR = "Authentication failed. Please try again.";

    String accessToken = "";

    int authenticationError = 0;

    public AuthenticateAsyncInterface authenticateAsyncInterface = null;

    Context context;

    ProgressDialog progressDialog;

    public AuthenticateAsyncTask(Context context){
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        Log.v(TAG, "PREEXECUTE");

        progressDialog = new ProgressDialog(context);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating...");
        progressDialog.show();
    }

    /**
     * Send the new user data to the server using HTTP POST.
     * @param urls
     * @return
     */
    @Override
    protected Boolean doInBackground(Object... urls) {
        String url = (String)urls[0];
        final String email = (String)urls[1];
        final String password = (String)urls[2];
        final String clientId = (String)urls[3];
        final String clientSecret = (String)urls[4];

        int responseCode = 0;
        authenticationError = 0;

        String data = null;
        try {
            data = URLEncoder.encode( "grant_type", "UTF-8" ) + "=" + URLEncoder.encode( GRANT_TYPE, "UTF-8" );
            data += "&" + URLEncoder.encode( "username", "UTF-8" ) + "=" + URLEncoder.encode( email, "UTF-8" );
            data += "&" + URLEncoder.encode( "password", "UTF-8" ) + "=" + URLEncoder.encode( password, "UTF-8" );
            data += "&" + URLEncoder.encode( "client_id", "UTF-8" ) + "=" + URLEncoder.encode( clientId, "UTF-8" );
            data += "&" + URLEncoder.encode( "client_secret", "UTF-8" ) + "=" + URLEncoder.encode( clientSecret, "UTF-8" );
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
            connection.setDoOutput( true );
            OutputStreamWriter osw = null;
            osw = new OutputStreamWriter( connection.getOutputStream() );
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

            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject(response.toString());
                accessToken = jsonObject.getString(ACCESS_TOKEN_TAG);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            Log.v( TAG, "response: "+response);
            Log.v( "Login", accessToken);
        }
        else
        {
            authenticationError = 1;
            Log.v( "CatalogClient", "Response code:" + responseCode );
        }

        progressDialog.dismiss();

        return true;
    }

    @Override
    protected void onPostExecute(Boolean result) {
        if(authenticationError == 1){
            authenticateAsyncInterface.onAuthenticationError(AUTH_ERROR);
        }else{
            authenticateAsyncInterface.onAuthenticationSuccess(accessToken);
        }
    }
}
