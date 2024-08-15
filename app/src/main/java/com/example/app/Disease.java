package com.example.app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.os.Bundle;

import com.example.gis.pageAdapter;
import com.google.android.material.tabs.TabLayout;

public class Disease extends AppCompatActivity {

    TabLayout tabLayout;
    ViewPager2 viewPager2;
    pageAdapter adapter;


    public static class Diseas {
        private int id;
        private String name;
        private String date;
        private String location;


        // Constructor
        public Diseas(int id, String name, String date, String location) {
            this.id = id;
            this.name = name;
            this.date = date;
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

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
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
        setContentView(R.layout.activity_disease);


//        Cursor cursor = databaseHelper.getAllPatients();
        tabLayout = findViewById(R.id.tablayout);

//        viewPager2 = findViewById(R.id.viewpager);
        adapter = new pageAdapter(this);
        Fragment fragment = new Inser_Disease();
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
                        selectedFragment = new Inser_Disease();
                        break;
                    case 1:
                        selectedFragment = new View_Disease();
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