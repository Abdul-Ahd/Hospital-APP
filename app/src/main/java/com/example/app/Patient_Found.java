package com.example.app;

import static com.example.gis.UserCheck.PERF_DIS;
import static com.example.gis.UserCheck.PERF_HOP;
import static com.example.gis.UserCheck.PERF_ROLE;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gis.DatabaseHelper;
import com.example.gis.OnlineDB;
import com.google.android.material.button.MaterialButton;

import org.jetbrains.annotations.Nullable;
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

public class Patient_Found extends AppCompatActivity implements OnlineDB.OnGetDataListener {
    private EditText date;
    private DatabaseHelper database;
    private AutoCompleteTextView patientSearchSpinner, hospitalSpinner, diseaseSpinner;
    private TextView patientNameTextView, clinicTextView, patientID, addressTextView;
    private TextView hospitalName, hospitalLocation, hospital_id, hospitalcap,diseaseNameForDoctor;
    private MaterialButton save;
    private Cursor cursor,dcursor,pcursor;
    String DID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_found);
        date = findViewById(R.id.date);
        date.setOnClickListener(v -> showDatePickerDialog(date));
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
        save = findViewById(R.id.saveButton);
        diseaseNameForDoctor = findViewById(R.id.DID);
        diseaseNameForDoctor.setVisibility(View.GONE);
        if (PERF_ROLE.equals("Doctor")) {
        findViewById(R.id.diseselayout).setVisibility(View.GONE);
        findViewById(R.id.hospitallayout).setVisibility(View.GONE);
        diseaseNameForDoctor.setVisibility(View.VISIBLE);
        fetchHospital(PERF_HOP);
        fetchDname(PERF_DIS);
        }
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              insert();
            }
        });
        // Open or create the database
        database = new DatabaseHelper(this);

        // Load patient names into AutoCompleteTextView

//
//
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
        diseaseSpinner.addTextChangedListener(new TextWatcher() {
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
                loadDiseasesByName();
            }


            @Override
            public void afterTextChanged(Editable s) {
                // This method is called after the text has been changed.
            }
        });
        hospitalSpinner.addTextChangedListener(new TextWatcher() {
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
                loadHospitalsByName();
            }

            @Override
            public void afterTextChanged(Editable s) {
                // This method is called after the text has been changed.
            }
        });
//         Listener to handle patient selection
        patientSearchSpinner.setOnItemClickListener((parent, view, position, id) -> {
            String selectedPatient = (String) parent.getItemAtPosition(position);
            // Fetch patient details based on the selected patient
            fetchPatientDetails(selectedPatient);
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



    }
    @Override
    public void onDataRetrieved(Cursor cursor1) {
        cursor =cursor1;
    }


    @Override
    public void onError(String errorMessage) {
        Toast.makeText(this, "Error: " + errorMessage, Toast.LENGTH_SHORT).show();
    }
    // Method to load patient names from the database into the AutoCompleteTextView
    @SuppressLint("Range")
    private void loadPatientNames() {
        String userInput = patientSearchSpinner.getText().toString().toLowerCase();
        String name;
        try {
            OnlineDB.getData("SELECT * FROM patient where patient like '%" + userInput + "%' OR cnic like '%" + userInput + "%'", this, this);
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
    private  void fetchDname(String id){
        OnlineDB.getData("Select * From Disease Where id like '" + id + "'", this, new OnlineDB.OnGetDataListener() {
            @Override
            public void onDataRetrieved(Cursor cursor) {
                try {
                    if (cursor != null && cursor.moveToFirst()) {
                     diseaseNameForDoctor.setText(cursor.getString(cursor.getColumnIndex("disease")));
                    }
                    else {
                        // Handle case where hospital is not found
                        Toast.makeText(getBaseContext(), "Disease not Found", Toast.LENGTH_SHORT).show();
                    }
                }
                catch (Exception e){
                    Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(String errorMessage) {

            }
        });
}

    @SuppressLint({"Range", "SetTextI18n"})
    private void fetchDisease(String hospital) {
        OnlineDB.getData("Select * From Disease Where disease like '" + hospital + "' ", this, new OnlineDB.OnGetDataListener() {
            @Override
            public void onDataRetrieved(Cursor cursor) {
                try {
                    if (cursor.moveToFirst()) {
                        DID = cursor.getString(cursor.getColumnIndex("id"));
                    } else {
                        // Handle case where hospital is not found
                        Toast.makeText(getBaseContext(), "Disease not Found", Toast.LENGTH_SHORT).show();
                    }
                }
                catch (Exception e){
                    Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(String errorMessage) {

            }
        });

    }
    // Method to fetch and display patient details based on the selected patient
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
                    OnlineDB.getData("Select * From Patient Where cnic like "+cnic+" " ,this,this);
                } else {
                    // If input is not a valid CNIC, query based on name
                    OnlineDB.getData("Select * From Patient Where patient like '"+name+"' " ,this,this);
                }

                if (cursor.moveToFirst()) {
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
    @SuppressLint({"Range", "SetTextI18n"})
    private void fetchHospital(String hospital) {
        OnlineDB.getData("Select * From Hospital Where hospital like '" + hospital + "' OR id like '"+hospital+"' ", this, new OnlineDB.OnGetDataListener() {
            @Override
            public void onDataRetrieved(Cursor cursor) {
                try  {

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
                catch (Exception e){
                    Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(String errorMessage) {

            }
        });

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
private void insert(){
        int diseaseid,patientid,hospitalid;
        String PID ,HID;
        if (PERF_ROLE.equals("Doctor")) {
        DID = PERF_DIS;
        HID = PERF_HOP;
        }else {
        HID = hospital_id.getText().toString();
         }
        PID = patientID.getText().toString();
        String dateText = date.getText().toString();
        EditText lat1 = findViewById(R.id.latitudeEditTextR);
        EditText lng1 = findViewById(R.id.longitudeEditTextR);
        String lat =lat1.getText().toString();
        String lng = lng1.getText().toString();
    Spinner weather = findViewById(R.id.weatherSpinner);
    Spinner condition = findViewById(R.id.conditionspinner);
    String weath = weather.getSelectedItem().toString();
    String cond = condition.getSelectedItem().toString();
    if (PID.isEmpty() || DID.isEmpty() || HID.isEmpty() || dateText.isEmpty() || weath.isEmpty()|| cond.isEmpty()|| lat.isEmpty()|| lng.isEmpty()) {
        Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
        return;
    }
    Double latitude = Double.parseDouble(lat);
    Double longitude = Double.parseDouble(lng);
    patientid = Integer.parseInt(PID);
    hospitalid = Integer.parseInt(HID);
    diseaseid =Integer.parseInt(DID);

//    Toast.makeText(this, "id1= "+ patientid+"   idD= " + diseaseid+ "   idH= "+hospitalid, Toast.LENGTH_SHORT).show();
    try {
        JSONObject requestData = new JSONObject();
        requestData.put("date", dateText);
        requestData.put("patientID", patientid);
        requestData.put("diseaseID", diseaseid);
        requestData.put("hospitalID", hospitalid);
        requestData.put("lat", latitude);
        requestData.put("`long`", longitude);
        requestData.put("`condition`", cond);
        requestData.put("weather", weath);
        String tableName = "Record"; // Assuming the table name is Patient
        String data = requestData.toString();
        OnlineDB.postData(tableName, data,this);

    } catch (Exception e) {
        e.printStackTrace();
        Toast.makeText(this, "Failed to insert data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
    }

}


}