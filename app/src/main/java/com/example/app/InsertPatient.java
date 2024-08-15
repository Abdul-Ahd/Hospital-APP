package com.example.app;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
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
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.gis.DatabaseHelper;
//import com.example.gis.MainActivity;
import com.example.gis.OnlineDB;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link InsertPatient#newInstance} factory method to
 * create an instance of this fragment.
 */
public class InsertPatient extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public InsertPatient() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment InsertPatient.
     */
    // TODO: Rename and change types and number of parameters
    public static InsertPatient newInstance(String param1, String param2) {
        InsertPatient fragment = new InsertPatient();
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
    EditText date;
    EditText fetchname;
    EditText fetchcnic;
    EditText fetchaddress;
    private View view;
    @SuppressLint("WrongViewCast")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        try {
            date = view.findViewById(R.id.birth);
            date.setOnClickListener(v -> showDatePickerDialog());
             fetchname = view.findViewById(R.id.name);
            fetchcnic = view.findViewById(R.id.Cnic);
            fetchaddress = view.findViewById(R.id.address);

            Button insertdata = view.findViewById(R.id.insert);
            insertdata.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    insert();

                }
            });
            fetchcnic.addTextChangedListener(new TextWatcher() {
                boolean isFormatting; // Variable to prevent infinite recursion
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    // No action needed
                }
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    // No action needed
                }
                @Override
                public void afterTextChanged(Editable s) {
                    // Remove hyphens and non-numeric characters
                    String input = s.toString().replaceAll("[^0-9]", "");

                    // Check if the input is exactly 13 characters long
                    if (input.length() != 13) {
                        fetchcnic.setError("CNIC must be exactly 13 digits long");
                        return;
                    }

                    // Format the input as "33100-1542754-3"
                    String formattedCnic = input.substring(0, 5) + "-" + input.substring(5, 12) + "-" + input.substring(12);

                    // Prevent infinite recursion while setting text
                    if (!isFormatting) {
                        isFormatting = true;
                        fetchcnic.setText(formattedCnic);
                        fetchcnic.setSelection(fetchcnic.length()); // Move cursor to the end of text
                        isFormatting = false;
                    }
                }
            });

        } catch (Exception e) {
            Log.e("DatabaseHelper", "Error accessing grid: " + e.getMessage());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_insert_patient, container, false);
        return view;
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
    private void insert() {
        if (view == null) {
            return;
        }

        EditText fetchName = view.findViewById(R.id.name);
        EditText fetchCnic = view.findViewById(R.id.Cnic);
        EditText fetchAddress = view.findViewById(R.id.address);
        EditText fetchDate = view.findViewById(R.id.birth);

        String name = fetchName.getText().toString();
        String cnic = fetchCnic.getText().toString();
        String address = fetchAddress.getText().toString();
        String date = fetchDate.getText().toString();

        if (name.isEmpty() || cnic.isEmpty() || address.isEmpty() || date.isEmpty()) {
            Toast.makeText(getContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        String cnicWithoutHyphens = cnic.replaceAll("-", ""); // Remove all hyphens
        long cnicNumber = Long.parseLong(cnicWithoutHyphens);

        RadioGroup radioGroupGender = view.findViewById(R.id.gender);
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

        if (cnicWithoutHyphens.length() < 13) {
            Toast.makeText(getContext(), "CNIC must be 13 digits long", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            JSONObject requestData = new JSONObject();
            requestData.put("patient", name);
            requestData.put("cnic", cnicNumber);
            requestData.put("address", address);
            requestData.put("birth", date);
            requestData.put("gender", gender);
            String tableName = "Patient"; // Assuming the table name is Patient
            String data = requestData.toString();
            OnlineDB.postData(tableName, data,getContext());

        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Failed to insert data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

//    private void insert(){
//
//        if (view == null) {
//            return;
//        }
//
//        EditText fetchname = view.findViewById(R.id.name);
//        EditText fetchcnic = view.findViewById(R.id.Cnic);
//        EditText fetchaddress = view.findViewById(R.id.address);
//
//        // Assuming `date` is also an EditText, initialize it similarly
//        EditText date = view.findViewById(R.id.birth);
//        String address = fetchaddress.getText().toString();
//        String name = fetchname.getText().toString();
//        String cnic = fetchcnic.getText().toString();
//        String dateText = date.getText().toString();
//        if (name.isEmpty() || cnic.isEmpty() || address.isEmpty() || dateText.isEmpty() ) {
//            Toast.makeText(getContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
//            return;
//        }
//        String cnicWithoutHyphens = cnic.replaceAll("-", ""); // Remove all hyphens
//        long cnicNumber = Long.parseLong(cnicWithoutHyphens);
//         // Assuming `date` is initialized
//        RadioGroup radioGroupGender = view.findViewById(R.id.gender);
//        int selectedId = radioGroupGender.getCheckedRadioButtonId();
//        String gender = null;
//
//        if (selectedId == R.id.male) {
//            gender = "Male";
//        } else if (selectedId == R.id.female) {
//            gender = "Female";
//        } else {
//            gender = null;
//        }
//    if (gender== null){
//    Toast.makeText(getContext(), "Select Gender", Toast.LENGTH_SHORT).show();
//    return;
//}
//        if (cnicWithoutHyphens.length() < 13) {
//            Toast.makeText(getContext(), "CNIC must be 13 digits long", Toast.LENGTH_SHORT).show();
//            return;
//        }
//        try {
//            DatabaseHelper dbHelper = new DatabaseHelper(getContext());
//            SQLiteDatabase db = dbHelper.getWritableDatabase();
//
//            String query = "INSERT INTO Patient (name, cnic, address, birth, gender) VALUES (?, ?, ?, ?, ?)";
//            SQLiteStatement statement = db.compileStatement(query);
//
//            statement.bindString(1, name);
//            statement.bindLong(2, cnicNumber);
//            statement.bindString(3, address);
//            statement.bindString(4, dateText);
//            statement.bindString(5, gender);
//
//            long rowId = statement.executeInsert();
//            if (rowId != -1) {
//                Toast.makeText(getContext(), "Data inserted successfully", Toast.LENGTH_SHORT).show();
//            } else {
//                Toast.makeText(getContext(), "Failed to insert data", Toast.LENGTH_SHORT).show();
//            }
//
//            db.close();
//        } catch (Exception e) {
//            e.printStackTrace();
//            Toast.makeText(getContext(), "Failed to insert data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
//        }
//
//
//    }
}