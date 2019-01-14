package com.searchplace.source;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;

public class LocationTrackerService extends Service implements ConnectionCallbacks, OnConnectionFailedListener {

	private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;

	private Location mLastLocation;

	// Google client to interact with Google API
	private GoogleApiClient mGoogleApiClient;

	// boolean flag to toggle periodic location updates
	private boolean mRequestingLocationUpdates = false;

	private LocationRequest mLocationRequest;

	// Location updates intervals in sec
	private static int UPDATE_INTERVAL = 10000; // 10 sec
	private static int FATEST_INTERVAL = 5000; // 5 sec
	private static int DISPLACEMENT = 500; // 10 meters

	@Override
	public void onCreate() {
		super.onCreate();
	}

	private void checkPermissionGranted() {
		Thread th = new Thread() {
			public void run() {
				try {

					while(!isPermissionGranted()){
                        //System.out.println("**********Location Permission not granted*******");
					    Thread.sleep(50);
					}
					//System.out.println("**********Location registeration start*******");

					registerLocationUpdates();

                    //System.out.println("**********Location registeration done*******");
				} catch (Exception e) {
					System.out.println("Exception: " + e + " ::LocationTrackerService::checkPermissionGranted");
				}
			}
		};
		th.start();
	}
	private boolean isPermissionGranted()
	{
		try
		{
			if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
				return false;
			}
			else
			{
				return true;
			}
		}
		catch(Exception e){}
		return false;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		try {
			if(mGoogleApiClient != null)
			{
				connectPlayService();
			}
			else
			{
				checkPermissionGranted();
			}
		} catch (Exception e) {
			System.out.println("Exception: " + e + " ::RegisterListener::onStartCommand");
		}
		return START_STICKY;
	}

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	private void connectPlayService() {
		try {
			if (mGoogleApiClient != null && !mGoogleApiClient.isConnected()) {
				MySharedPreferences.saveLocationServiceStatus(getApplicationContext(), Constants.LOCATION_SERVICE_CONNECTING);
				mGoogleApiClient.connect();
			} else if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
				saveLocation();
				MySharedPreferences.saveLocationServiceStatus(getApplicationContext(), Constants.LOCATION_SERVICE_CONNECTED);
			}
		} catch (Exception e) {

		}
	}

	private void registerLocationUpdates() {
		// First we need to check availability of play services

		if (checkPlayServices()) {
			// Building the GoogleApi client
			MySharedPreferences.saveLocationServiceStatus(getApplicationContext(), Constants.LOCATION_SERVICE_CONNECTING);
			buildGoogleApiClient();
			createLocationRequest();
			connectPlayService();
		}
	}

	/**
	 * Creating google api client object
	 * */
	protected synchronized void buildGoogleApiClient() {
		try {
			mGoogleApiClient = new GoogleApiClient.Builder(this)
					.addConnectionCallbacks(this)
					.addOnConnectionFailedListener(this)
					.addApi(LocationServices.API).build();
		} catch (Exception e) {
			System.out.println("Exception: " + e + " ::LocationTrackerService::buildGoogleApiClient");
		}
	}

	/**
	 * Creating location request object
	 * */
	protected void createLocationRequest() {

		try {
			mLocationRequest = new LocationRequest();
			mLocationRequest.setInterval(UPDATE_INTERVAL);
			mLocationRequest.setFastestInterval(FATEST_INTERVAL);
			mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
			mLocationRequest.setSmallestDisplacement(DISPLACEMENT);
		} catch (Exception e) {
			System.out.println("Exception: " + e + " ::LocationTrackerService::createLocationRequest");
		}
	}

	/**
	 * Method to verify google play services on the device
	 * */
	private boolean checkPlayServices() {
		int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
		if (resultCode != ConnectionResult.SUCCESS) {
			MySharedPreferences.saveLocationServiceStatus(getApplicationContext(), Constants.LOCATION_SERVICE_NOT_AVAILABLE);
			if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
				/*
				GooglePlayServicesUtil.getErrorDialog(resultCode, this,
						PLAY_SERVICES_RESOLUTION_REQUEST).show();*/
				System.out.println("Google play service error message");
			} else {
				/*
				Toast.makeText(getApplicationContext(),
						"This device is not supported.", Toast.LENGTH_LONG)
						.show();*/
				System.out.println("This device is not supported.");
			}
			return false;
		}
		return true;
	}

	@Override
	public void onConnectionFailed(ConnectionResult arg0) {
		// TODO Auto-generated method stub
		MySharedPreferences.saveLocationServiceStatus(getApplicationContext(), Constants.LOCATION_SERVICE_NOT_AVAILABLE);
		checkPermissionGranted();
		Toast.makeText(this, "*****onConnectionFailed******", Toast.LENGTH_LONG).show();
		//connectPlayService();
	}

	private void saveLocation() {
		try {


			if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
				// TODO: Consider calling
				//    ActivityCompat#requestPermissions
				// here to request the missing permissions, and then overriding
				//   public void onRequestPermissionsResult(int requestCode, String[] permissions,
				//                                          int[] grantResults)
				// to handle the case where the user grants the permission. See the documentation
				// for ActivityCompat#requestPermissions for more details.
				return;
			}
			mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

			if (mLastLocation != null) {
				double latitude = mLastLocation.getLatitude();
				double longitude = mLastLocation.getLongitude();

				MySharedPreferences.saveLocation(getApplicationContext(), latitude + "," + longitude);
			}

		} catch (Exception e) {

		}
	}

	@Override
	public void onConnected(Bundle arg0) {
		// TODO Auto-generated method stub
		try {
			saveLocation();
			MySharedPreferences.saveLocationServiceStatus(getApplicationContext(), Constants.LOCATION_SERVICE_CONNECTED);
			Intent intent = new Intent(this, LocationTransitionsIntentService.class);
			PendingIntent locationIntent = PendingIntent.getBroadcast(getApplicationContext(), 14872, intent, PendingIntent.FLAG_CANCEL_CURRENT);
			if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
				// TODO: Consider calling
				//    ActivityCompat#requestPermissions
				// here to request the missing permissions, and then overriding
				//   public void onRequestPermissionsResult(int requestCode, String[] permissions,
				//                                          int[] grantResults)
				// to handle the case where the user grants the permission. See the documentation
				// for ActivityCompat#requestPermissions for more details.
				return;
			}
			LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, locationIntent);


		}
		catch(Exception e)
		{

		}
	}

	@Override
	public void onConnectionSuspended(int arg0) {
		// TODO Auto-generated method stub
		try
		{
			MySharedPreferences.saveLocationServiceStatus(getApplicationContext(), Constants.LOCATION_SERVICE_CONNECTING);
			mGoogleApiClient.connect();
		}
		catch(Exception e)
		{

		}

	}


}