package com.chanyouji.android.map;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.ConsoleMessage;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;

import com.chanyouji.android.map.model.MarkerOptionsWrap;
import com.chanyouji.android.map.model.MarkerWrap;
import com.chanyouji.android.map.model.OnInfoWindowWrapClickListener;
import com.chanyouji.android.map.model.OnMarkerWrapClickListener;
import com.chanyouji.android.map.model.OnMarkerWrapDragListener;
import com.chanyouji.android.map.model.PolylineWrap;
import com.google.android.gms.maps.CameraUpdate;
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
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.GroundOverlay;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;

public class WebMapFragment extends Fragment implements Map {
	private static final String TAG = "WebMapFragment";
	private static final String Lat_Lng = "{'lat': %f , 'lng': %f }";
	private static final String Lat_Lng_Bounds = "{'sw': %s, 'ne': %s}";
	private static final String Marker_Options = "{'position': %s, 'draggable': %b, 'flat': %b, 'visible': %b, 'title': '%s', 'icon': '%s'}";
	private static final String Polyline_Options = "{'visible': %b, 'strokeWeight': %f, 'strokeColor': '%s', 'path': %s}";

	private static final String Info_Window_Options = "{'content':'%s'}";

	java.util.Map<String, MarkerWrap> markers = new HashMap<String, MarkerWrap>();
	java.util.Map<String, PolylineWrap> polylines = new HashMap<String, PolylineWrap>();

	WebView mWebView;

