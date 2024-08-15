package com.example.app;

import static com.example.gis.UserCheck.PERF_DIS;
import static com.example.gis.UserCheck.PERF_DOC;
import static com.example.gis.UserCheck.PERF_HOP;
import static com.example.gis.UserCheck.PERF_ROLE;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.example.gis.UserCheck;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.navigation.NavigationView.OnNavigationItemSelectedListener;

public class MainMenu extends AppCompatActivity implements OnNavigationItemSelectedListener {
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        drawerLayout = findViewById(R.id.Menulayout);
        navigationView = findViewById(R.id.navigationview);
        toolbar = findViewById(R.id.toolbar);
        View headerView = navigationView.getHeaderView(0);
        Menu menu = navigationView.getMenu();
        MenuItem disease = menu.findItem(R.id.Disease);
        MenuItem event = menu.findItem(R.id.EventMang);
        if(UserCheck.PERF_ROLE.equals("Simple User")){
            disease.setVisible(false);
            event.setVisible(false);
        }
        if (PERF_ROLE.equals("Doctor")) {
           UserCheck.getDoctorIds(this, new UserCheck.DoctorIdsCallback() {
                @Override
                public void onDoctorIdsRetrieved(String diseaseId, String hospitalId, String DoctorId) {
                    PERF_DIS = diseaseId;
                    PERF_HOP = hospitalId;
                    PERF_DOC = DoctorId;
                }
            });
        }
        if (PERF_ROLE.equals("Simple User")) {
            UserCheck userCheck = new UserCheck(this);
            userCheck.getRoles(this, new UserCheck.RolesCallback() {
                @Override
                public void onRolesRetrieved(String[] roles) {
                    for (String role : roles) {
                        if (role.equals("Disease Manager")) {
                            disease.setVisible(true);
                        } else if (role.equals("Event Manager")) {
                            event.setVisible(true);
                        }
                    }
                }
            });
        }


        TextView username =headerView.findViewById(R.id.username1);
        TextView usertype = headerView.findViewById(R.id.userType1);

        if (username != null && usertype != null) {
            // Set text to the TextViews
            username.setText("User: "+ UserCheck.PERF_USER);
            usertype.setText("  Role: "+UserCheck.PERF_ROLE);
        } else {
            // Handle case where TextViews are not found
            Toast.makeText(this,"text view not found",Toast.LENGTH_SHORT).show();
        }

        setSupportActionBar(toolbar);
        navigationView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigration_open, R.string.navigragtion_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);

        Fragment fragment = new dashboardtabs();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        int itemId = item.getItemId();

        Fragment fragment = null;

        if (itemId == R.id.homeMenu) {

            fragment = new home();

        } else if (itemId == R.id.Eventhandle) {
            fragment = new dashboardtabs();

        } else if (itemId == R.id.EventMang) {
            fragment = new eventManager();

        } else if (itemId == R.id.createUser) {
            fragment = new createuser();

        } else if (itemId == R.id.Disease) {
            fragment = new dManager();

        } else if (itemId == R.id.logout) {
            SharedPreferences sharedPreferences = getSharedPreferences(Login.PERF_NAME,0);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("hasLoggedIn",false);
            editor.commit();
            Intent intent = new Intent(MainMenu.this, Login.class);
            startActivity(intent);
            finish();
        }

        if (fragment != null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();
        }

        // Close the drawer after the item is selected
        drawerLayout.closeDrawer(GravityCompat.START);

        // Indicate that the item is checked
        item.setChecked(true);

        // Display a toast message for the selected item


        return true;
    }
    @Override
    public void onBackPressed() {
        // Close the drawer when the back button is pressed if it's open
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


}
