package com.chanyouji.android.map;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Bitmap;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout.LayoutParams;
import android.widget.TextView;

import com.chanyouji.android.map.model.InfoWindowWrapAdapter;
import com.chanyouji.android.map.model.MarkerOptionsWrap;
import com.chanyouji.android.map.model.MarkerWrap;
import com.chanyouji.android.map.model.OnInfoWindowWrapClickListener;
import com.chanyouji.android.map.model.OnMarkerWrapClickListener;
import com.chanyouji.android.map.model.OnMarkerWrapDragListener;
import com.chanyouji.android.map.model.PolylineWrap;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap.CancelableCallback;
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapLoadedCallback;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener;
import com.google.android.gms.maps.GoogleMap.OnMyLocationChangeListener;
import com.google.android.gms.maps.GoogleMap.SnapshotReadyCallback;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.GroundOverlay;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;

public class NativeMapFragment extends SupportMapFragment implements Map {
	List<MarkerWrap> markers = new ArrayList<MarkerWrap>();

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = super.onCreateView(inflater, container, savedInstanceState);
		return v;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		if (null != getMap()) {
			getMap().setInfoWindowAdapter(new DefaultInfoWindowAdapter());
		}
	}

	@Override
	public void setLayoutParams(LayoutParams lp) {
		getView().setLayoutParams(lp);
	}

	@Override
	public boolean isMapNative() {
		return true;
	}

	@Override
	public MarkerWrap addMarker(MarkerOptionsWrap options) {
		if (null == getMap()) {
			return null;
		}
		MarkerWrap mw = new MarkerWrap(getMap().addMarker(options.getOptions()));
		markers.add(mw);
		return mw;
	}

	@Override
	public void removeMarker(MarkerWrap mw) {
		if (null != mw.getMarker()) {
			mw.getMarker().remove();
		}
		markers.remove(mw);
	}

	@Override
	public void setMarkerPosition(MarkerWrap mw, LatLng position) {
		if (null != mw.getMarker()) {
			mw.getMarker().setPosition(position);
		}
	}

	@Override
	public void setMarkerTitle(MarkerWrap mw, String title) {
		if (null != mw.getMarker()) {
			mw.getMarker().setTitle(title);
		}
	}

	@Override
	public void setOnInfoWindowClickListener(OnInfoWindowWrapClickListener listener) {
		if (null != getMap()) {
			getMap().setOnInfoWindowClickListener(listener);
		}
	}

	@Override
	public void showInfoWindow(MarkerWrap wm) {
		if (null != wm.getMarker()) {
			wm.getMarker().showInfoWindow();
		}
	}

	@Override
	public MarkerWrap getMarkerWrap(Marker marker) {
		if (null != marker) {
			for (MarkerWrap mw : markers) {
				if (marker.equals(mw.getMarker())) {
					return mw;
				}
			}
		}
		return null;
	}

	@Override
	public PolylineWrap addPolyline(PolylineOptions options) {
		if (null == getMap()) {
			return null;
		}
		return new PolylineWrap(getMap().addPolyline(options));
	}

	@Override
	public void setPolylinePoints(PolylineWrap pw, List<LatLng> points) {
		if (null != pw.polyline) {
			pw.polyline.setPoints(points);
		}
	}

	@Override
	public void removePolyline(PolylineWrap pw) {
		if (null != pw.polyline) {
			pw.polyline.remove();
		}
	}

	@Override
	public void setPolylineVisible(PolylineWrap pw, boolean flag) {
		if (null != pw.polyline) {
			pw.polyline.setVisible(flag);
		}
	}

	@Override
	public void moveCameraPosition(LatLng position) {
		if (null != getMap()) {
			getMap().moveCamera(CameraUpdateFactory.newLatLng(position));
		}
	}

	@Override
	public void moveCameraPosition(LatLng sw, LatLng ne, int level) {

		LatLngBounds bounds = new LatLngBounds(sw, ne);

		// Set the camera to the greatest possible zoom level that includes the
		// bounds
		moveCameraPosition(bounds, level);
	}

	@Override
	public void moveCameraPosition(LatLngBounds bounds, int level) {
		getMap().moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, level));
	}

	@Override
	public void setCameraZoomLevel(float level) {
		getMap().moveCamera(CameraUpdateFactory.zoomTo(level));
	}

	@Override
	public void setZoomControlsEnabled(boolean enable) {
		getMap().getUiSettings().setZoomControlsEnabled(enable);
	}

	@Override
	public void setMyLocationButtonEnabled(boolean enable) {
		getMap().getUiSettings().setMyLocationButtonEnabled(enable);
	}

	@Override
	public Circle addCircle(CircleOptions options) {
		return getMap().addCircle(options);
	}

	@Override
	public GroundOverlay addGroundOverlay(GroundOverlayOptions options) {
		return getMap().addGroundOverlay(options);
	}

	@Override
	public Polygon addPolygon(PolygonOptions options) {
		return getMap().addPolygon(options);
	}

	@Override
	public TileOverlay addTileOverlay(TileOverlayOptions options) {
		return getMap().addTileOverlay(options);
	}

	@Override
	public void animateCamera(CameraUpdate update, int durationMs, CancelableCallback callback) {
		getMap().animateCamera(update, durationMs, callback);
	}

	@Override
	public void animateCamera(CameraUpdate update, CancelableCallback callback) {
		getMap().animateCamera(update, callback);
	}

	@Override
	public void animateCamera(CameraUpdate update) {
		getMap().animateCamera(update);
	}

	@Override
	public void clear() {
		if (null != getMap()) {
			getMap().clear();
		}
	}

	@Override
	public CameraPosition getCameraPosition() {
		return getMap().getCameraPosition();
	}

	@Override
	public int getMapType() {
		return getMap().getMapType();
	}

	@Override
	public float getMaxZoomLevel() {
		return getMap().getMaxZoomLevel();
	}

	@Override
	public float getMinZoomLevel() {
		return getMap().getMinZoomLevel();
	}

	@Override
	public Location getMyLocation() {
		return getMap().getMyLocation();
	}

	@Override
	public Projection getProjection() {
		return getMap().getProjection();
	}

	@Override
	public UiSettings getUiSettings() {
		return getMap().getUiSettings();
	}

	@Override
	public boolean isBuildingsEnabled() {
		return getMap().isBuildingsEnabled();
	}

	@Override
	public boolean isIndoorEnabled() {
		return getMap().isIndoorEnabled();
	}

	@Override
	public boolean isMyLocationEnabled() {
		return getMap().isMyLocationEnabled();
	}

	@Override
	public boolean isTrafficEnabled() {
		return getMap().isTrafficEnabled();
	}

	@Override
	public void setBuildingsEnabled(boolean enabled) {
		getMap().setBuildingsEnabled(enabled);
	}

	@Override
	public boolean setIndoorEnabled(boolean enabled) {
		return getMap().setIndoorEnabled(enabled);
	}

	@Override
	public void setLocationSource(LocationSource source) {
		getMap().setLocationSource(source);
	}

	@Override
	public void setMapType(int type) {
		getMap().setMapType(type);
	}

	@Override
	public void setMyLocationEnabled(boolean enabled) {
		getMap().setMyLocationEnabled(enabled);
	}

	@Override
	public void setOnCameraChangeListener(OnCameraChangeListener listener) {
		getMap().setOnCameraChangeListener(listener);
	}

	@Override
	public void setOnMapClickListener(OnMapClickListener listener) {
		getMap().setOnMapClickListener(listener);
	}

	@Override
	public void setOnMapLoadedCallback(final OnMapLoadedCallback callback) {
		if (null != getMap()) {
			getMap().setOnMapLoadedCallback(new OnMapLoadedCallback() {

				@Override
				public void onMapLoaded() {
					if (isUseDefaultLocation()) {
						Location latlng = new LocationUtil(getActivity(), new LocationUtil.LocationReceiver() {
							@Override
							public void onReceive(Location location) {
							}

							@Override
							public void onLocationServiceDisable() {
							}
						}).getLastKnownLocation();

						if (null != latlng) {
							moveCameraPosition(new LatLng(latlng.getLatitude(), latlng.getLongitude()));
							setCameraZoomLevel(15);
						} else {
							// default china
							LatLngBounds bounds = new LatLngBounds(new LatLng(18.25, 74.0), new LatLng(53.5, 134.5));
							CameraUpdate c = CameraUpdateFactory.newLatLngBounds(bounds, 0);
							animateCamera(c);
						}
					}
					if (null != callback) {
						callback.onMapLoaded();
					}
				}
			});
		}
	}

	@Override
	public void setOnMapLongClickListener(OnMapLongClickListener listener) {
		getMap().setOnMapLongClickListener(listener);
	}

	@Override
	public void setOnMarkerClickListener(OnMarkerWrapClickListener listener) {
		getMap().setOnMarkerClickListener(listener);
	}

	@Override
	public void setOnMarkerDragListener(OnMarkerWrapDragListener listener) {
		getMap().setOnMarkerDragListener(listener);
	}

	@Override
	public void setOnMyLocationButtonClickListener(OnMyLocationButtonClickListener listener) {
		getMap().setOnMyLocationButtonClickListener(listener);
	}

	@Override
	public void setOnMyLocationChangeListener(OnMyLocationChangeListener listener) {
		getMap().setOnMyLocationChangeListener(listener);
	}

	@Override
	public void setPadding(int left, int top, int right, int bottom) {
		getMap().setPadding(left, top, right, bottom);
	}

	@Override
	public void setTrafficEnabled(boolean enabled) {
		getMap().setTrafficEnabled(enabled);
	}

	@Override
	public void snapshot(SnapshotReadyCallback callback, Bitmap bitmap) {
		getMap().snapshot(callback, bitmap);
	}

	@Override
	public void snapshot(SnapshotReadyCallback callback) {
		getMap().snapshot(callback);
	}

	@Override
	public void stopAnimation() {
		getMap().stopAnimation();
	}

	public class DefaultInfoWindowAdapter implements InfoWindowWrapAdapter {

		private final View mWindow;
		private final View mContents;

		DefaultInfoWindowAdapter() {
			mWindow = getActivity().getLayoutInflater().inflate(R.layout.custom_info_window, null);
			mContents = getActivity().getLayoutInflater().inflate(R.layout.custom_info_contents, null);
		}

		@Override
		public MarkerWrap getMarkerWrap(Marker marker) {
			for (MarkerWrap mw : markers) {
				if (marker.equals(mw.getMarker())) {
					return mw;
				}
			}
			return null;
		}

		@Override
		public View getInfoWindow(Marker marker) {
			render(marker, mWindow);
			return mWindow;
		}

		@Override
		public View getInfoContents(Marker marker) {
			render(marker, mContents);
			return mContents;
		}

		private void render(Marker marker, View view) {
			MarkerWrap mw = getMarkerWrap(marker);
			boolean clickable = null == mw ? true : mw.isInfoWindowClickable();

			String title = marker.getTitle();
			TextView titleUi = ((TextView) view.findViewById(R.id.title));
			if (title != null) {
				// Spannable string allows us to edit the formatting of the
				// text.
				// SpannableString titleText = new SpannableString(title);
				// titleText.setSpan(new ForegroundColorSpan(Color.RED), 0,
				// titleText.length(), 0);
				titleUi.setText(title);
			} else {
				titleUi.setText("");
			}

			View badgeIn = view.findViewById(R.id.badge_in);
			if (!clickable) {
				badgeIn.setVisibility(View.GONE);
			} else {
				badgeIn.setVisibility(View.VISIBLE);
			}
		}
	}

	boolean useDefaultLocation = true; // default true

	@Override
	public boolean isUseDefaultLocation() {
		return useDefaultLocation;
	}

	@Override
	public void setUseDefaultLocationFirst(boolean useDefault) {
		useDefaultLocation = useDefault;
	}

}
