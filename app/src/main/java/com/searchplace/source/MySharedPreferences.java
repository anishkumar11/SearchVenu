package com.searchplace.source;

import android.content.Context;
import android.content.SharedPreferences;

public class MySharedPreferences {

    private static String SHARED_PREFERENCE_NAME = "search_place_perference";

    public static String getLastLocation(Context mContext)
    {
        try
        {
            SharedPreferences obj = mContext.getSharedPreferences(SHARED_PREFERENCE_NAME, mContext.MODE_PRIVATE);
            String str = obj.getString("location_lat_long", "");
            if(str.length() ==0)
            {
                str = "0.0,0.0";
            }
            return str;
        }
        catch(Exception e)
        {
            System.out.println("Exception: " + e + " ::Utilties::getLastLocation");
        }
        return "";
    }

    public static void saveLocation(Context mContext, String latLong)
    {
        try
        {
            SharedPreferences sharedPref = mContext.getSharedPreferences(SHARED_PREFERENCE_NAME, mContext.MODE_PRIVATE);
            SharedPreferences.Editor prefEditor = sharedPref.edit();

            prefEditor.putString("location_lat_long", latLong);
            prefEditor.commit();
        }
        catch(Exception e)
        {
            System.out.println("Exception: " + e + " ::Utilties::saveLocationServiceStatus");
        }
    }

    public static String getLocationServiceStatus(Context mContext)
    {
        try
        {
            SharedPreferences obj = mContext.getSharedPreferences(SHARED_PREFERENCE_NAME, mContext.MODE_PRIVATE);

            return obj.getString("location_service_status", "");
        }
        catch(Exception e)
        {
            System.out.println("Exception: " + e + " ::Utilties::getLoginInfo");
        }
        return "";
    }

    public static void saveLocationServiceStatus(Context mContext, String locationServiceStatus)
    {
        try
        {
            SharedPreferences sharedPref = mContext.getSharedPreferences(SHARED_PREFERENCE_NAME, mContext.MODE_PRIVATE);
            SharedPreferences.Editor prefEditor = sharedPref.edit();

            prefEditor.putString("location_service_status", locationServiceStatus);
            prefEditor.commit();
        }
        catch(Exception e)
        {
            System.out.println("Exception: " + e + " ::Utilties::saveLocationServiceStatus");
        }
    }
}
