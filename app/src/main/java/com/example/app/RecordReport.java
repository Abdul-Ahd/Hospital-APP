package com.example.app;

import static android.widget.Toast.LENGTH_SHORT;
import static com.example.gis.UserCheck.PERF_DIS;
import static com.example.gis.UserCheck.PERF_ROLE;
import static com.example.gis.UserCheck.PERF_USER;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.gis.DatabaseHelper;
import com.example.gis.OnlineDB;

import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import ir.beigirad.zigzagview.ZigzagView;

public class RecordReport extends AppCompatActivity {
    private DatabaseHelper database;
    private TextView patientNameTextView, clinicTextView, patientID, addressTextView;
    private AutoCompleteTextView patientSearchSpinner;
    String getID;
    GridView gridview, DoctorTreatment;
    private SimpleCursorAdapter adapter;
    private ZigzagView zigzagView;
    private ImageView imageView;
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static final int REQUEST_CREATE_DOCUMENT = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_report);
        patientSearchSpinner = findViewById(R.id.patientSpinnertoget);
        patientNameTextView = findViewById(R.id.patientNameTextView);
        clinicTextView = findViewById(R.id.ageCNICGenderTextView);
        patientID = findViewById(R.id.pID);
        addressTextView = findViewById(R.id.addressTextView);
        gridview = findViewById(R.id.reyclerview);
        zigzagView = findViewById(R.id.zigzagView);
        //for doctor info

        DoctorTreatment = findViewById(R.id.docTreatment);


        database = new DatabaseHelper(this);
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
            Button print = findViewById(R.id.print);
        print.setVisibility(View.GONE);
            print.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    // Permission has already been granted, perform your operation here
//                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE);

                    // For example, save PDF file
//                    Bitmap bitmap = createBitmapFromView(zigzagView);

                    Bitmap bitmap = Bitmap.createBitmap(zigzagView.getWidth(),zigzagView.getHeight(),Bitmap.Config.ARGB_8888);
                    Canvas canvas = new Canvas(bitmap);
                    zigzagView.draw(canvas);
