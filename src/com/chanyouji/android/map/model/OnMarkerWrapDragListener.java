package com.chanyouji.android.map.model;

import com.google.android.gms.maps.GoogleMap.OnMarkerDragListener;

public interface OnMarkerWrapDragListener extends OnMarkerDragListener {

    public void onMarkerWrapDragStart(MarkerWrap mw);

    public void onMarkerWrapDrag(MarkerWrap mw);

    public void onMarkerWrapDragEnd(MarkerWrap mw);
}