	OnInfoWindowWrapClickListener mInfoWidnowClickListener;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mWebView = new WebView(getActivity());
		mWebView.setBackgroundColor(getResources().getColor(R.color.web_map_bg));
		FrameLayout.LayoutParams p = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, 100);
		mWebView.setLayoutParams(p);
		mWebView.getSettings().setJavaScriptEnabled(true);
		mWebView.setWebChromeClient(new WebChromeClient() {
			public boolean onConsoleMessage(ConsoleMessage cm) {
				Log.d("ChanYouJi-GoogleMap", cm.message() + " -- From line " + cm.lineNumber() + " of " + cm.sourceId());
				return true;
			}

		});

		mWebView.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				if (url.startsWith("http://chanyouji.com/")) {
					if (mInfoWidnowClickListener != null) {

						try {
							URL u = new URL(url);
							String path = u.getPath();
							String markerId = path.substring(1);
							mInfoWidnowClickListener.onWebInfoWindowClicked(markers.get(markerId));
						} catch (MalformedURLException e) {
							e.printStackTrace();
						}
					}
					return true;
				}

				Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
				intent.addCategory(Intent.CATEGORY_DEFAULT).addCategory(Intent.CATEGORY_BROWSABLE);
				getActivity().startActivity(intent);
				return true;
			}
		});

		mWebView.addJavascriptInterface(new Object() {

			@JavascriptInterface
			public void onMapClick(final double lat, final double lng) {
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						hideInfoWindow();
						if (null != onMapClickListener) {
							onMapClickListener.onMapClick(new LatLng(lat, lng));
						}
						
					}
				});
			}

			@JavascriptInterface
			public void onMapLongClick(double lat, double lng) {
				if (null != onMapLongClickListener) {
					// onMapLongClickListener.onMapLongClick(new LatLng(lat,
					// lng));
				}

			}

			@JavascriptInterface
			public void onMarkerClick(final String tag) {
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						if (null != onMarkerClickListener) {
							if (markers.containsKey(tag)) {
								onMarkerClickListener.onMarkerWrapClick(markers.get(tag));
							}
						}
					}
				});
			}

			@JavascriptInterface
			public void onMarkerDragStart(final String tag, final double lat, final double lng) {
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						if (markers.containsKey(tag)) {
							MarkerWrap mw = markers.get(tag);
							LatLng position = new LatLng(lat, lng);
							mw.setPosition(position);
							if (null != onMarkerDragListener) {
								onMarkerDragListener.onMarkerWrapDragStart(mw);
							}

						}
					}
				});
			}

			@JavascriptInterface
			public void onMarkerDrag(final String tag, final double lat, final double lng) {
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						if (markers.containsKey(tag)) {
							MarkerWrap mw = markers.get(tag);
							LatLng position = new LatLng(lat, lng);
							mw.setPosition(position);

							if (null != onMarkerDragListener) {
								onMarkerDragListener.onMarkerWrapDrag(mw);
							}
						}
					}
				});

			}

			@JavascriptInterface
			public void onMarkerDragEnd(String tag, double lat, double lng) {
				if (markers.containsKey(tag)) {
					MarkerWrap mw = markers.get(tag);
					LatLng position = new LatLng(lat, lng);
					mw.setPosition(position);

					if (null != onMarkerDragListener) {
						onMarkerDragListener.onMarkerWrapDragEnd(mw);
					}
				}
			}

			@JavascriptInterface
			public void onMapLoadedCallback() {
				Log.i(TAG, "onMapLoadedCallback");
				if (isUseDefaultLocation()) {
					try {
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
							moveCameraPosition(bounds, 8);
						}
					} catch (Exception e) {
					}
				}

				if (null != onMapLoadedCallback) {
					onMapLoadedCallback.onMapLoaded();
				}
			}

		}, "JsInterface");

		// mWebView.loadUrl("file:///android_asset/map.html");
		mWebView.loadUrl("file:///android_res/raw/map.html");
		return mWebView;
	}

	@Override
	public void setLayoutParams(LayoutParams lp) {
		mWebView.setLayoutParams(lp);
	}

	@Override
	public boolean isMapNative() {
		return false;
	}

	@Override
	public MarkerWrap getMarkerWrap(Marker marker) {
		return null;
	}

	@Override
	public MarkerWrap addMarker(MarkerOptionsWrap options) {
		String tag = UUID.randomUUID().toString();
		MarkerWrap mw = new MarkerWrap(tag);
		mw.copyMarkerOptions(options);
		markers.put(tag, mw);
		runJavaScript("addMarker('" + tag + "'," + getMarkerOptions(options) + ")");
		return mw;
	}

	@Override
	public void removeMarker(MarkerWrap mw) {
		markers.remove(mw.getMarkerId());
		runJavaScript("removeMarker('" + mw.getMarkerId() + "')");
	}

	@Override
	public void setMarkerPosition(MarkerWrap mw, LatLng position) {
		mw.setPosition(position);
		String fn = "setMarkerPosition('" + mw.getMarkerId() + "', " + getPosition(position) + ")";
		runJavaScript(fn);
	}

	@Override
	public void setMarkerTitle(MarkerWrap mw, String title) {
		mw.setTitle(title);
		String fn = "setMarkerTitle('" + mw.getMarkerId() + "','" + title + "')";
		runJavaScript(fn);
	}

	public void setMarkerDraggable(MarkerWrap mw, boolean flag) {
		mw.setDraggable(flag);
		String fn = "setMarkerDraggable('" + mw.getMarkerId() + "'," + flag + ")";
		runJavaScript(fn);
	}

	public void setMarkerFlat(MarkerWrap mw, boolean flag) {
		mw.setFlat(flag);
		String fn = "setMarkerFlat('" + mw.getMarkerId() + "'," + flag + ")";
		runJavaScript(fn);
	}

	public void setMarkerVisible(MarkerWrap mw, boolean flag) {
		mw.setVisible(flag);
		String fn = "setMarkerVisible('" + mw.getMarkerId() + "'," + flag + ")";
		runJavaScript(fn);
	}

	private String getMarkerOptions(MarkerOptionsWrap mow) {
		MarkerOptions options = mow.getOptions();
		String position = getPosition(options.getPosition());
		String opt = String.format(Marker_Options, position, options.isDraggable(), options.isFlat(), options.isVisible(), options.getTitle(),
				mow.getWebIconPath());
		return opt;
	}

	private String getPosition(LatLng latLng) {
		return String.format(Lat_Lng, latLng.latitude, latLng.longitude);
	}

	@Override
	public void showInfoWindow(MarkerWrap wm) {
		if (wm != null && wm.getTitle() != null) {
			String title = wm.getTitle();
			String contentString = null;
			if (wm.isInfoWindowClickable()) { // http://chanyouji.com/attractions/%s?name=%s
				contentString = "<div id=\"content\" style=\"line-height:20px;height:20px;\"><a href=\"%s\" style=\"background:url(ic_trips_arrow.png) no-repeat right 2px;padding-right:20px;text-decoration:none;color: #333333;font-family: verdana, sans-serif;font-size: 18px;\">%s</a></div>";
				String url = String.format("http://chanyouji.com/%s", wm.getMarkerId());
				contentString = String.format(contentString, url, title);
			} else {
				contentString = "<div id=\"content\" style=\"color: #333333;font-family: verdana, sans-serif;font-size: 18px;\">%s</div>";
				contentString = String.format(contentString, title);
			}

			String infowindow = String.format(Info_Window_Options, contentString);

			runJavaScript("showInfoWindow('" + wm.getMarkerId() + "'," + infowindow + ")");

			// String title = wm.getTitle();
			// String[] ss = title.split(",");
			// if (ss != null) {
			// String id = ss[0];
			// title = ss[1];
			// String type = ss[2];
			// runJavaScript("showInfoWindow('" + id + "','" + title + "','"
			// + type + "'," + wm.getPosition().latitude + ","
			// + wm.getPosition().longitude + ")");
			// }
		}

	}

	@Override
	public void setMyLocationButtonEnabled(boolean enable) {

	}

	@Override
	public void setZoomControlsEnabled(boolean enable) {

	}

	@Override
	public Circle addCircle(CircleOptions options) {
		return null;
	}

	@Override
	public GroundOverlay addGroundOverlay(GroundOverlayOptions options) {
		return null;
	}

	@Override
	public Polygon addPolygon(PolygonOptions options) {
		return null;
	}

	@Override
	public PolylineWrap addPolyline(PolylineOptions options) {
		String tag = UUID.randomUUID().toString();
		PolylineWrap pl = new PolylineWrap(tag);
		pl.copyPolylineOptions(options);
		polylines.put(tag, pl);
		runJavaScript("addPolyline('" + tag + "'," + getPolylineOptions(options) + ")");
		return pl;
	}

	@Override
	public void setPolylinePoints(PolylineWrap pw, List<LatLng> points) {
		runJavaScript("setPolylinePoints('" + pw.tag + "', " + getPoints(points) + ")");
	}

	@Override
	public void removePolyline(PolylineWrap pw) {
		runJavaScript("removePolyline('" + pw.tag + "')");
	}

	@Override
	public void setPolylineVisible(PolylineWrap pw, boolean flag) {
		runJavaScript("setPolylineVisible('" + pw.tag + "', " + flag + ")");
	}

	private String getPoints(List<LatLng> points) {
		StringBuilder sb = new StringBuilder();
		sb.append("[");
		for (int i = 0; i < points.size(); i++) {
			sb.append(getPosition(points.get(i)));
			if (i != points.size() - 1) {
				sb.append(",");
			}

		}
		sb.append("]");

		return sb.toString();
	}

	private String getPolylineOptions(PolylineOptions options) {
		List<LatLng> points = options.getPoints();

		return String.format(Polyline_Options, options.isVisible(), options.getWidth(), Utils.colorToRgb(options.getColor()), getPoints(points));
	}

	@Override
	public void moveCameraPosition(LatLng position) {
		String latLng = String.format(Lat_Lng, position.latitude, position.longitude);
		String fn = "setCenter(" + latLng + ")";
		runJavaScript(fn);
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
		String sw = String.format(Lat_Lng, bounds.southwest.latitude, bounds.southwest.longitude);
		String ne = String.format(Lat_Lng, bounds.northeast.latitude, bounds.northeast.longitude);

		String latLngbounds = String.format(Lat_Lng_Bounds, sw, ne);
		String fn = "setBounds(" + latLngbounds + ")";
		runJavaScript(fn);
	}

	@Override
	public void setCameraZoomLevel(float level) {
		String fn = "setZoom(" + Math.round(level) + ")";
		runJavaScript(fn);
	}

	@Override
	public TileOverlay addTileOverlay(TileOverlayOptions options) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void animateCamera(CameraUpdate update, int durationMs, CancelableCallback callback) {
		// TODO Auto-generated method stub

	}

	@Override
	public void animateCamera(CameraUpdate update, CancelableCallback callback) {
		// TODO Auto-generated method stub

	}

	@Override
	public void animateCamera(CameraUpdate update) {
		// TODO Auto-generated method stub

	}

	@Override
	public void clear() {
		for (PolylineWrap line : polylines.values()) {
			runJavaScript("removePolyline('" + line.tag + "')");
		}
		polylines.clear();
		for (MarkerWrap mw : markers.values()) {
			runJavaScript("removeMarker('" + mw.getMarkerId() + "')");
		}
		markers.clear();
		hideInfoWindow();
	}
	
	
	private void hideInfoWindow(){
		runJavaScript("hideInfoWindow()");
	}

	@Override
	public CameraPosition getCameraPosition() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getMapType() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public float getMaxZoomLevel() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public float getMinZoomLevel() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Location getMyLocation() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Projection getProjection() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public UiSettings getUiSettings() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isBuildingsEnabled() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isIndoorEnabled() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isMyLocationEnabled() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isTrafficEnabled() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setBuildingsEnabled(boolean enabled) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean setIndoorEnabled(boolean enabled) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setLocationSource(LocationSource source) {

	}

	@Override
	public void setMapType(int type) {
	}

	@Override
	public void setMyLocationEnabled(boolean enabled) {
	}

	@Override
	public void setOnCameraChangeListener(OnCameraChangeListener listener) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setOnInfoWindowClickListener(OnInfoWindowWrapClickListener listener) {
		mInfoWidnowClickListener = listener;
	}

	OnMapClickListener onMapClickListener;

	@Override
	public void setOnMapClickListener(OnMapClickListener listener) {
		onMapClickListener = listener;
	}

	OnMapLoadedCallback onMapLoadedCallback;

	@Override
	public void setOnMapLoadedCallback(OnMapLoadedCallback callback) {
		onMapLoadedCallback = callback;
	}

	OnMapLongClickListener onMapLongClickListener;

	@Override
	public void setOnMapLongClickListener(OnMapLongClickListener listener) {
		onMapLongClickListener = listener;
	}

	OnMarkerWrapClickListener onMarkerClickListener;

	@Override
	public void setOnMarkerClickListener(OnMarkerWrapClickListener listener) {
		onMarkerClickListener = listener;
	}

	OnMarkerWrapDragListener onMarkerDragListener;

	@Override
	public void setOnMarkerDragListener(OnMarkerWrapDragListener listener) {
		onMarkerDragListener = listener;
	}

	@Override
	public void setOnMyLocationButtonClickListener(OnMyLocationButtonClickListener listener) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setOnMyLocationChangeListener(OnMyLocationChangeListener listener) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setPadding(int left, int top, int right, int bottom) {

	}

	@Override
	public void setTrafficEnabled(boolean enabled) {

	}

	@Override
	public void snapshot(SnapshotReadyCallback callback, Bitmap bitmap) {

	}

	@Override
	public void snapshot(SnapshotReadyCallback callback) {
		// TODO Auto-generated method stub

	}

	@Override
	public void stopAnimation() {
		// TODO Auto-generated method stub

	}

	Handler mHandler = new Handler();

	private void runJavaScript(String fn) {
		final String js = "javascript:" + fn;
		mHandler.post(new Runnable() {

			@Override
			public void run() {
				Log.i(TAG, js);
				mWebView.loadUrl(js);
			}
		});
	}

	private void runOnUiThread(Runnable r) {
		mHandler.post(r);
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
