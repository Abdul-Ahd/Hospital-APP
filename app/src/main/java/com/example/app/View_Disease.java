package com.example.app;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.database.Cursor;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import com.example.gis.DatabaseHelper;
import com.example.app.Disease.Diseas;
import com.example.gis.OnlineDB;
import com.example.gis.UserCheck;

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

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link View_Disease#newInstance} factory method to
 * create an instance of this fragment.
 */
public class View_Disease extends Fragment implements OnlineDB.OnGetDataListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public View_Disease() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment View_Disease.
     */
    // TODO: Rename and change types and number of parameters
    public static View_Disease newInstance(String param1, String param2) {
        View_Disease fragment = new View_Disease();
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
    private DatabaseHelper databaseHelper;

    @Override
    @SuppressLint("Range")
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        List<String> patients = new ArrayList<>();
        databaseHelper = new DatabaseHelper(getContext());
            OnlineDB.getData("Select id As _id,disease,Ddate,Dlocation FROM Disease",getContext(),this);
                gridView = view.findViewById(R.id.gridview);
                gridView.setAdapter(adapter);
                EditText searchEditText = view.findViewById(R.id.search);
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
                        String searchText = searchEditText.getText().toString().trim();

                        // Construct the SQL query dynamically based on the search query
                        String sqlQuery = "Select id As _id,disease,Ddate,Dlocation FROM Disease";

                        if (!searchText.isEmpty()) {
                            // Append the WHERE clause to filter results based on name or CNIC
                            sqlQuery += " WHERE name LIKE '%" + searchText + "%'";
                        }
                        OnlineDB.getData(sqlQuery,getContext(),View_Disease.this);

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
        return inflater.inflate(R.layout.fragment_view__disease, container, false);
    }
    public void onDataRetrieved(Cursor cursor) {
        try {
            String[] fromColumns = {
                    "_id",
                    "disease",
                    "Ddate",
                    "Dlocation",
            };

            int[] toViews = {
                    R.id.did,
                    R.id.dName,
                    R.id.dbirth,
                    R.id.dAddress,
            };

            adapter = new SimpleCursorAdapter(
                    requireContext(),
                    R.layout.item_disease,
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
                    String name = cursor.getString(cursor.getColumnIndex("disease"));
                    String date = cursor.getString(cursor.getColumnIndex("Ddate"));
                    String address = cursor.getString(cursor.getColumnIndex("Dlocation"));

                    Diseas Diseas = new Diseas(itemId, name, date, address);
                    // Set the ID as a tag to the delete button
                    Button btnDel = view.findViewById(R.id.DDel);
                    btnDel.setTag(itemId);
                    if(UserCheck.PERF_ROLE.equals("Simple User")){
                        view.findViewById(R.id.DDel).setVisibility(View.GONE);
                        view.findViewById(R.id.updateD).setVisibility(View.GONE);
                    }
                    UserCheck userCheck = new UserCheck(getContext());
                    userCheck.getRoles(getContext(), new UserCheck.RolesCallback() {
                        @Override
                        public void onRolesRetrieved(String[] roles) {
                            for (String role : roles) {
                                if (role.equals("Change Hospital")) {
                                    view.findViewById(R.id.DDel).setVisibility(View.VISIBLE);
                                    view.findViewById(R.id.updateD).setVisibility(View.VISIBLE);
                                }
                            }
                        }
                    });

                    // Set click listener for the delete button (PDel)
                    btnDel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // Get the ID of the patient to delete from the button's tag
                            int patientId = (int) v.getTag();

                            OnlineDB.deleteData("Disease",patientId);
                            OnlineDB.getData("Select id As _id,disease,Ddate,Dlocation FROM Disease",getContext(),View_Disease.this);
                            gridView.invalidateViews();
                            // Show a toast message indicating the delete button was clicked
                            Toast.makeText(getContext(), "Delete button clicked for ID: " + patientId, Toast.LENGTH_SHORT).show();
                        }
                    });

                    // Set click listener for the update button (updateP)
                    Button btnUpdate = view.findViewById(R.id.updateD);

                    btnUpdate.setTag(Diseas);
                    btnUpdate.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // Inflate the update_patient layout
                            LayoutInflater inflater = LayoutInflater.from(getContext());
                            View updatePatientView = inflater.inflate(R.layout.update_disease, null);

                            // Retrieve other data for the selected patient from the cursor
                            Diseas Diseas = (Disease.Diseas) v.getTag();
                            int id1 = Diseas.getId();
                            String name = Diseas.getName();
                            String date = Diseas.getDate();
                            String address = Diseas.getLocation();
                            DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                            DateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");
                            inputFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
                            Date birtht = null;
                            try {
                                birtht = inputFormat.parse(date);
                            } catch (ParseException e) {
                                throw new RuntimeException(e);
                            }
                            String formattedDate = outputFormat.format(birtht);
                            // Populate the update_patient layout with retrieved data
                            EditText id = updatePatientView.findViewById(R.id.id2);
                            EditText nameEditText = updatePatientView.findViewById(R.id.name2);
                            EditText DateEditText = updatePatientView.findViewById(R.id.date2);
                            EditText addressEditText = updatePatientView.findViewById(R.id.address2);
                            DateEditText.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    showDatePickerDialog(DateEditText); // Call a method to show DatePickerDialog
                                }
                            });

                            id.setText(String.valueOf(id1)); // Convert int to String
                            nameEditText.setText(name);
                            DateEditText.setText(formattedDate);
                            addressEditText.setText(address);

                            // Set up a "Save" button in the dialog to update the patient's data in the database
                            Button btnSave = updatePatientView.findViewById(R.id.saved);
                            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                            builder.setView(updatePatientView);
                            AlertDialog alertDialog = builder.create();
                            btnSave.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    // Retrieve updated data from the EditText fields
                                    String updatedName = nameEditText.getText().toString();
                                    String updatedDate = DateEditText.getText().toString();
                                    String updatedAddress = addressEditText.getText().toString();

                                    // Execute the SQL update statement
                                    JSONObject jsonData = new JSONObject();
                                    try {
                                        jsonData.put("disease", updatedName);
                                        jsonData.put("Ddate", updatedDate);
                                        jsonData.put("Dlocation", updatedAddress);

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                        return;
                                    }
                                    // Call the updateData method to update data in the online database
                                    OnlineDB.updateData("Disease", itemId, jsonData.toString());

                                    // Dismiss the dialog
                                    alertDialog.dismiss();
                                    OnlineDB.getData("Select id As _id,disease,Ddate,Dlocation FROM Disease",getContext(),View_Disease.this);
                                        gridView.invalidateViews();
                                        // Show a toast message indicating the update was successful
                                        Toast.makeText(getContext(), "Disease data updated successfully", Toast.LENGTH_SHORT).show();
                                }
                            });
                            alertDialog.show();
                        }
                    });
                    return view;
                }
                };

            // Set the adapter to the GridView
            gridView.setAdapter(adapter);
        }catch (Exception e){
            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }




    public void onError(String errorMessage) {
        // Handle error (e.g., display error message)
        Toast.makeText(requireContext(), "Error: " + errorMessage, Toast.LENGTH_SHORT).show();
    }
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
        DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(), new DatePickerDialog.OnDateSetListener() {
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

}