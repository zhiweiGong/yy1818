package com.android.tools.Misc;

import android.content.Context;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationProvider;
import android.os.Bundle;
import android.util.Log;

import java.util.Iterator;

public class UtilLocManager {
    private static final String TAG = "UtilLocManager";

    private Context mContext;
    private android.location.LocationManager mLocationManager;
    private boolean mRecordLocation = false;

    private String strSatellites = "";

    private LocationListener[] mLocationListeners = new LocationListener[]{
            new LocationListener(android.location.LocationManager.GPS_PROVIDER),
            new LocationListener(android.location.LocationManager.NETWORK_PROVIDER)
    };

    private static UtilLocManager mUtilLocManager = null;
    public static synchronized UtilLocManager getInstance(){
        if(mUtilLocManager == null){
            mUtilLocManager = new UtilLocManager();
        }
        return mUtilLocManager;
    }

    public UtilLocManager() {
    }

    public Location getCurrentLocation() {
        if (!mRecordLocation) {
            Log.d(TAG, "mRecordLocation is null");
            return null;
        }
        // go in best to worst order
        for (int i = 0; i < mLocationListeners.length; i++) {
            Location l = mLocationListeners[i].current();
            //Log.d(TAG,"mLocationListeners.length:"+i);
            if (l != null)
                return l;
        }
        //Log.d(TAG, "No location received yet.");
        return null;
    }

    public void RecordLocation(Context context,boolean recordLocation) {
        mContext = context;

        if (mRecordLocation != recordLocation) {
            mRecordLocation = recordLocation;
            if (recordLocation) {
                startReceivingLocationUpdates();
            } else {
                stopReceivingLocationUpdates();
            }
        }
    }

    public String GetCurrentGpsStatus() {
        return strSatellites;
    }

    private final GpsStatus.Listener statusListener = new GpsStatus.Listener() {
        public void onGpsStatusChanged(int event) {
            GpsStatus mStatus = null;

            try {
                mStatus = mLocationManager.getGpsStatus(null);
            } catch (Exception ex) {
                Log.i(TAG, "fail to remove location listners, ignore", ex);
            }

            if (mStatus != null){
                if (event == GpsStatus.GPS_EVENT_SATELLITE_STATUS){
					int maxSatellites = mStatus.getMaxSatellites();
					Iterator<GpsSatellite> it = mStatus.getSatellites().iterator();
					int count = 0;
					strSatellites="";
		            while (it.hasNext() && count <= maxSatellites) {
		                GpsSatellite s = it.next();
		                count++;
						strSatellites += ","+String.format("%d",s.getPrn());
						strSatellites += ","+String.format("%.0f",s.getSnr());
						strSatellites += ","+String.format("%b",s.usedInFix());
		            }
                }
            }
        }  
    };  
	
    private void startReceivingLocationUpdates() {
        if (mLocationManager == null) {
        	Log.d(TAG,"LocationManager init");
            mLocationManager = (android.location.LocationManager)mContext.getSystemService(Context.LOCATION_SERVICE);
            try {
                mLocationManager.addGpsStatusListener(statusListener);
            } catch (Exception ex) {
                Log.i(TAG, "fail to remove location listners, ignore", ex);
            }
        }
        if (mLocationManager != null) {
            try {
                mLocationManager.requestLocationUpdates(
                        android.location.LocationManager.NETWORK_PROVIDER,
                        1000,
                        0F,
                        mLocationListeners[1]);
                Log.d(TAG,"LocationManager init NETWORK_PROVIDER");
            } catch (SecurityException ex) {
                Log.i(TAG, "fail to request location update, ignore", ex);
            } catch (IllegalArgumentException ex) {
                Log.d(TAG, "provider does not exist " + ex.getMessage());
            }
            try {
                mLocationManager.requestLocationUpdates(
                        android.location.LocationManager.GPS_PROVIDER,
                        1000,
                        0F,
                        mLocationListeners[0]);
                Log.d(TAG,"LocationManager init GPS_PROVIDER");
            } catch (SecurityException ex) {
                Log.i(TAG, "fail to request location update, ignore", ex);
            } catch (IllegalArgumentException ex) {
                Log.d(TAG, "provider does not exist " + ex.getMessage());
            }
            Log.d(TAG, "startReceivingLocationUpdates");
        }else{
        	Log.d(TAG, "mLocationManager is null");
        }
    }

    private void stopReceivingLocationUpdates() {
        if (mLocationManager != null) {
            for (int i = 0; i < mLocationListeners.length; i++) {
                try {
/*                    if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }*/
                    mLocationManager.removeUpdates(mLocationListeners[i]);
                } catch (Exception ex) {
                    Log.i(TAG, "fail to remove location listners, ignore", ex);
                }
            }
            Log.d(TAG, "stopReceivingLocationUpdates");
        }
    }

    private class LocationListener implements android.location.LocationListener {
        Location mLastLocation;
        boolean mValid = false;
        String mProvider;

        public LocationListener(String provider) {
            mProvider = provider;
            mLastLocation = new Location(mProvider);
        }

        @Override
        public void onLocationChanged(Location newLocation) {
		    //Log.d(TAG, "onLocationChanged.");
		    
            if (newLocation.getLatitude() == 0.0
                    && newLocation.getLongitude() == 0.0) {
                // Hack to filter out 0.0,0.0 locations
                return;
            }
                                   
            // If GPS is available before start camera, we won't get status
            // update so update GPS indicator when we receive data.
            if (!mValid) {
                Log.d(TAG, "Got first location.");
            }
            mLastLocation.set(newLocation);
            mValid = true;
        }
        
        @Override
        public void onProviderEnabled(String provider) {
        }

        @Override
        public void onProviderDisabled(String provider) {
            mValid = false;
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
			//Log.d(TAG, "... onStatusChanged.");
            switch(status) {
                case LocationProvider.OUT_OF_SERVICE:
                case LocationProvider.TEMPORARILY_UNAVAILABLE: {
                    mValid = false;
                    break;
                }
            }
        }

        public Location current() {
            return mValid ? mLastLocation : null;
        }
    }
}
