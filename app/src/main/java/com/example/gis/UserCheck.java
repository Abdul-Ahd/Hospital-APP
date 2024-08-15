package com.example.gis;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

public class UserCheck {
    public  static  String PERF_USER="";
    public  static  String PERF_ROLE="";
    public  static  String PERF_DIS="";
    public  static  String PERF_HOP="";
    public  static  String PERF_DOC="";
    String check;
    private  DatabaseHelper database;
    private Boolean retc=false;
    public UserCheck(Context context) {
        database = new DatabaseHelper(context);
    }
    @SuppressLint("Range")
    public interface LoginCallback {
        void onLoginCompleted(int auth);
    }
    @SuppressLint("Range")
    public void login(String user, String pw, String ch, Context context, LoginCallback callback) {
        OnlineDB.getData("SELECT * FROM User WHERE user = '" + user + "' ", context, new OnlineDB.OnGetDataListener() {
            @Override
            public void onDataRetrieved(Cursor cursor) {
                AtomicBoolean retc = new AtomicBoolean(false);
                if (cursor != null && cursor.moveToFirst()) {
                    String password = cursor.getString(cursor.getColumnIndex("pass"));
                    String role = cursor.getString(cursor.getColumnIndex("type"));

                    if (pw.equals(password)) {
                        String name = cursor.getString(cursor.getColumnIndex("user"));
                        PERF_ROLE = role;
                        PERF_USER = user;
                        retc.set(true);
                        if (role.equals("Doctor")) {
                            getDoctorIds(context, new DoctorIdsCallback() {
                                @Override
                                public void onDoctorIdsRetrieved(String diseaseId, String hospitalId,String DoctorId) {
                                    PERF_DIS = diseaseId;
                                    PERF_HOP = hospitalId;
                                    PERF_DOC = DoctorId;
                                }
                            });
                        }
                    } else {
                        PERF_ROLE = "Admin";
                        PERF_USER = "Admin";
                    }
                }

                int auth = retc.get() ? 1 : 0;
                callback.onLoginCompleted(auth);
            }

            @Override
            public void onError(String errorMessage) {
                // Handle error if needed
            }
        });
    }

    public interface RolesCallback {
        void onRolesRetrieved(String[] roles);
    }
@SuppressLint("Range")
    public void getRoles(Context context, RolesCallback callback) {
        OnlineDB.getData("SELECT * FROM User WHERE user = '" + PERF_USER + "'", context, new OnlineDB.OnGetDataListener() {
            @Override
            public void onDataRetrieved(Cursor cursor) {
                String id = null;
                if (cursor != null && cursor.moveToFirst()) {
                    id = cursor.getString(cursor.getColumnIndex("id"));
                }

                if (id != null) {
                    OnlineDB.getData("SELECT * FROM UserRole WHERE id = " + id + " ", context, new OnlineDB.OnGetDataListener() {
                        @Override
                        public void onDataRetrieved(Cursor cursor) {
                            List<String> rolesList = new ArrayList<>();
                            if (cursor.moveToFirst()) {
                                do {
                                    String role = cursor.getString(cursor.getColumnIndex("role"));
                                    rolesList.add(role);
                                } while (cursor.moveToNext());
                            } else {
                                // Handle the case where id is null
                                Log.e("UserCheck", "ID is null");
                            }

                            String[] rolesArray = new String[rolesList.size()];
                            rolesList.toArray(rolesArray);

                            callback.onRolesRetrieved(rolesArray);
                        }

                        @Override
                        public void onError(String errorMessage) {
                            // Handle error if needed
                        }
                    });
                } else {
                    // Handle the case where id is null
                    Log.e("UserCheck", "ID is null");
                    callback.onRolesRetrieved(new String[0]); // Empty roles array
                }
            }

            @Override
            public void onError(String errorMessage) {
                // Handle error if needed
            }
        });
    }
    public interface DoctorIdsCallback {
        void onDoctorIdsRetrieved(String diseaseId, String hospitalId, String doctorId);
    }
    @SuppressLint("Range")
    public static void getDoctorIds(Context context, DoctorIdsCallback callback) {
        OnlineDB.getData("SELECT * FROM User WHERE user = '" + PERF_USER + "'", context, new OnlineDB.OnGetDataListener() {
            @Override
            public void onDataRetrieved(Cursor cursor) {
                String userId = null;
                if (cursor != null && cursor.moveToFirst()) {
                    userId = cursor.getString(cursor.getColumnIndex("id"));
                }

                if (userId != null) {
                    // Now fetch diseaseID and HospitalID from doctorlogin table using the obtained userId
                    OnlineDB.getData("SELECT * FROM doctorlogin WHERE id = " + userId, context, new OnlineDB.OnGetDataListener() {
                        @Override
                        public void onDataRetrieved(Cursor cursor) {
                            String diseaseId = null;
                            String hospitalId = null;
                            String doctorId = null;
                            if (cursor != null && cursor.moveToFirst()) {
                                diseaseId = cursor.getString(cursor.getColumnIndex("diseaseID"));
                                hospitalId = cursor.getString(cursor.getColumnIndex("HospitalID"));
                                doctorId = cursor.getString(cursor.getColumnIndex("doctorID"));
                            }

                            callback.onDoctorIdsRetrieved(diseaseId, hospitalId,doctorId);
                        }

                        @Override
                        public void onError(String errorMessage) {
                            // Handle error if needed
                        }
                    });
                } else {
                    // Handle the case where userId is null
                    Log.e("UserCheck", "UserID is null");
                    callback.onDoctorIdsRetrieved(null, null,null); // Passing null values if userId is null
                }
            }

            @Override
            public void onError(String errorMessage) {
                // Handle error if needed
            }
        });
    }


}