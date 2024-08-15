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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;
import com.example.app.Hospital.hospt;
import com.example.gis.OnlineDB;
import com.example.gis.UserCheck;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link View_Hospital#newInstance} factory method to
 * create an instance of this fragment.
 */
public class View_Hospital extends Fragment implements OnlineDB.OnGetDataListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public View_Hospital() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment View_Hospital.
     */
    // TODO: Rename and change types and number of parameters
    public static View_Hospital newInstance(String param1, String param2) {
        View_Hospital fragment = new View_Hospital();
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
        List<String> patients = new ArrayList<>();

                OnlineDB.getData("Select id AS _ID,hospital,Hlocation,capcity,doctor FROM Hospital",getContext(),this);

                // Set the adapter to the GridView
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
                        String sqlQuery = "Select id AS _ID,Hospital,Hlocation,capcity,doctor FROM Hospital";

                        if (!searchText.isEmpty()) {
                            // Append the WHERE clause to filter results based on name or CNIC
                            sqlQuery += " WHERE name LIKE '%" + searchText + "%'";
                        }
                        OnlineDB.getData(sqlQuery,getContext(),View_Hospital.this);

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
        return inflater.inflate(R.layout.fragment_view__hospital, container, false);
    }
    @SuppressLint("Range")
    public void onDataRetrieved(Cursor cursor) {
        try {
            String[] fromColumns = {
                    "_id",
                    "hospital",
                    "capcity",
                    "doctor",
                    "Hlocation",

            };

            int[] toViews = {
                    R.id.id,
                    R.id.Name,
                    R.id.cap,
                    R.id.doctor,
                    R.id.Address,
            };

            // Create a SimpleCursorAdapter to map the data to views
            adapter = new SimpleCursorAdapter(
                    requireContext(),
                    R.layout.item_hospital, // Layout for each item
                    cursor, // Cursor with data to display
                    fromColumns,
                    toViews,
                    0) {
                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    View view = super.getView(position, convertView, parent);

                    // Get the cursor at the current position
                    cursor.moveToPosition(position);

                    // Extract the ID of the current item
                   int itemId = cursor.getInt(cursor.getColumnIndex("_id"));
                    String name = cursor.getString(cursor.getColumnIndex("hospital"));
                    String capcity = cursor.getString(cursor.getColumnIndex("capcity"));
                    String address = cursor.getString(cursor.getColumnIndex("Hlocation"));
                    String doctor = cursor.getString(cursor.getColumnIndex("doctor"));

                    hospt rec = new hospt(itemId, name, capcity, doctor,address );
                    // Set the ID as a tag to the delete button
                    Button btnDel = view.findViewById(R.id.HDel);
                    btnDel.setTag(itemId);
                    if(UserCheck.PERF_ROLE.equals("Simple User")){
                        view.findViewById(R.id.HDel).setVisibility(View.GONE);
                        view.findViewById(R.id.updateH).setVisibility(View.GONE);
                    }
                    UserCheck userCheck = new UserCheck(getContext());
                    userCheck.getRoles(getContext(), new UserCheck.RolesCallback() {
                        @Override
                        public void onRolesRetrieved(String[] roles) {
                            for (String role : roles) {
                                if (role.equals("Change Disease")) {
                                    view.findViewById(R.id.HDel).setVisibility(View.VISIBLE);
                                    view.findViewById(R.id.updateH).setVisibility(View.VISIBLE);
                                }
                            }
                        }
                    });

                    // Set click listener for the delete button (PDel)
                    btnDel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // Get the ID of the patient to delete from the button's tag
                            int HId = (int) v.getTag();

                             OnlineDB.deleteData("Hospital",HId);
                            OnlineDB.getData("Select id AS _ID,hospital,Hlocation,capcity,doctor FROM Hospital",getContext(), View_Hospital.this);

                            gridView.invalidateViews();
                            // Show a toast message indicating the delete button was clicked
                            Toast.makeText(getContext(), "Delete button clicked for ID: " + HId, Toast.LENGTH_SHORT).show();
                        }
                    });

                    // Set click listener for the update button (updateP)
                    Button btnUpdate = view.findViewById(R.id.updateH);

                    btnUpdate.setTag(rec);
                    btnUpdate.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // Inflate the update_patient layout
                            LayoutInflater inflater = LayoutInflater.from(getContext());
                            View updatePatientView = inflater.inflate(R.layout.update_hospital, null);

                            // Retrieve other data for the selected patient from the cursor

                            hospt hop = (hospt) v.getTag();
                            int id1 = hop.getId();
                            String name = hop.getName();
                            String cap = hop.getCapcity();
                            String address = hop.getLocation();
                            String doc = hop.getDoctor();



                            // Populate the update_patient layout with retrieved data
                            EditText id = updatePatientView.findViewById(R.id.id);
                            EditText nameEditText = updatePatientView.findViewById(R.id.name);
                            EditText capEditText = updatePatientView.findViewById(R.id.cap);
                            EditText addressEditText = updatePatientView.findViewById(R.id.address);
                            EditText docEditText = updatePatientView.findViewById(R.id.doctor);


                            id.setText(String.valueOf(id1)); // Convert int to String
                            nameEditText.setText(name);
                            capEditText.setText(cap);
                            addressEditText.setText(address);
                            docEditText.setText(doc);

                            // Set up a "Save" button in the dialog to update the patient's data in the database
                            Button btnSave = updatePatientView.findViewById(R.id.saveH);
                            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                            builder.setView(updatePatientView);
                            AlertDialog alertDialog = builder.create();
                            btnSave.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    // Retrieve updated data from the EditText fields
                                    String updatedName = nameEditText.getText().toString();
                                    String cap = capEditText.getText().toString();
                                    String updatedAddress = addressEditText.getText().toString();
                                    String doctor = docEditText.getText().toString();

                                    JSONObject jsonData = new JSONObject();
                                    try {
                                        jsonData.put("hospital", updatedName);
                                        jsonData.put("capcity", cap);
                                        jsonData.put("Hlocation", updatedAddress);
                                        jsonData.put("doctor", doctor);

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                        return;
                                    }
                                    // Call the updateData method to update data in the online database
                                    OnlineDB.updateData("Hospital", itemId, jsonData.toString());

                                    // Dismiss the dialog
                                    alertDialog.dismiss();
                                    OnlineDB.getData("Select id AS _ID,hospital,Hlocation,capcity,doctor FROM Hospital",getContext(), View_Hospital.this);
                                    gridView.invalidateViews();
                                        // Show a toast message indicating the update was successful
                                        Toast.makeText(getContext(), "Hospital data updated successfully", Toast.LENGTH_SHORT).show();

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


}