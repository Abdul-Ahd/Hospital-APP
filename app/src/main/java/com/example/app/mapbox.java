package com.example.app;

import static android.view.View.GONE;
import static com.mapbox.maps.extension.style.expressions.dsl.generated.ExpressionDslKt.heatmapDensity;
import static com.mapbox.maps.extension.style.expressions.dsl.generated.ExpressionDslKt.literal;
import static com.mapbox.maps.extension.style.expressions.dsl.generated.ExpressionDslKt.rgba;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.gis.OnlineDB;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.Point;
import com.mapbox.maps.CameraOptions;
import com.mapbox.maps.MapView;
import com.mapbox.maps.MapboxMap;
import com.mapbox.maps.Style;
import com.mapbox.maps.extension.style.expressions.generated.Expression;
import com.mapbox.maps.extension.style.layers.LayerUtils;
import com.mapbox.maps.extension.style.layers.generated.HeatmapLayer;
import com.mapbox.maps.extension.style.layers.generated.SymbolLayer;
import com.mapbox.maps.extension.style.sources.SourceUtils;
import com.mapbox.maps.extension.style.sources.generated.GeoJsonSource;
import com.mapbox.maps.plugin.Plugin;
import com.mapbox.maps.plugin.annotation.AnnotationPlugin;
import com.mapbox.maps.plugin.annotation.AnnotationType;
import com.mapbox.maps.plugin.annotation.generated.CircleAnnotationManager;
import com.mapbox.maps.plugin.annotation.generated.PointAnnotation;
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationManager;
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions;


import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class mapbox extends AppCompatActivity {
    private MapView mapView;

    private static final String HEATMAP_SOURCE_ID = "heatmap-source";
    private static final String HEATMAP_LAYER_ID = "heatmap-layer";
    private static final String Disease_MARKER_ID = "marker-id";
    private static final String Hospital_MARKER_ID = "hospital-id";

    private boolean heatMap = false;
    private boolean marker =  false;
    private boolean hospmarker =  false;



    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mapbox);

        mapView = findViewById(R.id.mapView);
        ImageButton heatMapButton  = findViewById(R.id.heatBtn);
        ImageButton markerButton  = findViewById(R.id.markerBtn);
        ImageButton hospitalButton = findViewById(R.id.hospBtn);

        heatMapButton.setVisibility(View.VISIBLE);
