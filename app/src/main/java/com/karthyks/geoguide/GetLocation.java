package com.karthyks.geoguide;

import android.content.Context;
import android.content.Intent;

import android.location.Location;
import android.os.Bundle;
import android.os.Handler;


/**
 * Created by karthy07 on 10/3/2015.
 */
public class  GetLocation implements  GeoLocatorReceiver.Receiver{

    GeoLocatorReceiver mGeoLocatorReceiver;
    Intent mGeoLocatorIntent;

    String mResultText;
    String mLatitude;
    String mLongitude;


    Location mLastLocation;

    LocationReceiver mReceiverCallBack;
    private static final String TAG = "GeoLocator";


    public GetLocation()
    {
        mGeoLocatorReceiver = new GeoLocatorReceiver(new Handler());
        mGeoLocatorReceiver.setReceiver(this);
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
                mReceiverCallBack.onDecodeLocation(mResultText);
                break;
            case Constants.STATUS_ERROR:
                mResultText = resultData.getString("message");
                break;
        }
    }


    public interface LocationReceiver
    {
        void onDecodeLocation(String location);
    }

    public void DecodeLocation(Context context, double lat, double lng, LocationReceiver callBack)
    {
        mGeoLocatorIntent = new Intent(context, GeoLocator.class);
        mGeoLocatorIntent.putExtra("latitude", lat);
        mGeoLocatorIntent.putExtra("longitude", lng);
        mGeoLocatorIntent.putExtra("receiver", mGeoLocatorReceiver);
        context.startService(mGeoLocatorIntent);
        mReceiverCallBack = callBack;
    }
}
