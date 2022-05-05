package com.example.carespace.MainPage;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;

import com.example.carespace.Nearby.NearbySearch;
import com.example.carespace.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.HashMap;

import eightbitlab.com.blurview.BlurView;
import eightbitlab.com.blurview.RenderScriptBlur;

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
        requestWindowFeature(1);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        setContentView(R.layout.activity_main_page);


        //initialize widgets
        tabLayout =  findViewById(R.id.tabLayout_main);
        viewPager =  findViewById(R.id.vp_main);
        viewPager.setUserInputEnabled(false); //disable swiping across pages

        setupDynamicBlurView();
        setupTabBar();

        //save device token in firebase for notification use
        getToken();


        //when user is not sign in with google , this is neccessary
//        UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder()
//                .setDisplayName("LEe ing u")
//                .setPhotoUri(Uri.parse("https://via.placeholder.com/150C/O%20https://placeholder.com/"))
//                .build();
//
//        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//        user.updateProfile(profileChangeRequest);


    }

    private void getToken()
    {
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.d("TAG", "Fetching FCM registration token failed", task.getException());
                            return;
                        }

                        // Get new FCM registration token
                        String token = task.getResult();

                        saveTokenToFirebase(token);
                    }
                });
    }

    private void saveTokenToFirebase(String token)
    {
        //current user
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user!=null)
        {
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Tokens").child(user.getUid());
            ref.child("token").setValue(token);
        }
    }

    private void setupDynamicBlurView()
    {
        final float radius = 25f;
        final Drawable windowBackground = getWindow().getDecorView().getBackground();

        BlurView blurView = findViewById(R.id.blurView);
        ViewGroup root= findViewById(R.id.root);
        blurView.setupWith(root)
                .setFrameClearDrawable(windowBackground)
                .setBlurAlgorithm(new RenderScriptBlur(this))
                .setBlurRadius(radius)
                .setHasFixedTransformationMatrix(true);
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
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition(),true);
                int tabIconColor = ContextCompat.getColor(MainPage.this, R.color.purple_700);
                tab.getIcon().setColorFilter(tabIconColor, PorterDuff.Mode.SRC_IN);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                int tabIconColor = ContextCompat.getColor(MainPage.this,R.color.colorGrey);
                tab.getIcon().setColorFilter(tabIconColor, PorterDuff.Mode.SRC_IN);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                int tabIconColor = ContextCompat.getColor(MainPage.this, R.color.purple_700);
                tab.getIcon().setColorFilter(tabIconColor, PorterDuff.Mode.SRC_IN);
            }
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