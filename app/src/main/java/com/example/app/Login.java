package com.example.app;

import static com.example.gis.UserCheck.PERF_DIS;
import static com.example.gis.UserCheck.PERF_DOC;
import static com.example.gis.UserCheck.PERF_HOP;
import static com.example.gis.UserCheck.PERF_ROLE;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.gis.Loading;
//import com.example.gis.MainActivity;
import com.example.gis.UserCheck;

import java.util.concurrent.CompletableFuture;

public class Login extends AppCompatActivity {
public  static  String PERF_NAME="MyPerfsFile";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        String user ="admin";
        String pw = "admin";
        EditText username = findViewById(R.id.username);
        EditText password = findViewById(R.id.Password);
        Button loginbutton = findViewById(R.id.login);
//        Button proceed = findViewById(R.id.proceed);
        UserCheck userCheck = new UserCheck(this);
        loginbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(username.getText().toString().equals(user) && password.getText().toString().equals(pw)){

                    SharedPreferences sharedPreferences = getSharedPreferences(Login.PERF_NAME,0);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("hasLoggedIn",true);
                    editor.putString("role","Admin");
                    editor.putString("user","Master");
                    editor.commit();
                    Intent intent = new Intent(Login.this, MainMenu.class);
                    startActivity(intent);
                    finish();
                    Toast.makeText(Login.this, "Welcome: " + username.getText().toString() , Toast.LENGTH_SHORT).show();

                }else{
                    userCheck.login(username.getText().toString(), password.getText().toString(), "0", getBaseContext(), new UserCheck.LoginCallback() {
                        @Override
                        public void onLoginCompleted(int auth) {
                            if (auth == 1) {
                                SharedPreferences sharedPreferences = getSharedPreferences(Login.PERF_NAME, 0);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putBoolean("hasLoggedIn", true);
                                editor.putString("role", UserCheck.PERF_ROLE);
                                editor.putString("user", UserCheck.PERF_USER);
                                editor.commit();
                                Intent intent = new Intent(Login.this, MainMenu.class);
                                startActivity(intent);
                                finish();
                                if (PERF_ROLE.equals("Doctor")) {
                                    UserCheck.getDoctorIds(getBaseContext(), new UserCheck.DoctorIdsCallback() {
                                        @Override
                                        public void onDoctorIdsRetrieved(String diseaseId, String hospitalId, String DoctorID) {
                                            PERF_DIS = diseaseId;
                                            PERF_HOP = hospitalId;
                                            PERF_DOC = DoctorID;

                                        }
                                    });
                                }
                            } else {
                                // Display a toast message indicating incorrect user
                                Toast.makeText(Login.this, "Incorrect user", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }

//                     int auth = userCheck.login(username.getText().toString(),password.getText().toString(),"0",getBaseContext());
//                String check = String.valueOf(auth);
//                Toast.makeText(Login.this, check, Toast.LENGTH_SHORT).show();

            }
        });

        Button proceed = findViewById(R.id.proceed);

        proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an Intent to start a new Activity
                Intent intent = new Intent(Login.this, mapbox.class);
                startActivity(intent);
            }
        });

    }
}