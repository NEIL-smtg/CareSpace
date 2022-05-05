package com.example.carespace.Appointment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.bumptech.glide.Glide;
import com.chivorn.datetimeoptionspicker.DateTimePickerView;
import com.example.carespace.ClinicHospitalView.DoctorsModel;
import com.example.carespace.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class AppointmentInfoView extends AppCompatActivity {

    //incoming intent
    private DoctorsModel model;
    private String timeSlot;
    private Date appointmentDate;

    //widgets
    private EditText name,contact,email,description;
    private AppCompatButton datePickerBtn;
    private DateTimePickerView dateTimePickerView;
    private ImageButton back, next;
    private TextView dateTime,docName,docDepartment,price, bornDate;
    private CircleImageView docImg;

    //vars
    private String bornDateStr;
    private String dateTimeStr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment_info_view);

        //doctor model(data)
        model = (DoctorsModel) getIntent().getSerializableExtra("model");
        timeSlot = getIntent().getStringExtra("timeSlot");
        appointmentDate = (Date) getIntent().getSerializableExtra("appointmentDate");

        setUpID();
        setUpAppointmentCardView();
    }

    private void setUpID()
    {
        datePickerBtn = findViewById(R.id.datePickerBtn);
        setupDatePicker();
        back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        next = findViewById(R.id.next);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nextOnClick();
            }
        });

        name = findViewById(R.id.name);
        contact = findViewById(R.id.contact);
        email = findViewById(R.id.email);
        description = findViewById(R.id.description);
        bornDate = findViewById(R.id.bornDate);

        dateTime = findViewById(R.id.appointmentDateTime);
        docName = findViewById(R.id.docName);
        docDepartment = findViewById(R.id.department);
        price = findViewById(R.id.price);
        docImg = findViewById(R.id.docImg);
    }

    private void setupDatePicker()
    {

        Calendar startDate = Calendar.getInstance();
        Calendar initDate = Calendar.getInstance();
        Calendar endDate = Calendar.getInstance();

        Date date = new Date();
        String format = new SimpleDateFormat("MM-dd").format(date.getTime());
        String yrFormat = new SimpleDateFormat("yyyy").format(date.getTime());

        int start_year = Integer.parseInt(yrFormat) - 90;  //youngest = 90 year old
        String start = start_year + "-" + format;

        int init_year = Integer.parseInt(yrFormat) - 18;  //init = 18 year old
        String init = init_year + "-" + format;

        int end_year = Integer.parseInt(yrFormat) - 4;  //oldest = 4 year old
        String end = end_year + "-" + format;


        try {
            Date s = new SimpleDateFormat("yyyy-MM-dd").parse(start);
            startDate.setTime(s);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        try {
            Date i = new SimpleDateFormat("yyyy-MM-dd").parse(init);
            initDate.setTime(i);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        try {
            Date en = new SimpleDateFormat("yyyy-MM-dd").parse(end);
            endDate.setTime(en);
        } catch (ParseException e) {
            e.printStackTrace();
        }


        datePickerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dateTimePickerView = new DateTimePickerView.Builder(AppointmentInfoView.this, new DateTimePickerView.OnTimeSelectListener() {

                    @Override
                    public void onTimeSelect(Date date, View v) {//callback

                        Date d = date;
                        bornDateStr = new SimpleDateFormat("yyyy-MM-dd").format(d);
                        bornDate.setText(bornDateStr);
                    }
                })
                        .setType(new boolean[]{true, true, true, false, false, false})   // year-month-day-hour-min-sec
                        .setCancelText("Cancel")
                        .setSubmitText("Confirm")
                        .setContentSize(20)
                        .setOutSideCancelable(false)// default is true
                        .isCyclic(true)// default is false
                        .setTitleColor(Color.BLACK)
                        .setSubmitColor(Color.BLACK)
                        .setCancelColor(Color.BLACK)
                        .setRangDate(startDate, endDate)
                        .setDate(initDate)
                        .build();

                dateTimePickerView.show();
            }

        });

    }

    private void setUpAppointmentCardView()
    {
        docName.setText(model.getName());
        docDepartment.setText(model.getDepartment());
        price.setText("RM " + model.getPrice());
        Glide.with(this).load(model.getImgUrl()).into(docImg);

        dateTimeStr = new SimpleDateFormat("yyyy-MMMM-dd").format(appointmentDate);
        String[] arr = dateTimeStr.split("-"); // arr[0] = year, [1] = month, [2] = day
        dateTime.setText(arr[2] + " " + arr[1] + " " + arr[0] + ", " + timeSlot );
    }

    private void nextOnClick()
    {
        boolean valid = checkEditTextValidation();

        if (valid)
        {
//            //format appointment date to normal format
//            String date = new SimpleDateFormat("yyyy-MM-dd").format(appointmentDate);

            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Appointment");
            String AppointmentID = ref.push().getKey(); //appointment ID

            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            String uid = user.getUid();

            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("appointmentID" , AppointmentID);
            hashMap.put("patientID",uid);
            hashMap.put("name",name.getText().toString());
            hashMap.put("contactNum",contact.getText().toString());
            hashMap.put("email",email.getText().toString());
            hashMap.put("bornDate",bornDateStr);
            hashMap.put("description", description.getText().toString());
            hashMap.put("doctorID",model.getDoctorID());
            hashMap.put("doctorDepartment", model.getDepartment());
            hashMap.put("price",model.getPrice());
            hashMap.put("AppointmentDate",dateTimeStr);
            hashMap.put("timeSlot",timeSlot);
            hashMap.put("doctorImgUrl",model.getImgUrl());
            hashMap.put("hospitalID",model.getHospitalID());
            hashMap.put("docName", model.getName());

            //show progress dialog
            ProgressDialog dialog = new ProgressDialog(this);
            dialog.setMessage("Registering your appointment....Please wait...");
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();

            //add appointment for patient
            ref.child("Patient").child(AppointmentID).setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    Toast.makeText(AppointmentInfoView.this, "Your appointment is successfully registered.", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    finish();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(AppointmentInfoView.this, "Failed to register your appointment.", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    finish();
                }
            });

            //add appointment for doctor
//            ref.child("Doctor").child(model.getDoctorID()).setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
//                @Override
//                public void onSuccess(Void unused) {
//                    Toast.makeText(AppointmentInfoView.this, "Your appointment is successfully registered.", Toast.LENGTH_SHORT).show();
//                    dialog.dismiss();
//                    finish();
//                }
//            }).addOnFailureListener(new OnFailureListener() {
//                @Override
//                public void onFailure(@NonNull Exception e) {
//                    Toast.makeText(AppointmentInfoView.this, "Failed to register your appointment.", Toast.LENGTH_SHORT).show();
//                    dialog.dismiss();
//                    finish();
//                }
//            });
        }
    }


    private boolean checkEditTextValidation()
    {
        boolean validName,validEmail,validContact, validBornDate, validDesc;

        if (name.getText().toString().isEmpty() || name.getText().toString().equals(" "))
        {
            name.setError("Name should not be empty");
            validName = false;
        }
        else
        {
            validName = true;
        }

        if (contact.getText().toString().isEmpty() || contact.getText().toString().equals(" "))
        {
            contact.setError("Contact should not be empty");
            validContact = false;
        }
        else
        {
            validContact = true;
        }

        if (email.getText().toString().isEmpty() || email.getText().toString().equals(" ") || !Patterns.EMAIL_ADDRESS.matcher(email.getText().toString().trim()).matches())
        {
            email.setError("Enter a valid e-mail");
            validEmail = false;
        }
        else
        {
            validEmail = true;
        }

        if (description.getText().toString().isEmpty() || description.getText().toString().equals(" "))
        {
            description.setError("Please describe your problem");
            validDesc = false;
        }
        else
        {
            validDesc = true;
        }

        if (bornDate.getText().toString().equals(""))
        {
            Toast.makeText(this, "You have to pick your born date", Toast.LENGTH_SHORT).show();
            validBornDate = false;
        }
        else
        {
            validBornDate = true;
        }

        return validName && validBornDate && validContact && validEmail && validDesc;
    }
}