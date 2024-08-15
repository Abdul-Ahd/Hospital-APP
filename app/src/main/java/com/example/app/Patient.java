package com.example.app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.widget.GridView;
import android.widget.SimpleCursorAdapter;
import android.widget.TabHost;

import com.example.gis.DatabaseHelper;
import com.example.gis.pageAdapter;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class Patient extends AppCompatActivity {

    TabLayout tabLayout;
    ViewPager2 viewPager2;
    pageAdapter adapter;


    public static class Patients {
        private int id;
        private String name;
        private String cnic;
        private String address;
        private String birth;
        private String gender;

        // Constructor
        public Patients(int id, String name, String cnic, String address, String birth, String gender) {
            this.id = id;
            this.name = name;
            this.cnic = cnic;
            this.address = address;
            this.birth = birth;
            this.gender = gender;
        }

        // Getters and setters
        // You can generate these automatically in your IDE or write them manually
        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getCnic() {
            return cnic;
        }

        public void setCnic(String cnic) {
            this.cnic = cnic;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getBirth() {
            return birth;
        }

        public void setBirth(String birth) {
            this.birth = birth;
        }

        public String getGender() {
            return gender;
        }

        public void setGender(String gender) {
            this.gender = gender;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient);


//        Cursor cursor = databaseHelper.getAllPatients();
        tabLayout = findViewById(R.id.tablayout);

//        viewPager2 = findViewById(R.id.viewpager);
        adapter = new pageAdapter(this);
        Fragment fragment = new InsertPatient();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
//        viewPager2.setAdapter(adapter);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                Fragment selectedFragment = null;

                switch (position) {
                    case 0:
                        selectedFragment = new InsertPatient();
                        break;
                    case 1:
                        selectedFragment = new viewPatient();
                        break;
                    // Add more cases for additional tabs if needed
                }

                if (selectedFragment != null) {
                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.fragment_container, selectedFragment);
                    transaction.commit();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }



}