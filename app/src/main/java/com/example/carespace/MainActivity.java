package com.example.carespace;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Html;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.carespace.WalkthroughSlides.WalkthroughSliderAdapter;
import com.example.carespace.login.Login;

public class MainActivity extends AppCompatActivity {

    private ViewPager mSlideViewPager; //walkthrough slides
    private LinearLayout mDotLayout;

    //widgets
    private TextView[] mDots;
    private Button mNextbtn;
    private Button mBackbtn;

    private WalkthroughSliderAdapter walkthroughSliderAdapter;

    private int mCurrentPage;

    String prevStarted = "yes";


    //show walkthrough for first time user only, disable it next time click into the app
    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences sharedpreferences = getSharedPreferences(getString(R.string.app_name), Context.MODE_PRIVATE);
        if (!sharedpreferences.getBoolean(prevStarted, false))
        {
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putBoolean(prevStarted, Boolean.TRUE);
            editor.apply();
        }
        else
        {
            Login();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSlideViewPager = (ViewPager) findViewById(R.id.slide_viewpager);
        mDotLayout = (LinearLayout) findViewById(R.id.dots_layout);

        mNextbtn = (Button) findViewById(R.id.slide_next_btn);
        mBackbtn = (Button) findViewById(R.id.slide_back_btn);

        walkthroughSliderAdapter = new WalkthroughSliderAdapter(this);

        mSlideViewPager.setAdapter(walkthroughSliderAdapter);

        addDotIndicator(0);

        mSlideViewPager.addOnPageChangeListener(viewListener);

        mNextbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!mNextbtn.getText().equals("finish"))
                {
                    mSlideViewPager.setCurrentItem(mCurrentPage+1,true);
                }
                else
                {
                    Login();
                }
            }
        });

        mBackbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSlideViewPager.setCurrentItem(mCurrentPage-1,true);
            }
        });

    }

    public void Login()
    {
        Intent intent = new Intent(this, Login.class);
        startActivity(intent);
        finish();
    }

    public void addDotIndicator(int position)
    {
        mDots = new TextView[3];
        mDotLayout.removeAllViews(); //avoid adding dots endlessly

        for (int i = 0; i < mDots.length ; i++) {

            mDots[i] = new TextView(this);
            mDots[i].setText(Html.fromHtml("&#8226;"));
            mDots[i].setTextSize(35);
            mDots[i].setTextColor(getResources().getColor(R.color.grey));

            mDotLayout.addView(mDots[i]);
        }

        //highlight color of dot on currently view page
        mDots[position].setTextColor(getResources().getColor(R.color.black));
    }

    ViewPager.OnPageChangeListener viewListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            addDotIndicator(position);

            mCurrentPage = position;

            if (position ==0)
            {
                mNextbtn.setEnabled(true);
                mBackbtn.setEnabled(false);
                mBackbtn.setVisibility(View.GONE);
            }
            else if (position ==1)
            {
                mNextbtn.setEnabled(true);
                mBackbtn.setEnabled(true);
                mBackbtn.setVisibility(View.VISIBLE);

                mNextbtn.setText("next >");

            }
            else
            {
                mNextbtn.setEnabled(true);
                mBackbtn.setEnabled(true);
                mBackbtn.setVisibility(View.VISIBLE);

                mNextbtn.setText("finish");
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

}