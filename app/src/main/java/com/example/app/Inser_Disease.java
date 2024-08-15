package com.example.app;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
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
import com.example.gis.OnlineDB;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Inser_Disease#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Inser_Disease extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Inser_Disease() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Inser_Disease.
     */
    // TODO: Rename and change types and number of parameters
    public static Inser_Disease newInstance(String param1, String param2) {
        Inser_Disease fragment = new Inser_Disease();
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
    EditText fetchlocation;

    private View view;
    @SuppressLint("WrongViewCast")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        try {
            date = view.findViewById(R.id.dbirth);
            date.setOnClickListener(v -> showDatePickerDialog());
            fetchname = view.findViewById(R.id.name);
            fetchlocation = view.findViewById(R.id.location);
            Button insertdata = view.findViewById(R.id.insert);
            insertdata.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    insert();

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
        view = inflater.inflate(R.layout.fragment_inser__disease, container, false);
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
    private void insert(){

        if (view == null) {
            return;
        }

        EditText fetchname = view.findViewById(R.id.dname);
        EditText fetchaddress = view.findViewById(R.id.location);

        // Assuming `date` is also an EditText, initialize it similarly
        EditText date = view.findViewById(R.id.dbirth);
        String address = fetchaddress.getText().toString();
        String name = fetchname.getText().toString();

        String dateText = date.getText().toString();
        if (name.isEmpty() ||  address.isEmpty() || dateText.isEmpty() ) {
            Toast.makeText(getContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            JSONObject requestData = new JSONObject();
            requestData.put("disease", name);
            requestData.put("Dlocation", address);
            requestData.put("Ddate", dateText);
            String tableName = "Disease"; // Assuming the table name is Patient
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
//        EditText fetchname = view.findViewById(R.id.dname);
//        EditText fetchaddress = view.findViewById(R.id.location);
//
//        // Assuming `date` is also an EditText, initialize it similarly
//        EditText date = view.findViewById(R.id.dbirth);
//        String address = fetchaddress.getText().toString();
//        String name = fetchname.getText().toString();
//
//        String dateText = date.getText().toString();
//        if (name.isEmpty() ||  address.isEmpty() || dateText.isEmpty() ) {
//            Toast.makeText(getContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        try {
//            DatabaseHelper dbHelper = new DatabaseHelper(getContext());
//            SQLiteDatabase db = dbHelper.getWritableDatabase();
//
//            String query = "INSERT INTO Disease (name, location, date) VALUES (?, ?, ?)";
//            SQLiteStatement statement = db.compileStatement(query);
//
//            statement.bindString(1, name);
//            statement.bindString(2, address);
//            statement.bindString(3, dateText);
//
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