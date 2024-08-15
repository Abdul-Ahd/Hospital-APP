package com.example.app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.gis.DatabaseHelper;
import com.example.gis.OnlineDB;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ManageUser extends AppCompatActivity {
    private AutoCompleteTextView searchUser;
    private DatabaseHelper database;
    private EditText userName,Password;
    private ArrayAdapter<String> adapter;
    private ArrayAdapter<String> givenListAdapter;
    private ArrayList<String> mainListData;
    private ArrayList<String> givenListData;
    private ListView mainlist, givenlist;
    private RadioGroup radiogroup;
    private Button update;
    private AutoCompleteTextView  hospitalSpinner, diseaseSpinner;
    String DID,HID;
    private String getid;
    @SuppressLint("Range")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_user);
        LinearLayout foundUserLayout = findViewById(R.id.roletext);

        radiogroup = findViewById(R.id.usertype);
        searchUser = findViewById(R.id.searchUser);
        mainlist = findViewById(R.id.createUserlist1);
        givenlist = findViewById(R.id.createUserlist2);
        update = findViewById(R.id.upatedUser);
        database = new DatabaseHelper(this);
        userName = findViewById(R.id.username);
        Password = findViewById(R.id.password);
        hospitalSpinner = findViewById(R.id.HSpinner);
        diseaseSpinner = findViewById(R.id.DSpinner);
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
        foundUserLayout.setVisibility(View.GONE);
        findViewById(R.id.constraintLayout2).setVisibility(View.GONE);
        findViewById(R.id.doctorlayout).setVisibility(View.GONE);
        radiogroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.hospitalUser) {
                    foundUserLayout.setVisibility(View.VISIBLE);
                    findViewById(R.id.constraintLayout2).setVisibility(View.VISIBLE);
                    findViewById(R.id.doctorlayout).setVisibility(View.GONE);

                } else if (checkedId == R.id.doctor) {
                    findViewById(R.id.constraintLayout2).setVisibility(View.GONE);
                    findViewById(R.id.doctorlayout).setVisibility(View.VISIBLE);
                } else {
                    foundUserLayout.setVisibility(View.GONE);
                    findViewById(R.id.constraintLayout2).setVisibility(View.GONE);
                }
            }
        });

        searchUser.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // This method is called before the text is changed.
                // You can implement any pre-processing here if needed.
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                loaduser();
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // This method is called after the text has changed.
                // You can implement any post-processing here if needed.
            }
        });
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                update();
            }
        });
        mainListData = new ArrayList<>();
        givenListData = new ArrayList<>();

        String[] data = {"Disease Manager", "Event Manager", "Add Patient", "Add Disease", "Add Hospital", "Add Record",
                "View Record", "View Report", "Change Patient", "Change Disease", "Change Hospital","Change Record"};

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, mainListData);
        mainlist.setAdapter(adapter);

        givenListAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, givenListData);
        givenlist.setAdapter(givenListAdapter);

        mainlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String clickedItem = adapter.getItem(position);

                mainListData.remove(clickedItem);
                adapter.notifyDataSetChanged();

                givenListData.add(clickedItem);
                givenListAdapter.notifyDataSetChanged();
            }
        });

        givenlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = givenListAdapter.getItem(position);

                givenListData.remove(selectedItem);
                givenListAdapter.notifyDataSetChanged();

                mainListData.add(selectedItem);
                adapter.notifyDataSetChanged();
            }
        });

        // Add initial data to mainListData
        for (String item : data) {
            mainListData.add(item);
        }
        searchUser.setOnItemClickListener((parent, view, position, id) -> {
            mainListData.clear();
            givenListData.clear();
            for (String item : data) {
                mainListData.add(item);
            }
            String User = (String) parent.getItemAtPosition(position);

            // Fetch patient details based on the selected patient
            OnlineDB.getData("SELECT * FROM user Where user = '" + User + "'", this, new OnlineDB.OnGetDataListener() {
                @Override
                public void onDataRetrieved(Cursor cursor) {
                    String userid = null,name , pw ,type = null;
                    if (cursor != null && cursor.moveToFirst()) {
                        do {
                            name = cursor.getString(cursor.getColumnIndex("user"));
                            pw = cursor.getString(cursor.getColumnIndex("pass"));
                            type = cursor.getString(cursor.getColumnIndex("type"));
                            userid = cursor.getString(cursor.getColumnIndex("id"));
                            userName.setText(name);
                            Password.setText(pw);
                            getid = userid;
                            if (type.equals("Admin")) {
                                radiogroup.check(R.id.admin);
                            } else if (type.equals("Admin User")) {
                                radiogroup.check(R.id.user);
                            } else if (type.equals("Simple User")) {
                                radiogroup.check(R.id.hospitalUser);
                            }else if (type.equals("Doctor")) {
                                radiogroup.check(R.id.doctor);
                            }

                        } while (cursor.moveToNext());
                    }
                    cursor.close();
                    if (type != null && type.equals("Simple User") && userid != null) {
                        OnlineDB.getData("SELECT * From userrole where id = '" + userid + "'",ManageUser.this, new OnlineDB.OnGetDataListener() {
                            @Override
                            public void onDataRetrieved(Cursor cursor) {
                                if (cursor != null && cursor.moveToFirst()) {
                                    do {
                                        String role = cursor.getString(cursor.getColumnIndex("role"));
                                        givenListData.add(role);
                                        mainListData.remove(role);

                                    } while (cursor.moveToNext());
                                }


                                adapter.notifyDataSetChanged();
                                givenListAdapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onError(String errorMessage) {

                            }
                        });

                    } else if (type != null && type.equals("Doctor") && userid != null) {
                        OnlineDB.getData("SELECT Disease.disease, Hospital.hospital " +
                                "FROM doctorlogin " +
                                "INNER JOIN Disease ON doctorlogin.DiseaseID = Disease.ID " +
                                "INNER JOIN Hospital ON doctorlogin.HospitalID = Hospital.ID " +
                                "WHERE doctorlogin.id = '" + userid + "'", ManageUser.this, new OnlineDB.OnGetDataListener() {
                            @Override
                            public void onDataRetrieved(Cursor cursor) {
                                if (cursor != null && cursor.moveToFirst()) {
                                    diseaseSpinner.setText(cursor.getString(cursor.getColumnIndex("disease")));
                                    hospitalSpinner.setText(cursor.getString(cursor.getColumnIndex("hospital")));
                                }
                            }

                            @Override
                            public void onError(String errorMessage) {

                            }
                        });
                    }

                }

                @Override
                public void onError(String errorMessage) {

                }
            });


        });


    }
    @SuppressLint("Range")
    private void loaduser() {

        List<String> Names = new ArrayList<>();
        String userInput = searchUser.getText().toString().toLowerCase();
        OnlineDB.getData("SELECT * FROM user Where user like '%" + userInput + "%'", this, new OnlineDB.OnGetDataListener() {
            @Override
            public void onDataRetrieved(Cursor cursor) {
                if (cursor != null && cursor.moveToFirst()) {
                    do {
                        String name = cursor.getString(cursor.getColumnIndex("user"));
                        Names.add(name);

                    } while (cursor.moveToNext());
                }
                cursor.close();

                ArrayAdapter<String> adapter = new ArrayAdapter<>(getBaseContext(), android.R.layout.simple_dropdown_item_1line, Names);
                searchUser.setAdapter(adapter);
            }

            @Override
            public void onError(String errorMessage) {

            }
        });

    }
    String type = null;
    private void update() {
        String user = userName.getText().toString();
        String pw = Password.getText().toString();
        int selectedId = radiogroup.getCheckedRadioButtonId();


        if (selectedId == R.id.admin) {
            type = "Admin";
        } else if (selectedId == R.id.user) {
            type = "Admin User";
        } else if (selectedId == R.id.hospitalUser) {
            type = "Simple User";
        } else {
            type = null;
        }
        if (type == null) {
            Toast.makeText(this, "Select User Type", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            DatabaseHelper dbHelper = new DatabaseHelper(this);
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            OnlineDB.getData("SELECT COUNT(*) FROM User WHERE user = '" + user + "'", this, new OnlineDB.OnGetDataListener() {
                @Override
                public void onDataRetrieved(Cursor cursor) {
                    cursor.moveToFirst();
                    int count = cursor.getInt(0);
                    cursor.close();

                    if (count == 0) {
                        Toast.makeText(getBaseContext(), "User does not exist", Toast.LENGTH_SHORT).show();
                    } else {
                        // Update the user details
                        int upid = Integer.parseInt(getid);
                        try {

                            JSONObject jsondata = new JSONObject();
                            jsondata.put("pass",pw);
                            jsondata.put("type",type);
                            OnlineDB.updateData("user", upid, jsondata.toString() );
                        }
                        catch (JSONException e) {
                            e.printStackTrace();
                            return;
                        }

                        Toast.makeText(getBaseContext(), "User updated successfully", Toast.LENGTH_SHORT).show();

                        // If the user type is "Simple User", update user roles
                        if (type.equals("Simple User")) {
                            // Delete old roles
                            OnlineDB.deleteData("userrole",upid);
                            // Insert new roles for the user
                            for (String role : givenListData) {
                                try {
                                    JSONObject requestData = new JSONObject();
                                    requestData.put("id", upid);
                                    requestData.put("role", role);
                                    String tableName = "userrole"; // Assuming the table name is Patient
                                    String data = requestData.toString();
                                    OnlineDB.postData(tableName, data,ManageUser.this);

                                } catch (Exception e) {
                                    e.printStackTrace();
                                    Toast.makeText(ManageUser.this, "Failed to insert data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                }

                            }
                            Toast.makeText(getBaseContext(), "Roles updated successfully", Toast.LENGTH_SHORT).show();
                        }
                    }
                }




                @Override
                public void onError(String errorMessage) {

                }
            });
            // Check if the user exists


        } catch (Exception e) {
            e.printStackTrace();
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
    @SuppressLint({"Range", "SetTextI18n"})
    private void fetchHospital(String hospital) {
        OnlineDB.getData("Select * From Hospital Where hospital like '" + hospital + "' ", this, new OnlineDB.OnGetDataListener() {
            @Override
            public void onDataRetrieved(Cursor cursor) {
                try {
                    if (cursor.moveToFirst()) {
                        HID = cursor.getString(cursor.getColumnIndex("id"));
                    } else {
                        // Handle case where hospital is not found
                        Toast.makeText(getBaseContext(), "Hospital not Found", Toast.LENGTH_SHORT).show();
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
    @SuppressLint("Range")
    private int getUserId(String username, SQLiteDatabase db) {
        Cursor cursor = db.rawQuery("SELECT id FROM User WHERE name = ?", new String[]{username});
        int userId = -1;
        if (cursor.moveToFirst()) {
            userId = cursor.getInt(cursor.getColumnIndex("id"));
        }
        cursor.close();
        return userId;
    }
    private void refreshActivity() {
        Intent intent = getIntent();
        finish(); // Finish the current activity
        startActivity(intent); // Start a new instance of the activity
    }

}