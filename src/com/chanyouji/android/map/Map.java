package com.chanyouji.android.map;

import java.util.List;

import android.graphics.Bitmap;
import android.location.Location;
import android.widget.FrameLayout.LayoutParams;

import com.chanyouji.android.map.model.MarkerOptionsWrap;
import com.chanyouji.android.map.model.MarkerWrap;
import com.chanyouji.android.map.model.OnInfoWindowWrapClickListener;
import com.chanyouji.android.map.model.OnMarkerWrapClickListener;
import com.chanyouji.android.map.model.OnMarkerWrapDragListener;
import com.chanyouji.android.map.model.PolylineWrap;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.Projection;
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

public interface Map {

	public void setLayoutParams(LayoutParams lp);

	public boolean isMapNative();

	// for marker
	public MarkerWrap addMarker(MarkerOptionsWrap options);

	public void removeMarker(MarkerWrap mw);

	public void setMarkerPosition(MarkerWrap mw, LatLng position);
	
	public void setMarkerTitle(MarkerWrap mw, String title);

	public void setOnMarkerClickListener(OnMarkerWrapClickListener listener);

	public void setOnMarkerDragListener(OnMarkerWrapDragListener listener);

	public void setOnInfoWindowClickListener(OnInfoWindowWrapClickListener listener);

	public void showInfoWindow(MarkerWrap wm);

	public MarkerWrap getMarkerWrap(Marker marker);

	// set polyline
	public PolylineWrap addPolyline(PolylineOptions options);

	public void removePolyline(PolylineWrap pw);

	public void setPolylinePoints(PolylineWrap pw, List<LatLng> points);

	public void setPolylineVisible(PolylineWrap pw, boolean flag);

	// for camera
	public void moveCameraPosition(LatLng position);

	public void moveCameraPosition(LatLng sw, LatLng ne, int level);

	public void moveCameraPosition(LatLngBounds bounds, int level);

	public void setCameraZoomLevel(float level);

	// for UISettings
	public void setZoomControlsEnabled(boolean enable);

	public void setMyLocationButtonEnabled(boolean enable);

	public Circle addCircle(CircleOptions options);

	public GroundOverlay addGroundOverlay(GroundOverlayOptions options);

	public Polygon addPolygon(PolygonOptions options);

	public TileOverlay addTileOverlay(TileOverlayOptions options);

	public void animateCamera(CameraUpdate update, int durationMs, GoogleMap.CancelableCallback callback);

	public void animateCamera(CameraUpdate update, GoogleMap.CancelableCallback callback);

	public void animateCamera(CameraUpdate update);

	public void clear();

	public CameraPosition getCameraPosition();

	public int getMapType();

	public float getMaxZoomLevel();

	public float getMinZoomLevel();

	public Location getMyLocation();

	public Projection getProjection();

	public UiSettings getUiSettings();

	public boolean isBuildingsEnabled();

	public boolean isIndoorEnabled();

	public boolean isMyLocationEnabled();

	public boolean isTrafficEnabled();

	public void setBuildingsEnabled(boolean enabled);

	public boolean setIndoorEnabled(boolean enabled);

	public void setLocationSource(LocationSource source);

	public void setMapType(int type);

	public void setMyLocationEnabled(boolean enabled);

	public void setOnCameraChangeListener(GoogleMap.OnCameraChangeListener listener);

	public void setOnMapClickListener(GoogleMap.OnMapClickListener listener);

	void setOnMapLoadedCallback(GoogleMap.OnMapLoadedCallback callback);

	public void setOnMapLongClickListener(GoogleMap.OnMapLongClickListener listener);

	public void setOnMyLocationButtonClickListener(GoogleMap.OnMyLocationButtonClickListener listener);

	public void setOnMyLocationChangeListener(GoogleMap.OnMyLocationChangeListener listener);

	public void setPadding(int left, int top, int right, int bottom);

	public void setTrafficEnabled(boolean enabled);

	public void snapshot(GoogleMap.SnapshotReadyCallback callback, Bitmap bitmap);

	public void snapshot(GoogleMap.SnapshotReadyCallback callback);

	public void stopAnimation();
	

	public void setUseDefaultLocationFirst(boolean useDefault);
	
	public boolean isUseDefaultLocation();
}
