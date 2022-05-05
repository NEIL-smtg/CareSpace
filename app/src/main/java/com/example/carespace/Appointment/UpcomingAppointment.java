package com.example.carespace.Appointment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.bumptech.glide.Glide;
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
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;


public class UpcomingAppointment extends Fragment implements AppointmentViewAdapter.onItemClickListener {

    //widgets
    private TextView upcomingDateTime,docDepartment, docName;
    private CircleImageView docImg;
    private RecyclerView rv;
    private CardView upcomingApptCard;

    //model for rv
    private ArrayList<AppointmentModel> appointmentList;
    private AppointmentModel upcomingModel;
    private FirebaseUser user;
    private String uid;

    //check appointment is due
    private boolean checked = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_upcoming_appointment, container, false);

        init(view);
        setupView();

        return view;
    }

    private void setupView()
    {
        ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        //get uid to compare with data in firebase
        user = FirebaseAuth.getInstance().getCurrentUser();
        uid = user.getUid();

        appointmentList = new ArrayList<>(); //init
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Appointment").child("Patient");

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (getActivity() == null) {
                    return;
                }
                appointmentList.clear();

                for (DataSnapshot ds : snapshot.getChildren())
                {
                    if (ds.child("patientID").getValue().toString().equals(uid))
                    {
                        AppointmentModel model = ds.getValue(AppointmentModel.class);
                        appointmentList.add(model);
                    }
                }

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

                if (!checked)
                {
                    checkIfAppointmentDue(); // check if appointment is already due, if already due, delete the appointment
                    checked = true;
                }

                if (appointmentList.size() != 0)
                {
                    upcomingApptCard.setVisibility(View.VISIBLE);

                    //for first upcoming appointment
                    AppointmentModel m = appointmentList.get(0);
                    String[] arr = m.getAppointmentDate().split("-"); // arr[0] = year, [1] = month, [2] = day
                    String dateStr = arr[2] + " " + arr[1] + " " + arr[0] + ", " + m.getTimeSlot();
                    upcomingDateTime.setText(dateStr);


                    Glide.with(getActivity()).load(m.getDoctorImgUrl()).into(docImg);
                    docDepartment.setText(m.getDoctorDepartment());
                    docName.setText(m.getDocName());

                    upcomingModel = appointmentList.get(0);
                    appointmentList.remove(0); //delete the main upcoming event

                    AppointmentViewAdapter adapter = new AppointmentViewAdapter(getActivity(),appointmentList, UpcomingAppointment.this);
                    rv.setAdapter(adapter);

                }
                else
                {
                    upcomingApptCard.setVisibility(View.GONE);
                }
                progressDialog.dismiss();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("DatabaseError" , error.getMessage());
            }
        });

    }

    private void checkIfAppointmentDue()
    {
        ArrayList<AppointmentModel> outdatedList = new ArrayList<>();  //arraylist to store appointment that set to delete

        try {
            for(int i = 0 ; i < appointmentList.size(); i++)
            {
                String dateStr = appointmentList.get(i).getAppointmentDate() + " " + appointmentList.get(i).getTimeSlot();
                Date date = new SimpleDateFormat("yyyy-MMMM-dd hh a").parse(dateStr);
                Date curDate = new SimpleDateFormat("yyyy-MMMM-dd hh a").parse(Calendar.getInstance().getTime().toString());

                if (curDate.after(date))
                {
                    outdatedList.add(appointmentList.get(i));
                }
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (outdatedList.size() != 0)
        {
            removeAppointment(outdatedList);
        }
    }

    private void removeAppointment(ArrayList<AppointmentModel> outdatedList)
    {
        DatabaseReference dbref = FirebaseDatabase.getInstance().getReference("Appointment");

        for (int i = 0 ; i < outdatedList.size() ; i++)
        {
            String id = outdatedList.get(i).getAppointmentID();
            dbref.child("Patient").child(id).removeValue();
        }

        //add outdated appointment to another reference for reviewing use
        for (int i = 0 ; i < outdatedList.size() ; i++)
        {
            String id = outdatedList.get(i).getAppointmentID();
            dbref.child("Outdated Appointment").child(id).setValue(outdatedList.get(i));
        }
    }

    private void init(View view)
    {
        upcomingDateTime = view.findViewById(R.id.dateTime);
        docImg = view.findViewById(R.id.docImg);
        docName = view.findViewById(R.id.docName);
        docDepartment = view.findViewById(R.id.docDepartment);
        rv = view.findViewById(R.id.rv);
        upcomingApptCard = view.findViewById(R.id.upcomingApptCard);


        upcomingApptCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), AppointmentDetailView.class);
                intent.putExtra("model",upcomingModel);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onAppointmentClicked(int position) {
        AppointmentModel model = appointmentList.get(position);
        Intent intent = new Intent(getActivity(), AppointmentDetailView.class);
        intent.putExtra("model",model);
        startActivity(intent);
    }
}