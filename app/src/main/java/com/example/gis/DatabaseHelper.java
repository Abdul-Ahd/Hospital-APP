package com.example.gis;
import static java.security.AccessController.getContext;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "disease1DB.db";
    private static final int DATABASE_VERSION = 8;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d("DatabaseHelper", "onCreate method called");
       try {
       String CREATE_PATIENT_TABLE = "CREATE TABLE Patient (" +
               "id INTEGER PRIMARY KEY AUTOINCREMENT," +
               "name TEXT NOT NULL," +
               "cnic INTEGER NOT NULL," +
               "address TEXT," +
                "birth REAL NOT NULL," +
                "gender TEXT NOT NULL" +
                ")";
        db.execSQL(CREATE_PATIENT_TABLE);
           String Diesetable = "CREATE TABLE Disease (" +
                   "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                   "name TEXT NOT NULL," +
                   "date REAL NOT NULL," +
                   "location TEXT NOT NULL" +
                   ")";
           db.execSQL(Diesetable);
           String hospitaltabe = "CREATE TABLE Hospital (" +
                   "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                   "name TEXT NOT NULL," +
                   "capcity INTEGER NOT NULL," +
                   "doctor INTEGER NOT NULL," +
                   "location TEXT NOT NULL" +
                   ")";
           db.execSQL(hospitaltabe);
           String datatable = "CREATE TABLE Record (" +
                   "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                   "date REAL NOT NULL," +
                   "patientID INTEGER NOT NULL," +
                   "diseaseID INTEGER NOT NULL," +
                   "hospitalID INTEGER NOT NULL," +
                   "lat REAL NOT NULL," +
                   "long REAL NOT NULL," +
                   "condition TEXT NOT NULL," +
                   "weather TEXT NOT NULL" +
                   ")";
           db.execSQL(datatable);
           String Newuser = "CREATE TABLE User (" +
                   "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                   "name TEXT NOT NULL," +
                   "pass TEXT NOT NULL," +
                   "type TEXT NOT NULL" +
                   ")";
           db.execSQL(Newuser);
           String userrole = "CREATE TABLE UserRole (" +
                   "id INTEGER Not NULL," +
                   "role TEXT NOT NULL" +
                   ")";
           db.execSQL(userrole);

        } catch (Exception e) {
            Log.e("DatabaseHelper", "Error accessing database: " + e.getMessage());
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Add upgrade logic here if needed



    }

    public Cursor getAllPatients(String tableName) {

        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT id AS _id, * FROM " + tableName, null);

    }
    public void deletePatient(int patientId, String tableName) {
//        Toast.makeText(context, "function runing", Toast.LENGTH_SHORT).show();
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(tableName, "id=?", new String[]{String.valueOf(patientId)});

        db.close();
    }
}
