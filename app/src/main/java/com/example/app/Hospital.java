package com.example.app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.os.Bundle;

import com.example.gis.pageAdapter;
import com.google.android.material.tabs.TabLayout;

public class Hospital extends AppCompatActivity {
    TabLayout tabLayout;
    ViewPager2 viewPager2;
    pageAdapter adapter;
    public static class hospt {
        private int id;
        private String name;
        private String capcity;
        private String doctor;
        private String location;


        // Constructor
        public hospt(int id, String name, String capcity,String doctor, String location) {
            this.id = id;
            this.name = name;
            this.capcity = capcity;
            this.doctor=doctor;
            this.location = location;

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

        public String getCapcity() {
            return capcity;
        }

        public void setCapcity(String capcity) {
            this.capcity = capcity;
        }
        public String getDoctor() {
            return doctor;
        }

        public void setDoctor(String doctor) {
            this.doctor = doctor;
        }

        public String getLocation() {
            return location;
        }

        public void setLocation(String location) {
            this.location = location;
        }

    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hospital);
//        Cursor cursor = databaseHelper.getAllPatients();
        tabLayout = findViewById(R.id.tablayout);

//        viewPager2 = findViewById(R.id.viewpager);
        adapter = new pageAdapter(this);
        Fragment fragment = new Insert_Hospital();
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
                        selectedFragment = new Insert_Hospital();
                        break;
                    case 1:
                        selectedFragment = new View_Hospital();
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