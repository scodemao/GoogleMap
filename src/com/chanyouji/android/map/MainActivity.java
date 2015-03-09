package com.chanyouji.android.map;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;

import com.chanyouji.android.map.model.MarkerOptionsWrap;
import com.chanyouji.android.map.model.MarkerWrap;
import com.chanyouji.android.map.model.OnMarkerWrapClickListener;
import com.chanyouji.android.map.model.OnMarkerWrapDragListener;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

public class MainActivity extends FragmentActivity {

    Map map;
    MarkerWrap mw;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        MapFragment mf = (MapFragment) getSupportFragmentManager().findFragmentById(R.id.chanyouji_map);

        map = mf.getMap();

        map.setOnMapClickListener(new OnMapClickListener() {

            @Override
            public void onMapClick(LatLng arg0) {
                MarkerOptions options = new MarkerOptions();
                options.flat(true);
                options.visible(true);
                options.draggable(true);
                options.title("Test Marker");
                options.position(arg0);
                map.addMarker(new MarkerOptionsWrap(options));
            }
        });

        map.setOnMarkerClickListener(new OnMarkerWrapClickListener() {

            @Override
            public boolean onMarkerClick(Marker arg0) {
                System.out.println(arg0.getPosition());
                return false;
            }

            @Override
            public void onMarkerWrapClick(MarkerWrap mw) {
                System.out.println(mw.getPosition());
            }
        });

     

        map.setOnMarkerDragListener(new OnMarkerWrapDragListener() {

            @Override
            public void onMarkerDragStart(Marker arg0) {
                System.out.println(arg0.getPosition());
            }

            @Override
            public void onMarkerDragEnd(Marker arg0) {
                System.out.println(arg0.getPosition());
            }

            @Override
            public void onMarkerDrag(Marker arg0) {
                System.out.println(arg0.getPosition());
            }

            @Override
            public void onMarkerWrapDragStart(MarkerWrap mw) {
                System.out.println(mw.getPosition());
            }

            @Override
            public void onMarkerWrapDragEnd(MarkerWrap mw) {
                System.out.println(mw.getPosition());
            }

            @Override
            public void onMarkerWrapDrag(MarkerWrap mw) {
                System.out.println(mw.getPosition());
            }
        });

        findViewById(R.id.test).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (null == mw) {
                    LatLng latLng = new LatLng(-34.397, 150.644);
                    MarkerOptions options = new MarkerOptions();
                    options.flat(true);
                    options.visible(true);
                    options.draggable(true);
                    options.title("Test Marker");
                    options.position(latLng);
                    mw = map.addMarker(new MarkerOptionsWrap(options));
                } else {
//                    map.setMarkerPosition(mw, new LatLng(-30.397, 140.644));
                    
                    PolylineOptions plo = new PolylineOptions();
                    plo.color(getResources().getColor(android.R.color.darker_gray));
                    plo.width(2);
                    plo.visible(true);
                    plo.add(new LatLng(-34.397, 150.644));
                    plo.add(new LatLng(-30.397, 140.644));


                    plo.add(new LatLng(-33.397, 150.644));
                    plo.add(new LatLng(-32.397, 145.644));
                    map.addPolyline(plo);
                }
            }
        });



    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

}
