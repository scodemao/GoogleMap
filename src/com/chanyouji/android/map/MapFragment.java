package com.chanyouji.android.map;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.umeng.analytics.MobclickAgent;

public class MapFragment extends Fragment {
	private static final String TAG = "MapFragment";
	Map map;
	WebMapFragment wmap;
	private boolean isNativeMap = false;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_map, null);
		// Try to obtain the map from the SupportMapFragment.

		FragmentManager fm = getActivity().getSupportFragmentManager();
		NativeMapFragment nf = (NativeMapFragment) fm.findFragmentById(R.id.native_map);
		wmap = ((WebMapFragment) fm.findFragmentById(R.id.web_map));

		GoogleMap gmap = nf.getMap();
		
		String webPreferred = MobclickAgent.getConfigParams(getActivity(), "web_map_preferred");

		if (null == gmap || "true".equalsIgnoreCase(webPreferred)) {
			Log.i(TAG, "no play service is available, user web view");
			// WebMapFragment webFragment = new WebMapFragment();
			//
			// getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.map_container,
			// webFragment)
			// .commit();
			fm.beginTransaction().hide(nf).commit();
			map = wmap;
		} else {
			fm.beginTransaction().hide(wmap).commit();
			map = (NativeMapFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.native_map);
			try {
				MapsInitializer.initialize(getActivity());
			} catch (GooglePlayServicesNotAvailableException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			setNativeMap(true);
		}
		map.setCameraZoomLevel(8);

		return view;
	}

	public Map getMap() {
		return map;
	}

	public boolean isNativeMap() {
		return isNativeMap;
	}

	public void setNativeMap(boolean isNativeMap) {
		this.isNativeMap = isNativeMap;
	}

}
