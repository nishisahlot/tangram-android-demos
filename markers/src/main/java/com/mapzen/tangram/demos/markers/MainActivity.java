package com.mapzen.tangram.demos.markers;

import android.graphics.PointF;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;

import com.mapzen.tangram.LngLat;
import com.mapzen.tangram.MapController;
import com.mapzen.tangram.MapView;
import com.mapzen.tangram.Marker;
import com.mapzen.tangram.TouchInput;
import com.mapzen.tangram.geometry.Polygon;
import com.mapzen.tangram.geometry.Polyline;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements MapView.OnMapReadyCallback {

    MapController map;
    MapView view;

    Marker pointMarker;
    Marker lineMarker;
    Marker polygonMarker;

    String pointStyle = "{ style: 'points', color: 'white', size: [50px, 50px], order: 2000, collide: false }";
    String lineStyle = "{ style: 'lines', color: '#06a6d4', width: 5px, order: 2000 }";
    String polygonStyle = "{ style: 'polygons', color: '#06a6d4', width: 5px, order: 2000 }";

    Marker current;

    Button clearButton;

    ArrayList<LngLat> taps = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        view = (MapView)findViewById(R.id.map);
        view.onCreate(savedInstanceState);
        view.getMapAsync(this, "bubble-wrap/bubble-wrap.yaml");

        clearButton = (Button)findViewById(R.id.clear_button);
    }

    @Override
    public void onMapReady(MapController mapController) {
        map = mapController;

        resetMarkers();

        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            resetMarkers();
            }
        });

        map.setTapResponder(new TouchInput.TapResponder() {
            @Override
            public boolean onSingleTapUp(float x, float y) {
                LngLat tap = map.screenPositionToLngLat(new PointF(x, y));
                taps.add(tap);
                if (current == pointMarker) {
                    pointMarker.setPoint(tap);
                    taps.clear();
                } else if (current == lineMarker && taps.size() >= 2) {
                    lineMarker.setPolyline(new Polyline(taps, null));
                    taps.remove(0);
                } else if (current == polygonMarker && taps.size() >= 3) {
                    ArrayList<List<LngLat>> polygon = new ArrayList<>();
                    polygon.add(taps);
                    polygonMarker.setPolygon(new Polygon(polygon, null));
                    taps.remove(0);
                }
                map.requestRender();
                return false;
            }

            @Override
            public boolean onSingleTapConfirmed(float x, float y) {
                return false;
            }
        });
    }

    void resetMarkers() {
        map.removeAllMarkers();
        pointMarker = null;
        lineMarker = null;
        polygonMarker = null;

        pointMarker = map.addMarker();
        pointMarker.setStyling(pointStyle);
        pointMarker.setDrawable(R.drawable.mapzen_logo);

        lineMarker = map.addMarker();
        lineMarker.setStyling(lineStyle);

        polygonMarker = map.addMarker();
        polygonMarker.setStyling(polygonStyle);
    }

    public void onRadioButtonClicked(View view) {

        boolean checked = ((RadioButton) view).isChecked();

        if (!checked) {
            return;
        }

        taps.clear();

        switch(view.getId()) {
            case R.id.radio_points:
                current = pointMarker;
                break;
            case R.id.radio_lines:
                current = lineMarker;
                break;
            case R.id.radio_polygons:
                current = polygonMarker;
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        view.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        view.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        view.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        view.onLowMemory();
    }
}
