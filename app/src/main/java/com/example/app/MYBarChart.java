package com.example.app;

import static android.widget.Toast.LENGTH_SHORT;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.database.Cursor;
import android.os.Bundle;

import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Toast;

import com.example.gis.DatabaseHelper;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MYBarChart extends AppCompatActivity {
    private EditText startDate, endDate;
    private AutoCompleteTextView diseaseSpinner;
    private BarChart barChart;
    private DatabaseHelper database;
    private  String DID;
    private String Start,End;
    private SimpleDateFormat sdf = new SimpleDateFormat("d/MM/yyyy",Locale.ENGLISH);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bar_chart);
        startDate = findViewById(R.id.startDateEditText);
        endDate = findViewById(R.id.endDateEditText);
        barChart = findViewById(R.id.barChart);
        diseaseSpinner = findViewById(R.id.diseaseAutoCompleteTextView);
        database = new DatabaseHelper(this);
         loadDiseasesByName();
        diseaseSpinner.setOnItemClickListener((parent, view, position, id) -> {
            String disease = (String) parent.getItemAtPosition(position);
            // Fetch patient details based on the selected patient
           fetchDisease(disease);
            makeBarChart(disease);
        });

        startDate.setOnClickListener(v -> {
            showMonthYearPickerDialog(startDate,true);

        });

        endDate.setOnClickListener(v -> {
            showMonthYearPickerDialog(endDate,false);

        });
    }
    private void handleDateSelection(EditText editText, boolean isStartDate) {
        String dateString = editText.getText().toString().trim();
        Calendar calendar = Calendar.getInstance();

        try {
            // Parse the input text into a Calendar object
            SimpleDateFormat sdf = new SimpleDateFormat("MMMM yyyy", Locale.ENGLISH);
            Date date = sdf.parse(dateString);
            calendar.setTime(date);

            // Set the selected date's respective first and last day of the month
            if (isStartDate) {
                calendar.set(Calendar.DAY_OF_MONTH, 1); // Set to first day of the month
            } else {
                int lastDayOfMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
                calendar.set(Calendar.DAY_OF_MONTH, lastDayOfMonth); // Set to last day of the month
            }

            // Format the first or last day of the month
            SimpleDateFormat outputSdf = new SimpleDateFormat("d/M/yyyy", Locale.ENGLISH);
            String formattedDate = outputSdf.format(calendar.getTime());

            // Update the EditText field with the formatted date
            if(isStartDate){
                Start = formattedDate;
            }else{
                End=formattedDate;
            }


        } catch (ParseException e) {
            e.printStackTrace();
            // Handle parsing exception
            Toast.makeText(this, "Error parsing date", Toast.LENGTH_SHORT).show();
        }
    }

    @SuppressLint("Range")
    private void makeBarChart(String name){
        barChart.clear();
        SimpleDateFormat sdf = new SimpleDateFormat("d/M/yyyy", Locale.ENGLISH);
        long startTimestamp = 0;
        long endTimestamp = 0;
        try {
            Date startDate = sdf.parse(Start);
            Date endDate = sdf.parse(End);
            startTimestamp = startDate.getTime() ; // Convert milliseconds to seconds
            endTimestamp = endDate.getTime();// Convert milliseconds to seconds
            Toast.makeText(this, Start+"   "+End, LENGTH_SHORT).show();
        } catch (ParseException e) {
            e.printStackTrace();
            // Handle parsing exception
        }
        HashMap<String, Integer> monthEntryCountMap = new HashMap<>();
        String query = "SELECT * FROM Record WHERE diseaseID = ? AND date >= ? and date <= ? ";
        try (Cursor cursor = database.getReadableDatabase().rawQuery(query, new String[]{DID, Start ,End})) {
            if (cursor.moveToFirst()) {
                do {
                    String dateString = cursor.getString(cursor.getColumnIndex("date"));

                    // Parse the date string into a Date object
                    Date date = parseRealDate(dateString);

                    // Format the date to get the day string (e.g., "27")
                    String dayOfMonth = new SimpleDateFormat("dd", Locale.US).format(date);

                    // Format the date to get the month-year string (e.g., "February 2024")
                    String monthYear = new SimpleDateFormat("MMMM yyyy", Locale.US).format(date);

                    // Increment the count of entries for the corresponding month
                    monthEntryCountMap.put(monthYear, monthEntryCountMap.getOrDefault(monthYear, 0) + 1);

                    // Add the day string to the bar chart description
                    barChart.getDescription().setText(dayOfMonth);
                } while (cursor.moveToNext());
            }

            cursor.close();
        }
        // Prepare data for the bar chart
        // Prepare data for the bar chart
        ArrayList<BarEntry> barEntries = new ArrayList<>();
        ArrayList<String> labels = new ArrayList<>();
        int index = 0;
        for (Map.Entry<String, Integer> entry : monthEntryCountMap.entrySet()) {
            String monthYear = entry.getKey();
            int entryCount = entry.getValue();
            barEntries.add(new BarEntry(index++, entryCount));
            labels.add(monthYear);
        }


        // Create a BarDataSet from the barEntries
        BarDataSet barDataSet = new BarDataSet(barEntries, name);

        // Create a BarData object from the barDataSet
        BarData barData = new BarData(barDataSet);

        // Configure the bar chart
        barChart.setData(barData);
        barChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(labels));
        barChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        barChart.getXAxis().setLabelRotationAngle(45f);
        barChart.getDescription().setEnabled(false);
        barChart.invalidate(); // Refresh the chart
    }

    private Date parseRealDate(String dateString) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
        try {
            return dateFormat.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
            return new Date(); // Return current date in case of parsing error
        }
    }
    @SuppressLint({"Range", "SetTextI18n"})
    private String fetchDisease(String hospital) {
        String name = "";
        try (Cursor cursor = database.getReadableDatabase().rawQuery("SELECT * FROM Disease WHERE name = ? OR id = ?", new String[]{hospital,hospital})) {
            if (cursor.moveToFirst()) {
                DID = cursor.getString(cursor.getColumnIndex("id"));
                name = cursor.getString(cursor.getColumnIndex("name"));
            } else {
                // Handle case where hospital is not found
                Toast.makeText(this, "Disease not Found", LENGTH_SHORT).show();
            }
        }
        return name;
    }
    @SuppressLint("Range")
    private void loadDiseasesByName() {
        List<String> diseaseNames = new ArrayList<>();
        String userInput = diseaseSpinner.getText().toString();

        Cursor cursor = database.getReadableDatabase().rawQuery("SELECT * FROM Disease WHERE name LIKE ?", new String[]{"%" + userInput + "%"});
        if (cursor.moveToFirst()) {
            do {
                String name = cursor.getString(cursor.getColumnIndex("name"));
                diseaseNames.add(name);
            } while (cursor.moveToNext());
        }
        cursor.close();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, diseaseNames);
        diseaseSpinner.setAdapter(adapter);
    }
    private void showMonthYearPickerDialog(EditText editText, Boolean isMonth) {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);

        // Set up DatePickerDialog to show only month and year
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, selectedYear, selectedMonth, selectedDayOfMonth) -> {
                    Calendar selectedDate = Calendar.getInstance();
                    selectedDate.set(selectedYear, selectedMonth, 1); // Set day to 1 to show only month and year

                    // Format the selected date
                    SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM yyyy", Locale.US);
                    String monthYear = dateFormat.format(selectedDate.getTime());
                    editText.setText(monthYear);

                    // Call handleDateSelection with the selected date and whether it's for month or not
                    handleDateSelection(editText,  isMonth);
                },
                year, month, 1); // Set day to 1 to show only month and year

        datePickerDialog.show();
    }

}