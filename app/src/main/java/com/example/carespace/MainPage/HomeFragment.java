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

public class HomeFragment extends Fragment {

    //widgets
    private CardView btnNearbymed;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        //widgets
        btnNearbymed = (CardView) view.findViewById(R.id.btnNearbyMed);

        btnNearbymed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), NearbySearch.class));
                getActivity().finish();
            }
        });


        return view;
    }
}