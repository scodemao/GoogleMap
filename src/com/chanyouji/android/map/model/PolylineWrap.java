package com.chanyouji.android.map.model;

import java.util.List;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

public class PolylineWrap {
    public String tag;
    public Polyline polyline;

    List<LatLng> points;
    boolean visible;
    float width;

    public PolylineWrap(String tag) {
        this.tag = tag;
    }

    public PolylineWrap(Polyline polyline) {
        this.polyline = polyline;
    }

    public void copyPolylineOptions(PolylineOptions options) {
        this.points = options.getPoints();
        this.visible = options.isVisible();
        this.width = options.getWidth();
    }
}
