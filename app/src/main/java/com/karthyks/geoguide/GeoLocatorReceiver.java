package com.karthyks.geoguide;

import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;

/**
 * Created by mTap on 9/30/2015.
 */
public class GeoLocatorReceiver extends ResultReceiver {

  private Receiver mReceiver;

  public GeoLocatorReceiver(Handler handler) {
    super(handler);
  }

  public void setReceiver(Receiver receiver) {
    mReceiver = receiver;
  }

  @Override
  protected void onReceiveResult(int resultCode, Bundle resultData) {
    super.onReceiveResult(resultCode, resultData);
    if (mReceiver != null) {
      mReceiver.onReceiveResult(resultCode, resultData);
    }
  }

  /**
   * Create a new ResultReceive to receive results.  Your
   * {@link #onReceiveResult} method will be called from the thread running
   * <var>handler</var> if given, or from an arbitrary thread if null.
   * <p/>
   * //@param handler
   */

  public interface Receiver {
    void onReceiveResult(int resultCode, Bundle resultData);
  }
}
