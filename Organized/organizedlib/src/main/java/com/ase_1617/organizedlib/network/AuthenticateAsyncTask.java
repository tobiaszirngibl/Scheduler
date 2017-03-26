package com.ase_1617.organizedlib.network;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.ase_1617.organizedlib.scribejava.OrganizedOAuth20Api;
import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Verb;
import com.github.scribejava.core.oauth.OAuth20Service;

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
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

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
        final String secretState = (String)urls[5];
        final String protectedURl = (String)urls[6];

        int responseCode = 0;
        authenticationError = 0;

        /*Log.v(TAG, "DOIN"+url+email+password);

        final OAuth20Service service = new ServiceBuilder()
                .apiKey(clientId)
                .apiSecret(clientSecret)
                .state(secretState)
                .callback("https://example.com/callback")
                .build(OrganizedOAuth20Api.instance());
        final Scanner in = new Scanner(System.in, "UTF-8");

        System.out.println("=== " + "Organized" + "'s OAuth Workflow ===");
        System.out.println();

        // Obtain the Authorization URL
        System.out.println("Fetching the Authorization URL...");
        //pass access_type=offline to get refresh token
        final Map<String, String> additionalParams = new HashMap<>();
        additionalParams.put("access_type", "offline");
        //force to reget refresh token (if usera are asked not the first time)
        additionalParams.put("prompt", "consent");
        final String authorizationUrl = service.getAuthorizationUrl(additionalParams);
        System.out.println("Got the Authorization URL!");
        System.out.println("Now go and authorize ScribeJava here:");
        System.out.println(authorizationUrl);
        System.out.println("And paste the authorization code here");
        System.out.print(">>");
        System.out.print("++");
        final String code = in.nextLine();
        System.out.println();

        System.out.println("And paste the state from server here. We have set 'secretState'='" + secretState + "'.");
        System.out.print(">>");
        final String value = in.nextLine();
        if (secretState.equals(value)) {
            System.out.println("State value does match!");
        } else {
            System.out.println("Ooops, state value does not match!");
            System.out.println("Expected = " + secretState);
            System.out.println("Got      = " + value);
            System.out.println();
        }

        // Trade the Request Token and Verfier for the Access Token
        System.out.println("Trading the Request Token for an Access Token...");
        OAuth2AccessToken accessToken;
        try {
            accessToken = service.getAccessToken(code);
        } catch (IOException e) {
            e.printStackTrace();
            accessToken = null;
        }
        System.out.println("Got the Access Token!");
        //System.out.println("(If you're curious, it looks like this: " + accessToken
        //        + ", 'rawResponse'='" + accessToken.getRawResponse() + "')");

        System.out.println();

        // Now let's go and ask for a protected resource!
        System.out.println("Now we're going to access a protected resource...");
        final OAuthRequest request = new OAuthRequest(Verb.GET, protectedURl, service);
        service.signRequest(accessToken, request);
        final com.github.scribejava.core.model.Response response = request.send();
        System.out.println("Got it! Lets see what we found...");
        System.out.println();
        System.out.println(response.getCode());
        try {
            System.out.println(response.getBody());
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Thats it man! Go and build something awesome with ScribeJava! :)");
*/

        String data = null;
        try {
            data = URLEncoder.encode( "grant_type", "UTF-8" ) + "=" + URLEncoder.encode( GRANT_TYPE, "UTF-8" );
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        try {
            data += "&" + URLEncoder.encode( "username", "UTF-8" ) + "=" + URLEncoder.encode( email, "UTF-8" );
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        try {
            data += "&" + URLEncoder.encode( "password", "UTF-8" ) + "=" + URLEncoder.encode( password, "UTF-8" );
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        try {
            data += "&" + URLEncoder.encode( "client_id", "UTF-8" ) + "=" + URLEncoder.encode( clientId, "UTF-8" );
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        try {
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

            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject(response.toString());
                accessToken = jsonObject.getString(ACCESS_TOKEN_TAG);
            } catch (JSONException e) {
                e.printStackTrace();
            }

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
