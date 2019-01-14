/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.searchplace.source;


import java.net.URLEncoder;
import java.util.Vector;

import com.google.android.gms.location.FusedLocationProviderApi;

import android.app.IntentService;
import android.content.Intent;
import android.location.Location;


/**
 * Listens for location transition changes.
 */
public class LocationTransitionsIntentService extends IntentService
{
	public static final String BROADCAST_INTENT_ACTION = LocationTransitionsIntentService.class.getCanonicalName() + ".LOCATION_TRANSITION";
    public static final String LATITUDE_EXTRA_ID = "latitude";
    public static final String LONGITUDE_EXTRA_ID = "longitude";

    public LocationTransitionsIntentService() {
        super(LocationTransitionsIntentService.class.getSimpleName());
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    /**
     * Handles incoming intents.
     * @param intent The Intent sent by Location Services. This Intent is provided to Location
     * Services (inside a PendingIntent) when addGeofences() is called.
     */
    @Override
    protected void onHandleIntent(Intent intent)
    {
    	try
    	{
    		Location location = (Location) intent.getExtras().get(FusedLocationProviderApi.KEY_LOCATION_CHANGED);

        	if(location != null)
        	{

        		MySharedPreferences.saveLocation(getApplicationContext(), location.getLatitude()+","+location.getLongitude());

        	}
    	}
    	catch(Exception e)
    	{

    	}
    }

}
