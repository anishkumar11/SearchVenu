package com.searchplace.source;

import android.content.Intent;
import android.support.multidex.MultiDexApplication;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

/**
 * Created by Login on 1/31/2018.
 */

public class MyApplication  extends MultiDexApplication {
    public void onCreate() {
        super.onCreate();


        try {
            CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                    .setDefaultFontPath("fonts/helvetica.ttf")
                    .setFontAttrId(R.attr.fontPath)
                    .build()
            );

            startService(new Intent(getApplicationContext(), LocationTrackerService.class));
        }
        catch(Exception e)
        {
            System.out.println("Exception: " + e + " ::App::onCreate");
        }
    }

}

