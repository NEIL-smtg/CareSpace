package com.example.carespace.MainPage;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class TabPageAdapter extends FragmentStateAdapter {

    private int tabCount;

    public TabPageAdapter(@NonNull FragmentActivity fragmentActivity, int tabCount) {
        super(fragmentActivity);
        this.tabCount = tabCount;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {

        switch (position){
            case 1:
                return new SleepFragment();

            case 2:
                return new ExerciseTutorialFragment();

            case 3:
                return new ProfileFragment();

            default:
                return new HomeFragment();
        }

    }

    @Override
    public int getItemCount() {
        return tabCount;
    }
}
