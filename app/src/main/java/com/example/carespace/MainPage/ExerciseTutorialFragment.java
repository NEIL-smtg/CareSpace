package com.example.carespace.MainPage;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.carespace.Exercise.ExerciseTabPageAdapter;
import com.example.carespace.R;
import com.example.carespace.VideoActivity.VideoUploaderActivity;
import com.example.carespace.VideoActivity.VideoAdapter;
import com.example.carespace.VideoActivity.VideoModel;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ExerciseTutorialFragment extends Fragment {

    //widgets
    private TextView btnAdd_video;
    private RecyclerView rv_exercise;
    private CardView tutorial_card;
    private TabLayout tabLayout;
    private ViewPager2 viewPager;

    private ArrayList<VideoModel> videoList;

    private VideoAdapter videoAdapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_exercise_tutorial, container, false);

        btnAdd_video = view.findViewById(R.id.btnAdd_video);
        btnAdd_video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().startActivity(new Intent(getActivity(), VideoUploaderActivity.class));
            }
        });

        //rv_exercise = view.findViewById(R.id.rv_exercise);
        //loadVideosFromFirebase();

        tabLayout = view.findViewById(R.id.tabLayout_exercise);
        viewPager = view.findViewById(R.id.vp_exercise);
        setupTab();

        return view;
    }

    private void setupTab()
    {
        ExerciseTabPageAdapter tabPageAdapter = new ExerciseTabPageAdapter(getActivity(),tabLayout.getTabCount());
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


    private void loadVideosFromFirebase()
    {
        videoList = new ArrayList<>();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Videos");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //clear list before adding data into it
                for (DataSnapshot ds : snapshot.getChildren())
                {
                    //get data
                    VideoModel videoModel = ds.getValue(VideoModel.class);
                    //add model into list
                    videoList.add(videoModel);
                }
                videoAdapter = new VideoAdapter(getActivity(),videoList);
                rv_exercise.setAdapter(videoAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }
}