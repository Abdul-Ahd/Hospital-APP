//package com.example.app;
//
//import android.annotation.SuppressLint;
//import android.database.Cursor;
//import android.graphics.Color;
//import android.os.Bundle;
//
//import androidx.annotation.NonNull;
//import androidx.fragment.app.Fragment;
//import androidx.viewpager2.widget.ViewPager2;
//
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.view.ViewTreeObserver;
//import android.widget.Toast;
//
//import com.airbnb.lottie.LottieAnimationView;
//import com.example.gis.OnlineDB;
//import com.example.gis.pageAdapter;
//import com.github.mikephil.charting.charts.BarChart;
//import com.github.mikephil.charting.charts.LineChart;
//import com.github.mikephil.charting.charts.PieChart;
//import com.github.mikephil.charting.components.XAxis;
//import com.github.mikephil.charting.data.BarData;
//import com.github.mikephil.charting.data.BarDataSet;
//import com.github.mikephil.charting.data.BarEntry;
//import com.github.mikephil.charting.data.Entry;
//import com.github.mikephil.charting.data.LineData;
//import com.github.mikephil.charting.data.LineDataSet;
//import com.github.mikephil.charting.data.PieData;
//import com.github.mikephil.charting.data.PieDataSet;
//import com.github.mikephil.charting.data.PieEntry;
//import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
//import com.google.android.material.tabs.TabLayout;
//
//import org.jetbrains.annotations.Nullable;
//
//import java.text.DateFormatSymbols;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Locale;
//import java.util.Map;
//import java.util.Random;
//
///**
// * A simple {@link Fragment} subclass.
// * Use the {@link dashborad#newInstance} factory method to
// * create an instance of this fragment.
// */
//
//
//
//public class dashborad extends Fragment {
//
//    private static final String ARG_PARAM1 = "param1";
//    private static final String ARG_PARAM2 = "param2";
//
//    private String mParam1;
//    private String mParam2;
//
//    TabLayout tabLayout;
//    ViewPager2 viewPager2;
//    pageAdapter adapter;
//
//    private LottieAnimationView lottie;
//
//    public dashborad() {
//        // Required empty public constructor
//    }
//
//    public static dashborad newInstance(String param1, String param2) {
//        dashborad fragment = new dashborad();
//        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
//        fragment.setArguments(args);
//        return fragment;
//    }
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_dashborad, container, false);
//    }
//    private BarChart barChart;
//    private LineChart lineChart;
//    private PieChart pieChart;
//    @Override
//    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//
//        // Find the LottieAnimationView
//        lottie = view.findViewById(R.id.animation_view);
//        barChart = view.findViewById(R.id.bar_chart);
//        lineChart = view.findViewById(R.id.line_chart);
//        pieChart = view.findViewById(R.id.pie_chart);
//        // Start the animation
//        lottie.playAnimation();
//        makeBarChart();
//        MakeLineChart();
//        MakePieChart();
//    }
//    @SuppressLint("Range")
//    private void makeBarChart() {
//        // Execute the SQL query to fetch data from the database
//        OnlineDB.getData("SELECT disease.disease, COUNT(record.id) AS record_count " +
//                "FROM record " +
//                "INNER JOIN disease ON record.diseaseID = disease.id " +
//                "GROUP BY disease.disease", getContext(), new OnlineDB.OnGetDataListener() {
//            @Override
//            public void onDataRetrieved(Cursor cursor) {
//                List<String> diseases = new ArrayList<>();
//                List<Integer> recordCounts = new ArrayList<>();
//
//                // Parse the data from the Cursor
//                if (cursor != null && cursor.moveToFirst()) {
//                    do {
//                        String diseaseName = cursor.getString(cursor.getColumnIndex("disease"));
//                        int recordCount = cursor.getInt(cursor.getColumnIndex("record_count"));
//                        diseases.add(diseaseName);
//                        recordCounts.add(recordCount);
//                    } while (cursor.moveToNext());
//                }
//
//                // Create entries for the bar chart
//                List<BarEntry> barEntries = new ArrayList<>();
//                List<Integer> colors = new ArrayList<>();
//                Random rnd = new Random();
//                for (int i = 0; i < diseases.size(); i++) {
//                    barEntries.add(new BarEntry(i, recordCounts.get(i)));
//                    // Generate random color
//                    int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
//                    colors.add(color);
//                }
//
//                // Create a dataset for the bar chart
//                BarDataSet barDataSet = new BarDataSet(barEntries, "Records per Disease");
//                barDataSet.setColors(colors);
//
//                BarData barData = new BarData(barDataSet);
//
//                // Customize the bar chart
//                barChart.setData(barData);
//                barChart.getDescription().setEnabled(false);
//                barChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(diseases));
//                barChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
//                barChart.getXAxis().setGranularity(1f);
//                barChart.getXAxis().setGranularityEnabled(true);
//                barChart.invalidate(); // Refresh the chart
//            }
//
//            @Override
//            public void onError(String errorMessage) {
//                // Handle error if needed
//                Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
//            }
//        });
//    }
//    @SuppressLint("range")
//    private void MakeLineChart() {
//        // Execute the SQL query to fetch data from the database
//        OnlineDB.getData("SELECT disease.disease, record.date, COUNT(record.id) AS record_count " +
//                "FROM record " +
//                "INNER JOIN disease ON record.diseaseID = disease.id " +
//                "GROUP BY disease.disease, record.date", getContext(), new OnlineDB.OnGetDataListener() {
//            @Override
//            public void onDataRetrieved(Cursor cursor) {
//                Map<String, Integer> monthlyRecordCounts = new HashMap<>();
//
//                // Parse the data from the Cursor and group by month and year
//                if (cursor != null && cursor.moveToFirst()) {
//                    do {
//                        String date = cursor.getString(cursor.getColumnIndex("date"));
//                        int recordCount = cursor.getInt(cursor.getColumnIndex("record_count"));
//
//                        String monthYear = convertToMonthYear(date);
//                        if (monthlyRecordCounts.containsKey(monthYear)) {
//                            monthlyRecordCounts.put(monthYear, monthlyRecordCounts.get(monthYear) + recordCount);
//                        } else {
//                            monthlyRecordCounts.put(monthYear, recordCount);
//                        }
//                    } while (cursor.moveToNext());
//                }
//
//                // Create entries for the line chart
//                List<Entry> entries = new ArrayList<>();
//                List<String> labels = new ArrayList<>();
//                int index = 0;
//                for (Map.Entry<String, Integer> entry : monthlyRecordCounts.entrySet()) {
//                    entries.add(new Entry(index++, entry.getValue()));
//                    labels.add(entry.getKey());
//                }
//
//                // Create a dataset for the line chart
//                LineDataSet lineDataSet = new LineDataSet(entries, "Trend of Diseases over Time");
//                LineData lineData = new LineData(lineDataSet);
//
//                // Customize the line chart
//                lineChart.setData(lineData);
//                lineChart.getDescription().setEnabled(false);
//                lineChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(labels));
//                lineChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
//                lineChart.getXAxis().setGranularity(1f);
//                lineChart.getXAxis().setGranularityEnabled(true);
//                lineChart.invalidate(); // Refresh the chart
//            }
//
//            @Override
//            public void onError(String errorMessage) {
//                // Handle error if needed
//                Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
//            }
//        });
//    }
//
//    // Method to convert date to month-year format
//    private String convertToMonthYear(String date) {
//        String[] parts = date.split("-");
//        int year = Integer.parseInt(parts[0]);
//        int month = Integer.parseInt(parts[1]);
//        return new DateFormatSymbols(Locale.ENGLISH).getMonths()[month - 1] + " " + year;
//    }
//    @SuppressLint("range")
//    private void MakePieChart() {
//        // Execute the SQL query to fetch data from the database
//        OnlineDB.getData("SELECT disease.disease, COUNT(record.id) AS record_count " +
//                "FROM record " +
//                "INNER JOIN disease ON record.diseaseID = disease.id " +
//                "GROUP BY disease.disease", getContext(), new OnlineDB.OnGetDataListener() {
//            @Override
//            public void onDataRetrieved(Cursor cursor) {
//                List<String> diseases = new ArrayList<>();
//                List<Integer> recordCounts = new ArrayList<>();
//
//                // Parse the data from the Cursor
//                if (cursor != null && cursor.moveToFirst()) {
//                    do {
//                        String diseaseName = cursor.getString(cursor.getColumnIndex("disease"));
//                        int recordCount = cursor.getInt(cursor.getColumnIndex("record_count"));
//
//                        diseases.add(diseaseName);
//                        recordCounts.add(recordCount);
//                    } while (cursor.moveToNext());
//                }
//
//                // Create entries and set custom colors for the pie chart
//                List<PieEntry> pieEntries = new ArrayList<>();
//                List<Integer> colors = new ArrayList<>();
//                for (int i = 0; i < diseases.size(); i++) {
//                    pieEntries.add(new PieEntry(recordCounts.get(i), diseases.get(i)));
//
//                    // Set custom color for each entry
//                    int color = getRandomColor();
//                    colors.add(color);
//                }
//
//                // Create a dataset for the pie chart
//                PieDataSet pieDataSet = new PieDataSet(pieEntries, "Distribution of Diseases among Patients");
//                pieDataSet.setColors(colors);
//
//                PieData pieData = new PieData(pieDataSet);
//
//                // Customize the pie chart
//                pieChart.setData(pieData);
//                pieChart.getDescription().setEnabled(false);
//                pieChart.invalidate(); // Refresh the chart
//            }
//
//            @Override
//            public void onError(String errorMessage) {
//                // Handle error if needed
//                Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
//            }
//        });
//    }
//    private int getRandomColor() {
//        Random rnd = new Random();
//        return Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
//    }
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//
//        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
//        }
//    }
//}
