package com.chanyouji.android.map;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

/**
 * Location Utils.
 * 
 * @author leo
 * 
 */
public class LocationUtil {
	private Location mLocation;

	private LocationManager mLocationManager;

	private Context mContext;

	private LocationListener mLocationListener = null;

	LocationReceiver mReceiver;

	public LocationUtil(Context context, LocationReceiver receiver) {
		this.mContext = context;
		mLocationManager = (LocationManager) context
				.getSystemService(Context.LOCATION_SERVICE);
		mLocationListener = new MyLocationListner();
		mReceiver = receiver;
	}

	public boolean isLocationServiceEnable() {
		if (mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
				|| mLocationManager
						.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
			return true;
		return false;
	}

	public Location getLastKnownLocation() {

		Location gps = mLocationManager
				.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		Location network = mLocationManager
				.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

		Location best = network;
		if (null != gps && isBetterLocation(gps, network)) {
			best = gps;
		}

		return best;
	}

	public void getLocation() {
		mLocation = mLocationManager
				.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		if (null == mLocation) {
			mLocation = mLocationManager
					.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
		}
		if (!isLocationServiceEnable()) {
			if (null != mReceiver) {
				if (null != mLocation) {// use last location
					mReceiver.onReceive(mLocation);
				} else {
					mReceiver.onLocationServiceDisable();
				}
			}
			return;
		}

		if (mLocationManager
				.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
			mLocationManager.requestLocationUpdates(
					LocationManager.NETWORK_PROVIDER, 1000 * 3, 0,
					mLocationListener);
		}
		if (mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			mLocationManager.requestLocationUpdates(
					LocationManager.GPS_PROVIDER, 1000 * 3, 0,
					mLocationListener);
		}

		Handler handler = new Handler();
		handler.postDelayed(new Runnable() {

			@Override
			public void run() {
				mLocationManager.removeUpdates(mLocationListener);
			}
		}, 1000 * 60 );
	}

	public static interface LocationReceiver {
		public void onReceive(Location location);

		public void onLocationServiceDisable();
	}

	private class MyLocationListner implements LocationListener {

		public void onLocationChanged(Location newLocation) {
			if (null != mLocation) {
				if (isBetterLocation(newLocation, mLocation)) {
					Log.v("GPSTEST", "It's a better location");
					mLocation = newLocation;
				} else {
					Log.v("GPSTEST", "Not very good!");
				}
			} else {
				Log.v("GPSTEST", "It's first location");
				mLocation = newLocation;
			}
			mLocationManager.removeUpdates(this);
			if (null != mReceiver) {
				mReceiver.onReceive(mLocation);
			}
		}

		public void onStatusChanged(String provider, int status, Bundle extras) {
		}

		public void onProviderEnabled(String provider) {
		}

		public void onProviderDisabled(String provider) {
		}
	};

	// This method is from android dev guide.
	private static final int CHECK_INTERVAL = 1000 * 30;

	protected boolean isBetterLocation(Location location,
			Location currentBestLocation) {
		if (null == currentBestLocation) {
			// A new location is always better than no location
			return true;
		}

		// Check whether the new location fix is newer or older
		long timeDelta = location.getTime() - currentBestLocation.getTime();
		boolean isSignificantlyNewer = timeDelta > CHECK_INTERVAL;
		boolean isSignificantlyOlder = timeDelta < -CHECK_INTERVAL;
		boolean isNewer = timeDelta > 0;

		// If it's been more than two minutes since the current
		// location,
		// use the new location
		// because the user has likely moved
		if (isSignificantlyNewer) {
			return true;
			// If the new location is more than two minutes older,
			// it must
			// be worse
		} else if (isSignificantlyOlder) {
			return false;
		}

		// Check whether the new location fix is more or less accurate
		int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation
				.getAccuracy());
		boolean isLessAccurate = accuracyDelta > 0;
		boolean isMoreAccurate = accuracyDelta < 0;
		boolean isSignificantlyLessAccurate = accuracyDelta > 200;

		// Check if the old and new location are from the same provider
		boolean isFromSameProvider = isSameProvider(location.getProvider(),
				currentBestLocation.getProvider());

		// Determine location quality using a combination of timeliness
		// and
		// accuracy
		if (isMoreAccurate) {
			return true;
		} else if (isNewer && !isLessAccurate) {
			return true;
		} else if (isNewer && !isSignificantlyLessAccurate
				&& isFromSameProvider) {
			return true;
		}
		return false;
	}

	/** Checks whether two providers are the same */
	private boolean isSameProvider(String provider1, String provider2) {
		if (provider1 == null) {
			return provider2 == null;
		}
		return provider1.equals(provider2);
	}

}
