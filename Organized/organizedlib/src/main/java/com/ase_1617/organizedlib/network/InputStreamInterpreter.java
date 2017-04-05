package com.ase_1617.organizedlib.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.HttpURLConnection;

/**
 * Created by bob on 04.04.17.
 */

public class InputStreamInterpreter {

    public static String interpretInputStream(HttpURLConnection connection){
        BufferedReader reader = null;
        StringBuffer response = new StringBuffer();
        String line = "";

        try{
            reader = new BufferedReader( new java.io.InputStreamReader( connection.getInputStream() ) );
            while((line = reader.readLine()) != null){
                response.append( line );
            }
        }catch(IOException e){
            e.printStackTrace();
        }

        return response.toString();
    }
}
