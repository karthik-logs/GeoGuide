package com.karthyks.geoguide;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by karthik on 8/10/15.
 * db.execSQL(
 "create table contacts " +
 "(id integer primary key, name text,phone text,email text, street text,place text)"
 );
 */
public class DBHelper extends SQLiteOpenHelper {
  Context mContext;
  public DBHelper(Context context)
  {
    super(context, Constants.DATABASE_NAME, null, 1);
    mContext = context;
  }

  @Override public void onCreate(SQLiteDatabase db) {
    String CREATE_CONTACTS_TABLE = "CREATE TABLE " + Constants.LOCATIONS_TABLE_NAME + "(id INTEGER " +
        "PRIMARY KEY AUTOINCREMENT, " + Constants.LOCATIONS_ADDRESS + " TEXT, "
        + Constants.LOCATIONS_LATITUDE + " TEXT, " + Constants.LOCATIONS_LONGITUDE + " TEXT,"
        + Constants.LOCATIONS_TRAVELLED_DATE + " TEXT"+ ")";
    Log.i("SQL", CREATE_CONTACTS_TABLE);
    db.execSQL(CREATE_CONTACTS_TABLE);
  }

  @Override public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    db.execSQL("DROP TABLE IF EXISTS " + Constants.LOCATIONS_TABLE_NAME);
    onCreate(db);
  }

  public boolean insertIntoLocationsTable(String locationAddress, String locationLat,
                                 String locationLong,
                                 String locationTravelledTime){
    SQLiteDatabase database = this.getWritableDatabase();
    ContentValues contentValues = new ContentValues();
    contentValues.put(Constants.LOCATIONS_ADDRESS, locationAddress);
    contentValues.put(Constants.LOCATIONS_LATITUDE, locationLat);
    contentValues.put(Constants.LOCATIONS_LONGITUDE, locationLong);
    contentValues.put(Constants.LOCATIONS_TRAVELLED_DATE, locationTravelledTime);
    database.insert(Constants.LOCATIONS_TABLE_NAME, null, contentValues);
    return true;
  }

  public Cursor getData(int id){
    SQLiteDatabase db = this.getReadableDatabase();
    Cursor res =  db.rawQuery( "select * from contacts where id="+id+"", null );
    return res;
  }

  public int numberOfRows(){
    SQLiteDatabase db = this.getReadableDatabase();
    int numRows = (int) DatabaseUtils.queryNumEntries(db, Constants.LOCATIONS_TABLE_NAME);
    return numRows;
  }

  public Integer deleteContact (Integer id) {
    SQLiteDatabase db = this.getWritableDatabase();
    return db.delete(Constants.LOCATIONS_TABLE_NAME,
        "id = ? ",
        new String[] { Integer.toString(id) });
  }

  public ArrayList<String> getAllLocations() {
    ArrayList<String> array_list = new ArrayList<>();
    SQLiteDatabase db = this.getReadableDatabase();
    Cursor res =  db.rawQuery( "select * from " + Constants.LOCATIONS_TABLE_NAME, null );
    res.moveToFirst();

    while(res.isAfterLast() == false){
      array_list.add(res.getString(res.getColumnIndex(Constants.LOCATIONS_ADDRESS)));
      res.moveToNext();
    }
    return array_list;
  }

  public ArrayList<LocationProperty> getAllLocationProperties(){
    ArrayList<LocationProperty> locationProperties = new ArrayList<>();
    SQLiteDatabase db = this.getReadableDatabase();
    Cursor res =  db.rawQuery( "select * from " + Constants.LOCATIONS_TABLE_NAME, null );
    res.moveToFirst();
    while(res.isAfterLast() == false){
      String Loc = res.getString(res.getColumnIndex(Constants.LOCATIONS_ADDRESS));
      String Lat = res.getString(res.getColumnIndex(Constants.LOCATIONS_LATITUDE));
      String Lon = res.getString(res.getColumnIndex(Constants.LOCATIONS_LONGITUDE));
      String Travelled = res.getString(res.getColumnIndex(Constants.LOCATIONS_TRAVELLED_DATE));
      LocationProperty locationProperty = new LocationProperty(Loc, Lat, Lon, Travelled);
      locationProperties.add(locationProperty);
      res.moveToNext();
    }
    return locationProperties;
  }

  public LocationProperty getLocationProperty(String loc, ArrayList<LocationProperty> locationProperties){
    if(locationProperties.size() < 1)
      return  null;
    for(int i = 0; i < locationProperties.size(); i++){
      //Log.d("GetLocationProperty", "Location : " + loc);
      //Log.d("GetLocationProperty", "loc from locProp : " + locationProperties.get(i).getLocation());
      if(loc.equalsIgnoreCase(locationProperties.get(i).getLocation())){
        return locationProperties.get(i);
      }
    }
    return null;
  }
}
