package com.example.carespace.Appointment;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.carespace.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class addReview extends AppCompatActivity {

    //widgets
    private ImageButton back,upload;
    private RatingBar ratingBar;
    private EditText review;
    private CircleImageView img;

    //firebase user
    private FirebaseUser user;

    //incoming vars
    private String doctorID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_review);

        doctorID = getIntent().getStringExtra("doctorID");
        init();
        uploadOnClick();
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
        upload = findViewById(R.id.uploadBtn);
        ratingBar = findViewById(R.id.ratingBar);
        review = findViewById(R.id.review);
        img = findViewById(R.id.img);

        user = FirebaseAuth.getInstance().getCurrentUser();
        Glide.with(this).load(user.getPhotoUrl()).into(img);
    }

    private void uploadOnClick()
    {
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!review.getText().toString().isEmpty())
                {
                    uploadToFirebase();
                }
                else if (review.getText().toString().isEmpty())
                {
                    Toast.makeText(addReview.this, "You can't post a empty review", Toast.LENGTH_SHORT).show();
                }
                else if (ratingBar.getRating() == 0)
                {
                    Toast.makeText(addReview.this, "Rating cannot be 0", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void uploadToFirebase()
    {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();;

        Date current = Calendar.getInstance().getTime();
        String timestamp = new SimpleDateFormat("yyyy-MM-dd hh:mm a").format(current);

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("uid",user.getUid());
        hashMap.put("photoUrl",user.getPhotoUrl().toString());
        hashMap.put("name",user.getDisplayName());
        hashMap.put("timestamp",timestamp);
        hashMap.put("rating",ratingBar.getRating()+"");
        hashMap.put("review",review.getText().toString());
        hashMap.put("doctorID",doctorID);


        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Doctor Review");
        ref.push().setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(addReview.this, "Your review is published.", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(addReview.this, "Failed to published", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        });
    }


}