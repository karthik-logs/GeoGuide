package com.karthyks.geoguide;

import android.app.IntentService;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;

import java.io.IOException;
import java.util.List;

/**
 * Created by mtap on 9/30/2015.
 */
public class GeoLocator extends IntentService {
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * param name Used to name the worker thread, important only for debugging.
     */

    ResultReceiver geoLocatorReceiver;

    private static final String TAG = "GeoLocator";

    double mLatitude;
    double mLongitude;
    String resultLocation;
    public GeoLocator() {
        super(GeoLocator.class.getSimpleName());
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        geoLocatorReceiver = intent.getParcelableExtra("receiver");
        mLatitude = intent.getDoubleExtra("latitude", 0);
        mLongitude = intent.getDoubleExtra("longitude", 0);
        Log.d(TAG, "Latitude : " + mLatitude);
        Log.d(TAG, "Longitude : " + mLongitude);

        Geocoder gc = new Geocoder(this);
        try
        {
            List<Address> addressList = gc.getFromLocation(mLatitude, mLongitude, 1);
            if(addressList.size() > 0)
            {
                Address address = addressList.get(0);
                String location = null;
                if(address.getAddressLine(0) != null)
                {
                    location = address.getAddressLine(0);
                }
                if(address.getAddressLine(1) != null)
                {
                    location += address.getAddressLine(1);
                }
                if(address.getAddressLine(2) != null)
                {
                    location += address.getAddressLine(2);
                }
                resultLocation = location;
                Bundle bundle = new Bundle();
                bundle.putString("location", resultLocation);
                if(resultLocation == null)
                {
                    geoLocatorReceiver.send(Constants.STATUS_RUNNING, bundle);
                }
                else
                {
                    geoLocatorReceiver.send(Constants.STATUS_FINISHED, bundle);
                }
            }
        }
        catch (IOException e)
        {
            Bundle error = new Bundle();
            error.putString("message", e.toString());
            Log.d(TAG, e.getMessage());
            geoLocatorReceiver.send(Constants.STATUS_ERROR, error);
        }
    }
}
