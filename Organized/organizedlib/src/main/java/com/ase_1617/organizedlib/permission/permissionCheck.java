package com.ase_1617.organizedlib.permission;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;

/**
 * Class to check whether the app is granted the permission to create new calendar events.
 * If it is not granted. Request the permission from the user. Show an info alert if necessary.
 */

public class permissionCheck {

    /**
     * Check the calendar write permission.
     * Request the permission if necessary.
     * @param activity The activity from which the permission was requested
     * @return Boolean value showing whether the permission is granted or not
     */
    public static boolean permissionGrantedWriteCal(Activity activity){
        if (ContextCompat.checkSelfPermission(activity,
                Manifest.permission.WRITE_CALENDAR)
                != PackageManager.PERMISSION_GRANTED) {

            //Show an explanation if necessary.
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
                    Manifest.permission.WRITE_CALENDAR)) {

                final Activity finalActivity = activity;

                //Show an explanation dialog that explains why the app does need the permission.
                AlertDialog permRequestDialog = new AlertDialog.Builder(finalActivity)
                        .setTitle("Missing permission")
                        .setMessage("Organized needs permission to create calendar events.")
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

                //If no explanation needed just request the permission.
                ActivityCompat.requestPermissions(activity,
                        new String[]{Manifest.permission.WRITE_CALENDAR},
                        1);
            }
            return false;
        }else{
            return true;
        }
    }
}
