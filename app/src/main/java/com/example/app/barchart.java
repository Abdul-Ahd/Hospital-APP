package com.example.app;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.example.gis.OnlineDB;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link barchart#newInstance} factory method to
 * create an instance of this fragment.
 */
public class barchart extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public barchart() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment barchart.
     */
    // TODO: Rename and change types and number of parameters
    public static barchart newInstance(String param1, String param2) {
        barchart fragment = new barchart();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }
    private BarChart barChart;
    private EditText startDate,endDate;
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        barChart = view.findViewById(R.id.bar_chart);
        startDate=view.findViewById(R.id.startDateEditText);
        endDate=view.findViewById(R.id.endDateEditText);
        startDate.setOnClickListener(v -> showDatePickerDialog(startDate));
        endDate.setOnClickListener(v -> showDatePickerDialog(endDate));
        TextWatcher dateTextWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Not needed for your case, but required to implement TextWatcher
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Not needed for your case, but required to implement TextWatcher
            }

            @Override
            public void afterTextChanged(Editable s) {

                String startDateText = startDate.getText().toString().trim();
                String endDateText = endDate.getText().toString().trim();
                if (!startDateText.isEmpty() && !endDateText.isEmpty()) {
                    BarChart(startDateText,endDateText);
                }
            }
        };

        startDate.addTextChangedListener(dateTextWatcher);
        endDate.addTextChangedListener(dateTextWatcher);
        makeBarChart();

    }
    @SuppressLint("SimpleDateFormat")
    private void showDatePickerDialog(final EditText editText) {
        // Get initial date from editText
        String initialDateStr = editText.getText().toString();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()); // Change the format pattern
        Date initialDate = new Date();
        try {
            initialDate = dateFormat.parse(initialDateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        // Get initial year, month, and day from initial date
        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(initialDate);
        int initialYear = calendar.get(Calendar.YEAR);
        int initialMonth = calendar.get(Calendar.MONTH);
        int initialDay = calendar.get(Calendar.DAY_OF_MONTH);

        // Create date picker dialog with initial date
        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                // Update EditText with selected date
                String selectedDate = String.format(Locale.getDefault(), "%d-%02d-%02d", year, monthOfYear + 1, dayOfMonth); // Change the format pattern
                editText.setText(selectedDate);
            }
        }, initialYear, initialMonth, initialDay);

        // Show date picker dialog
        datePickerDialog.show();
    }
    @SuppressLint("Range")
    private void BarChart(String Start,String End) {

        OnlineDB.getData("SELECT disease.disease, COUNT(record.id) AS record_count " +
                "FROM record " +
                "INNER JOIN disease ON record.diseaseID = disease.id " +
                "WHERE record.date BETWEEN '" + Start + "' AND '" + End + "' " +
                "GROUP BY disease.disease", getContext(), new OnlineDB.OnGetDataListener() {
            @Override
            public void onDataRetrieved(Cursor cursor) {
                List<String> diseases = new ArrayList<>();
                List<Integer> recordCounts = new ArrayList<>();

                // Parse the data from the Cursor
                if (cursor != null && cursor.moveToFirst()) {
                    do {
                        String diseaseName = cursor.getString(cursor.getColumnIndex("disease"));
                        int recordCount = cursor.getInt(cursor.getColumnIndex("record_count"));
                        diseases.add(diseaseName);
                        recordCounts.add(recordCount);
                    } while (cursor.moveToNext());
                }

                // Create entries for the bar chart
                List<BarEntry> barEntries = new ArrayList<>();
                List<Integer> colors = new ArrayList<>();
                Random rnd = new Random();
                for (int i = 0; i < diseases.size(); i++) {
                    barEntries.add(new BarEntry(i, recordCounts.get(i)));
                    // Generate random light color
                    int red = rnd.nextInt(128) + 128; // Red component between 128 and 255
                    int green = rnd.nextInt(128) + 128; // Green component between 128 and 255
                    int blue = rnd.nextInt(128) + 128; // Blue component between 128 and 255
                    int color = Color.rgb(red, green, blue);
                    colors.add(color);
                }

                BarDataSet barDataSet = new BarDataSet(barEntries, "Records per Disease");
                barDataSet.setColors(colors);

                BarData barData = new BarData(barDataSet);

                Legend legend = barChart.getLegend();
                legend.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
                legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
                legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
                legend.setDrawInside(true);
                legend.setForm(Legend.LegendForm.SQUARE);
                legend.setFormSize(9f);
                legend.setTextSize(11f);
                legend.setXEntrySpace(4f);


                barChart.setData(barData);


                barChart.getDescription().setEnabled(false);

                XAxis xAxis = barChart.getXAxis();
                xAxis.setValueFormatter(new IndexAxisValueFormatter(diseases));
                xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                xAxis.setGranularity(1f);
                xAxis.setGranularityEnabled(true);
                xAxis.setTextColor(Color.BLACK);
                xAxis.setTextSize(12f);


                YAxis yAxisLeft = barChart.getAxisLeft();
                yAxisLeft.setTextColor(Color.BLACK);
                yAxisLeft.setTextSize(12f);


                barChart.getAxisRight().setEnabled(false);


                Legend legend1 = barChart.getLegend();
                legend1.setEnabled(false); // Disable legend

                barChart.setDrawGridBackground(false);
                barChart.getAxisLeft().setDrawGridLines(false);
                xAxis.setDrawGridLines(false);

                barChart.animateY(1000);

                barChart.invalidate();

            }

            @Override
            public void onError(String errorMessage) {
                // Handle error if needed
                Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }
    @SuppressLint("Range")
    private void makeBarChart() {
        // Execute the SQL query to fetch data from the database
        OnlineDB.getData("SELECT disease.disease, COUNT(record.id) AS record_count " +
                "FROM record " +
                "INNER JOIN disease ON record.diseaseID = disease.id " +
                "GROUP BY disease.disease", getContext(), new OnlineDB.OnGetDataListener() {
            @Override
            public void onDataRetrieved(Cursor cursor) {
                List<String> diseases = new ArrayList<>();
                List<Integer> recordCounts = new ArrayList<>();

                // Parse the data from the Cursor
                if (cursor != null && cursor.moveToFirst()) {
                    do {
                        String diseaseName = cursor.getString(cursor.getColumnIndex("disease"));
                        int recordCount = cursor.getInt(cursor.getColumnIndex("record_count"));
                        diseases.add(diseaseName);
                        recordCounts.add(recordCount);
                    } while (cursor.moveToNext());
                }

                // Create entries for the bar chart
                List<BarEntry> barEntries = new ArrayList<>();
                List<Integer> colors = new ArrayList<>();
                Random rnd = new Random();
                for (int i = 0; i < diseases.size(); i++) {
                    barEntries.add(new BarEntry(i, recordCounts.get(i)));
                    // Generate random light color
                    int red = rnd.nextInt(128) + 128; // Red component between 128 and 255
                    int green = rnd.nextInt(128) + 128; // Green component between 128 and 255
                    int blue = rnd.nextInt(128) + 128; // Blue component between 128 and 255
                    int color = Color.rgb(red, green, blue);
                    colors.add(color);
                }

                BarDataSet barDataSet = new BarDataSet(barEntries, "Records per Disease");
                barDataSet.setColors(colors);

                BarData barData = new BarData(barDataSet);

                Legend legend = barChart.getLegend();
                legend.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
                legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
                legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
                legend.setDrawInside(true);
                legend.setForm(Legend.LegendForm.SQUARE);
                legend.setFormSize(9f);
                legend.setTextSize(11f);
                legend.setXEntrySpace(4f);

                barChart.setData(barData);


                barChart.getDescription().setEnabled(false);

                XAxis xAxis = barChart.getXAxis();
                xAxis.setValueFormatter(new IndexAxisValueFormatter(diseases));
                xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                xAxis.setGranularity(1f);
                xAxis.setGranularityEnabled(true);
                xAxis.setTextColor(Color.BLACK);
                xAxis.setTextSize(12f);


                YAxis yAxisLeft = barChart.getAxisLeft();
                yAxisLeft.setTextColor(Color.BLACK);
                yAxisLeft.setTextSize(12f);


                barChart.getAxisRight().setEnabled(false);


                Legend legend1 = barChart.getLegend();
                legend1.setEnabled(true); // Disable legend

                barChart.setDrawGridBackground(false);
                barChart.getAxisLeft().setDrawGridLines(false);
                xAxis.setDrawGridLines(false);

                barChart.animateY(1000);

                barChart.invalidate();
            }

            @Override
            public void onError(String errorMessage) {
                // Handle error if needed
                Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_barchart, container, false);
    }
}