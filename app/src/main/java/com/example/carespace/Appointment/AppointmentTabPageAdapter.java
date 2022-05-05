package com.example.carespace.Appointment;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class AppointmentTabPageAdapter extends FragmentStateAdapter {

    private int tabCount;

    public AppointmentTabPageAdapter(@NonNull FragmentActivity fragmentActivity, int tabCount) {
        super(fragmentActivity);
        this.tabCount = tabCount;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {

        if (position == 1) {
            return new HistoryAppointment();
        }
        return new UpcomingAppointment();

    }

    @Override
    public int getItemCount() {
        return tabCount;
    }
}
