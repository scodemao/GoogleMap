package com.chanyouji.android.map.model;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MarkerWrap {
	private String markerId;

	private Marker marker;

	private LatLng position;
	private String title;
	private boolean draggable;
	private boolean flat;
	private boolean visible;
	private boolean infoWindowClickable;

	private Object tag;

	public MarkerWrap() {
	}

	public MarkerWrap(Marker marker) {
		this.marker = marker;
	}

	public MarkerWrap(String markerId) {
		this.markerId = markerId;
	}

	public void copyMarkerOptions(MarkerOptionsWrap mow) {
		MarkerOptions options = mow.getOptions();
		this.position = options.getPosition();
		this.title = options.getTitle();
		this.draggable = options.isDraggable();
		this.flat = options.isFlat();
		this.visible = options.isVisible();
	}

	public String getMarkerId() {
		return markerId;
	}

	public void setMarkerId(String markerId) {
		this.markerId = markerId;
	}

	public Marker getMarker() {
		return marker;
	}

	public void setMarker(Marker marker) {
		this.marker = marker;
	}

	public LatLng getPosition() {
		if (null != marker) {
			return marker.getPosition();
		}
		return position;
	}

	public void setPosition(LatLng position) {
		if (null != marker) {
			marker.setPosition(position);
		}
		this.position = position;
	}

	public String getTitle() {
		if (null != marker) {
			return marker.getTitle();
		}
		return title;
	}

	public void setTitle(String title) {
		if (null != marker) {
			marker.setTitle(title);
		}
		this.title = title;
	}

	public boolean isDraggable() {
		if (null != marker) {
			return marker.isDraggable();
		}
		return draggable;
	}

	public void setDraggable(boolean draggable) {
		if (null != marker) {
			marker.setDraggable(draggable);
		}
		this.draggable = draggable;
	}

	public boolean isFlat() {
		if (null != marker) {
			return marker.isFlat();
		}
		return flat;
	}

	public void setFlat(boolean flat) {
		if (null != marker) {
			marker.setFlat(flat);
		}
		this.flat = flat;
	}

	public boolean isVisible() {
		if (null != marker) {
			return marker.isVisible();
		}
		return visible;
	}

	public void setVisible(boolean visible) {
		if (null != marker) {
			marker.setVisible(visible);
		}
		this.visible = visible;
	}

	public boolean isInfoWindowClickable() {
		return infoWindowClickable;
	}

	public void setInfoWindowClickable(boolean infoWindowClickable) {
		this.infoWindowClickable = infoWindowClickable;
	}

	public Object getTag() {
		return tag;
	}

	public void setTag(Object tag) {
		this.tag = tag;
	}

}
