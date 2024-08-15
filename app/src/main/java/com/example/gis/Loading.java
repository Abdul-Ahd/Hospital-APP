package com.example.gis;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentStatePagerAdapter;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;


import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.example.app.Login;
import com.example.app.MainMenu;
import com.example.app.R;

public class Loading extends AppCompatActivity{
        Handler handler;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);
        TextView view = findViewById(R.id.textani);

                YoYo.with(Techniques.BounceInRight)
                        .duration(1000)
                        .repeat(0)
                        .playOn(view);
                YoYo.with(Techniques.RubberBand)
                        .duration(700)
                        .repeat(1)
                        .playOn(view);
        handler = new Handler();
        handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                        SharedPreferences sharedPreferences = getSharedPreferences(Login.PERF_NAME,0);
                        boolean hasLoggedIn= sharedPreferences.getBoolean("hasLoggedIn",false);
                        String savedRole = sharedPreferences.getString("role", "");
                        String saveduser = sharedPreferences.getString("user", "");
                        if(hasLoggedIn) {
                                Intent intent = new Intent(Loading.this, MainMenu.class);
                                startActivity(intent);
                                finish();
                                UserCheck.PERF_USER = saveduser;
                                UserCheck.PERF_ROLE=savedRole;
                        }else {
                                Intent intent = new Intent(Loading.this, Login.class);
                                startActivity(intent);
                                finish();
                        }
                }
        },3000);
        }


}