package com.karthyks.geoguide;

/**
 * Created by karthik on 9/10/15.
 */
public class LocationProperty{
  String mLatitude;
  String mLongitude;
  String mLocation;
  String mTravelledTime;


  public String getLatitude(){
    return this.mLatitude;
  }
  public String getLongitude(){
    return this.mLongitude;
  }
  public String getLocation(){
    return this.mLocation;
  }
  public String getTravelledTime(){
    return this.mTravelledTime;
  }

  public LocationProperty(String Location, String Latitude, String Longitude, String travelledTime){
    mLocation = Location;
    mLatitude = Latitude;
    mLongitude = Longitude;
    mTravelledTime = travelledTime;
  }
}
