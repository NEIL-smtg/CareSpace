package com.example.carespace.MainPage;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Toast;

import com.example.carespace.Nearby.NearbySearch;
import com.example.carespace.R;
import com.google.android.material.tabs.TabLayout;

public class MainPage extends AppCompatActivity {

    //widgets
    private TabLayout tabLayout;
    private ViewPager2 viewPager;

    //double tap to exit
    private boolean doubleBackToExitPressedOnce;
    private Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);

        //initialize widgets
        tabLayout = (TabLayout) findViewById(R.id.tabLayout_main);
        viewPager = (ViewPager2) findViewById(R.id.vp_main);
        viewPager.setUserInputEnabled(false); //disable swiping across pages

        setupTabBar();
    }

    private void setupTabBar()
    {
        TabPageAdapter tabPageAdapter = new TabPageAdapter(this,tabLayout.getTabCount());
        viewPager.setAdapter(tabPageAdapter);

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                //super.onPageSelected(position);
                tabLayout.selectTab(tabLayout.getTabAt(position));
            }
        });

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) { viewPager.setCurrentItem(tab.getPosition(),true); }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) { }

            @Override
            public void onTabReselected(TabLayout.Tab tab) { }
        });
    }

    //below are all methods for double tap to exit
    private final Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            doubleBackToExitPressedOnce = false;
        }
    };

    @Override
    protected void onDestroy()
    {
        super.onDestroy();

        if (mHandler != null) { mHandler.removeCallbacks(mRunnable); }
    }

    @Override
    public void onBackPressed() {

        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            moveTaskToBack(true); //EXIT5
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(MainPage.this, R.string.doubleTapExit, Toast.LENGTH_SHORT).show();

        mHandler.postDelayed(mRunnable, 2000);
    }
}