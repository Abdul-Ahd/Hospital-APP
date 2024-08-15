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

public class newDoctor extends AppCompatActivity {

    TabLayout tabLayout;
    ViewPager2 viewPager2;
    pageAdapter adapter;


    public static class Doctor {
        private int id;
        private String name;
        private String disease;
        private String hospital;
        private String docBirth;
        private String gender;

        // Constructor
        public Doctor(int id, String name, String disease, String hospital, String docBirth, String gender) {
            this.id = id;
            this.name = name;
            this.disease = disease;
            this.hospital = hospital;
            this.docBirth = docBirth;
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

        public String getDisease() {
            return disease;
        }

        public void setDisease(String disease) {
            this.disease = disease;
        }

        public String gethospital() {
            return hospital;
        }

        public void sethospital(String hospital) {
            this.hospital = hospital;
        }

        public String getdocBirth() {
            return docBirth;
        }

        public void setdocBirth(String docBirth) {
            this.docBirth = docBirth;
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
        setContentView(R.layout.activity_new_doctor);


//        Cursor cursor = databaseHelper.getAllPatients();
        tabLayout = findViewById(R.id.tablayout);

//        viewPager2 = findViewById(R.id.viewpager);
        adapter = new pageAdapter(this);
        Fragment fragment = new insert_doctor();
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
                        selectedFragment = new insert_doctor();
                        break;
                    case 1:
                        selectedFragment = new view_doctor();
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