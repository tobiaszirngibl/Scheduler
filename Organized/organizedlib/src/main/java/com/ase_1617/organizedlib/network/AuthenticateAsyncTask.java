package com.ase_1617.organizedlib.network;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.ase_1617.organizedlib.utility.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Class extending the AsyncTask class to authenticate user login data
 * and get an access token from the oauth server.
 */

public class AuthenticateAsyncTask extends AsyncTask<Object, Void, Boolean> {

    private String accessToken = "";
    private int authenticationError = 0;

    private Context context;

    private ProgressDialog progressDialog;

    public AuthenticateAsyncInterface authenticateAsyncInterface = null;

    public AuthenticateAsyncTask(Context context){
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        progressDialog = new ProgressDialog(context);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating...");
        progressDialog.show();
    }

    /**
     * Send the user login data to the server using HTTP POST.
     * @param urls Parameters containing
     *             the server url,
     *             the user email,
     *             the user password,
     *             the client id,
     *             the client secret
     * @return Boolean value
     */
    @Override
    protected Boolean doInBackground(Object... urls) {
        final String email = (String)urls[1];
        final String password = (String)urls[2];
        final String clientId = (String)urls[3];
        final String clientSecret = (String)urls[4];

        int responseCode = 0;

        String url = (String)urls[0];
        String data = "";
        String serverResponse;

        URL server = null;

        HttpURLConnection connection = null;

        OutputStreamWriter osw;

        authenticationError = 0;

        //Encode the user's data
        try{
            data = URLEncoder.encode("grant_type" , "UTF-8") + "=" + URLEncoder.encode("password" , "UTF-8");
            data += "&" + URLEncoder.encode("username" , "UTF-8") + "=" + URLEncoder.encode(email , "UTF-8");
            data += "&" + URLEncoder.encode("password" , "UTF-8") + "=" + URLEncoder.encode(password , "UTF-8");
            data += "&" + URLEncoder.encode("client_id" , "UTF-8") + "=" + URLEncoder.encode(clientId , "UTF-8");
            data += "&" + URLEncoder.encode("client_secret" , "UTF-8") + "=" + URLEncoder.encode(clientSecret , "UTF-8");
        }catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        //Create an URL object from the created url String
        try{
            server = new URL( url );
        }catch(MalformedURLException e) {
            e.printStackTrace();
        }

        //Open a connection to the created URL
        //Write the encoded answer into the output stream
        //Save the connection's response code
        if(server != null){
            try{

                connection = (HttpURLConnection) server.openConnection();
                connection.setDoOutput(true);

                osw = new OutputStreamWriter(connection.getOutputStream());
                osw.write(data);
                osw.flush();

                responseCode = connection.getResponseCode();
            }catch(IOException e){
                e.printStackTrace();
            }
        }

        //Interpret the server response and react to it
        if(responseCode == HttpURLConnection.HTTP_OK){
            serverResponse = InputStreamInterpreter.interpretInputStream(connection);

            //Create a json object from the server response
            //Extract and save the access token
            JSONObject jsonObject;
            try{
                jsonObject = new JSONObject(serverResponse);
                accessToken = jsonObject.getString(Constants.PREFS_ACCESS_TOKEN_KEY);
            }catch(JSONException e){
                e.printStackTrace();
            }
        }
        else{
            authenticationError = 1;
        }

        progressDialog.dismiss();

        return true;
    }

    /**
     * On post execution of the async task use the according success or failure
     * interface method to either show an error message or process the granted access token.
     * @param result Boolean value
     */
    @Override
    protected void onPostExecute(Boolean result) {
        if(authenticationError == 1){
            authenticateAsyncInterface.onAuthenticationError(Constants.AUTH_ERROR_MESSAGE);
        }else{
            authenticateAsyncInterface.onAuthenticationSuccess(accessToken);
        }
    }
}
