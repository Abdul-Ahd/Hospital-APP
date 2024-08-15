//package com.example.gis;
//
//import android.annotation.SuppressLint;
//import android.app.DatePickerDialog;
//import android.content.DialogInterface;
//import android.content.Intent;
//import android.database.Cursor;
//import android.graphics.Bitmap;
//import android.graphics.Color;
//import android.graphics.drawable.BitmapDrawable;
//import android.graphics.drawable.Drawable;
//import android.os.Bundle;
//
//import androidx.activity.OnBackPressedCallback;
//import androidx.appcompat.app.AlertDialog;
//import androidx.appcompat.app.AppCompatActivity;
//
//
//import com.esri.arcgisruntime.concurrent.ListenableFuture;
//import com.esri.arcgisruntime.geometry.Point;
//import com.esri.arcgisruntime.geometry.SpatialReferences;
//import com.esri.arcgisruntime.mapping.view.DefaultMapViewOnTouchListener;
//import com.esri.arcgisruntime.mapping.view.Graphic;
//import com.esri.arcgisruntime.mapping.view.GraphicsOverlay;
//import com.esri.arcgisruntime.mapping.view.IdentifyGraphicsOverlayResult;
//import com.esri.arcgisruntime.symbology.PictureMarkerSymbol;
//
//import com.example.app.MainMenu;
//import com.example.app.R;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.MotionEvent;
//import android.widget.ArrayAdapter;
//import android.widget.Button;
//import android.widget.DatePicker;
//import android.widget.EditText;
//import android.widget.ImageButton;
//import android.widget.Spinner;
//import android.widget.Toast;
//
//import androidx.core.content.ContextCompat;
//import androidx.databinding.DataBindingUtil;
//import android.widget.LinearLayout;
//import com.esri.arcgisruntime.ArcGISRuntimeEnvironment;
//import com.esri.arcgisruntime.mapping.ArcGISMap;
//import androidx.appcompat.app.AppCompatActivity;
//import com.esri.arcgisruntime.mapping.BasemapStyle;
//import com.esri.arcgisruntime.mapping.Viewpoint;
//import android.view.View;
//import android.widget.Button;
//import android.widget.Toast;
//import com.esri.arcgisruntime.mapping.view.MapView;
//import com.example.app.BuildConfig;
//import com.example.app.ViewRecordOnMap;
//
//import java.text.DateFormat;
//import java.text.SimpleDateFormat;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.Calendar;
//import java.util.Date;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Locale;
//import java.util.Map;
//import java.util.concurrent.ExecutionException;
//
//public class MainActivity extends AppCompatActivity  {
//    private MapView mapView;
//    private Cursor cursor,Pcursor,Dcursor,Hcursor;
//    private String check;
//    private int successfulQueriesCounter = 0;
//    GraphicsOverlay graphicsOverlay = new GraphicsOverlay();
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_view_record_on_map);
//        String apiKey = BuildConfig.ARC_GIS_API_KEY;
//        ArcGISRuntimeEnvironment.setApiKey(apiKey);
//        mapView = findViewById(R.id.mapView);
//        ArcGISMap map = new ArcGISMap(BasemapStyle.ARCGIS_TOPOGRAPHIC);
//        mapView.setMap(map);
//        mapView.setViewpoint(new Viewpoint(33.02700, 73.80543, 18922223.819286));
//        mapView.getGraphicsOverlays().add(graphicsOverlay);
//        ImageButton showmarker = findViewById(R.id.viewmarker);
//        ImageButton hospitalMarker = findViewById(R.id.hospitalMarker);
//        showmarker.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                getmarker();
//            }
//        });
//        hospitalMarker.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                removeMarkers();
//                displayhospitalmarker();
//            }
//        });
//    }
//    @SuppressLint("Range")
//    private void getmarker() {
//        try {
//            Drawable def = ContextCompat.getDrawable(this, R.drawable.diseaseblueicon);
//            BitmapDrawable defDrawable = (BitmapDrawable) def;
//            OnlineDB.getData("SELECT Patient.patient, Disease.disease, Hospital.hospital, Record.date, Record.weather, Record.condition, Record.lat, Record.long\n" +
//                    " FROM Record\n" +
//                    " INNER JOIN Patient ON Record.PatientID = Patient.ID\n" +
//                    " INNER JOIN Disease ON Record.DiseaseID = Disease.ID\n" +
//                    " INNER JOIN Hospital ON Record.HospitalID = Hospital.ID;", this, new OnlineDB.OnGetDataListener() {
//                @Override
//                public void onDataRetrieved(Cursor cursor) {
//                    Drawable def = ContextCompat.getDrawable(getBaseContext(), R.drawable.diseaseblueicon);
//                    BitmapDrawable defDrawable = (BitmapDrawable) def;
//                    if (cursor.moveToFirst()) {
//
//                        do {
//                            String[] columnNames = cursor.getColumnNames();
//                            Log.d("ColumnNames", Arrays.toString(columnNames));
//                            // Extract data from the cursor
//                            String patientid = cursor.getString(cursor.getColumnIndex("patient")); // Replace with actual column names
//                            String diseaseid = cursor.getString(cursor.getColumnIndex("Disease"));
//                            String date = cursor.getString(cursor.getColumnIndex("date"));
//                            String hospitalid = cursor.getString(cursor.getColumnIndex("Hospital"));
//                            String weathertext = cursor.getString(cursor.getColumnIndex("weather"));
//                            String conditiontext = cursor.getString(cursor.getColumnIndex("condition"));
//                            double latitude = cursor.getDouble(cursor.getColumnIndex("lat"));
//                            double longitude = cursor.getDouble(cursor.getColumnIndex("long"));
//
//                            // Create a point
//                            Point point = new Point(longitude, latitude, SpatialReferences.getWgs84());
//
//                            // Create a marker symbol
////                    SimpleMarkerSymbol markerSymbol = new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.CIRCLE, Color.RED, 10);
//                            PictureMarkerSymbol markerSymbol = new PictureMarkerSymbol(defDrawable);
//                            markerSymbol.setHeight(35);
//                            markerSymbol.setWidth(35);
//                            // Create a graphic
//                            Graphic graphic = new Graphic(point, markerSymbol);
//
//                            // Set attributes for popup
//                            Map<String, Object> attributes = new HashMap<>();
//                            attributes.put("Name", patientid);
//                            attributes.put("Disease", diseaseid);
//                            attributes.put("Date", date);
//                            attributes.put("Hospital", hospitalid);
//                            attributes.put("Weather", weathertext);
//                            attributes.put("Condition", conditiontext);
//                            attributes.put("Latitude",latitude);
//                            attributes.put("Longitude",longitude);
//
//                            // Set attributes to the graphic
//                            graphic.getAttributes().putAll(attributes);
//                            // Add graphic to the graphics overlay
//                            graphicsOverlay.getGraphics().add(graphic);
//                            mapView.setOnTouchListener(new DefaultMapViewOnTouchListener(getBaseContext(), mapView) {
//                                @Override
//                                public boolean onSingleTapConfirmed(MotionEvent e) {
//                                    // Get the clicked screen point
//                                    android.graphics.Point screenPoint = new android.graphics.Point((int) e.getX(), (int) e.getY());
//
//                                    // Identify graphics at the clicked location
//                                    ListenableFuture<IdentifyGraphicsOverlayResult> identifyResultFuture = mapView.identifyGraphicsOverlayAsync(graphicsOverlay, screenPoint, 10, false, 1);
//                                    identifyResultFuture.addDoneListener(() -> {
//                                        try {
//                                            // Get the identify result
//                                            IdentifyGraphicsOverlayResult identifyResult = identifyResultFuture.get();
//
//                                            // Get the list of identified graphics
//                                            List<Graphic> identifiedGraphics = identifyResult.getGraphics();
//
//                                            // If graphics are identified, show popup for the first graphic
//                                            if (!identifiedGraphics.isEmpty()) {
//                                                showPopupForMarker(identifiedGraphics.get(0));
//                                            }
//                                        } catch (InterruptedException | ExecutionException ex) {
//                                            ex.printStackTrace();
//                                        }
//                                    });
//
//                                    return super.onSingleTapConfirmed(e);
//                                }
//                            });
//                        } while (cursor.moveToNext());
//                        cursor.close();
//                        Toast.makeText(getBaseContext(), "Marker Placed", Toast.LENGTH_SHORT).show();
//                    }
//                }
//
//                @Override
//                public void onError(String errorMessage) {
//                    // Handle error
//                }
//            });
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//
//    @SuppressLint("Range")
//    private void displayhospitalmarker() {
//        try {
//            Drawable hospmark = ContextCompat.getDrawable(this, R.drawable.hospitalicon);
//
//            OnlineDB.getData("Select * from Hospital", this, new OnlineDB.OnGetDataListener() {
//                @Override
//                public void onDataRetrieved(Cursor cursor) {
//                    Drawable hospMarkerRef = ContextCompat.getDrawable(getBaseContext(), R.drawable.hospitalicon);
//                    BitmapDrawable hospDrawable = (BitmapDrawable) hospMarkerRef;
//
//                    if (cursor!= null && cursor.moveToFirst()) {
//                        do {
//                            String[] columnNames = cursor.getColumnNames();
//                            Log.d("ColumnNames", Arrays.toString(columnNames));
//                            // Extract data from the cursor
//                            String hospid = cursor.getString(cursor.getColumnIndex("id")); // Replace with actual column names
//                            String hospital = cursor.getString(cursor.getColumnIndex("hospital"));
//                            String capacity = cursor.getString(cursor.getColumnIndex("capcity"));
//                            String doctor = cursor.getString(cursor.getColumnIndex("doctor"));
//                            String Hlocation = cursor.getString(cursor.getColumnIndex("Hlocation"));
//                            double hospitalLat = cursor.getDouble(cursor.getColumnIndex("Hlat"));
//                            double hospitalLong = cursor.getDouble(cursor.getColumnIndex("Hlong"));
//                            Point Hpoint = new Point(hospitalLong, hospitalLat, SpatialReferences.getWgs84());
//                            // Create a marker symbol
////                    SimpleMarkerSymbol markerSymbol = new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.CIRCLE, Color.RED, 10);
//                            PictureMarkerSymbol markerSymbol = new PictureMarkerSymbol(hospDrawable);
//                            markerSymbol.setHeight(35);
//                            markerSymbol.setWidth(35);
//                            // Create a graphic
//                            Graphic graphic = new Graphic(Hpoint, markerSymbol);
//
//                            // Set attributes for popup
//                            Map<String, Object> attributes = new HashMap<>();
//                            attributes.put("id", hospid);
//                            attributes.put("Hospital", hospital);
//                            attributes.put("Capacity", capacity);
//                            attributes.put("Doctor", doctor);
//                            attributes.put("Location", Hlocation);
//                            attributes.put("Latitude", hospitalLat);
//                            attributes.put("Longitude",hospitalLong);
//
//                            // Set attributes to the graphic
//                            graphic.getAttributes().putAll(attributes);
//                            // Add graphic to the graphics overlay
//                            graphicsOverlay.getGraphics().add(graphic);
//                            mapView.setOnTouchListener(new DefaultMapViewOnTouchListener(getBaseContext(), mapView) {
//
//                                @SuppressLint("ClickableViewAccessibility")
//                                @Override
//                                public boolean onSingleTapConfirmed(MotionEvent e) {
//                                    // Get the clicked screen point
//                                    android.graphics.Point screenPoint = new android.graphics.Point((int) e.getX(), (int) e.getY());
//
//                                    // Identify graphics at the clicked location
//                                    ListenableFuture<IdentifyGraphicsOverlayResult> identifyResultFuture = mapView.identifyGraphicsOverlayAsync(graphicsOverlay, screenPoint, 10, false, 1);
//                                    identifyResultFuture.addDoneListener(() -> {
//                                        try {
//                                            // Get the identify result
//                                            IdentifyGraphicsOverlayResult identifyResult = identifyResultFuture.get();
//
//                                            // Get the list of identified graphics
//                                            List<Graphic> identifiedGraphics = identifyResult.getGraphics();
//
//                                            // If graphics are identified, show popup for the first graphic
//                                            if (!identifiedGraphics.isEmpty()) {
//                                                showPopupForhopitalMarker(identifiedGraphics.get(0));
//                                            }
//                                        } catch (InterruptedException | ExecutionException ex) {
//                                            ex.printStackTrace();
//                                        }
//                                    });
//
//                                    return super.onSingleTapConfirmed(e);
//                                }
//                            });
//                        } while (cursor.moveToNext());
//                        cursor.close();
//                        Toast.makeText(getBaseContext(), "Marker Placed", Toast.LENGTH_SHORT).show();
//                    }
//                }
//                @Override
//                public void onError(String errorMessage) {
//
//                }
//            });
//        }
//        catch(Exception e){
//            Toast.makeText(this, "Failed to place marker for Hospitals, Try Again.", Toast.LENGTH_SHORT).show();
//        }
//    }
//
//    private void showPopupForMarker(Graphic markerGraphic) {
//        Map<String, Object> attributes = markerGraphic.getAttributes();
//        String name = (String) attributes.get("Name");
//        double latitude = (double) attributes.get("Latitude");
//        double longitude = (double) attributes.get("Longitude");
//        String disease = (String) attributes.get("Disease");
//        String hospital = (String) attributes.get("Hospital");
//        String weathertext = (String) attributes.get("Weather");
//        String conditiontext = (String) attributes.get("Condition");
//        String date =(String) attributes.get("Date");
//
//        String popupContent = "\n"+
////                "Name: "+ name +"\n"+
////                "\n"+
//                "Disease: " + disease+"\n"+
//                "\n"+
//                "Latitude: ( "+latitude+" )"+"\n"+
//                "\n"+
//                "Longitude: ( "+longitude+" )"+"\n"+
//                "\n"+
////                "Hospital "+ hospital+"\n"+
////                "\n"+
//                "Date: "+ date+ "\n";
////                "\n"+
////                "Weather: "+weathertext+"\n"+
////                "\n"+
////                "Condition: " + conditiontext
//
//        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
//        builder.setTitle("Marker Details");
//        builder.setMessage(popupContent);
//        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                dialog.dismiss();
//            }
//        });
//        builder.show();
//    }
//
//    private void showPopupForhopitalMarker(Graphic markerGraphic) {
//        Map<String, Object> attributes = markerGraphic.getAttributes();
//        String Hospital = (String) attributes.get("Hospital");
//        String Capacity = (String) attributes.get("Capacity");
//        double Latitude = (double) attributes.get("Latitude");
//        double Longitude = (double) attributes.get("Longitude");
//        String Location = (String) attributes.get("Location");
//        String Doctor = (String) attributes.get("Doctor");
//
//
//        String popupContent = "\n"+
//                "Name: "+ Hospital +"\n"+
//                "\n"+
//                "Location: " + Location+"\n"+
//                "\n"+
//                "Lat,Long: ( "+Latitude+" ,"+Longitude+" )"+"\n"+
//                "\n"+
//                "Doctors: "+ Doctor+ "\n"+
//                "\n"+
//                "Capacity: "+Capacity+"\n";
//
//        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
//        builder.setTitle("Hospital Details");
//        builder.setMessage(popupContent);
//        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                dialog.dismiss();
//            }
//        });
//        builder.show();
//    }
//
//    private void removeMarkers() {
//        graphicsOverlay.getGraphics().clear();
//    }
//
//
//
//
//}