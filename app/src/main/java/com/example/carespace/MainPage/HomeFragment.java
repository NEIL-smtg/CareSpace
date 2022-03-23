package com.example.carespace.MainPage;

import android.content.Intent;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.carespace.Nearby.NearbySearch;
import com.example.carespace.R;
import com.example.carespace.TimerAlarm.Alarm;

public class HomeFragment extends Fragment {

    //widgets
    private CardView cardbtnNearbymed,cardbtnTimer;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        //widgets
        cardbtnNearbymed = (CardView) view.findViewById(R.id.cardbtnNearbyMed);
        cardbtnTimer = (CardView) view.findViewById(R.id.cardbtn_addAlarm);

        cardbtnNearbymed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), NearbySearch.class));
                getActivity().finish();
            }
        });

        cardbtnTimer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), Alarm.class));
            }
        });

        return view;
    }
}