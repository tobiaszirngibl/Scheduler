package com.ase_1617.organizedlib.network;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

/**
 * Class extending the AsyncTask class to send user data(Name, Email, Password) to the web server
 * and create a new oganized account.
 */

public class SignupAsyncTask extends AsyncTask<Object, Void, Boolean> {

    private Context context;

    private ProgressDialog progressDialog;

    public SignupAsyncInterface signupAsyncInterface = null;

    public SignupAsyncTask(Context context){
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        progressDialog = new ProgressDialog(context);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Creating Account...");
        progressDialog.show();
    }

    /**
     * Send the new user data to the server using volley stringrequest.
     * @param urls Parameters containing
     *             the server url,
     *             the user email,
     *             the user password,
     *             the user name
     * @return Boolean value
     */
    @Override
    protected Boolean doInBackground(Object... urls) {
        final String email = (String)urls[1];
        final String password = (String)urls[2];
        final String name = (String)urls[3];

        String url = (String)urls[0];

        //Create a new volley stringrequest, response and error listener
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        signupAsyncInterface.onSignUpSuccess(email, password);
                    }
                },
                new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        signupAsyncInterface.onSignUpError(error.toString());
                    }
                })
        {
            //Supply the necessary parameters
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<>();
                params.put("email",email);
                params.put("password",password);
                params.put("first_name",name);
                params.put("last_name","");
                params.put("bio","");
                return params;
            }
        };

        //Add the stringrequest to the volley requestqueue
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);

        return true;
    }

    @Override
    protected void onPostExecute(Boolean result) {
    }
}
