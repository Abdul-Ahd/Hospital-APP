package com.example.app;

import static android.widget.Toast.LENGTH_SHORT;
import static java.security.AccessController.getContext;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gis.DatabaseHelper;
import com.example.gis.OnlineDB;

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

public class Manage_Record extends AppCompatActivity {
    private EditText date;
    private DatabaseHelper database;
    private AutoCompleteTextView patientSearchSpinner, hospitalSpinner, diseaseSpinner,getrecord;
    private TextView patientNameTextView, clinicTextView, patientID, addressTextView;
    private TextView hospitalName, hospitalLocation, hospital_id, hospitalcap;
    String DID,getID;

    @Override
    @SuppressLint("Range")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_record);
        date = findViewById(R.id.date);
        date.setOnClickListener(v -> showDatePickerDialog(date));
        getrecord = findViewById(R.id.patientSpinnertoget);
        patientSearchSpinner = findViewById(R.id.patientSpinner);
        hospitalSpinner = findViewById(R.id.HSpinner);
        diseaseSpinner = findViewById(R.id.DSpinner);
        patientNameTextView = findViewById(R.id.patientNameTextView);
        clinicTextView = findViewById(R.id.ageCNICGenderTextView);
        patientID = findViewById(R.id.pID);
        addressTextView = findViewById(R.id.addressTextView);
        hospitalName=findViewById(R.id.Hname);
        hospital_id=findViewById(R.id.HID);
        hospitalLocation = findViewById(R.id.Hlocation);
        hospitalcap = findViewById(R.id.Hcap);

        // Open or create the database
        database = new DatabaseHelper(this);

        // Load patient names into AutoCompleteTextView
        loadPatientdata();
        loadPatientNames();
        loadDiseasesByName();
        loadHospitalsByName();
        // Listener to handle patient selection
        patientSearchSpinner.setOnItemClickListener((parent, view, position, id) -> {
            String selectedPatient = (String) parent.getItemAtPosition(position);
            // Fetch patient details based on the selected patient
            fetchPatientDetails(selectedPatient);
        });
        getrecord.setOnItemClickListener((parent, view, position, id) -> {
            String selectedPatient = (String) parent.getItemAtPosition(position);
            // Fetch patient details based on the selected patient
            fetchPatientDetails(selectedPatient);
                    String[] parts = selectedPatient.split("-");

                    if (parts.length == 2) {
                        String name = parts[0].trim();
                        String cnic = parts[1].trim();

            OnlineDB.getData("SELECT Patient.patient, Disease.disease, Record.id FROM Record\n" +
                    " INNER JOIN Patient ON Record.PatientID = Patient.ID \n" +
                    " INNER JOIN Disease ON Record.DiseaseID = Disease.ID\n" +
                    "  WHERE cnic = '"+ cnic +"' ", this, new OnlineDB.OnGetDataListener() {
                @Override
                public void onDataRetrieved(Cursor cursor) {
                    if (cursor.moveToFirst()) {
                        ArrayList<String> patientDiseaseList = new ArrayList<>();

                        do {
                            String patientName =cursor.getString(cursor.getColumnIndex("patient"));
                            String billid = cursor.getString(cursor.getColumnIndex("id"));
                            String diseaseID = cursor.getString(cursor.getColumnIndex("disease"));
                            patientDiseaseList.add(billid+"  "+ patientName+ " with "  + diseaseID);
                        } while (cursor.moveToNext());

                        String[] patientDiseaseArray = patientDiseaseList.toArray(new String[0]);

                        AlertDialog.Builder builder = new AlertDialog.Builder(Manage_Record.this);
                        builder.setTitle("Patient Information");
                        builder.setItems(patientDiseaseArray, (dialog, which) -> {
                            String clickedItem = patientDiseaseArray[which];
                            String[] parts = clickedItem.split(" ");
                            if (parts.length > 0) {
                                String billId = parts[0];
                                Toast.makeText(getBaseContext(), "Clicked Bill ID: " + billId, LENGTH_SHORT).show();
                                fetchRecord(billId);
                            } else {
                                // Handle if the clicked item format is incorrect
                                Toast.makeText(getBaseContext(), "Invalid clicked item format", LENGTH_SHORT).show();
                            }

                        });
                        builder.setPositiveButton("OK", null);
                        builder.show();
                    }else{
                        Toast.makeText(getBaseContext(),"Record not Found", LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onError(String errorMessage) {

                }
            });
                    }
        });
        hospitalSpinner.setOnItemClickListener((parent, view, position, id) -> {
            String hospital = (String) parent.getItemAtPosition(position);
            // Fetch patient details based on the selected patient
            fetchHospital(hospital);
        });
        diseaseSpinner.setOnItemClickListener((parent, view, position, id) -> {
            String disease = (String) parent.getItemAtPosition(position);
            // Fetch patient details based on the selected patient
            fetchDisease(disease);
        });

        Button del = findViewById(R.id.deleteRec);
        del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView id = findViewById(R.id.billnumber);
                String delid= id.getText().toString();
                if (TextUtils.isEmpty(delid) || !TextUtils.isDigitsOnly(delid)) {
                    Toast.makeText(Manage_Record.this, "Invalid Bill ID", Toast.LENGTH_SHORT).show();
                    return;
                }
                int del = Integer.parseInt(delid);
                OnlineDB.deleteData("Record",del);
                Toast.makeText(Manage_Record.this, del +" has been removed.", Toast.LENGTH_SHORT).show();
                clearall();

            }
        });
        Button updatebutton = findViewById(R.id.updateRec);
        updatebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               update();
            }
        });

    }
    // Method to load patient names from the database into the AutoCompleteTextView
    @SuppressLint("Range")
    private void loadPatientdata() {
        List<String> patientNames = new ArrayList<>();
        String userInput = getrecord.getText().toString().toLowerCase();
        OnlineDB.getData("Select * From Patient where patient like '%" + userInput + "%' or cnic like '%" + userInput + "%' ", this, new OnlineDB.OnGetDataListener() {
            @Override
            public void onDataRetrieved(Cursor cursor) {

                if (cursor.moveToFirst()) {
                    do {
                        String name = cursor.getString(cursor.getColumnIndex("patient"));
                        String cnic = cursor.getString(cursor.getColumnIndex("cnic"));
                        String id = cursor.getString(cursor.getColumnIndex("id"));
                        // Add both name and CNIC to suggestions
                        patientNames.add(name + " - " + cnic);

                    } while (cursor.moveToNext());
                }
                cursor.close();

                ArrayAdapter<String> adapter = new ArrayAdapter<>(getBaseContext(), android.R.layout.simple_dropdown_item_1line, patientNames);
                getrecord.setAdapter(adapter);
            }

            @Override
            public void onError(String errorMessage) {

            }
        });
    }

    @SuppressLint("Range")
    private void loadPatientNames() {
        String userInput = patientSearchSpinner.getText().toString().toLowerCase();


            OnlineDB.getData("SELECT * FROM patient where patient like '%" + userInput + "%' OR cnic like '%" + userInput + "%'", this, new OnlineDB.OnGetDataListener() {
                @Override
                public void onDataRetrieved(Cursor cursor) {
                    List<String> patientNames = new ArrayList<>();
                    final List<String> patientCNICs = new ArrayList<>(); // Separate list for CNICs
                    if (cursor != null) {
                        if (cursor.moveToFirst()) {
                            if (cursor.getColumnIndex("cnic") != -1) {
                                do {
                                    String name = cursor.getString(cursor.getColumnIndex("patient"));
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

                    ArrayAdapter<String> adapter = new ArrayAdapter<>(getBaseContext(), android.R.layout.simple_dropdown_item_1line, patientNames);
                    patientSearchSpinner.setAdapter(adapter);
                }

                @Override
                public void onError(String errorMessage) {

                }
            });


    }


    @SuppressLint("Range")
    private void loadDiseasesByName() {

        String userInput = diseaseSpinner.getText().toString();

        OnlineDB.getData("Select * From Disease Where disease like '%"+userInput+"%'",this,new OnlineDB.OnGetDataListener() {

            @Override
            public void onDataRetrieved(Cursor cursor) {
                List<String> diseaseNames = new ArrayList<>();
                if(cursor != null && cursor.moveToFirst()) {
                    do {
                        String name = cursor.getString(cursor.getColumnIndex("disease"));
                        diseaseNames.add(name);
                    } while (cursor.moveToNext());
                }
                ArrayAdapter<String> Dadapter = new ArrayAdapter<>(getBaseContext(), android.R.layout.simple_dropdown_item_1line, diseaseNames);
                diseaseSpinner.setAdapter(Dadapter);
            }

            @Override
            public void onError(String errorMessage) {

            }
        });

    }

    @SuppressLint("Range")
    private void loadHospitalsByName() {
        String userInput = hospitalSpinner.getText().toString();
        OnlineDB.getData("Select * From Hospital Where hospital like '%"+userInput+"%'",this,new OnlineDB.OnGetDataListener() {
            @Override
            public void onDataRetrieved(Cursor cursor) {
                List<String> hospitalNames = new ArrayList<>();
                if (cursor != null && cursor.moveToFirst()) {
                    do {
                        String name = cursor.getString(cursor.getColumnIndex("hospital"));
                        hospitalNames.add(name);
                    } while (cursor.moveToNext());
                    cursor.close();
                }
                ArrayAdapter<String>  Hadapter = new ArrayAdapter<>(getBaseContext(), android.R.layout.simple_dropdown_item_1line, hospitalNames);
                hospitalSpinner.setAdapter(Hadapter);
            }
            @Override
            public void onError(String errorMessage) {

            }
        });


    }


    @SuppressLint({"Range", "SetTextI18n"})
    private String fetchDisease(String hospital) {
        String name = "";
        OnlineDB.getData("Select * FROM Disease where disease = '" + hospital + "' ", this, new OnlineDB.OnGetDataListener() {
            @Override
            public void onDataRetrieved(Cursor cursor) {
                if (cursor.moveToFirst()) {
                    DID = cursor.getString(cursor.getColumnIndex("id"));
                    String name = cursor.getString(cursor.getColumnIndex("disease"));
                } else {
                    // Handle case where hospital is not found
                    Toast.makeText(getBaseContext(), "Disease not Found", LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(String errorMessage) {

            }
        });

        return name;
    }
    @SuppressLint("Range")
    private void fetchRecord(String data){
            OnlineDB.getData("SELECT Patient.patient, Disease.disease,Record.diseaseID, Hospital.hospital,Record.id, Record.date, Record.weather, Record.condition, Record.lat, Record.long\n" +
                    " FROM Record\n" +
                    " INNER JOIN Patient ON Record.PatientID = Patient.ID\n" +
                    " INNER JOIN Disease ON Record.DiseaseID = Disease.ID\n" +
                    " INNER JOIN Hospital ON Record.HospitalID = Hospital.ID\n" +
                    " Where Record.id = "+data+" ", this, new OnlineDB.OnGetDataListener() {
                @Override
                public void onDataRetrieved(Cursor cursor) {
                    if (cursor.moveToFirst()) {
                        TextView billno = findViewById(R.id.billnumber);
                        billno.setText(cursor.getString(cursor.getColumnIndex("id")));
                        String date1 = (cursor.getString(cursor.getColumnIndex("date")));
                        String patient = cursor.getString(cursor.getColumnIndex("patient"));
                        String hospital = cursor.getString(cursor.getColumnIndex("hospital"));
                        String disea =  cursor.getString(cursor.getColumnIndex("disease"));
                        DID = cursor.getString(cursor.getColumnIndex("diseaseID"));
                        DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                        DateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");
                        inputFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
                        Date birtht = null;
                        try {
                            birtht = inputFormat.parse(date1);
                        } catch (ParseException e) {
                            throw new RuntimeException(e);
                        }
                        String formattedDate = outputFormat.format(birtht);
                        date.setText(formattedDate);
                        patientSearchSpinner.setText(patient);
                        hospitalSpinner.setText(hospital);
                        diseaseSpinner.setText(disea);
                        EditText lat = findViewById(R.id.latitudeEditTextR);
                        EditText lng = findViewById(R.id.longitudeEditTextR);
                        Spinner weather = findViewById(R.id.weatherSpinner);
                        Spinner condition = findViewById(R.id.conditionspinner);
                        lat.setText(cursor.getString(cursor.getColumnIndex("lat")));
                        lng.setText(cursor.getString(cursor.getColumnIndex("long")));
                        String weatherValue = cursor.getString(cursor.getColumnIndex("weather"));
                        fetchHospital(hospital);
                        ArrayAdapter<CharSequence> weatherAdapter = (ArrayAdapter<CharSequence>) weather.getAdapter();
                        int weatherPosition = weatherAdapter.getPosition(weatherValue);
                        if (weatherPosition != -1) {
                            weather.setSelection(weatherPosition);
                        }

                        String conditionValue = cursor.getString(cursor.getColumnIndex("condition"));
                        ArrayAdapter<CharSequence> conditionAdapter = (ArrayAdapter<CharSequence>) condition.getAdapter();
                        int conditionPosition = conditionAdapter.getPosition(conditionValue);
                        if (conditionPosition != -1) {
                            condition.setSelection(conditionPosition);
                        }
                    } else {
                        // Handle case where record is not found
                        Toast.makeText(getBaseContext(), "Record not Found", LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onError(String errorMessage) {

                }
            });

    }

    // Method to fetch and display patient details based on the selected patient
    @SuppressLint({"Range", "SetTextI18n"})
    private String fetchPatientDetails(String patientNameOrCNIC) {
        String Name = "";



        // Check if the input is a single number (ID)
        if (patientNameOrCNIC.matches("\\d+")) {
            // Query based on ID

        } else {
            // Assume input is in the format "name - CNIC"
            String[] parts = patientNameOrCNIC.split("-");
            if (parts.length == 2) {
                String name = parts[0].trim();
                String cnic = parts[1].trim();

                // Query based on name or CNIC
                OnlineDB.getData("SELECT * FROM Patient WHERE patient like '" + name + "' OR cnic like " + cnic + " ", this, new OnlineDB.OnGetDataListener() {
                    @Override
                    public void onDataRetrieved(Cursor cursor) {
                        if (cursor != null) {
                            try {
                                if (cursor.moveToFirst()) {
                                    patientID.setText(cursor.getString(cursor.getColumnIndex("id")));
                                    getID = cursor.getString(cursor.getColumnIndex("id"));
                                    patientNameTextView.setText("Name: " + cursor.getString(cursor.getColumnIndex("patient")));
                                   String Name = cursor.getString(cursor.getColumnIndex("patient"));
                                    String birth = cursor.getString(cursor.getColumnIndex("birth"));
                                    String gender = cursor.getString(cursor.getColumnIndex("gender"));
                                    String cnic1 = cursor.getString(cursor.getColumnIndex("cnic"));
                                    int age = calculateAge(birth);

                                    String patientDetails = "Age: " + age + "   CNIC: " + cnic1 + "   Gender: " + gender;
                                    clinicTextView.setText(patientDetails);
                                    addressTextView.setText("Address: " + cursor.getString(cursor.getColumnIndex("address")));
                                } else {
                                    // Handle case where patient is not found
                                    patientNameTextView.setText("Patient not found");
                                    clinicTextView.setText("");
                                    addressTextView.setText("");
                                }
                            } finally {
                                cursor.close();
                            }
                        } else {
                            // Handle invalid input or null cursor
                            patientNameTextView.setText("Invalid input or null cursor");
                            clinicTextView.setText("");
                            addressTextView.setText("");
                        }
                    }

                    @Override
                    public void onError(String errorMessage) {

                    }
                });
            }
        }



        return Name;
    }

    @SuppressLint({"Range", "SetTextI18n"})
    private String fetchHospital(String hospital) {
        String name="";
        OnlineDB.getData("Select * From Hospital Where hospital = '" + hospital + "' " , this, new OnlineDB.OnGetDataListener() {
            @Override
            public void onDataRetrieved(Cursor cursor) {

                if (cursor.moveToFirst()) {
                    hospital_id.setText(cursor.getString(cursor.getColumnIndex("id")));
                    hospitalName.setText("Name: " + cursor.getString(cursor.getColumnIndex("hospital")));
                    String capacity = cursor.getString(cursor.getColumnIndex("capcity"));
                    String doctor = cursor.getString(cursor.getColumnIndex("doctor"));
                    String hospitalDetails = "Capacity: " + capacity + ", Doctor: " + doctor;
                    hospitalcap.setText(hospitalDetails);

                    hospitalLocation.setText("Address: " + cursor.getString(cursor.getColumnIndex("Hlocation")));
                } else {
                    // Handle case where hospital is not found
                    hospitalName.setText("Hospital not found");

                }
            }

            @Override
            public void onError(String errorMessage) {

            }
        });
        return name;
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
    //    @SuppressLint("SimpleDateFormat")
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
    private void update() {
        String PID, HID;
        TextView bill_number = findViewById(R.id.billnumber);
        PID = patientID.getText().toString();
        HID = hospital_id.getText().toString();
        String dateText = date.getText().toString();
        EditText lat1 = findViewById(R.id.latitudeEditTextR);
        EditText lng1 = findViewById(R.id.longitudeEditTextR);
        String lat = lat1.getText().toString();
        String lng = lng1.getText().toString();
        Spinner weather = findViewById(R.id.weatherSpinner);
        Spinner condition = findViewById(R.id.conditionspinner);
        String weath = weather.getSelectedItem().toString();
        String cond = condition.getSelectedItem().toString();
        String billNumber = bill_number.getText().toString(); // Assuming bill_number is the TextView containing the ID

        if (PID.isEmpty() || DID.isEmpty() || HID.isEmpty() || dateText.isEmpty() || weath.isEmpty() || cond.isEmpty() || lat.isEmpty() || lng.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        int diseaseid = Integer.parseInt(DID);
        int patientid = Integer.parseInt(PID);
        int hospitalid = Integer.parseInt(HID);

        Double latitude = Double.parseDouble(lat);
        Double longitude = Double.parseDouble(lng);
        int bill=Integer.parseInt(billNumber);

        try {
            JSONObject jsonData = new JSONObject();
            try {
                jsonData.put("date", dateText);
                jsonData.put("patientID", patientid);
                jsonData.put("diseaseID", diseaseid);
                jsonData.put("hospitalID", hospitalid);
                jsonData.put("lat", latitude);
                jsonData.put("`long`", longitude);
                jsonData.put("`condition`", cond);
                jsonData.put("weather", weath);


            } catch (JSONException e) {
                e.printStackTrace();
                return;
            }
            OnlineDB.updateData("Record", bill, jsonData.toString());
            Toast.makeText(this, "Data updated successfully", Toast.LENGTH_SHORT).show();
            clearall();

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to update data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    private void clearall(){
        EditText lat = findViewById(R.id.latitudeEditTextR);
        EditText lng = findViewById(R.id.longitudeEditTextR);
        TextView bill_number = findViewById(R.id.billnumber);
        getrecord.setText(" ");
        patientSearchSpinner.setText(" ");
        hospitalSpinner.setText(" ");
        diseaseSpinner.setText(" ");
        date.setText(" ");
        lat.setText(" ");
        lng.setText(" ");
        bill_number.setText("Bill NO");
        patientID.setText(" ");
        patientNameTextView.setText("Name:");
        clinicTextView.setText("Cnic:  Age:  Gender:");
        addressTextView.setText("Address: ");
        hospital_id.setText(" ");
        hospitalName.setText("Name: ");
        hospitalcap.setText("Capacity:  Doctor: ");
        hospitalLocation.setText("Address: ");
    }

}