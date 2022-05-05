package com.example.carespace.Gallery;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.example.carespace.R;
import com.google.android.material.tabs.TabLayout;

public class GalleryCameraFragmentContainer extends AppCompatActivity {

    private ViewPager mViewPager;
    private ImageButton close;
    private Button next;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery_camera_fragment_container);

        close = (ImageButton) findViewById(R.id.container_close);
        next = (Button) findViewById(R.id.container_next);

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        setupViewPager();

    }

    private void setupViewPager()
    {
        SectionPagerAdapter adapter = new SectionPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new Gallery());
        adapter.addFragment(new Camera());

        mViewPager = (ViewPager) findViewById(R.id.container_viewpager);
        mViewPager.setAdapter(adapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.container_tabsBottom);
        tabLayout.setupWithViewPager(mViewPager);

        tabLayout.getTabAt(0).setText("GALLERY");
        tabLayout.getTabAt(1).setText("CAMERA");

    }

    public int getCurrentTabNumber()
    {
        return mViewPager.getCurrentItem();
    }
}