package com.example.carespace.ClinicHospitalView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.example.carespace.Exercise.ExerciseTabPageAdapter;
import com.example.carespace.R;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class ClinicHospitalView extends AppCompatActivity {

    //widget
    private TabLayout tabLayout;
    private ViewPager2 vp;
    private ImageButton back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clinic_hospital_view);

        tabLayout = findViewById(R.id.tabLayout);
        vp = findViewById(R.id.vp);
        back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        setupTab();
    }

    private void setupTab()
    {
        vp.setUserInputEnabled(false); //disable swiping across pages
        ClinicHospitalTabPageAdapter adapter = new ClinicHospitalTabPageAdapter(this,tabLayout.getTabCount());
        vp.setAdapter(adapter);

        vp.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                //super.onPageSelected(position);
                tabLayout.selectTab(tabLayout.getTabAt(position));
            }
        });

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) { vp.setCurrentItem(tab.getPosition(),true); }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) { }

            @Override
            public void onTabReselected(TabLayout.Tab tab) { }
        });
    }
}