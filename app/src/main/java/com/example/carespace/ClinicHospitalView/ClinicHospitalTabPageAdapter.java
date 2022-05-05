package com.example.carespace.ClinicHospitalView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.carespace.MainPage.ExerciseTutorialFragment;
import com.example.carespace.MainPage.HomeFragment;
import com.example.carespace.MainPage.ProfileFragment;
import com.example.carespace.MainPage.SleepFragment;

public class ClinicHospitalTabPageAdapter extends FragmentStateAdapter {

    private int tabCount;

    public ClinicHospitalTabPageAdapter(@NonNull FragmentActivity fragmentActivity, int tabCount) {
        super(fragmentActivity);
        this.tabCount = tabCount;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
       return new AdaptiveView(position);
    }

    @Override
    public int getItemCount() {
        return tabCount;
    }
}