//                    imageView.setImageBitmap(bitmap);
                    if (bitmap != null) {
                        // Convert bitmap to PDF
                        saveBitmapAsPdf(bitmap);
                    } else {
                        // Handle error: Unable to capture bitmap
                        Toast.makeText(getBaseContext(), "Error capturing content", Toast.LENGTH_SHORT).show();
                    }

                }
            });

    }
    private Bitmap createBitmapFromView(View view) {
        int visibleHeight = view.getHeight();

        // Create a bitmap with the same width as the view and the height of the visible portion
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), visibleHeight, Bitmap.Config.ARGB_8888);

        // Create a canvas using the bitmap
        Canvas canvas = new Canvas(bitmap);

        // Calculate the scroll offset to capture only the visible portion
        int scrollY = view.getScrollY();

        // Translate the canvas to the negative scroll offset to capture the visible portion
        canvas.translate(0, -scrollY);

        // Draw the view onto the canvas
        view.draw(canvas);

        return bitmap;
    }
    private void saveBitmapAsPdf(Bitmap bitmap) {
        // Create a new PdfDocument
        PdfDocument document = new PdfDocument();

        // Create a PageInfo
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(bitmap.getWidth(), bitmap.getHeight(), 1).create();

        // Start a new page
        PdfDocument.Page page = document.startPage(pageInfo);

        // Get the Canvas from the page
        Canvas canvas = page.getCanvas();

        // Draw the bitmap onto the Canvas
        canvas.drawBitmap(bitmap, 0, 0, null);
        imageView.setImageBitmap(bitmap);
        // Finish the page
        document.finishPage(page);

        // Close the document
        document.close();

        // Create an intent for saving the file using SAF
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/pdf");
        intent.putExtra(Intent.EXTRA_TITLE, "Report.pdf");

        // Start the activity for result, expecting a result with the URI of the created document
        startActivityForResult(intent, REQUEST_CREATE_DOCUMENT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CREATE_DOCUMENT && resultCode == RESULT_OK) {
            if (data != null) {
                Uri uri = data.getData();
                if (uri != null) {
                    // Write the PDF data to the selected URI
                    writePdfDataToUri(uri);
                }
            }
        }
    }

    private void writePdfDataToUri(Uri uri) {
        try {
            OutputStream outputStream = getContentResolver().openOutputStream(uri);
            if (outputStream != null) {
                // Write the PDF data to the OutputStream
                // Here you would write the PDF data generated earlier to the OutputStream
                Toast.makeText(this, "PDF saved successfully", Toast.LENGTH_SHORT).show();
                outputStream.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to save PDF", Toast.LENGTH_SHORT).show();
        }
    }


    @SuppressLint("Range")
    private void loadPatientNames() {
        String userInput = patientSearchSpinner.getText().toString().toLowerCase();
        List<String> patientNames = new ArrayList<>();
        OnlineDB.getData("Select * from Patient", this, new OnlineDB.OnGetDataListener() {
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
    Cursor cursor = null;

    @SuppressLint({"Range", "SetTextI18n"})
    private String fetchPatientDetails(String patientNameOrCNIC) {
        String Name = "";
        // Check if the input is a single number (ID)
        if (patientNameOrCNIC.matches("\\d+")) {
            Toast.makeText(this, "id", LENGTH_SHORT).show();
            // Query based on ID
            OnlineDB.getData("SELECT * FROM Patient WHERE id = "+patientNameOrCNIC+ " ", this, new OnlineDB.OnGetDataListener() {
                @Override
                public void onDataRetrieved(Cursor cursor1) {
                    cursor = cursor1;
                }

                @Override
                public void onError(String errorMessage) {

                }
            });
        } else {
            if (PERF_ROLE.equals("Doctor")) {
                String[] parts = patientNameOrCNIC.split("-"); // Split the string based on '-'
                String patientName = parts[0].trim();
//                String patientName = null; // Replace null with the actual patient name
                OnlineDB.getData("SELECT DISTINCT u.user AS doctor_name " +
                        "FROM patient p " +
                        "JOIN record r ON p.id = r.patientID " +
                        "JOIN doctorlogin dl ON r.diseaseID = dl.diseaseID AND r.hospitalID = dl.hospitalID " +
                        "JOIN user u ON dl.id = u.id " +
                        "WHERE p.patient = '" + patientName + "'", this, new OnlineDB.OnGetDataListener() {
                    @Override
                    public void onDataRetrieved(Cursor cursor) {

                    }
                    @Override
                    public void onError(String errorMessage) {
                        // Handle error
                    }
                });
            }
            // Assume input is in the format "name - CNIC"
            String[] parts = patientNameOrCNIC.split("-");
            if (parts.length == 2) {
                String name = parts[0].trim();
                String cnic = parts[1].trim();
                Toast.makeText(this, name + " " + cnic, LENGTH_SHORT).show();
                // Query based on name or CNIC
                String query = "SELECT Patient.patient, Patient.id AS patientID, Patient.birth, Patient.gender, Patient.cnic, Patient.address, " +
                        "Disease.disease, Hospital.hospital, Record.date, Record.weather, Record.condition " +
                        "FROM Record " +
                        "INNER JOIN Patient ON Record.PatientID = Patient.ID " +
                        "INNER JOIN Disease ON Record.DiseaseID = Disease.ID " +
                        "INNER JOIN Hospital ON Record.HospitalID = Hospital.ID " +
                        "WHERE (Patient.patient LIKE '%" + name + "%' OR Patient.cnic LIKE '%" + cnic + "%') AND (record.diseaseID = '" + PERF_DIS +"')";

                OnlineDB.getData(query, this, new OnlineDB.OnGetDataListener() {
                    @Override
                    public void onDataRetrieved(Cursor cursor) {
                        if (cursor != null) {
                            try {
                                MatrixCursor matrixCursor = new MatrixCursor(new String[]{"_id", "disease", "date", "condition", "weather", "hospital"});
                                if (cursor.moveToFirst()) {
                                    do {
                                        // Fetch patient details
                                        patientID.setText(cursor.getString(cursor.getColumnIndex("patientID")));
                                        getID = cursor.getString(cursor.getColumnIndex("patientID"));
                                        patientNameTextView.setText("Name: " + cursor.getString(cursor.getColumnIndex("patient")));
                                        String birth = cursor.getString(cursor.getColumnIndex("birth"));
                                        String gender = cursor.getString(cursor.getColumnIndex("gender"));
                                        String cnic1 = cursor.getString(cursor.getColumnIndex("cnic"));
                                        int age = calculateAge(birth);
                                        String patientDetails = "Age: " + age + "   CNIC: " + cnic1 + "   Gender: " + gender;
                                        clinicTextView.setText(patientDetails);
                                        addressTextView.setText("Address: " + cursor.getString(cursor.getColumnIndex("address")));

                                        // Fetch patient records
                                        String hospitalID = cursor.getString(cursor.getColumnIndex("hospital"));
                                        String diseaseID = cursor.getString(cursor.getColumnIndex("disease"));
                                        String date = cursor.getString(cursor.getColumnIndex("date"));
                                        String condition = cursor.getString(cursor.getColumnIndex("condition"));
                                        String weather = cursor.getString(cursor.getColumnIndex("weather"));
                                        String Hospitalname = "Treatment In" + "\n" + hospitalID;
                                        String Dateview = "Dated: " + date;
                                        String Cond = "Condition at that time was " + condition;
                                        String weath = "On a " + weather + " Day";

                                        // Add records to the cursor
                                        matrixCursor.addRow(new Object[]{cursor.getPosition(), diseaseID, Dateview, Cond, weath, Hospitalname});
                                    } while (cursor.moveToNext());

                                    // Add patient name to the list
                                    String[] fromColumns = {
                                            "disease",
                                            "date",
                                            "condition",
                                            "weather",
                                            "hospital",
                                    };

                                    int[] toViews = {
                                            R.id.diseas,
                                            R.id.date,
                                            R.id.cond,
                                            R.id.weather,
                                            R.id.hospital,
                                    };

                                    // Create a SimpleCursorAdapter to map the data to views
                                    adapter = new SimpleCursorAdapter(
                                            getBaseContext(),
                                            R.layout.report_item, // Layout for each item
                                            matrixCursor, // Cursor with data to display
                                            fromColumns,
                                            toViews,
                                            0);
                                    // Loop through the cursor to add disease ID(s) to the list
                                    gridview.setAdapter(adapter);
                                } else {
                                    // Handle case where patient is not found
                                    patientNameTextView.setText("Patient not found");
                                    clinicTextView.setText("");
                                    addressTextView.setText("");
                                }
                            } finally {
                                cursor.close();
                            }
                            OnlineDB.getData("SELECT * FROM doc_effort d\n" +
                                    " LEFT JOIN medic m ON d.id = m.id\n" +
                                    " LEFT JOIN medicaltest t ON d.id = t.id\n" +
//                                    " LEFT JOIN patientstatus s ON s.patientID = d.patientId\n" +
                                    " Left Join doctor doc on doc.id  = d.doctorID" +
                                    " Left JOIN hospital h on d.hospitalId = h.id " +
                                    " WHERE d.patientID = '"+getID+"' ", getBaseContext(), new OnlineDB.OnGetDataListener() {
                                @Override
                                public void onDataRetrieved(Cursor cursor) {
                                    MatrixCursor matrixCursor1 = new MatrixCursor(new String[]{"_id", "medicine", "test", "date","doctor","checkup"});
                                    if (cursor != null && cursor.moveToFirst()) {
                                        String previousId = "";
                                        StringBuilder medicines = new StringBuilder();
                                        StringBuilder tests = new StringBuilder();
                                        String date = null;
                                        String mad="1";
                                        String te = "1";
                                        String doc = null;
                                        String check1 = null;
//                                        String checkup = cursor.getString(cursor.getColumnIndex("checkup"));
//                                        String stauts = cursor.getString(cursor.getColumnIndex("status"));
//                                        String hospital = cursor.getString(cursor.getColumnIndex("hospital"));
//                                        String Doctor = cursor.getString(cursor.getColumnIndex("doctor"));
//                                        DocHospital.setText("Hospital : "+hospital);
//                                        PatientStatus.setText("Status : "+stauts);
//                                        CheckUpType.setText("Checkup Type : "+checkup);
                                        do {
                                            String id = cursor.getString(cursor.getColumnIndex("id"));
                                            if (!id.equals(previousId) && !previousId.isEmpty()) {
                                                matrixCursor1.addRow(new Object[]{previousId, medicines.toString(), tests.toString(),date, doc,check1});
                                                medicines.setLength(0);
                                                tests.setLength(0);
                                            }
                                            String medicine = cursor.getString(cursor.getColumnIndex("medicine"));
                                            String test = cursor.getString(cursor.getColumnIndex("tests"));
                                            String date2 = cursor.getString(cursor.getColumnIndex("edate"));
                                            String checkup = cursor.getString(cursor.getColumnIndex("checkup"));
                                            String hospital = cursor.getString(cursor.getColumnIndex("hospital"));
                                            String Doctor1 = cursor.getString(cursor.getColumnIndex("doctorN"));
                                            String date1 = trimTimeFromDate(date2);
                                            String check = checkup+" In "+ hospital;
                                            String Doctor= "Doctor: "+ Doctor1;
                                                date = date1;
                                                check1 = check;
                                                doc =Doctor;

                                            // Add medicine and test for current ID
                                            if (medicine != null && !mad.equals(medicine) ) {
                                                medicines.append(medicine).append("\n");

                                            }
                                            if (test != null && !te.equals(test) ) {
                                                tests.append(test).append("\n");

                                            }

                                            previousId = id;
                                            mad=medicine;
                                            te=test;
                                        } while (cursor.moveToNext());
                                        // Add the combined data for the last ID encountered
                                    matrixCursor1.addRow(new Object[]{previousId, medicines.toString(), tests.toString(),date,doc,check1});
                                    }
                                    String[] fromColumns = {
                                            "medicine",
                                            "test",
                                            "date",
                                            "doctor",
                                            "checkup"
                                    };

                                    int[] toViews = {
                                            R.id.name,
                                            R.id.hospital,
                                            R.id.date,
                                            R.id.doctor,
                                            R.id.checkup
                                    };
                                    SimpleCursorAdapter adapter1 = new SimpleCursorAdapter( getBaseContext(),
                                            R.layout.meditest, // Layout for each item
                                            matrixCursor1, // Cursor with data to display
                                            fromColumns,
                                            toViews,
                                            0);
                                    DoctorTreatment.setAdapter(adapter1);
                                    }

                                @Override
                                public void onError(String errorMessage) {

                                }
                            });
                        } else {
                            // Handle invalid input or null cursor
                            patientNameTextView.setText("Invalid input or null cursor");
                            clinicTextView.setText("");
                            addressTextView.setText("");
                        }
                    }

                    @Override
                    public void onError(String errorMessage) {
                        // Handle error
                    }
                });

            }
        }

        return Name;
    }

    private String trimTimeFromDate(String dateTime) {
        if (dateTime.contains("T")) {
            return dateTime.substring(0, dateTime.indexOf("T"));
        }
        return dateTime;
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
}