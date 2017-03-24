package com.ase_1617.organizedlib.network;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * Class extending the AsyncTask class to fetch event data from the organized web server
 */

public class SignupAsyncTask extends AsyncTask<Object, Void, Boolean> {
    private final static String TAG = "SignupAsyncTask";

    public SignupAsyncInterface signupAsyncInterface = null;

    Context context;

    ProgressDialog progressDialog;

    public SignupAsyncTask(Context context){
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        Log.v(TAG, "PREEXECUTE");

        progressDialog = new ProgressDialog(context);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Creating Account...");
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
        final String name = (String)urls[3];

        Log.v(TAG, "DOIN"+url+email+password+name);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.v(TAG, "Response: " + response);

                        progressDialog.dismiss();
                        signupAsyncInterface.onSignUpSuccess(name, email, password);
                    }
                },
                new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.v(TAG, "Error: " + error);

                        progressDialog.dismiss();
                        signupAsyncInterface.onSignUpError(error.toString());
                    }
                })
        {
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put("email",email);
                params.put("password",password);
                params.put("first_name",name);
                params.put("last_name","");
                params.put("bio","");
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);

        return true;
    }


    /**
     * Call the given fetchEventsAsyncInterface method when the async task has finished
     * @param result
     */
    protected void onPostExecute(Boolean result) {
        //signupAsyncInterface.eventAccepted(eventPosition);
    }
}
