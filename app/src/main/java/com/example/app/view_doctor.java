package com.example.app;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.gis.OnlineDB;
import com.example.gis.UserCheck;

import org.json.JSONException;
import org.json.JSONObject;
import com.example.app.newDoctor.Doctor;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link view_doctor#newInstance} factory method to
 * create an instance of this fragment.
 */
public class view_doctor extends Fragment implements OnlineDB.OnGetDataListener  {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public view_doctor() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment view_doctor.
     */
    // TODO: Rename and change types and number of parameters
    public static view_doctor newInstance(String param1, String param2) {
        view_doctor fragment = new view_doctor();
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

    GridView gridView;



    private SimpleCursorAdapter adapter;

    @Override
    @SuppressLint("Range")
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Inflate the layout for this fragment
        try {
            OnlineDB.getData("SELECT d.id as _id, d.doctorN, d.docBirth, de.disease, h.hospital, d.gender FROM doctor d\n" +
                    " INNER JOIN disease de on d.idDisease = de.id\n" +
                    " INNER JOIN hospital h on d.idHospital = h.id;\n", getContext(), this);
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Error accessing grid: " + e.getMessage());
        }

        gridView = view.findViewById(R.id.gridview);
        gridView.setAdapter(adapter);
        EditText searchEditText = view.findViewById(R.id.docSearch);
        // Add a text changed listener to your EditText
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Not needed for your implementation
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // This method is called whenever the text is changed in the EditText

                // Get the search query from the EditText
                String searchText = searchEditText.getText().toString();

                try {
                    // URL encode the search text
                    String encodedSearchText = URLEncoder.encode(searchText, "UTF-8");

                    // Construct the SQL query dynamically based on the search query
                    String sqlQuery = "SELECT d.id AS _id, d.doctorN, d.docBirth, d.gender, de.disease, h.hospital FROM doctor d " +
                            " INNER JOIN disease de on d.idDisease = de.id\n" +
                            " INNER JOIN hospital h on d.idHospital = h.id ";

                    if (!searchText.isEmpty()) {
                        // Append the WHERE clause to filter results based on name or CNIC
                        sqlQuery += " WHERE d.doctorN LIKE '%" + encodedSearchText + "%' ";
                    }

                    // Execute the SQL query
                    OnlineDB.getData(sqlQuery, getContext(), view_doctor.this);
                } catch (UnsupportedEncodingException e) {
                    // Handle encoding error
                    Log.e("DatabaseHelper", "Error encoding search text: " + e.getMessage());
                } catch (Exception e) {
                    // Handle other exceptions
                    Log.e("DatabaseHelper", "Error executing SQL query: " + e.getMessage());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Not needed for your implementation
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return  inflater.inflate(R.layout.fragment_view_doctor, container, false);

    }


    @Override
    public void onDataRetrieved(Cursor cursor) {
        try {
            String[] fromColumns = {
                    "_id",
                    "doctorN",
                    "disease",
                    "hospital",
                    "docBirth",
                    "gender",
            };

            int[] toViews = {
                    R.id.id,
                    R.id.Name,
                    R.id.diseaseName,
                    R.id.hospitalName,
                    R.id.DocBirth,
                    R.id.gender,
            };

            adapter = new SimpleCursorAdapter(
                    requireContext(),
                    R.layout.item_doctor,
                    cursor,
                    fromColumns,
                    toViews,
                    0)
            {
                @Override
                @SuppressLint("Range")
                public View getView(int position, View convertView, ViewGroup parent) {
                    View view = super.getView(position, convertView, parent);

                    // Get the cursor at the current position
                    cursor.moveToPosition(position);
                    // Extract the ID of the current item
                    int itemId = cursor.getInt(cursor.getColumnIndex("_id"));
                    String name = cursor.getString(cursor.getColumnIndex("doctorN"));
                    String disease = cursor.getString(cursor.getColumnIndex("disease"));
                    String hospital = cursor.getString(cursor.getColumnIndex("hospital"));
                    String docBirth = cursor.getString(cursor.getColumnIndex("docBirth"));
                    String gender = cursor.getString(cursor.getColumnIndex("gender"));
                    Doctor doctor = new Doctor(itemId, name, disease, hospital, docBirth, gender);
                    // Set the ID as a tag to the delete button
                    Button btnDel = view.findViewById(R.id.DDel);
                    btnDel.setTag(itemId);
                    view.findViewById(R.id.updateD).setVisibility(View.GONE);
//                                if (UserCheck.PERF_ROLE.equals("Simple User")) {
//                                    view.findViewById(R.id.PDel).setVisibility(View.GONE);
//                                    view.findViewById(R.id.updateP).setVisibility(View.GONE);
//                                }
//                                UserCheck userCheck = new UserCheck(getContext());
//                                userCheck.getRoles(getContext(), new UserCheck.RolesCallback() {
//                                    @Override
//                                    public void onRolesRetrieved(String[] roles) {
//                                        for (String role : roles) {
//                                            if (role.equals("Change Patient")) {
//                                                view.findViewById(R.id.PDel).setVisibility(View.VISIBLE);
//                                                view.findViewById(R.id.updateP).setVisibility(View.VISIBLE);
//                                            }
//                                        }
//                                    }
//                                });

                    // Set click listener for the delete button (PDel)
                    btnDel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // Get the ID of the patient to delete from the button's tag
                            int patientId = (int) v.getTag();

                            // Delete the patient from the database using DatabaseHelper
                            OnlineDB.deleteData("doctor", patientId);
//                                        OnlineDB.getData("SELECT id AS _id , patient, cnic, address, birth, gender FROM Patient", getContext(), view_doctor.this);
                            gridView.invalidateViews();
                            // Show a toast message indicating the delete button was clicked
                            Toast.makeText(getContext(), "Delete button clicked for ID: " + patientId, Toast.LENGTH_SHORT).show();
                        }
                    });

                    // Set click listener for the update button (updateP)
                    Button btnUpdate = view.findViewById(R.id.updateD);
                    btnUpdate.setTag(doctor);
                    btnUpdate.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // Inflate the update_patient layout
                            LayoutInflater inflater = LayoutInflater.from(getContext());
                            View updatePatientView = inflater.inflate(R.layout.update_patient, null);

                            // Retrieve other data for the selected patient from the cursor
                            Doctor patient = (Doctor) v.getTag();
                            int id1 = patient.getId();
                            String name = patient.getName();
                            String disease = patient.getDisease();
                            String hospital = patient.gethospital();
                            String birth = patient.getdocBirth();
                            String gender = patient.getGender();
                            DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                            DateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");
                            inputFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
                            Date birtht = null;
                            try {

                                birtht = inputFormat.parse(birth);


                            } catch (ParseException e) {
                                throw new RuntimeException(e);
                            }

                            String formattedDate = outputFormat.format(birtht);
                            // Populate the update_patient layout with retrieved data
                            EditText id = updatePatientView.findViewById(R.id.id1);
                            EditText nameEditText = updatePatientView.findViewById(R.id.name1);
                            EditText cnicEditText = updatePatientView.findViewById(R.id.cnic1);
                            EditText addressEditText = updatePatientView.findViewById(R.id.address1);
                            EditText birthEditText = updatePatientView.findViewById(R.id.birth1);
                            Spinner genderSpinner = updatePatientView.findViewById(R.id.Gender1);
                            birthEditText.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
//                                    showDatePickerDialog(birthEditText); // Call a method to show DatePickerDialog
                                }
                            });
                            id.setText(String.valueOf(id1)); // Convert int to String
                            nameEditText.setText(name);
                            cnicEditText.setText(disease);
                            addressEditText.setText(hospital);
                            birthEditText.setText(formattedDate);
                            if (gender.equals("Male")) {
                                genderSpinner.setSelection(0); // Select the first item ("Male")
                            } else if (gender.equals("Female")) {
                                genderSpinner.setSelection(1); // Select the second item ("Female")
                            }
                            // Set up a "Save" button in the dialog to update the patient's data in the database
                            Button btnSave = updatePatientView.findViewById(R.id.savep);
                            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                            builder.setView(updatePatientView);
                            AlertDialog alertDialog = builder.create();
                            btnSave.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    // Retrieve updated data from the EditText fields
                                    String updatedName = nameEditText.getText().toString();
                                    String updatedCnic = cnicEditText.getText().toString();
                                    String updatedAddress = addressEditText.getText().toString();
                                    String updatedBirth = birthEditText.getText().toString();
                                    String updatedGender = genderSpinner.getSelectedItem().toString();

                                    // Execute the SQL update statement
                                    JSONObject jsonData = new JSONObject();
                                    try {
                                        jsonData.put("patient", updatedName);
                                        jsonData.put("cnic", updatedCnic);
                                        jsonData.put("address", updatedAddress);
                                        jsonData.put("birth", updatedBirth);
                                        jsonData.put("gender", updatedGender);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                        return;
                                    }
                                    // Call the updateData method to update data in the online database
//                                    OnlineDB.updateData("Patient", itemId, jsonData.toString());

                                    // Dismiss the dialog
                                    alertDialog.dismiss();

                                    gridView.invalidateViews();
                                    Toast.makeText(getContext(), "Patient data updated successfully", Toast.LENGTH_SHORT).show();

                                }
                            });


                            alertDialog.show();
                        }
                    });

                    return view;
                }
            };
            gridView.setAdapter(adapter);
        }
        catch (Exception e){

        }
    }

    @Override
    public void onError(String errorMessage) {

    }
}
