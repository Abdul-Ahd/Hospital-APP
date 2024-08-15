package com.example.app;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.Toast;

import com.example.gis.DatabaseHelper;
import com.example.gis.OnlineDB;
import com.example.gis.UserCheck;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

public class ChangePassword extends AppCompatActivity {
    private TextInputEditText oldPasswordEditText, newPasswordEditText, rePasswordEditText;
    private TextInputLayout oldPasswordLayout, newPasswordLayout, rePasswordLayout;
    private Button updatePasswordButton;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        // Initialize views
        oldPasswordEditText = findViewById(R.id.oldpassword);
        newPasswordEditText = findViewById(R.id.newpassword);
        rePasswordEditText = findViewById(R.id.repassword);
        oldPasswordLayout = findViewById(R.id.textInputLayout1);
        newPasswordLayout = findViewById(R.id.textInputLayout2);
        rePasswordLayout = findViewById(R.id.textInputLayout3);
        updatePasswordButton = findViewById(R.id.updatePassword);
        databaseHelper = new DatabaseHelper(this);
        updatePasswordButton.setOnClickListener(v -> updatePassword());
        TextWatcher passwordWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                String passwordStr = newPasswordEditText.getText().toString();
                String repasswordStr = rePasswordEditText.getText().toString();
                if (passwordStr.length() < 6 || !passwordStr.matches(".*[A-Z].*") || !passwordStr.matches(".*\\d.*")) {
                    newPasswordLayout.setError("Password must be 6 characters long 1 uppercase letter and at least one number");
                    rePasswordLayout.setError(null);
                } else if (!passwordStr.equals(repasswordStr)){
                    rePasswordLayout.setError("Passwords do not match");
                    newPasswordLayout.setError(null);
                }
                else{
                    newPasswordLayout.setError(null);

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
                String passwordStr = newPasswordEditText.getText().toString();
                String repasswordStr = rePasswordEditText.getText().toString();
                if (!passwordStr.equals(repasswordStr)) {
                    rePasswordLayout.setError("Passwords do not match");
                } else {
                    rePasswordLayout.setError(null);
                }
            }
        };

        newPasswordEditText.addTextChangedListener(passwordWatcher);
        rePasswordEditText.addTextChangedListener(repasswordWatcher);
    }
    private void updatePassword() {
        String username = UserCheck.PERF_USER;
        String oldPassword = oldPasswordEditText.getText().toString();
        String newPassword = newPasswordEditText.getText().toString();
        String rePassword = rePasswordEditText.getText().toString();
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        OnlineDB.getData("SELECT * FROM User WHERE user = '" + username + "' AND pass = '" + oldPassword + "' ", this, new OnlineDB.OnGetDataListener() {
            @Override
            public void onDataRetrieved(Cursor cursor) {
                int id;
                if (!cursor.moveToFirst() || cursor == null ) {
                    cursor.close();
                    oldPasswordLayout.setError("Incorrect password");
                    return;
                }
                else{
                    id=cursor.getInt(cursor.getColumnIndex("id"));
                }
                oldPasswordLayout.setError(null);
                cursor.close();
                boolean anyError = oldPasswordLayout.getError() != null || newPasswordLayout.getError() != null || rePasswordLayout.getError() != null;
                if (anyError || newPassword.isEmpty()) {

                    Toast.makeText(getBaseContext(), "Fix Errors First", Toast.LENGTH_SHORT).show();
                    return;
                }
                try {

                    JSONObject jsondata = new JSONObject();
                    jsondata.put("pass",newPassword);
                    OnlineDB.updateData("user", id, jsondata.toString() );
                }
                catch (JSONException e) {
                    e.printStackTrace();
                    return;
                }
                    Toast.makeText(getBaseContext(), "Password updated successfully", Toast.LENGTH_SHORT).show();

            }
            @Override
            public void onError(String errorMessage) {
                Toast.makeText(getBaseContext(), errorMessage, Toast.LENGTH_SHORT).show();
            }
        });

    }
    private void refreshActivity() {
        Intent intent = getIntent();
        finish(); // Finish the current activity
        startActivity(intent); // Start a new instance of the activity
    }
}