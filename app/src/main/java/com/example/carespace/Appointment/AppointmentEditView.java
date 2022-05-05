package com.example.carespace.Appointment;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.chivorn.datetimeoptionspicker.DateTimePickerView;
import com.example.carespace.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import devs.mulham.horizontalcalendar.HorizontalCalendar;
import devs.mulham.horizontalcalendar.utils.HorizontalCalendarListener;

public class AppointmentEditView extends AppCompatActivity {

    //incoming model
    private AppointmentModel model;

    //widgets
    private ImageButton back,next;
    private EditText name,contact,email,description;
    private AppCompatButton bornDatePickerBtn;
    private TextView bornDate;
    private DateTimePickerView dateTimePickerView;

    //borndate vars
    private String bornDateStr;
    private Date date;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment_edit_view);

        model = (AppointmentModel) getIntent().getSerializableExtra("model");

        init();
        setupView();

    }

    private void setupView()
    {
        name.setText(model.getName());
        contact.setText(model.getContactNum());
        email.setText(model.getEmail());
        description.setText(model.getDescription());

        Calendar currBornDate = Calendar.getInstance();

        try {
            date = new SimpleDateFormat("yyyy-MM-dd").parse(model.getBornDate());
            Toast.makeText(this, ""+date, Toast.LENGTH_SHORT).show();

        } catch (ParseException e) {
            e.printStackTrace();
        }

        currBornDate.setTime(date);
        bornDate.setText(new SimpleDateFormat("yyyy-MM-dd").format(date));

        setupBornDatePicker(currBornDate);
        nextOnClick();
    }

    private void setupBornDatePicker(Calendar currBornDate) {
        Calendar startDate = Calendar.getInstance();
        Calendar endDate = Calendar.getInstance();

        Date date = new Date();
        String format = new SimpleDateFormat("MM-dd").format(date.getTime());
        String yrFormat = new SimpleDateFormat("yyyy").format(date.getTime());

        int start_year = Integer.parseInt(yrFormat) - 90;  //youngest = 90 year old
        String start = start_year + "-" + format;

        int end_year = Integer.parseInt(yrFormat) - 4;  //oldest = 4 year old
        String end = end_year + "-" + format;


        try {
            Date s = new SimpleDateFormat("yyyy-MM-dd").parse(start);
            startDate.setTime(s);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        try {
            Date en = new SimpleDateFormat("yyyy-MM-dd").parse(end);
            endDate.setTime(en);
        } catch (ParseException e) {
            e.printStackTrace();
        }


        bornDatePickerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dateTimePickerView = new DateTimePickerView.Builder(AppointmentEditView.this, new DateTimePickerView.OnTimeSelectListener() {

                    @Override
                    public void onTimeSelect(Date date, View v) {//callback

                        Date d = date;
                        bornDateStr = new SimpleDateFormat("yyyy-MMMM-dd").format(d);
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
                        .setDate(currBornDate)
                        .build();

                dateTimePickerView.show();
            }

        });
    }

    private void nextOnClick()
    {
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               boolean valid = checkEditTextValidation();

               if (valid)
               {
                   AlertDialog alertDialog = new AlertDialog.Builder(AppointmentEditView.this)
                           .setMessage("Are you confirm to change your details ?")
                           .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                               @Override
                               public void onClick(DialogInterface dialogInterface, int i) {
                                   updateToFirebase();
                               }
                           }).setNegativeButton("Cancel",null)
                           .show();
               }
            }
        });
    }

    private boolean checkEditTextValidation() {
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

    private void updateToFirebase()
    {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("bornDate",bornDateStr);
        hashMap.put("name", name.getText().toString());
        hashMap.put("contactNum", contact.getText().toString());
        hashMap.put("email", email.getText().toString());
        hashMap.put("description", description.getText().toString());

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Appointment").child("Patient");
        ref.child(model.getAppointmentID()).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(AppointmentEditView.this, "Your detail has been successfully updated.", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(AppointmentEditView.this, "Failed to update your detail. Please try again.", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        });
    }


    private void init()
    {
        back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        name = findViewById(R.id.name);
        contact = findViewById(R.id.contactNum);
        email = findViewById(R.id.email);
        description = findViewById(R.id.description);
        bornDate = findViewById(R.id.bornDate);
        bornDatePickerBtn = findViewById(R.id.bornDatePickerBtn);
        next = findViewById(R.id.next);
    }


}