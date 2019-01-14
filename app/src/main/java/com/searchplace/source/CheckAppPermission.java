package com.searchplace.source;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import java.util.Vector;

/**
 * Created by Login on 4/11/2018.
 */

public class CheckAppPermission {

    private static final int PERMISSION_REQUEST_CODE = 101;

    public static void askPermission(Activity activity)
    {
        int readLocationPermissionCheck = ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION);
        int readCoarseLocPermissionCheck = ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION);

        Vector<String> vv = new Vector<String>();


        if (readLocationPermissionCheck != PackageManager.PERMISSION_GRANTED) {
            vv.addElement(Manifest.permission.ACCESS_FINE_LOCATION);
        }

        if (readCoarseLocPermissionCheck != PackageManager.PERMISSION_GRANTED) {
            vv.addElement(Manifest.permission.ACCESS_COARSE_LOCATION);
        }


        if(vv.size() > 0)
        {
            String[] permissions = new String[vv.size()];
            for(int i =0 ;i < vv.size(); i++)
            {
                permissions[i] = vv.elementAt(i);
            }
            ActivityCompat.requestPermissions(activity, permissions, PERMISSION_REQUEST_CODE);
        }

    }


}
