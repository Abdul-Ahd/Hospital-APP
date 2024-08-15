package com.example.app;

import static com.example.gis.UserCheck.PERF_DIS;
import static com.example.gis.UserCheck.PERF_DOC;
import static com.example.gis.UserCheck.PERF_HOP;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.gis.DatabaseHelper;
import com.example.gis.OnlineDB;
import com.google.android.material.button.MaterialButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class doctor_effort extends AppCompatActivity implements OnlineDB.OnGetDataListener {

    private EditText date;
    private DatabaseHelper database;
    private AutoCompleteTextView patientSearchSpinner, hospitalSpinner, diseaseSpinner;
    private TextView patientNameTextView, clinicTextView, patientID, addressTextView,medicine,test;
    private TextView hospitalName, hospitalLocation, hospital_id, hospitalcap,diseaseNameForDoctor;
    private MaterialButton save;

    private Spinner treatment;
    private Cursor cursor,dcursor,pcursor;

    ArrayAdapter<String> medGridAdapter;
    ArrayAdapter<String> testGridAdapter;
    ArrayList<String> medicineData;
    ArrayList<String> testData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_effort);
        date = findViewById(R.id.date);
        date.setOnClickListener(v -> showDatePickerDialog(date));
        patientSearchSpinner = findViewById(R.id.patientSpinner);
        patientID = findViewById(R.id.pID);
        addressTextView = findViewById(R.id.addressTextView);
        patientNameTextView = findViewById(R.id.patientNameTextView);
        clinicTextView = findViewById(R.id.ageCNICGenderTextView);

        //medicine grid view
        EditText medEditText = findViewById(R.id.medispinner);
        GridView medGridView = findViewById(R.id.medigridView);
        Button addMedButton = findViewById(R.id.addMeddiButton);

        //test grid view
        EditText testEditText = findViewById(R.id.testSpinner);
        GridView testGridView = findViewById(R.id.testgridView);
        Button addTestButton = findViewById(R.id.addTestButton);

        save = findViewById(R.id.saveButton);
        medicine = findViewById(R.id.medispinner);
        test = findViewById(R.id.testSpinner);
        treatment = findViewById(R.id.treatmentType);

    save.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          insert();
        }
    });
        patientSearchSpinner.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // This method is called before the text is changed.
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String userInput = s.toString().trim().toLowerCase();
                // Check if the text matches the pattern "name - CNIC" (e.g., "fjhajshf - 5456456465")
                if (userInput.matches(".*\\s-\\s\\d{10,13}")) {
                    // If the pattern matches, return early without executing loadPatientNames()
                    return;
                }
                loadPatientNames();
            }

            @Override
            public void afterTextChanged(Editable s) {
                // This method is called after the text has been changed.
            }
        });
        patientSearchSpinner.setOnItemClickListener((parent, view, position, id) -> {
            String selectedPatient = (String) parent.getItemAtPosition(position);
            // Fetch patient details based on the selected patient
            fetchPatientDetails(selectedPatient);
        });


        // Create adapter for medicine GridView
        medGridAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        medGridView.setAdapter(medGridAdapter);
        // Create adapter for test GridView
        testGridAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        testGridView.setAdapter(testGridAdapter);

        // ArrayLists to store medicine and test data
        medicineData = new ArrayList<>();
        testData = new ArrayList<>();

        // Add item to medicine GridView on button click
        medGridView.setOnItemClickListener((parent, view, position, id) -> {
            // Retrieve the item that was clicked
            String selectedItem = medGridAdapter.getItem(position);
            // Remove the item from the adapter
            medGridAdapter.remove(selectedItem);
            // Remove the item from the ArrayList
            medicineData.remove(selectedItem);
        });

        // Add item to test GridView on button click
        testGridView.setOnItemClickListener((parent, view, position, id) -> {
            // Retrieve the item that was clicked
            String selectedItem = testGridAdapter.getItem(position);
            // Remove the item from the adapter
            testGridAdapter.remove(selectedItem);
            // Remove the item from the ArrayList
            testData.remove(selectedItem);
        });

        // Add item to medicine GridView on button click
        addMedButton.setOnClickListener(v -> {
            String newItem = medEditText.getText().toString().trim(); // Trim to remove leading and trailing spaces

            if (!newItem.isEmpty()) { // Check if the text is not empty
                medGridAdapter.add(newItem); // Add the text to the GridView
                medicineData.add(newItem); // Add the text to the ArrayList
                medEditText.setText(""); // Clear the EditText
            }
        });

        // Add item to test GridView on button click
        addTestButton.setOnClickListener(v -> {
            String newItem = testEditText.getText().toString().trim(); // Trim to remove leading and trailing spaces

            if (!newItem.isEmpty()) { // Check if the text is not empty
                testGridAdapter.add(newItem); // Add the text to the GridView
                testData.add(newItem); // Add the text to the ArrayList
                testEditText.setText(""); // Clear the EditText
            }
        });

        // Now you can use medicineData and testData ArrayLists to send the data to your database.


    }


    @SuppressLint("Range")
    private void loadPatientNames() {
        String userInput = patientSearchSpinner.getText().toString().toLowerCase();
        String name;
        try {
            OnlineDB.getData("SELECT * FROM patient where patient like '%" + userInput + "%' OR cnic like '%" + userInput + "%'", doctor_effort.this, this);
            List<String> patientNames = new ArrayList<>();
            final List<String> patientCNICs = new ArrayList<>(); // Separate list for CNICs
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    if (cursor.getColumnIndex("cnic") != -1) {
                        do {
                            name = cursor.getString(cursor.getColumnIndex("patient"));
                            String cnic = cursor.getString(cursor.getColumnIndex("cnic"));
                            // Check if user input matches name or CNIC
                            if (cnic.toLowerCase().contains(userInput) || name.toLowerCase().contains(userInput)) {
                                patientNames.add(name + " - " + cnic); // Add both name and CNIC to the suggestions
                                patientCNICs.add(cnic); // Add only CNIC to the separate list
                            }
                        } while (cursor.moveToNext());
                        cursor.close();
                    }
                }
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, patientNames);
            patientSearchSpinner.setAdapter(adapter);
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @SuppressLint({"Range", "SetTextI18n"})
    private void fetchPatientDetails(String patientNameOrCNIC) {
        String[] parts = patientNameOrCNIC.split("-");
        if (parts.length == 2) {
            String name = parts[0].trim();
            String cnic = parts[1].trim();
            Toast.makeText(this, name+" "+cnic, Toast.LENGTH_SHORT).show();
            try {
                // Check if the input is a valid CNIC (13 digits)
                if (cnic.matches("\\d{13}")) {
                    // If input is a valid CNIC, query based on CNIC
                    OnlineDB.getData("Select * From Patient Where cnic like "+cnic+" " ,doctor_effort.this,this);
                } else {
                    // If input is not a valid CNIC, query based on name
                    OnlineDB.getData("Select * From Patient Where patient like '"+name+"' " ,this,this);
                }

                if (cursor != null && cursor.moveToFirst()) {
                    patientID.setText(cursor.getString(cursor.getColumnIndex("id")));
                    patientNameTextView.setText("Name: " + cursor.getString(cursor.getColumnIndex("patient")));
                    String birth = cursor.getString(cursor.getColumnIndex("birth"));
                    String gender = cursor.getString(cursor.getColumnIndex("gender"));
                    String cnic1 = cursor.getString(cursor.getColumnIndex("cnic"));
                    int age = calculateAge(birth);

                    String patientDetails = "Age: " + age + "   CNIC: " + cnic1 + "   Gender: " + gender ;
                    clinicTextView.setText(patientDetails);
                    // Update other TextViews with respective columns
//                    clinicTextView.setText("CNIC: " + cursor.getString(cursor.getColumnIndex("cnic")));

                    addressTextView.setText("Address: " + cursor.getString(cursor.getColumnIndex("address")));
                } else {
                    // Handle case where patient is not found
                    // Display appropriate message or clear fields
                    // For example:
                    patientNameTextView.setText("Patient not found");

                    clinicTextView.setText("");
                    addressTextView.setText("");
                }
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        } else {
            // Handle invalid input format (name-cnic expected)
            // Display appropriate message or clear fields
            // For example:
            patientNameTextView.setText("Invalid input format");

            clinicTextView.setText("");
            addressTextView.setText("");
        }
    }




    private void insert(){

        String dateText = date.getText().toString();
        String patientid = patientID.getText().toString();
        String diseaseId = PERF_DIS;
        String hospitalId = PERF_HOP;
        String DoctorId = PERF_DOC;

        ArrayList<String> medValues = new ArrayList<>();
        for (int i = 0; i < medGridAdapter.getCount(); i++) {
            medValues.add(medGridAdapter.getItem(i));
        }

        ArrayList<String> testValues = new ArrayList<>();
        for (int i = 0; i < testGridAdapter.getCount(); i++) {
            testValues.add(testGridAdapter.getItem(i));
        }

        String treatmentType = treatment.getSelectedItem().toString();

        if (patientid.isEmpty() || dateText.isEmpty() || diseaseId.isEmpty() || hospitalId.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }
        if ( medValues.isEmpty() && testValues.isEmpty()){
            Toast.makeText(this, "Please add a medicine or a test", Toast.LENGTH_SHORT).show();
        return;
    }

        int Pid = Integer.parseInt(patientid);
        int Did = Integer.parseInt(diseaseId);
        int Hid = Integer.parseInt(hospitalId);
        int docID=Integer.parseInt(DoctorId);
//    Toast.makeText(this, "id1= "+ patientid+"   idD= " + diseaseid+ "   idH= "+hospitalid, Toast.LENGTH_SHORT).show();
        try {
            JSONObject requestData = new JSONObject();
            requestData.put("edate", dateText);
            requestData.put("patientID", Pid);
            requestData.put("diseaseID", Did);
            requestData.put("hospitalID", Hid);
            requestData.put("checkup", treatmentType);
            requestData.put("DoctorId",docID);
            String tableName = "doc_effort"; // Assuming the table name is Patient
            String data = requestData.toString();
            OnlineDB.postData(tableName, data,this);

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to insert data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        OnlineDB.getData("SELECT id FROM doc_effort ORDER BY id DESC LIMIT 1", this, new OnlineDB.OnGetDataListener() {
            @SuppressLint("range")
            @Override
            public void onDataRetrieved(Cursor cursor) {
                String id = null;
                if (cursor != null && cursor.moveToFirst()) {
                    id = cursor.getString(cursor.getColumnIndex("id"));

                }
                for (int i = 0; i < medGridAdapter.getCount(); i++) {
                    String med = medGridAdapter.getItem(i);
                    try {
                        JSONObject reqData = new JSONObject();
                        reqData.put("id", id);
                        reqData.put("medicine", med);
                         String data = reqData.toString();
                         OnlineDB.postData("medic", data, getBaseContext());
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }
                for (int i = 0; i < testGridAdapter.getCount(); i++) {
                    String test = testGridAdapter.getItem(i);
                    try {
                        JSONObject reqData = new JSONObject();
                        reqData.put("id", id);
                        reqData.put("tests", test);
                        String data = reqData.toString();
                        OnlineDB.postData("medicaltest", data, getBaseContext());
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
            @Override
            public void onError(String errorMessage) {

            }
        });

    }

private int calculateAge(String birthString) {

    DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
    DateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy");
    inputFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
    Date birtht = null;
    try {
        birtht = inputFormat.parse(birthString);
    } catch (ParseException e) {
        throw new RuntimeException(e);
    }
    String formattedDate = outputFormat.format(birtht);
    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
    Toast.makeText(this, formattedDate, Toast.LENGTH_SHORT).show();
    try {
        Date birthDate = sdf.parse(formattedDate);
        Calendar dob = Calendar.getInstance();
        dob.setTime(birthDate);
        Calendar today = Calendar.getInstance();
        int age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);
        if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)) {
            age--;
        }
        return age;
    } catch (ParseException e) {
        e.printStackTrace();
        return -1; // Error occurred in parsing birth date
    }
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
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
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


    @Override
    public void onDataRetrieved(Cursor cursor1) {
        cursor = cursor1;
    }

    @Override
    public void onError(String errorMessage) {

    }
}