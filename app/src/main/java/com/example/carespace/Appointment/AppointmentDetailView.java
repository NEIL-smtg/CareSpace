package com.example.carespace.Appointment;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.carespace.ClinicHospitalView.ClinicHospitalModel;
import com.example.carespace.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;

public class AppointmentDetailView extends AppCompatActivity {

    //incoming intent vars
    private AppointmentModel model;

    //widgets
    private ImageButton back,edit;
    private RelativeLayout deleteBtn;
    private TextView appointmentDateTime,docName,docDepartment,price,hospitalName,hospitalAddr,patientName,patientBornDate,patientDesc,patientContact,patientEmail;
    private CircleImageView docImg;

    //database reference
    private static final String[] hospitalClinic = {"Recommend Hospital","Top Rated Hospital" ,"Recommend Clinic","Top Rated Clinic"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment_detail_view);

        model = (AppointmentModel)  getIntent().getSerializableExtra("model");

        init();
        setupView();
    }

    private void setupView()
    {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        String[] arr = model.getAppointmentDate().split("-");
        String dateStr = arr[2] + " " + arr[1] + " " + arr[0] + " ," + model.getTimeSlot(); // DAY MONTH YEAR TIME
        appointmentDateTime.setText(dateStr);

        docName.setText(model.getDocName());
        docDepartment.setText(model.getDoctorDepartment());
        Glide.with(this).load(model.getDoctorImgUrl()).into(docImg);

        String pricestr = "RM " + model.getPrice();
        price.setText(pricestr);

        patientName.setText(model.getName());
        patientBornDate.setText(model.getBornDate());
        patientDesc.setText(model.getDescription());
        patientContact.setText(model.getContactNum());
        patientEmail.setText(model.getEmail());

        String hospitalID = model.getHospitalID();

        for ( int i = 0; i < hospitalClinic.length; i++) {

            DatabaseReference ref = FirebaseDatabase.getInstance().getReference(hospitalClinic[i]);
            ref.child(hospitalID).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists())
                    {
                        hospitalName.setText(snapshot.child("name").getValue().toString());
                        hospitalAddr.setText(snapshot.child("address").getValue().toString());
                        progressDialog.dismiss();
                    }
                    else
                    {
                        ref.removeEventListener(this);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.d("db error", error.getMessage());
                }
            });

            if (i == hospitalClinic.length-1)
            {
                Log.d("hospitalID not found", "hospitalID not found");
                progressDialog.dismiss();
            }

        }
    }

    private void deleteAppointment()
    {
        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setTitle("Delete Appointment")
                .setMessage("Are you sure you want to delete this appointment ?\nThis cannot be undone.")
                .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        ProgressDialog progressDialog = new ProgressDialog(AppointmentDetailView.this);
                        progressDialog.setCanceledOnTouchOutside(false);
                        progressDialog.show();

                       String id = model.getAppointmentID();
                       DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Appointment").child("Patient");
                       ref.child(id).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                           @Override
                           public void onSuccess(Void unused) {
                               Toast.makeText(AppointmentDetailView.this, "Your appointment has been cancelled.", Toast.LENGTH_SHORT).show();
                               progressDialog.dismiss();
                               finish();
                           }
                       }).addOnFailureListener(new OnFailureListener() {
                           @Override
                           public void onFailure(@NonNull Exception e) {
                               Toast.makeText(AppointmentDetailView.this, "Failed to cancel your appointment. Please try again.", Toast.LENGTH_SHORT).show();
                               progressDialog.dismiss();
                           }
                       });

                    }
                })
                .setNegativeButton("Cancel",null)
                .show();
    }

    private void init()
    {
        back = findViewById(R.id.back);
        edit = findViewById(R.id.editBtn);
        deleteBtn = findViewById(R.id.deleteBtn);
        appointmentDateTime = findViewById(R.id.dateTime);
        docName = findViewById(R.id.docName);
        docDepartment = findViewById(R.id.docDepartment);
        hospitalName = findViewById(R.id.hospital);
        hospitalAddr = findViewById(R.id.address);
        patientName = findViewById(R.id.name);
        patientBornDate = findViewById(R.id.bornDate);
        patientDesc = findViewById(R.id.description);
        docImg = findViewById(R.id.docImg);
        price = findViewById(R.id.price);
        patientContact = findViewById(R.id.contactNum);
        patientEmail = findViewById(R.id.email);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),AppointmentEditView.class);
                intent.putExtra("model",model);
                startActivity(intent);
            }
        });

        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteAppointment();
            }
        });
    }

}