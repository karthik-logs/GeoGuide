package com.karthyks.geoguide;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.text.DateFormat;
import java.util.Date;

public class LocationUpdateService extends Service implements GeoLocatorReceiver.Receiver, LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    /** indicates how to behave if the service is killed */
    int mStartMode;

    /** interface for clients that bind */
    IBinder mBinder;

    /** indicates whether onRebind should be used */
    boolean mAllowRebind;



    GeoLocatorReceiver mGeoLocatorReceiver;
    Intent mGeoLocatorIntent;

    String mResultText;
    String mLastUpdateTime;
    String mLatitude;
    String mLongitude;

    /** Called when the service is being created. */
    @Override
    public void onCreate() {
    }

    /** The service is starting, due to a call to startService() */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("Location Service", "Entered");
        mGeoLocatorReceiver = new GeoLocatorReceiver(new Handler());
        mGeoLocatorReceiver.setReceiver(this);
        buildGoogleApiClient();
        mGoogleApiClient.connect();
        createLocationRequest();
        return mStartMode;

    }

    private void GeoLocatorIntent()
    {
        Log.d("GeoLocator", "Called");
        mGeoLocatorIntent = new Intent(this, GeoLocator.class);
        mGeoLocatorIntent.putExtra("latitude", mLastLocation.getLatitude());
        mGeoLocatorIntent.putExtra("longitude", mLastLocation.getLongitude());
        mGeoLocatorIntent.putExtra("receiver", mGeoLocatorReceiver);
        startService(mGeoLocatorIntent);

    }
    /** A client is binding to the service with bindService() */
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /** Called when all clients have unbound with unbindService() */
    @Override
    public boolean onUnbind(Intent intent) {

        return mAllowRebind;
    }

    /** Called when a client is binding to the service with bindService()*/
    @Override
    public void onRebind(Intent intent) {

    }

    /** Called when The service is no longer used and is being destroyed */
    @Override
    public void onDestroy() {
        stopLocationUpdates();
        if(mGoogleApiClient.isConnected())
            mGoogleApiClient.disconnect();
    }


    LocationRequest mLocationRequest;

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
        mLatitude = String.valueOf(mLastLocation.getLatitude());
        mLongitude = String.valueOf(mLastLocation.getLongitude());
        mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
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
                mResultText = "Searching...";
                break;
            case Constants.STATUS_FINISHED:
                mResultText = resultData.getString("location");
                mLatitude = String.valueOf(mLastLocation.getLatitude());
                mLongitude = String.valueOf(mLastLocation.getLongitude());
                broadcastResult();
                break;
            case Constants.STATUS_ERROR:
                mResultText = resultData.getString("message");
                break;
        }
    }

    public void broadcastResult() {
        Intent resultIntent = new Intent("LocationResult");
        resultIntent.putExtra("latitude", mLatitude);
        resultIntent.putExtra("longitude", mLongitude);
        resultIntent.putExtra("location", mResultText);
        resultIntent.putExtra("lastUpdateTime", mLastUpdateTime);
        LocalBroadcastManager.getInstance(this).sendBroadcast(resultIntent);
    }
    //region Google API client connection
    GoogleApiClient mGoogleApiClient;
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    Location mLastLocation;
    @Override
    public void onConnected(Bundle bundle) {
        Log.d("Google API Connection", "Connected");
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        GeoLocatorIntent();
        startLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d("Google API Connection", "Suspended");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d("Google API Connection", "Failed");
    }
    //endregion


}
