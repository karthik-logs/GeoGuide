package com.karthyks.geoguide;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.Binder;
import android.os.Bundle;
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

public class LocationUpdateService extends Service implements LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    /** indicates how to behave if the service is killed */
    int mStartMode;

    /** interface for clients that bind */
    IBinder mBinder;

    /** indicates whether onRebind should be used */
    boolean mAllowRebind;
    String mLastUpdateTime;
    String mLatitude;
    String mLongitude;

    Location mLastLocation;
    DummyBinder binder;

    GoogleApiClient mGoogleApiClient;
    /** Called when the service is being created. */
    @Override
    public void onCreate() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        binder = new DummyBinder();
    }

    /** The service is starting, due to a call to startService() */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mGoogleApiClient.connect();
        return mStartMode;
    }

    /** A client is binding to the service with bindService() */
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
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


    protected LocationRequest createLocationRequest() {
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        return mLocationRequest;
    }
    protected void startLocationUpdates() {
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, createLocationRequest(), this);
    }

    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient, this);
    }
    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
        mLatitude = String.valueOf(mLastLocation.getLatitude());
        mLongitude = String.valueOf(mLastLocation.getLongitude());
        mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
        broadcastResult();
    }


    public void broadcastResult() {
        Intent resultIntent = new Intent("LocationResult");
        resultIntent.putExtra("latitude", mLatitude);
        resultIntent.putExtra("longitude", mLongitude);
        resultIntent.putExtra("lastUpdateTime", mLastUpdateTime);
        LocalBroadcastManager.getInstance(this).sendBroadcast(resultIntent);
    }


    @Override
    public void onConnected(Bundle bundle) {
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);

        startLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }
    //endregion
    public class DummyBinder extends Binder {

        String error = "Not yet Updated";
        public DummyBinder() {

        }

        public void requestLocationUpdates() {
            if(mGoogleApiClient.isConnected())
                startLocationUpdates();
            else
            {
                mGoogleApiClient.connect();
            }
        }

        public Location LastKnownLocation()
        {
            return (mLastLocation != null) ? mLastLocation : null;
        }

        public String LastKnownLatitude()
        {
            return (mLatitude != null) ? mLatitude : error;
        }
        public String LastKnownLongitude()
        {
            return (mLongitude != null) ? mLongitude : error;
        }
    }
}
