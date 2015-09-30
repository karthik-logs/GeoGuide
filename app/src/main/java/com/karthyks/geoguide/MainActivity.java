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
import com.google.android.gms.location.LocationServices;


public class MainActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, GeoLocatorReceiver.Receiver
{

    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    TextView _currentLocation;

    Intent geoLocatorIntent;
    GeoLocatorReceiver geoLocatorReceiver;

    String resultText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        geoLocatorReceiver = new GeoLocatorReceiver(new Handler());
        geoLocatorReceiver.setReceiver(this);
        _currentLocation = (TextView) findViewById(R.id.curr_loc);
        buildGoogleApiClient();
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
        if(resultText != null)
        {
            _currentLocation.setText(resultText);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("lastLocation", resultText);
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

    @Override
    public void onConnected(Bundle bundle)
    {
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

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
                break;
            case Constants.STATUS_ERROR:
                resultText = resultData.getString("message");
                _currentLocation.setText(resultText);
                break;
        }
    }
}
