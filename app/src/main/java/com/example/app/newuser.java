package com.example.app;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.gis.DatabaseHelper;
//import com.example.gis.MainActivity;
import com.example.gis.OnlineDB;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class newuser extends AppCompatActivity {
    private ListView mainlist, givenlist;
    private ArrayAdapter<String> adapter;
    private ArrayAdapter<String> givenListAdapter;
    private ArrayList<String> mainListData;
    private ArrayList<String> givenListData;
    private RadioGroup radiogroup;
    private EditText username, password,repassword;
    private AutoCompleteTextView   DoctorSpinner;
    private DatabaseHelper database;
    String DID,HID,DIID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newuser);
        ImageButton showButton = findViewById(R.id.showButton);
        mainlist = findViewById(R.id.createUserlist1);
        givenlist = findViewById(R.id.createUserlist2);
        radiogroup = findViewById(R.id.usertype);
        username = findViewById(R.id.username);
        TextInputLayout passwordInputLayout = findViewById(R.id.textInputLayout4);
        TextInputLayout repasswordInputLayout = findViewById(R.id.textInputLayout5);
        password = findViewById(R.id.password);
        repassword = findViewById(R.id.repassword);
        DoctorSpinner = findViewById(R.id.DSpinner);
        DoctorSpinner.addTextChangedListener(new TextWatcher() {
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
        DoctorSpinner.setOnItemClickListener((parent, view, position, id) -> {
            String disease = (String) parent.getItemAtPosition(position);
            // Fetch patient details based on the selected patient
            fetchDisease(disease);
        });
        Button insertuser = findViewById(R.id.insertuser);
        insertuser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                        insert();
            }
        });
        showButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selectionStart = password.getSelectionStart();
                int selectionEnd = password.getSelectionEnd();

                // Toggle password visibility
                if (password.getInputType() == InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD) {
                    password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    repassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    showButton.setImageResource(R.drawable.ic_baseline_visibility_off_24);
                } else {
                    password.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    repassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    showButton.setImageResource(R.drawable.ic_baseline_visibility_24);
                }

            }
        });
        TextWatcher passwordWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                String passwordStr = password.getText().toString();
                String repasswordStr = repassword.getText().toString();
                if (passwordStr.length() < 6 || !passwordStr.matches(".*[A-Z].*") || !passwordStr.matches(".*\\d.*")) {
                    passwordInputLayout.setError("Password must be 6 characters long 1 uppercase letter and at least one number");
                    repasswordInputLayout.setError(null);
//                } else if (!passwordStr.matches(".*[A-Z].*")) {
//                    passwordInputLayout.setError("Password must contain at least one uppercase letter");
//                    repasswordInputLayout.setError(null);
//                } else if (!passwordStr.matches(".*\\d.*")) {
//                    passwordInputLayout.setError("Password must contain at least one number");
//                    repasswordInputLayout.setError(null);
                } else if (!passwordStr.equals(repasswordStr)) {
                    repasswordInputLayout.setError("Passwords do not match");
                    passwordInputLayout.setError(null);
                } else {
                    passwordInputLayout.setError(null);


                }
            }
        };
        TextWatcher repasswordWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                String passwordStr = password.getText().toString();
                String repasswordStr = repassword.getText().toString();
                      if (!passwordStr.equals(repasswordStr)) {
                         repasswordInputLayout.setError("Passwords do not match");
                      } else {

                    repasswordInputLayout.setError(null);
                }
            }
        };

        password.addTextChangedListener(passwordWatcher);
        repassword.addTextChangedListener(repasswordWatcher);
        findViewById(R.id.constraintLayout2).setVisibility(View.GONE);
        findViewById(R.id.createUserlist1).setVisibility(View.GONE);
        findViewById(R.id.createUserlist2).setVisibility(View.GONE);
        findViewById(R.id.doctorlayout).setVisibility(View.GONE);
        radiogroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.hospitalUser) {
                    findViewById(R.id.constraintLayout2).setVisibility(View.VISIBLE);
                    findViewById(R.id.createUserlist1).setVisibility(View.VISIBLE);
                    findViewById(R.id.createUserlist2).setVisibility(View.VISIBLE);
                    findViewById(R.id.doctorlayout).setVisibility(View.GONE);
                } else if (checkedId == R.id.doctor) {
                    findViewById(R.id.doctorlayout).setVisibility(View.VISIBLE);
                    findViewById(R.id.constraintLayout2).setVisibility(View.GONE);
                    findViewById(R.id.createUserlist1).setVisibility(View.GONE);
                    findViewById(R.id.createUserlist2).setVisibility(View.GONE);
                } else {
                    findViewById(R.id.constraintLayout2).setVisibility(View.GONE);
                    findViewById(R.id.createUserlist1).setVisibility(View.GONE);
                    findViewById(R.id.createUserlist2).setVisibility(View.GONE);
                    findViewById(R.id.doctorlayout).setVisibility(View.GONE);
                }
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
    }
    int count;
    String getid;
    String type = null;
    private void insert(){
        String user = username.getText().toString();
        String pw = password.getText().toString();
        String repw = repassword.getText().toString();
        if (user.isEmpty() || pw.isEmpty() || repw.isEmpty()  ) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }
        int selectedId = radiogroup.getCheckedRadioButtonId();


        if (selectedId == R.id.admin) {
            type = "Admin";
        } else if (selectedId == R.id.user) {
            type = "Admin User";
        } else if (selectedId == R.id.hospitalUser) {
            type = "Simple User";
        } else if (selectedId == R.id.doctor) {
            type = "Doctor";
        } else {
            type = null;
        }
        if (type== null){
            Toast.makeText(this, "Select User Type", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!pw.equals(repw)) {
            Toast.makeText(this, "Password doesn't Match", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            OnlineDB.getData("Select  COUNT(*) FROM User WHERE user like '" + user + "' ", this, new OnlineDB.OnGetDataListener() {
                @Override
                public void onDataRetrieved(Cursor cursor) {
                    cursor.moveToFirst();
                    count = cursor.getInt(0);
                    cursor.close();
                    if (count > 0) {
                        // Username already exists, show a message or handle the situation accordingly
                        Toast.makeText(newuser.this, "Username already exists", Toast.LENGTH_SHORT).show();
                    } else {
                        try {
                            JSONObject requestData = new JSONObject();
                            requestData.put("user", user);
                            requestData.put("pass", pw);
                            requestData.put("type", type);

                            String tableName = "user"; // Assuming the table name is Patient
                            String data = requestData.toString();
                            OnlineDB.postData(tableName, data,newuser.this);

                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(newuser.this, "Failed to insert data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                        if (type.equals("Simple User")) {

                            OnlineDB.getData("Select  * FROM User WHERE user like '" + user + "' ", newuser.this, new OnlineDB.OnGetDataListener() {
                                @Override
                                @SuppressLint("range")
                                public void onDataRetrieved(Cursor cursor) {
                                    if(cursor.moveToFirst()){
                                        getid=cursor.getString(cursor.getColumnIndex("id"));
                                        int userId = Integer.parseInt(getid);

                                        for (String role : givenListData) {
                                            try {
                                                JSONObject requestData = new JSONObject();
                                                requestData.put("id", userId);
                                                requestData.put("role", role);
                                                String tableName = "userrole"; // Assuming the table name is Patient
                                                String data = requestData.toString();
                                                OnlineDB.postData(tableName, data,newuser.this);

                                            } catch (Exception e) {
                                                e.printStackTrace();
                                                Toast.makeText(newuser.this, "Failed to insert data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                        Toast.makeText(newuser.this, "ROLES inserted successfully", Toast.LENGTH_SHORT).show();
                                    }
                                    cursor.close();
                                }

                                @Override
                                public void onError(String errorMessage) {

                                }
                            });



                        } else if (type.equals("Doctor")) {
                            OnlineDB.getData("Select  * FROM User WHERE user like '" + user + "' ", newuser.this, new OnlineDB.OnGetDataListener() {
                                @Override
                                @SuppressLint("range")
                                public void onDataRetrieved(Cursor cursor) {
                                    if(cursor.moveToFirst()){
                                        getid=cursor.getString(cursor.getColumnIndex("id"));
                                        int userId = Integer.parseInt(getid);

                                        try {
                                            JSONObject requestData = new JSONObject();
                                            requestData.put("id", userId);
                                            requestData.put("diseaseID", DIID);
                                            requestData.put("HospitalID", HID);
                                            requestData.put("doctorID",DID);

                                            String tableName = "doctorlogin"; // Assuming the table name is Patient
                                            String data = requestData.toString();
                                            OnlineDB.postData(tableName, data,newuser.this);

                                        } catch (Exception e) {
                                            e.printStackTrace();
                                            Toast.makeText(newuser.this, "Failed to insert data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                        }
                                        Toast.makeText(newuser.this, "Doctor inserted successfully", Toast.LENGTH_SHORT).show();
                                    }



                                @Override
                                public void onError(String errorMessage) {

                                }
                            });

                        }
                    }
                }

                @Override
                public void onError(String errorMessage) {

                }
            });


        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @SuppressLint("Range")
    private void loadDiseasesByName() {

        String userInput = DoctorSpinner.getText().toString();

        OnlineDB.getData("Select * From Doctor Where doctorN like '%"+userInput+"%'",this,new OnlineDB.OnGetDataListener() {

            @Override
            public void onDataRetrieved(Cursor cursor) {
                List<String> diseaseNames = new ArrayList<>();
                if(cursor != null && cursor.moveToFirst()) {
                    do {
                        String name = cursor.getString(cursor.getColumnIndex("doctorN"));
                        diseaseNames.add(name);
                    } while (cursor.moveToNext());
                }
                ArrayAdapter<String> Dadapter = new ArrayAdapter<>(getBaseContext(), android.R.layout.simple_dropdown_item_1line, diseaseNames);
                DoctorSpinner.setAdapter(Dadapter);
            }

            @Override
            public void onError(String errorMessage) {

            }
        });

    }





    @SuppressLint({"Range", "SetTextI18n"})
    private void fetchDisease(String hospital) {
        OnlineDB.getData("Select * From doctor Where doctorN like '" + hospital + "' ", this, new OnlineDB.OnGetDataListener() {
            @Override
            public void onDataRetrieved(Cursor cursor) {
                try {
                    if (cursor.moveToFirst()) {
                        DID = cursor.getString(cursor.getColumnIndex("id"));
                        HID = cursor.getString(cursor.getColumnIndex("idHospital"));
                        DIID = cursor.getString(cursor.getColumnIndex("idDisease"));
                    } else {
                        // Handle case where hospital is not found
                        Toast.makeText(getBaseContext(), "Doctor not Found", Toast.LENGTH_SHORT).show();
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

    private void refreshActivity() {
        Intent intent = getIntent();
        finish(); // Finish the current activity
        startActivity(intent); // Start a new instance of the activity
    }
//    String query = "INSERT INTO UserRole (id, role) VALUES (?, ?)";
//    SQLiteStatement statement = db.compileStatement(query);
//                statement.bindString(1, user);
//                statement.bindString(2, pw);
//    Long rowId = statement.executeInsert();
}