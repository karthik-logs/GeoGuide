package com.karthyks.geoguide;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.LocalBroadcastManager;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

  Intent mLocationServiceIntent;
  Marker mMarker;
  Polyline mLine;
  private GoogleMap mMap; // Might be null if Google Play services APK is not available.
  private double mLatitude;
  private double mLongitude;
  private GetLocation.LocationReceiver p = new GetLocation.LocationReceiver() {
    @Override
    public void onDecodeLocation(String location) {
      setUpMap();
    }
  };
  private BroadcastReceiver locationUpdates = new BroadcastReceiver() {
    @Override
    public void onReceive(Context context, Intent intent) {
      Double lat = Double.parseDouble(intent.getStringExtra("latitude"));
      Double lon = Double.parseDouble(intent.getStringExtra("longitude"));
      mLatitude = lat;
      mLongitude = lon;
      GetLocation getLocation = new GetLocation();
      getLocation.DecodeLocation(getApplicationContext(), mLatitude, mLongitude, p);
    }
  };

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Intent intent = getIntent();
    mLatitude = intent.getDoubleExtra("Latitude", 0);
    mLongitude = intent.getDoubleExtra("Longitude", 0);
    setContentView(R.layout.activity_maps);
    mLocationServiceIntent = new Intent(this, LocationUpdateService.class);
    startService(mLocationServiceIntent);
    setUpMapIfNeeded();
  }

  @Override
  protected void onResume() {
    super.onResume();
    startService(mLocationServiceIntent);
    LocalBroadcastManager.getInstance(this).registerReceiver(locationUpdates, new IntentFilter("LocationResult"));
    setUpMapIfNeeded();
  }

  @Override
  protected void onPause() {
    super.onPause();
    stopService(mLocationServiceIntent);
    LocalBroadcastManager.getInstance(this).unregisterReceiver(locationUpdates);
  }

  @Override
  protected void onStop() {
    super.onStop();
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
  }

  /**
   * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
   * installed) and the map has not already been instantiated.. This will ensure that we only ever
   * call {@link #setUpMap()} once when {@link #mMap} is not null.
   * <p/>
   * If it isn't installed {@link SupportMapFragment} (and
   * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
   * install/update the Google Play services APK on their device.
   * <p/>
   * A user can return to this FragmentActivity after following the prompt and correctly
   * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
   * have been completely destroyed during this process (it is likely that it would only be
   * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
   * method in {@link #onResume()} to guarantee that it will be called.
   */
  private void setUpMapIfNeeded() {
    // Do a null check to confirm that we have not already instantiated the map.
    if (mMap == null) {
      // Try to obtain the map from the SupportMapFragment.
      mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
          .getMap();
      SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
          .findFragmentById(R.id.map);
      mapFragment.getMapAsync(this);
      // Check if we were successful in obtaining the map.
      if (mMap != null) {
        setUpMap();
      }
    }


  }

  /**
   * This is where we can add markers or lines, add listeners or move the camera. In this case, we
   * just add a marker near Africa.
   * <p/>
   * This should only be called once and when we are sure that {@link #mMap} is not null.
   */
  private void setUpMap() {
    if (mMarker == null)
      mMarker = mMap.addMarker(new MarkerOptions().position(new LatLng(mLatitude, mLongitude)).title("You are here!"));
    else {
      mMarker.setPosition(new LatLng(mLatitude, mLongitude));
    }
    if (mLine == null) {
      mLine = mMap.addPolyline(new PolylineOptions()
              .add(new LatLng(mLatitude, mLongitude), new LatLng(mLatitude, mLongitude))
              .width(5)
              .color(Color.BLUE)
      );
    } else {
      List<LatLng> wayPoints = mLine.getPoints();
      wayPoints.add(new LatLng(mLatitude, mLongitude));
      mLine.setPoints(wayPoints);
    }
    mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(mLatitude, mLongitude)));
    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
        new LatLng(mLatitude, mLongitude), 13));

    CameraPosition cameraPosition = new CameraPosition.Builder()
        .target(new LatLng(mLatitude, mLongitude))      // Sets the center of the map to location user
        .zoom(17)                   // Sets the zoom
        .bearing(90)                // Sets the orientation of the camera to east
        .tilt(40)                   // Sets the tilt of the camera to 30 degrees
        .build();                   // Creates a CameraPosition from the builder
    mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
  }

  @Override
  public void onMapReady(GoogleMap googleMap) {
    setUpMap();
  }
}
