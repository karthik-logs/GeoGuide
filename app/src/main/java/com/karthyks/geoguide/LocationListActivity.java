package com.karthyks.geoguide;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import java.util.List;

public class LocationListActivity extends AppCompatActivity {


  ListView mLocationListView;
  List<String> mLocationList;
  LocationListAdapter mListAdapter;
  private static final int MIN = 0, MAX = 10000;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_location_list);
    mLocationListView = (ListView) findViewById(R.id.locationList);
    mListAdapter = new LocationListAdapter(this);
    mLocationListView.setAdapter(mListAdapter);
  }

  private void initViews() {
    mLocationListView = (ListView) findViewById(R.id.locationList);
  }
  private void addItemsToList() {
    int randomVal = MIN + (int) (Math.random() * ((MAX - MIN) + 1));
    mLocationList.add(String.valueOf(randomVal));
    mListAdapter.notifyDataSetChanged();
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.menu_location_list, menu);
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
}
