package com.searchplace.source;

import android.Manifest;
import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.LocationManager;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.searchplace.source.adapters.SearchResultListAdapter;
import com.searchplace.source.local_objects.Place;
import com.searchplace.source.network.HTTPClient;
import com.searchplace.source.network.HTTPThread;
import com.searchplace.source.network.ResponseParser;
import com.searchplace.source.network.WebApi;

import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.Vector;

public class MainActivity extends AppCompatActivity implements
                            SearchView.OnQueryTextListener, SearchView.OnCloseListener {


    private ProgressBar _loadingBar;
    private CardView _cardView;
    private TextView _countLabel, _emptyLabel, _gpsStatus;
    private SearchResultListAdapter _mAdapter;
    private SearchView _searchView;
    private HTTPThread _searchThread;
    private Vector<Place> _listStore = new Vector<Place>();
    private int REQUEST_STATUS_CANCEL = 101,
                       REQUEST_STATUS = -1;

    private static final int REQUEST_CHECK_SETTINGS = 0x1;
    private static GoogleApiClient mGoogleApiClient;
    private static final int ACCESS_FINE_LOCATION_INTENT_ID = 3;
    private static final String BROADCAST_ACTION = "android.location.PROVIDERS_CHANGED";

    private boolean _isGPSEnable;
    private String _serachKeyword = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initializeComponent();

        initGoogleAPIClient();

    }

    private void initializeComponent()
    {
        try
        {
            SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);

            _loadingBar = (ProgressBar) findViewById(R.id.loadingBar);
            _countLabel = (TextView) findViewById(R.id.countLabel);
            _emptyLabel = (TextView) findViewById(R.id.emptyLabel);
            _gpsStatus = (TextView) findViewById(R.id.gpsStatus);
            _cardView = (CardView) findViewById(R.id.cardView);
            _searchView = (SearchView) findViewById(R.id.search);

            _searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName
                    ()));
            _searchView.setIconifiedByDefault(false);
            _searchView.setOnQueryTextListener(this);
            _searchView.setOnCloseListener(this);
            _searchView.clearFocus();



            InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            mgr.hideSoftInputFromWindow(_searchView.getWindowToken(), 0);

            RecyclerView _list = (RecyclerView)findViewById(R.id.list);

            _mAdapter = new SearchResultListAdapter(_listStore);

            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
            _list.setLayoutManager(mLayoutManager);
            _list.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
            _list.setItemAnimator(new DefaultItemAnimator());

            _list.setAdapter(_mAdapter);



        }
        catch(Exception e)
        {

        }
    }

    /* Initiate Google API Client  */
    private void initGoogleAPIClient() {
        //Without Google API Client Auto Location Dialog will not work
        mGoogleApiClient = new GoogleApiClient.Builder(MainActivity.this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    /* Check Location Permission for Marshmallow Devices */
    private void checkPermissions() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(MainActivity.this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED)
                requestLocationPermission();
            else
                showSettingDialog();
        } else
            showSettingDialog();

    }

    /*  Show Popup to access User Permission  */
    private void requestLocationPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION)) {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    ACCESS_FINE_LOCATION_INTENT_ID);

        } else {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    ACCESS_FINE_LOCATION_INTENT_ID);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        checkPermissions();
        checkGpsStatus(getApplicationContext());
    }

    @Override
    protected void onResume() {
        super.onResume();
        try
        {
            //Register broadcast receiver to check the status of GPS
            registerReceiver(gpsLocationReceiver, new IntentFilter(BROADCAST_ACTION));
        }
        catch(Exception e)
        {
            System.out.println("Exception: " + e + " ::MainActivity::onResume");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try
        {
            //Unregister receiver on destroy
            if (gpsLocationReceiver != null)
                unregisterReceiver(gpsLocationReceiver);
        }
        catch(Exception e)
        {
            System.out.println("Exception: " + e + " ::MainActivity::onDestroy");
        }
    }

    @Override
    public boolean onClose() {
        return false;
    }

    @Override
    public boolean onQueryTextSubmit(String str) {
        _searchView.clearFocus();
        _serachKeyword = str.trim();

        if(_serachKeyword.length() > 0)
        {
            searchAction(_serachKeyword);
        }
        else
        {
            clearSearchResult();
            //showHideLoadingBar();
        }
        return true;
    }

    @Override
    public boolean onQueryTextChange(String str) {
        _serachKeyword = str.trim();
        if(_serachKeyword.length() > 0)
        {
            searchAction(_serachKeyword);
        }
        else
        {
            clearSearchResult();
            //showHideLoadingBar();
        }
        return false;
    }

    private void cancelReqest()
    {
        try {
            if(_searchThread != null && _searchThread.isAlive())
            {
                REQUEST_STATUS = REQUEST_STATUS_CANCEL;
                try {
                    _searchThread.getHttpCallRequest().cancel();
                }
                catch(Exception e)
                {

                }

                try {
                    _searchThread.interrupt();
                }
                catch(Exception e){}
            }
        }
        catch(Exception e)
        {

        }
    }

    private void clearSearchResult()
    {
        cancelReqest();
        try {
            _listStore.clear();
            _cardView.setVisibility(View.GONE);
            _countLabel.setVisibility(View.GONE);
            _emptyLabel.setVisibility(View.GONE);
            _mAdapter.notifyDataSetChanged();
        }
        catch(Exception e)
        {
            System.out.println("Exception: " + e + " ::MainActivity::clearSearchResult");
            return;
        }
    }

    private void searchAction(final String keyword)
    {
        clearSearchResult();
        if(!_isGPSEnable)
        {
            _emptyLabel.setVisibility(View.VISIBLE);
            _emptyLabel.setText("GPS is disabled. Please turn on GPS of your device.");
            return;
        }
        showHideLoadingBar();
        _searchThread = new HTTPThread()
        {
            public void run()
            {
                try
                {

                    //String latlong = "40.7484,-73.9857";
                    String latlong = MySharedPreferences.getLastLocation(getApplicationContext());
                    String url = WebApi.URL_SEARCH_VENU+latlong+
                                 "&query="+ URLEncoder.encode(keyword, "UTF-8");

                    byte[] data = HTTPClient.getViaHttpConnection(url, this);
                    if(data != null)
                    {
                        ResponseParser.parseSearchResult(new String(data), _listStore);
                        refreshList();
                    }
                    else
                    {
                        showHideLoadingBar();
                        if(REQUEST_STATUS == REQUEST_STATUS_CANCEL)
                        {
                            REQUEST_STATUS = -1;
                            return;
                        }

                    }
                }
                catch(Exception e)
                {
                    showHideLoadingBar();
                    System.out.println("Exception: " + e + " ::SignUpScreen::sendRequestToLoadFolderList");
                }
            }
        };
        _searchThread.start();
    }

    private void showHideLoadingBar()
    {
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                try
                {
                    if(_loadingBar.getVisibility() == View.VISIBLE)
                    {
                        _loadingBar.setVisibility(View.GONE);
                    }
                    else
                    {
                        _loadingBar.setVisibility(View.VISIBLE);
                    }
                }
                catch(Exception e)
                {
                    System.out.println("Exception: " + e + " ::MainActivity::showHideLoadingBar");
                }
            }
        }, 5);
    }

    private void refreshList()
    {
        try
        {
            showHideLoadingBar();
            new Handler(Looper.getMainLooper()).postDelayed(new Runnable()
            {
                public void run()
                {
                    _mAdapter.notifyDataSetChanged();
                    checkForEmptyList();
                }
            }, 10);


        }
        catch(Exception e)
        {
            System.out.println("Exception: " + e + " FragmentInvoiceScreen::refreshCategoryList::");
        }

    }

    private void checkForEmptyList()
    {
        try
        {
            if(_listStore.size() > 0)
            {
                _emptyLabel.setVisibility(View.GONE);
                _cardView.setVisibility(View.VISIBLE);
                _countLabel.setVisibility(View.VISIBLE);
                int size = _listStore.size();
                if(size > 1)
                {
                    _countLabel.setText(size+" Results Found");
                }
                else
                {
                    _countLabel.setText(size+" Result Found");
                }

            }
            else
            {
                _cardView.setVisibility(View.GONE);
                _emptyLabel.setVisibility(View.VISIBLE);
                _countLabel.setVisibility(View.GONE);
            }
        }
        catch (Exception e)
        {

        }
    }

    //Method to update GPS status text
    private void updateGPSStatus(String status) {
        _gpsStatus.setText(status);
    }


    /* Broadcast receiver to check status of GPS */
    private BroadcastReceiver gpsLocationReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            //If Action is Location
            if (intent.getAction().matches(BROADCAST_ACTION)) {

                checkGpsStatus(context);
                if(_serachKeyword.length() > 0)
                {
                    searchAction(_serachKeyword);
                }
            }
        }
    };

    /* Check GPS status*/
    private void checkGpsStatus(Context context)
    {
        try
        {
            _isGPSEnable = false;
            LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            //Check if GPS is turned ON or OFF
            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                _isGPSEnable = true;
                updateGPSStatus("GPS is Enabled");
            } else {
                //If GPS turned OFF show Location Dialog
                new Handler(Looper.getMainLooper()).postDelayed(new Runnable()
                {
                    @Override
                    public void run() {
                        updateGPSStatus("GPS is Disabled");
                        showSettingDialog();
                    }
                }, 10);
            }
        }
        catch(Exception e)
        {
            System.out.println("Exception: " + e + " ::MainActivity::checkGpsStatus");
        }
    }


    /* Show Location Access Dialog */
    private void showSettingDialog() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);//Setting priotity of Location request to high
        locationRequest.setInterval(30 * 1000);
        locationRequest.setFastestInterval(5 * 1000);//5 sec Time interval for location update
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);
        builder.setAlwaysShow(true); //this is the key ingredient to show dialog always when GPS is off

        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                final LocationSettingsStates state = result.getLocationSettingsStates();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        // All location settings are satisfied. The client can initialize location
                        // requests here.
                        updateGPSStatus("GPS is Enabled");
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // Location settings are not satisfied. But could be fixed by showing the user
                        // a dialog.
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            status.startResolutionForResult(MainActivity.this, REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException e) {
                            e.printStackTrace();
                            // Ignore the error.
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // Location settings are not satisfied. However, we have no way to fix the
                        // settings so we won't show the dialog.
                        break;
                }
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            // Check for the integer request code originally supplied to startResolutionForResult().
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case RESULT_OK:
                        updateGPSStatus("GPS is Enabled in your device");
                        //startLocationUpdates();
                        break;
                    case RESULT_CANCELED:
                        updateGPSStatus("GPS is Disabled in your device");
                        break;
                }
                break;
        }
    }


    /* On Request permission method to check the permisison is granted or not for Marshmallow+ Devices  */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case ACCESS_FINE_LOCATION_INTENT_ID: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    //If permission granted show location dialog if APIClient is not null
                    if (mGoogleApiClient == null) {
                        initGoogleAPIClient();
                        showSettingDialog();
                    } else
                        showSettingDialog();


                } else {
                    updateGPSStatus("Location Permission denied.");
                    Toast.makeText(MainActivity.this, "Location Permission denied.", Toast.LENGTH_SHORT).show();
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
        }
    }
}
