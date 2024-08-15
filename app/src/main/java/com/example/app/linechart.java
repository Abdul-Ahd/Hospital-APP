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
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link linechart#newInstance} factory method to
 * create an instance of this fragment.
 */
public class linechart extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public linechart() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment linechart.
     */
    // TODO: Rename and change types and number of parameters
    public static linechart newInstance(String param1, String param2) {
        linechart fragment = new linechart();
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

    private LineChart lineChart;
    private EditText startDate,endDate;
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        lineChart = view.findViewById(R.id.line_chart);
        startDate = view.findViewById(R.id.startDateEditText);
        endDate = view.findViewById(R.id.endDateEditText);
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
                    MakeLineChart(startDateText,endDateText);
                }
            }
        };
        startDate.addTextChangedListener(dateTextWatcher);
        endDate.addTextChangedListener(dateTextWatcher);
        LineChart();
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
    @SuppressLint("range")
    private void MakeLineChart(String Start, String End) {
        // Execute the SQL query to fetch data from the database
        OnlineDB.getData("SELECT disease.disease, record.date, COUNT(record.id) AS record_count " +
                "FROM record " +
                "INNER JOIN disease ON record.diseaseID = disease.id " +
                "WHERE record.date BETWEEN '" + Start + "' AND '" + End + "' " +
                "GROUP BY disease.disease, record.date", getContext(), new OnlineDB.OnGetDataListener() {
            @Override
            public void onDataRetrieved(Cursor cursor) {
                Map<String, Integer> dailyRecordCounts = new HashMap<>();

                // Parse the data from the Cursor and group by date
                if (cursor != null && cursor.moveToFirst()) {
                    do {
                        String date = cursor.getString(cursor.getColumnIndex("date"));
                        int recordCount = cursor.getInt(cursor.getColumnIndex("record_count"));

                        String trimmedDate = trimTimeFromDate(date);
                        dailyRecordCounts.put(trimmedDate, recordCount);
                    } while (cursor.moveToNext());
                }

                List<Map.Entry<String, Integer>> sortedEntries = new ArrayList<>(dailyRecordCounts.entrySet());
                Collections.sort(sortedEntries, new Comparator<Map.Entry<String, Integer>>() {
                    @Override
                    public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
                        // Sort by date
                        return o1.getKey().compareTo(o2.getKey());
                    }
                });

                // Create entries for the line chart using the sorted list
                List<Entry> entries = new ArrayList<>();
                List<String> labels = new ArrayList<>();
                int index = 0;
                for (Map.Entry<String, Integer> entry : sortedEntries) {
                    entries.add(new Entry(index++, entry.getValue()));
                    labels.add(entry.getKey());
                }

                // Create a dataset for the line chart
                LineDataSet lineDataSet = new LineDataSet(entries, "Trend of Diseases over Time");
                LineData lineData = new LineData(lineDataSet);

                // Customize the line chart
                lineChart.setData(lineData);

                // Set line properties
                LineDataSet dataSet = (LineDataSet) lineChart.getData().getDataSetByIndex(0);
                dataSet.setColor(Color.parseColor("#000180")); // Navy blue color
                dataSet.setLineWidth(2f); // Thicker line

                lineChart.getDescription().setEnabled(false);
                lineChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(labels));
                lineChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
                lineChart.getXAxis().setGranularity(1f);
                lineChart.getXAxis().setGranularityEnabled(true);

                lineChart.invalidate(); // Refresh the chart
            }

            @Override
            public void onError(String errorMessage) {
                // Handle error if needed
                Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String trimTimeFromDate(String dateTime) {
        if (dateTime.contains("T")) {
            return dateTime.substring(0, dateTime.indexOf("T"));
        }
        return dateTime;
    }

    @SuppressLint("range")
    private void LineChart() {
        // Execute the SQL query to fetch data from the database
        OnlineDB.getData("SELECT disease.disease, record.date, COUNT(record.id) AS record_count " +
                "FROM record " +
                "INNER JOIN disease ON record.diseaseID = disease.id " +
                "GROUP BY disease.disease, record.date", getContext(), new OnlineDB.OnGetDataListener() {
            @Override
            public void onDataRetrieved(Cursor cursor) {
                Map<String, Integer> recordCounts = new HashMap<>();

                // Parse the data from the Cursor and group by date
                if (cursor != null && cursor.moveToFirst()) {
                    do {
                        String dateWithTime = cursor.getString(cursor.getColumnIndex("date"));
                        // Trim the time part to keep only the date
                        String date = dateWithTime.split("T")[0];
                        int recordCount = cursor.getInt(cursor.getColumnIndex("record_count"));

                        recordCounts.put(date, recordCount);
                    } while (cursor.moveToNext());
                }

                List<Map.Entry<String, Integer>> sortedEntries = new ArrayList<>(recordCounts.entrySet());
                Collections.sort(sortedEntries, new Comparator<Map.Entry<String, Integer>>() {
                    @Override
                    public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
                        // Sort by date
                        return o1.getKey().compareTo(o2.getKey());
                    }
                });

                // Create entries for the line chart using the sorted list
                List<Entry> entries = new ArrayList<>();
                List<String> labels = new ArrayList<>();
                int index = 0;
                for (Map.Entry<String, Integer> entry : sortedEntries) {
                    entries.add(new Entry(index++, entry.getValue()));
                    labels.add(entry.getKey()); // Use date as label
                }

                // Create a dataset for the line chart
                LineDataSet lineDataSet = new LineDataSet(entries, "Trend of Diseases over Time");
                lineDataSet.setColor(Color.parseColor("#000180")); // Navy blue color
                lineDataSet.setLineWidth(2f); // Thicker line

                // Set line properties
                LineData lineData = new LineData(lineDataSet);


                Legend legend = lineChart.getLegend();
                legend.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
                legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
                legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
                legend.setDrawInside(true);
                legend.setForm(Legend.LegendForm.SQUARE);
                legend.setFormSize(9f);
                legend.setTextSize(11f);
                legend.setXEntrySpace(4f);


                // Customize the line chart
                lineChart.setData(lineData);
                lineChart.getDescription().setEnabled(false);

                // Customize X axis
                XAxis xAxis = lineChart.getXAxis();
                xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));
                xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                xAxis.setGranularity(1f);
                xAxis.setGranularityEnabled(true);
                xAxis.setTextColor(Color.BLACK); // Set X axis label color
                xAxis.setTextSize(12f); // Set X axis label text size

                // Customize Y axis
                YAxis yAxisLeft = lineChart.getAxisLeft();
                yAxisLeft.setTextColor(Color.BLACK); // Set Y axis label color
                yAxisLeft.setTextSize(12f); // Set Y axis label text size

                // Hide right Y axis
                lineChart.getAxisRight().setEnabled(false);

                // Set grid lines
                lineChart.setDrawGridBackground(false);
                lineChart.getAxisLeft().setDrawGridLines(false);
                xAxis.setDrawGridLines(false);

                // Set animation
                lineChart.animateY(1500);

                // Refresh the chart
                lineChart.invalidate();
            }

            @Override
            public void onError(String errorMessage) {
                // Handle error if needed
                Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }



    private String convertToMonthYear(String date) {
        String[] parts = date.split("-");
        int year = Integer.parseInt(parts[0]);
        int month = Integer.parseInt(parts[1]);
        return new DateFormatSymbols(Locale.ENGLISH).getMonths()[month - 1] + " " + year;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_linechart, container, false);
    }
}