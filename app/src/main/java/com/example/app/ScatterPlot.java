package com.example.app;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.gis.OnlineDB;
import com.github.mikephil.charting.charts.ScatterChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.ScatterData;
import com.github.mikephil.charting.data.ScatterDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.util.ArrayList;
import java.util.List;

public class ScatterPlot extends Fragment {

    private ScatterChart scatterChart;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_scatter_chart, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        scatterChart = view.findViewById(R.id.scatter_chart);

        // Fetch data from database and plot scatter chart
        scatterChart();
    }
    @SuppressLint("Range")
    private void scatterChart() {
        // Execute the SQL query to fetch data from the database
        OnlineDB.getData("SELECT `condition`, weather FROM record", getContext(), new OnlineDB.OnGetDataListener() {
            @Override
            public void onDataRetrieved(Cursor cursor) {
                List<Integer> conditionValues = new ArrayList<>();
                List<Integer> weatherValues = new ArrayList<>();

                // Parse the data from the Cursor
                if (cursor.moveToFirst()) {
                    do {
                         String condition = cursor.getString(cursor.getColumnIndex("condition"));
                         String weatherCondition = cursor.getString(cursor.getColumnIndex("weather"));

                        // Map condition string to numerical value
                        int conditionValue = mapConditionToValue(condition);
                        // Map weather string to numerical value
                        int weatherValue = mapWeatherToValue(weatherCondition);

                        conditionValues.add(conditionValue);
                        weatherValues.add(weatherValue);
                    } while (cursor.moveToNext());
                }

                // Create entries for the scatter plot
                List<Entry> entries = new ArrayList<>();
                for (int i = 0; i < conditionValues.size(); i++) {
                    // Introduce some randomness to x and y values to prevent overlapping
                    float xOffset = (float) Math.random() * 1f; // Adjust the multiplier as needed
                    float yOffset = (float) Math.random() * 1f; // Adjust the multiplier as needed
                    float xValue = weatherValues.get(i) + xOffset;
                    float yValue = conditionValues.get(i) + yOffset;
                    entries.add(new Entry(xValue, yValue));
                }

                // Create a dataset for the scatter plot
                ScatterDataSet dataSet = new ScatterDataSet(entries, "Patient Conditions vs Weather Conditions");
                dataSet.setDrawIcons(false); // Disable drawing of grid lines for the dataset

                ScatterData scatterData = new ScatterData(dataSet);

                dataSet.setScatterShape(ScatterChart.ScatterShape.CIRCLE); // Set shape of the points (you can also use SQUARE, TRIANGLE, or CROSS)
                dataSet.setScatterShapeSize(30f); // Set size of the points
                dataSet.setColor(0xFF030627); // Set color for the points (0xFF010627 is a sample color value)

                // Customize the scatter chart
                scatterChart.setData(scatterData);
                scatterChart.getDescription().setEnabled(false);

                // Customize XAxis
                XAxis xAxis = scatterChart.getXAxis();
                xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                xAxis.setDrawLabels(true); // Enable drawing x labels
                xAxis.setValueFormatter(new IndexAxisValueFormatter(getWeatherLabels())); // Set custom labels for x-axis

                // Customize YAxis
                YAxis yAxis = scatterChart.getAxisLeft(); // Use left YAxis for customization
                yAxis.setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART);
                yAxis.setDrawLabels(true); // Enable drawing y labels
                yAxis.setValueFormatter(new IndexAxisValueFormatter(getConditionLabels())); // Set custom labels for y-axis

                // Disable grid lines on both x and y axes
                xAxis.setDrawGridLines(false);
                yAxis.setDrawGridLines(false);

                // Refresh the chart
                scatterChart.invalidate();
            }

            private int mapConditionToValue(String condition) {
                // Mapping for patient condition
                switch (condition) {
                    case "Undetermined":
                        return 0;
                    case "Good":
                        return 1;
                    case "Fair":
                        return 2;
                    case "Serious":
                        return 3;
                    case "Critical":
                        return 4;
                    default:
                        return -1; // Handle unknown values
                }
            }

            private int mapWeatherToValue(String weather) {
                // Mapping for weather condition
                switch (weather) {
                    case "Spring":
                        return 0;
                    case "Summer":
                        return 1;
                    case "Autumn/Fall":
                        return 2;
                    case "Winter":
                        return 3;
                    default:
                        return -1; // Handle unknown values
                }
            }

            private String[] getWeatherLabels() {
                return new String[]{"Spring", "Summer", "Autumn/Fall", "Winter"};
            }

            private String[] getConditionLabels() {
                return new String[]{"Undetermined", "Good", "Fair", "Serious", "Critical"};
            }

            @Override
            public void onError(String errorMessage) {
                // Handle error if needed
                Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

}
