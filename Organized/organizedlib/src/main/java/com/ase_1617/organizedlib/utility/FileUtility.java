package com.ase_1617.organizedlib.utility;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

/**
 * Created by bob on 17.04.17.
 *
 * An utility class to read and interpret data from a config text file
 */

public class FileUtility{
    private final String TAG = "FileUtility";

    public String readFromFile(Context context) {

        String fileString = "";

        try{
            InputStream inputStream = context.openFileInput("config.txt");

            if(inputStream != null ){
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString;
                StringBuilder stringBuilder = new StringBuilder();

                while((receiveString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append(receiveString);
                }

                inputStream.close();
                fileString = stringBuilder.toString();
            }
        }
        catch(FileNotFoundException e){
            Log.e(TAG, "File not found: " + e.toString());
        }catch(IOException e) {
            Log.e(TAG, "Can not read file: " + e.toString());
        }

        return fileString;
    }

    /**
     *
     * @param data
     * @param context
     */
    public static void writeToFile(String data, Context context) {

        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput("FCBAYERN.txt", Context.MODE_PRIVATE));
            outputStreamWriter.write(data);
            outputStreamWriter.close();
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

}
