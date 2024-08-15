package com.example.gis;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.app.InsertPatient;
import com.example.app.viewPatient;

public class pageAdapter extends FragmentStateAdapter {
    public pageAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int postion){
        switch (postion){
            case 0:
                return new InsertPatient();
            case 1:
                return new viewPatient();
            default:
                return null;
        }

    }


    @Override
    public int getItemCount() {
        return 2;
    }
}
