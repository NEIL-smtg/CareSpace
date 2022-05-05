package com.example.carespace.MainPage;

import android.content.Intent;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.carespace.Appointment.AppointmentView;
import com.example.carespace.Chat.ChatUserList;
import com.example.carespace.ClinicHospitalView.ClinicHospitalView;
import com.example.carespace.Discover.DiscoverView;
import com.example.carespace.Nearby.NearbySearch;
import com.example.carespace.R;
import com.example.carespace.Alarm.Alarm;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class HomeFragment extends Fragment {

    //widgets
    private CardView cardBtnNearbymed, cardBtnTimer, cardBtnAppointment, cardBtnDiscover, cardBtnClinicHospital, cardChatBtn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        //widgets
        cardBtnNearbymed =  view.findViewById(R.id.cardbtnNearbyMed);
        cardBtnTimer =  view.findViewById(R.id.cardbtn_addAlarm);
        cardBtnAppointment = view.findViewById(R.id.appointment_cardBtn);
        cardBtnDiscover = view.findViewById(R.id.cardbtnDiscover);
        cardBtnClinicHospital = view.findViewById(R.id.clinic_hospital_cardBtn);
        cardChatBtn = view.findViewById(R.id.chat_card_btn);


        cardBtnNearbymed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), NearbySearch.class));
            }
        });

        cardBtnTimer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), Alarm.class));
            }
        });

        cardBtnAppointment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), AppointmentView.class));
            }
        });

        cardBtnDiscover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), DiscoverView.class));
            }
        });

        cardBtnClinicHospital.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), ClinicHospitalView.class));
            }
        });

        cardChatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), ChatUserList.class));
            }
        });

        return view;
    }

}