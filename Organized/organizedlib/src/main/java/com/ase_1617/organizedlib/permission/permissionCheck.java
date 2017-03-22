package com.ase_1617.organizedlib.permission;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;

/**
 * Created by bob on 09.03.17.
 */

public class permissionCheck {

    private static AlertDialog permRequestDialog;


    /**Check whether the app can write in the calendar device app
     * Request the necessary permisison if not*/
    public static boolean permissionGrantedWriteCal(Activity activity){
        if (ContextCompat.checkSelfPermission(activity,
                Manifest.permission.WRITE_CALENDAR)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
                    Manifest.permission.WRITE_CALENDAR)) {

                final Activity finalActivity = activity;

                permRequestDialog = new AlertDialog.Builder(finalActivity)
                        .setTitle("Calendar read permission needed")
                        .setMessage("The app needs the calendar read permission to get the events from the default calendar app.")
                        .setPositiveButton("Accept", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                //Request the permission if the user accepts
                                ActivityCompat.requestPermissions(finalActivity,
                                        new String[]{Manifest.permission.WRITE_CALENDAR},
                                        1);
                            }
                        })
                        .setNegativeButton("Decline", null)
                        .create();
                permRequestDialog.show();

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(activity,
                        new String[]{Manifest.permission.WRITE_CALENDAR},
                        1);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
            return false;
        }else{
            return true;
        }
    }
}
