package com.example.app;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.database.Cursor;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.gis.OnlineDB;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link insert_doctor#newInstance} factory method to
 * create an instance of this fragment.
 */
public class insert_doctor extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    EditText date;
    EditText docName;
    private AutoCompleteTextView hospitalSpinner, diseaseSpinner;
    private View view;
    String DID,HID;
    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment insert_doctor.
     */
    // TODO: Rename and change types and number of parameters
    public static insert_doctor newInstance(String param1, String param2) {
        insert_doctor fragment = new insert_doctor();
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
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_insert_doctor, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        try {
            date = view.findViewById(R.id.birth);
            date.setOnClickListener(v -> showDatePickerDialog());

            hospitalSpinner = view.findViewById(R.id.HSpinner);
            diseaseSpinner = view.findViewById(R.id.DSpinner);


            Button insertDocdata = view.findViewById(R.id.insert);
            insertDocdata.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                insertDoc();

                }
            });
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Error accessing grid: " + e.getMessage());
        }

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
        hospitalSpinner.setOnItemClickListener((parent, view1, position, id) -> {
            String hospital = (String) parent.getItemAtPosition(position);
            // Fetch patient details based on the selected patient
            fetchHospital(hospital);
        });
        diseaseSpinner.setOnItemClickListener((parent, view1, position, id) -> {
            String disease = (String) parent.getItemAtPosition(position);
            // Fetch patient details based on the selected patient
            fetchDisease(disease);
        });

    }
    @SuppressLint("Range")
    private void loadDiseasesByName() {

        String userInput = diseaseSpinner.getText().toString();

        OnlineDB.getData("Select * From Disease Where disease like '%"+userInput+"%'",getContext(),new OnlineDB.OnGetDataListener() {

            @Override
            public void onDataRetrieved(Cursor cursor) {
                List<String> diseaseNames = new ArrayList<>();
                if(cursor != null && cursor.moveToFirst()) {
                    do {
                        String name = cursor.getString(cursor.getColumnIndex("disease"));
                        diseaseNames.add(name);
                    } while (cursor.moveToNext());
                }
                ArrayAdapter<String> Dadapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_dropdown_item_1line, diseaseNames);
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
        OnlineDB.getData("Select * From Hospital Where hospital like '%"+userInput+"%'",getContext(),new OnlineDB.OnGetDataListener() {
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
                ArrayAdapter<String>  Hadapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_dropdown_item_1line, hospitalNames);
                hospitalSpinner.setAdapter(Hadapter);
            }
            @Override
            public void onError(String errorMessage) {

            }
        });


    }
    @SuppressLint({"Range", "SetTextI18n"})
    private void fetchDisease(String hospital) {
        OnlineDB.getData("Select * From Disease Where disease like '" + hospital + "' ", getContext(), new OnlineDB.OnGetDataListener() {
            @Override
            public void onDataRetrieved(Cursor cursor) {
                try {
                    if (cursor.moveToFirst()) {
                        DID = cursor.getString(cursor.getColumnIndex("id"));
                    } else {
                        // Handle case where hospital is not found
                        Toast.makeText(getContext(), "Disease not Found", Toast.LENGTH_SHORT).show();
                    }
                }
                catch (Exception e){
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(String errorMessage) {

            }
        });

    }
    @SuppressLint({"Range", "SetTextI18n"})
    private void fetchHospital(String hospital) {
        OnlineDB.getData("Select * From Hospital Where hospital like '" + hospital + "' ", getContext(), new OnlineDB.OnGetDataListener() {
            @Override
            public void onDataRetrieved(Cursor cursor) {
                try {
                    if (cursor.moveToFirst()) {
                        HID = cursor.getString(cursor.getColumnIndex("id"));
                    } else {
                        // Handle case where hospital is not found
                        Toast.makeText(getContext(), "Hospital not Found", Toast.LENGTH_SHORT).show();
                    }
                }
                catch (Exception e){
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(String errorMessage) {

            }
        });

    }

    private Calendar selectedDate;

    private void showDatePickerDialog() {
        final Calendar currentDate = Calendar.getInstance();
        int year = currentDate.get(Calendar.YEAR);
        int month = currentDate.get(Calendar.MONTH);
        int day = currentDate.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                (DatePicker view, int year1, int monthOfYear, int dayOfMonth) -> {
                    selectedDate = Calendar.getInstance();
                    selectedDate.set(year1, monthOfYear, dayOfMonth);
                    updateDateEditText();
                }, year, month, day);

        datePickerDialog.show();
    }

    private void updateDateEditText() {

        if (selectedDate != null) {
            int year = selectedDate.get(Calendar.YEAR);
            int month = selectedDate.get(Calendar.MONTH) + 1; // Month is zero-based
            int day = selectedDate.get(Calendar.DAY_OF_MONTH);

            date.setText(String.format("%d-%02d-%02d", year, month, day));
        }
    }

    private void insertDoc() {
        if (view == null) {
            return;
        }

        EditText docName = view.findViewById(R.id.name);
        EditText fetchDate = view.findViewById(R.id.birth);
        String name = docName.getText().toString();
        String date = fetchDate.getText().toString();

        RadioGroup radioGroupGender = view.findViewById(R.id.gender);

        if (name.isEmpty() || date.isEmpty() || DID.isEmpty() || HID.isEmpty()) {
            Toast.makeText(getContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        int selectedId = radioGroupGender.getCheckedRadioButtonId();
        String gender = null;

        if (selectedId == R.id.male) {
            gender = "Male";
        } else if (selectedId == R.id.female) {
            gender = "Female";
        }

        if (gender == null) {
            Toast.makeText(getContext(), "Select Gender", Toast.LENGTH_SHORT).show();
            return;
        }
        int diseaseID = Integer.parseInt(DID);
        int hospitalID = Integer.parseInt(HID);
        try {
            JSONObject requestData = new JSONObject();
            requestData.put("doctorN", name);
            requestData.put("idDisease", diseaseID);
            requestData.put("idHospital", hospitalID);
            requestData.put("docBirth", date);
            requestData.put("gender", gender);
            String tableName = "doctor"; // Assuming the table name is Patient
            String data = requestData.toString();
            OnlineDB.postData(tableName, data,getContext());

        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Failed to insert data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }


}


