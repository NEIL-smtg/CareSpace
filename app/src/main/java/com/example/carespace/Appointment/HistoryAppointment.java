package com.example.carespace.Appointment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.carespace.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class HistoryAppointment extends Fragment {

    //recycler view
    private RecyclerView rv;
    private ArrayList<AppointmentModel> appointmentList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_history_appointment, container, false);

        rv = view.findViewById(R.id.rv);
        setupRV();

        return view;
    }

    private void setupRV()
    {
        appointmentList = new ArrayList<>();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Appointment").child("Outdated Appointment");

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                appointmentList.clear();
                for (DataSnapshot ds : snapshot.getChildren())
                {
                    if (ds.child("patientID").getValue().toString().equals(user.getUid()))
                    {
                        AppointmentModel model = ds.getValue(AppointmentModel.class);
                        appointmentList.add(model);
                    }
                }
                OutdatedAppointmentAdapter adapter = new OutdatedAppointmentAdapter(getActivity(),appointmentList);
                rv.setAdapter(adapter);

                //sort the list with date
                Collections.sort(appointmentList, new Comparator<AppointmentModel>() {
                    DateFormat f = new SimpleDateFormat("yyyy-MMMM-dd hh a");
                    @Override
                    public int compare(AppointmentModel o1, AppointmentModel o2) {
                        try {
                            return f.parse(o1.getAppointmentDate() + " " + o1.getTimeSlot()).compareTo(f.parse(o2.getAppointmentDate() + " " + o2.getTimeSlot()));
                        } catch (ParseException e) {
                            throw new IllegalArgumentException(e);
                        }
                    }
                });

                //latest on top
                Collections.reverse(appointmentList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("db error", error.getMessage());
            }
        });
    }

}