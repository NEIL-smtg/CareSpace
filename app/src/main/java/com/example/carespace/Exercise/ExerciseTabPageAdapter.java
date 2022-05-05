package com.example.carespace.Exercise;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class ExerciseTabPageAdapter extends FragmentStateAdapter {

    private int tabCount;

    public ExerciseTabPageAdapter(@NonNull FragmentActivity fragmentActivity, int tabCount) {
        super(fragmentActivity);
        this.tabCount = tabCount;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {

        switch (position)
        {
            case 0:{
                //abs
                return new TutorialAdaptiveFragment(0);
            }
            case 1:{
                //arm
                return new TutorialAdaptiveFragment(1);
            }
            case 2:{
                //yoga
                return new TutorialAdaptiveFragment(2);
            }

        }

        return null;
    }

    @Override
    public int getItemCount() {
        return tabCount;
    }
}
