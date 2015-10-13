package com.karthyks.geoguide;

/**
 * Created by mtap on 9/30/2015.
 */
public class Constants {

  public static final int STATUS_RUNNING = 0;
  public static final int STATUS_FINISHED = 1;
  public static final int STATUS_ERROR = 2;

  public static final String LOCATION_KEY = "locationKey";
  public static final String REQUESTING_LOCATION_UPDATES_KEY = "requestLocationUpdatesKey";
  public static final String LAST_UPDATED_TIME_STRING_KEY = "lastUpdatedTimeStringKey";


  //Database Constants

  public static final String DATABASE_NAME = "VisitedLocations";
  public static final String LOCATIONS_TABLE_NAME = "locationsTable";
  public static final String LOCATIONS_ADDRESS = "locationAddress";
  public static final String LOCATIONS_LATITUDE = "locationLatitude";
  public static final String LOCATIONS_LONGITUDE = "locationLongitude";
  public static final String LOCATIONS_TRAVELLED_DATE = "locationTravelledTime";


  // ContentProvider Constants
  public static final String PACKAGE_NAME = "com.karthyks.geoguide";
}
