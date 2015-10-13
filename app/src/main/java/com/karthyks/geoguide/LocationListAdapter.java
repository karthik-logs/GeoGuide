package com.karthyks.geoguide;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by karthik on 9/10/15.
 *
 */
public class LocationListAdapter extends BaseAdapter {

  Activity mActivity;
  List<String> mLocationValues;
  LayoutInflater mLayoutInflater;
  DBHelper mDBHelper;

  public LocationListAdapter(Activity activity){
    mDBHelper = new DBHelper(activity.getBaseContext());
    mActivity = activity;
    mLocationValues = mDBHelper.getAllLocations();
    mLayoutInflater = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
  }
  @Override public int getCount() {
    return mLocationValues.size();
  }

  @Override public Object getItem(int position) {
    return mLocationValues.get(position);
  }

  @Override public long getItemId(int position) {
    return 0;
  }

  @Override public View getView(int position, View convertView, ViewGroup parent) {
    View v = convertView;
    LocationListViewHolder viewHolder;
    if (convertView == null) {
      LayoutInflater li = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
      v = li.inflate(R.layout.location_list_layout, null);
      viewHolder = new LocationListViewHolder(v, position, mActivity.getBaseContext
          ());
      v.setTag(viewHolder);
    } else {
      viewHolder = (LocationListViewHolder) v.getTag();
    }
    viewHolder.mTextItem.setText(mDBHelper.getLocationProperty(position, mDBHelper.getAllLocationProperties()).getIndex()
            + " " + mLocationValues.get(position));
    return v;
  }
}
class LocationListViewHolder {
  public TextView mTextItem;
  public List<LocationProperty> mLocationProperties;
  public Context mContext;
  DBHelper mDBHelper;
  public LocationListViewHolder(View base, final int pos, Context context) {
    mContext = context;
    mTextItem = (TextView) base.findViewById(R.id.listTextView);
    mDBHelper = new DBHelper(mContext);
    mLocationProperties = mDBHelper.getAllLocationProperties();
    mTextItem.setClickable(true);
    mTextItem.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        if (v.getId() == mTextItem.getId()) {
          goToMap(mDBHelper.getLocationProperty(pos,
                  (ArrayList<LocationProperty>) mLocationProperties).getLatitude(),
              mDBHelper.getLocationProperty(pos,
                  (ArrayList<LocationProperty>) mLocationProperties).getLongitude());
        }
      }
    });
    mTextItem.setLongClickable(true);
    mTextItem.setOnLongClickListener(new View.OnLongClickListener() {
      @Override public boolean onLongClick(View v) {
        if (v.getId() == mTextItem.getId()) {
          String travelledTime = mDBHelper.getLocationProperty(pos,
              (ArrayList<LocationProperty>) mLocationProperties).getTravelledTime();

          Toast.makeText(mContext,"Last Travelled : " + travelledTime, Toast.LENGTH_LONG).show();
        }
        return true;
      }
    });
  }

  public void goToMap(String lat, String lon){
    Intent mapIntent = new Intent(mContext, MapsActivity.class);
    mapIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    mapIntent.putExtra("Latitude", Double.parseDouble(lat));
    mapIntent.putExtra("Longitude", Double.parseDouble(lon));
    mContext.startActivity(mapIntent);
  }
}
