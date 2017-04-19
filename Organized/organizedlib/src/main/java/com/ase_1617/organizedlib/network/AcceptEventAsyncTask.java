package com.ase_1617.organizedlib.network;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.ase_1617.organizedlib.utility.Constants;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Class extending the AsyncTask class to send the user's answer
 * according an event to the server.
 */

public class AcceptEventAsyncTask extends AsyncTask<Object, Void, Boolean> {
    private Integer eventPosition = -1;
    private int responseCode = 0;

    private String answer;
    private String eventTitle;

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
     * @param urls Parameters containing
     *             the oauth access token,
     *             the event id,
     *             the event position,
     *             the event answer,
     *             the event title
     * @return Boolean value
     */
    @Override
    protected Boolean doInBackground(Object... urls) {
        final String accessToken = (String)urls[0];
        final Integer eventId = (Integer)urls[1];

        eventPosition = (Integer)urls[2];
        answer = (String)urls[3];
        eventTitle = (String)urls[4];

        String url = Constants.FEEDBACK_URL_START + eventId + Constants.FEEDBACK_URL_END;
        String data = "";

        URL server = null;

        HttpURLConnection connection;

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
        if(server != null){
            try{
                connection = (HttpURLConnection) server.openConnection();
                connection.setRequestProperty("Authorization", "Bearer " + accessToken);
                connection.setDoOutput(true);

                osw = new OutputStreamWriter(connection.getOutputStream());
                osw.write(data);
                osw.flush();

                responseCode = connection.getResponseCode();
            }catch(IOException e){
                e.printStackTrace();
            }
        }

        progressDialog.dismiss();

        return true;
    }

    /**
     * Check whether the server sent an OK result.
     * Call the according fetchEventsAsyncInterface method when the async task has finished.
     * @param result Boolean value
     */
    protected void onPostExecute(Boolean result) {
        if(responseCode == Constants.FEEDBACK_SUCCESS_CODE){
            Toast toast = Toast.makeText(context, "Participation of event: " + eventTitle + " set to '" + answer + "'.", Toast.LENGTH_SHORT);
            toast.show();

            //Call the accept/decline interface method
            if(answer.equals("yes")){
                acceptEventAsyncInterface.eventAccepted(eventPosition);
            }else{
                acceptEventAsyncInterface.eventDeclined(eventPosition);
            }
        }else{
            Toast toast = Toast.makeText(context, Constants.FEEDBACK_ERROR_MESSAGE, Toast.LENGTH_SHORT);
            toast.show();
        }

    }
}
