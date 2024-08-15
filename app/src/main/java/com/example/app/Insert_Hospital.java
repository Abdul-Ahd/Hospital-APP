package com.example.app;

import android.annotation.SuppressLint;
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
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.gis.DatabaseHelper;
import com.example.gis.OnlineDB;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Insert_Hospital#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Insert_Hospital extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Insert_Hospital() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Insert_Hospital.
     */
    // TODO: Rename and change types and number of parameters
    public static Insert_Hospital newInstance(String param1, String param2) {
        Insert_Hospital fragment = new Insert_Hospital();
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
    EditText capcity;
    EditText fetchname;
    EditText doctor;
    EditText fetchaddress;
    private View view;
    @SuppressLint("WrongViewCast")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        try {

            fetchname = view.findViewById(R.id.name);
            capcity = view.findViewById(R.id.cap);
            doctor=view.findViewById(R.id.doctor);
            fetchaddress = view.findViewById(R.id.address);

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
        view = inflater.inflate(R.layout.fragment_insert__hospital, container, false);
        return view;

    }
    private void insert(){

        if (view == null) {
            return;
        }

        EditText fetchname = view.findViewById(R.id.name);
        EditText fetchcapcity = view.findViewById(R.id.cap);
        EditText fetchdoctor = view.findViewById(R.id.doctor);
        EditText fetchaddress = view.findViewById(R.id.address);
        EditText Hlat = view.findViewById(R.id.latitudeEditTextR);
        EditText Hlong = view.findViewById(R.id.longitudeEditTextR);

        String address = fetchaddress.getText().toString();
        String name = fetchname.getText().toString();
        String doctor = fetchdoctor.getText().toString();
        String cap = fetchcapcity.getText().toString();
        String lat = Hlat.getText().toString();
        String lng = Hlong.getText().toString();

        if (name.isEmpty() || doctor.isEmpty() || address.isEmpty() || cap.isEmpty() || lat.isEmpty() || lng.isEmpty() ) {
            Toast.makeText(getContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }
        Double latitude = Double.parseDouble(lat);
        Double longitude = Double.parseDouble(lng);
        long capcity = Long.parseLong(cap);
        long doctor1 = Long.parseLong(doctor);
        try {
            JSONObject requestData = new JSONObject();
            requestData.put("hospital", name);
            requestData.put("Hlocation", address);
            requestData.put("doctor", doctor1);
            requestData.put("capcity", capcity);
            requestData.put("Hlat", latitude);
            requestData.put("Hlong", longitude);
            String tableName = "Hospital"; // Assuming the table name is Patient
            String data = requestData.toString();
            OnlineDB.postData(tableName, data,getContext());

        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Failed to insert data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }


    }
}