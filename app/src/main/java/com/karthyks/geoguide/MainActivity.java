package com.karthyks.geoguide;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationListener;

import java.text.DateFormat;
import java.util.Date;


public class MainActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, GeoLocatorReceiver.Receiver, LocationListener
{

    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    TextView _currentLocation;
    TextView _latitudeText;
    TextView _longitudeText;

    Intent geoLocatorIntent;
    GeoLocatorReceiver geoLocatorReceiver;

    String resultText;
    String mLastUpdateTime;
    private boolean mRequestingLocationUpdates;
    LocationRequest mLocationRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        geoLocatorReceiver = new GeoLocatorReceiver(new Handler());
        geoLocatorReceiver.setReceiver(this);
        _currentLocation = (TextView) findViewById(R.id.curr_loc);
        _latitudeText = (TextView) findViewById(R.id.latitude);
        _longitudeText = (TextView) findViewById(R.id.longitude);
        buildGoogleApiClient();
        createLocationRequest();
        updateValuesFromBundle(savedInstanceState);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        resultText = savedInstanceState.getString("lastLocation");
        _latitudeText.setText(savedInstanceState.getString("lastLocationLatitude"));
        _longitudeText.setText(savedInstanceState.getString("lastLocationLongitude"));
        if(resultText != null)
        {
            _currentLocation.setText(resultText);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(Constants.REQUESTING_LOCATION_UPDATES_KEY,
                mRequestingLocationUpdates);
        outState.putParcelable(Constants.LOCATION_KEY, mLastLocation);
        outState.putString(Constants.LAST_UPDATED_TIME_STRING_KEY, mLastUpdateTime);
        super.onSaveInstanceState(outState);
        outState.putString("lastLocation", resultText);
        outState.putString("lastLocationLatitude", _latitudeText.getText().toString());
        outState.putString("lastLocationLongitude", _longitudeText.getText().toString());
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (mGoogleApiClient.isConnected() && !mRequestingLocationUpdates) {
            startLocationUpdates();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    @Override
    protected void onStop() {
        if(mGoogleApiClient.isConnected())
            mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public void GeoLocate(View v)
    {
        if(mLastLocation != null)
        {
            geoLocatorIntent = new Intent(this, GeoLocator.class);
            geoLocatorIntent.putExtra("latitude", mLastLocation.getLatitude());
            geoLocatorIntent.putExtra("longitude", mLastLocation.getLongitude());
            geoLocatorIntent.putExtra("receiver", geoLocatorReceiver);
            startService(geoLocatorIntent);
        }
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    @Override
    public void onConnected(Bundle bundle)
    {
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        _latitudeText.setText(String.valueOf(mLastLocation.getLatitude()));
        _longitudeText.setText(String.valueOf(mLastLocation.getLongitude()));
        if(mRequestingLocationUpdates)
        {
            startLocationUpdates();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    protected void startLocationUpdates() {
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);
    }

    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient, this);
    }

    @Override
    public void onReceiveResult(int resultCode, Bundle resultData) {
        switch(resultCode)
        {
            case Constants.STATUS_RUNNING:
                resultText = "Searching...";
                _currentLocation.setText("Searching...");
                break;
            case Constants.STATUS_FINISHED:
                resultText = resultData.getString("location");
                _currentLocation.setText(resultText);
                _latitudeText.setText(String.valueOf(mLastLocation.getLatitude()));
                _longitudeText.setText(String.valueOf(mLastLocation.getLongitude()));
                break;
            case Constants.STATUS_ERROR:
                resultText = resultData.getString("message");
                _currentLocation.setText(resultText);
                break;
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
        _latitudeText.setText(String.valueOf(mLastLocation.getLatitude()));
        _longitudeText.setText(String.valueOf(mLastLocation.getLongitude()));
        mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
    }


    private void updateValuesFromBundle(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            // Update the value of mRequestingLocationUpdates from the Bundle, and
            // make sure that the Start Updates and Stop Updates buttons are
            // correctly enabled or disabled.
            if (savedInstanceState.keySet().contains(Constants.REQUESTING_LOCATION_UPDATES_KEY)) {
                mRequestingLocationUpdates = savedInstanceState.getBoolean(
                        Constants.REQUESTING_LOCATION_UPDATES_KEY);
            }

            // Update the value of mCurrentLocation from the Bundle and update the
            // UI to show the correct latitude and longitude.
            if (savedInstanceState.keySet().contains(Constants.LOCATION_KEY)) {
                // Since LOCATION_KEY was found in the Bundle, we can be sure that
                // mCurrentLocation is not null.
                mLastLocation = savedInstanceState.getParcelable(Constants.LOCATION_KEY);
                _latitudeText.setText(String.valueOf(mLastLocation.getLatitude()));
                _longitudeText.setText(String.valueOf(mLastLocation.getLongitude()));
            }

            // Update the value of mLastUpdateTime from the Bundle and update the UI.
            if (savedInstanceState.keySet().contains(Constants.LAST_UPDATED_TIME_STRING_KEY)) {
                mLastUpdateTime = savedInstanceState.getString(
                        Constants.LAST_UPDATED_TIME_STRING_KEY);
            }
        }
    }
}