//        hospitalButton.setVisibility(View.VISIBLE);

        heatMapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (heatMap) {
                    mapView.getMapboxMap().loadStyle(Style.LIGHT);
                    heatMap = false;
                } else {
                    addHeatmapData(mapView.getMapboxMap().getStyle());
                    heatMap = true;
                }
            }
        });
        markerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (marker) {
                    // Remove all point annotations (markers) from the map
                    mapView.getPlugin(Plugin.MAPBOX_ANNOTATION_PLUGIN_ID).cleanup();
                    marker = false;
                } else {
                    // Add markers to the map
                    addDiseaseMarkers(Objects.requireNonNull(mapView.getMapboxMap().getStyle()));

                    marker = true;
                }
            }
        });
        hospitalButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (hospmarker) {
                    // Remove all point annotations (markers) from the map
                    mapView.getPlugin(Plugin.MAPBOX_ANNOTATION_PLUGIN_ID).cleanup();
                    hospmarker = false;
                } else {
                    // Add markers to the map
                    AddHospitalMarkers(Objects.requireNonNull(mapView.getMapboxMap().getStyle()));
                    hospmarker = true;
                }
            }
        });


        mapView.getMapboxMap().loadStyle(Style.LIGHT, new Style.OnStyleLoaded() {
            @Override
            public void onStyleLoaded(@NonNull Style style) {
                // Set initial camera position
                mapView.getMapboxMap().setCamera(new CameraOptions.Builder()
                        .center(Point.fromLngLat(73.014092, 33.616980))
                        .zoom(5.0)
                        .bearing(0.0)
                        .pitch(25.0)
                        .build());
//                AddHospitalMarkers(style);
            }
        });
    }

    // Method to add heatmap data to the map
    private void addHeatmapData(@NonNull Style style) {
        // Replace this with your actual heatmap data

    getHeatmapPoints(new HeatmapDataListener() {
    @Override
    public void onDataLoaded(List<Point> Points) {
//        List<Point> heatmapPoints = loadHeatmapData();
        final List<Point> heatmapPoints = new ArrayList<>();
        heatmapPoints.addAll(Points);
        List<Feature> features = new ArrayList<>();
        for (Point point : heatmapPoints) {
            features.add(Feature.fromGeometry(point));
        }
        FeatureCollection featureCollection = FeatureCollection.fromFeatures(features);

        // Convert FeatureCollection to JSON string
        String geoJsonString = featureCollection.toJson();

        // Add GeoJsonSource to the map using the builder pattern
        GeoJsonSource geoJsonSource = new GeoJsonSource.Builder(HEATMAP_SOURCE_ID)
                .data(geoJsonString)
                .build();
        SourceUtils.addSource(style,geoJsonSource);

        // Add HeatmapLayer to the map
        HeatmapLayer heatmapLayer = new HeatmapLayer(HEATMAP_LAYER_ID, HEATMAP_SOURCE_ID)
                .heatmapColor(Expression.interpolate(
                        Expression.linear(),
                        heatmapDensity(),
                        literal(0),
                        rgba(33.0,102.0,172.0,0.0),
                        literal(0.2),
                        Expression.rgb(103.0,169.0,207.0),
                        literal(0.4),
                        Expression.rgb(209.0,229.0,240.0),
                        literal(0.6),
                        Expression.rgb(253.0,219.0,240.0),
                        literal(0.8),
                        Expression.rgb(239.0,138.0,98.0),
                        literal(1),
                        Expression.rgb(178.0,24.0,43.0)
                ));
        LayerUtils.addLayerAbove(style,heatmapLayer,"waterway-label");
    }

        @Override
        public void onError(String errorMessage) {
        }
    });
    }

        private void addDiseaseMarkers(@NonNull Style style) {


            getHeatmapPoints(new HeatmapDataListener() {

                @Override
                public void onDataLoaded(List<Point> heatmapPoints) {
                    List<Point> markerData = new ArrayList<>();
                    markerData.addAll(heatmapPoints);

                    AnnotationPlugin annotationPlugin = mapView.getPlugin(Plugin.MAPBOX_ANNOTATION_PLUGIN_ID);

                    assert annotationPlugin != null;
                    PointAnnotationManager pointAnnotationManager = (PointAnnotationManager)annotationPlugin.createAnnotationManager(AnnotationType.PointAnnotation, null);

                    Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.diseaseblueicon2);

                    style.addImage(Disease_MARKER_ID,bitmap);

                    for (Point point : heatmapPoints) {
                        PointAnnotationOptions pointAnnotationOptions = new PointAnnotationOptions()
                                .withPoint(point)
                                .withIconImage(Disease_MARKER_ID)
                                .withIconSize(0.07);
                        pointAnnotationManager.create(pointAnnotationOptions);
                    }
                }
                @Override
                public void onError(String errorMessage) {

                }
            });
        }

    // Method to get sample heatmap points (Replace with your actual data)
    private interface HeatmapDataListener {
        void onDataLoaded(List<Point> heatmapPoints);
        void onError(String errorMessage);
    }

    private void getHeatmapPoints(final HeatmapDataListener listener) {
        final List<Point> heatmapPoints = new ArrayList<>();
        // Sample heatmap points
        OnlineDB.getData("SELECT * FROM record", this, new OnlineDB.OnGetDataListener() {
            @Override
            @SuppressLint("range")
            public void onDataRetrieved(Cursor cursor) {
                if (cursor != null && cursor.moveToFirst()) {
                    do {

                        String lat = cursor.getString(cursor.getColumnIndex("lat"));
                        String lng = cursor.getString(cursor.getColumnIndex("long"));
                        double lt = Double.parseDouble(lat);
                        double lg = Double.parseDouble(lng);
                        heatmapPoints.add(Point.fromLngLat(lg, lt));
                    } while (cursor.moveToNext());
                    cursor.close(); // Close the cursor after use
                    listener.onDataLoaded(heatmapPoints); // Notify listener with the data
                } else {
                    // Handle case when cursor is empty
                    listener.onDataLoaded(heatmapPoints); // Notify listener with empty data
                }
            }

            @Override
            public void onError(String errorMessage) {
                // Handle error case
                listener.onError(errorMessage);
            }
        });
    }



    //hospital markers
    private void AddHospitalMarkers(@NonNull Style style){
        GetHospitalPoints(new HospitalPointsListener() {
            @Override
            public void onDataLoaded(List<Point> HospitalPoints) {
                List<Point> hosplitalMarkers = new ArrayList<>();
                hosplitalMarkers.addAll(HospitalPoints);

                AnnotationPlugin annotationPlugin = mapView.getPlugin(Plugin.MAPBOX_ANNOTATION_PLUGIN_ID);

                assert annotationPlugin != null;
                PointAnnotationManager pointAnnotationManager = (PointAnnotationManager)annotationPlugin.createAnnotationManager(AnnotationType.PointAnnotation, null);

                Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.hospitalicon);

                style.addImage(Hospital_MARKER_ID,bitmap);

                for (Point point: HospitalPoints){
                    PointAnnotationOptions pointAnnotationOptions = new PointAnnotationOptions()
                            .withPoint(point)
                            .withIconImage(Hospital_MARKER_ID)
                            .withIconSize(0.15);
                    pointAnnotationManager.create(pointAnnotationOptions);
                }
            }

            @Override
            public void onError(String errorMessage) {

            }
        });
    }
    //callback
    private interface HospitalPointsListener {
        void onDataLoaded(List<Point> HospitalPoints);
        void onError(String errorMessage);
    }

    private void GetHospitalPoints(final HospitalPointsListener listener){

        final List<Point> HospitalPoints = new ArrayList<>();
        OnlineDB.getData("SELECT * FROM hospital", this, new OnlineDB.OnGetDataListener() {
            @SuppressLint("range")
            @Override
            public void onDataRetrieved(Cursor cursor) {
                if (cursor != null && cursor.moveToFirst()){
                    do{
                        String Hlat = cursor.getString(cursor.getColumnIndex("Hlat"));
                        String Hlong = cursor.getString(cursor.getColumnIndex("Hlong"));
                        double HospLat = Double.parseDouble(Hlat);
                        double HospLong = Double.parseDouble(Hlong);
                        HospitalPoints.add(Point.fromLngLat(HospLong,HospLat));
                    }while (cursor.moveToNext());
                    cursor.close();
                    listener.onDataLoaded(HospitalPoints);
                }else{
                    listener.onDataLoaded(HospitalPoints);
                }
            }

            @Override
            public void onError(String errorMessage) {
                listener.onError(errorMessage);
            }
        });

    }


}
