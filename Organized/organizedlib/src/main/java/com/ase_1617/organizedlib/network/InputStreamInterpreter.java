package com.ase_1617.organizedlib.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.HttpURLConnection;

/**
 * Created by bob on 04.04.17.
 *
 * Class to read a httpUrlConnection and return the response as string.
 */

public class InputStreamInterpreter {

    /**
     * Read a httpUrlConnection and return the response as string.
     * @param connection The connection to be read
     * @return The response string
     */
    public static String interpretInputStream(HttpURLConnection connection){
        BufferedReader reader;
        StringBuffer response = new StringBuffer();
        String line;

        try{
            reader = new BufferedReader(new java.io.InputStreamReader(connection.getInputStream()));
            while((line = reader.readLine()) != null){
                response.append(line);
            }
        }catch(IOException e){
            e.printStackTrace();
        }

        return response.toString();
    }
}
