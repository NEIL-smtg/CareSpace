package com.example.carespace.DoctorView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.carespace.Appointment.AppointmentPickerView;
import com.example.carespace.Chat.ChatActivity;
import com.example.carespace.ClinicHospitalView.ClinicHospitalProfile;
import com.example.carespace.ClinicHospitalView.DoctorsModel;
import com.example.carespace.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class DoctorProfileView extends AppCompatActivity {

    //intent model
    private DoctorsModel model;

    //widgets
    private TextView name,department,ratingInfo,description,workingHour,workingExperience,price,chatBtn, appointmentBtn, viewReviewBtn;
    private RatingBar ratingBar;
    private RecyclerView rv_comment;
    private ImageButton back;
    private CircleImageView img;
    private TextView commentNull;
    private Button followBtn;

    //recycler view
    private ArrayList<ReviewModel> reviewList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_profile_view);

        getIncomingIntent();
        setupID();
        setUpView();
        setUpRV();
        Chat();
        setOnClickFollowing();
    }

    private void Chat()
    {
        chatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DoctorProfileView.this, ChatActivity.class);
                intent.putExtra("targetID",model.getDoctorID());
                intent.putExtra("targetName",model.getName());
                intent.putExtra("targetImg",model.getImgUrl());
                startActivity(intent);
            }
        });
    }

    private void setUpRV()
    {
        reviewList = new ArrayList<>();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Doctor Review");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                reviewList.clear();

                for (DataSnapshot ds: snapshot.getChildren())
                {
                    if (ds.child("doctorID").getValue().toString().equals(model.getDoctorID()))
                    {
                        rv_comment.setVisibility(View.VISIBLE);
                        commentNull.setVisibility(View.INVISIBLE);

                        //get children count = get reviews count
                        ratingInfo.setText(ds.getChildrenCount() + " reviews");

                        ReviewModel reviewModel = ds.getValue(ReviewModel.class);
                        reviewList.add(reviewModel);
                    }

                    if (!ds.child("doctorID").exists())
                    {
                        commentNull.setVisibility(View.VISIBLE);
                        rv_comment.setVisibility(View.INVISIBLE);
                    }

                    DoctorProfileCommentAdapter adapter = new DoctorProfileCommentAdapter(DoctorProfileView.this, reviewList);
                    rv_comment.setAdapter(adapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(DoctorProfileView.this, ""+error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setUpView()
    {
        name.setText(model.getName());
        department.setText(model.getDepartment() + " Specialist");
        ratingBar.setRating(Float.parseFloat(model.getRating()));
        description.setText(model.getDescription());
        workingHour.setText(model.getWorkingHours());
        workingExperience.setText(model.getWorkingExperience() + " years of working experience");
        price.setText("RM " + model.getPrice());
        Glide.with(this).load(model.getImgUrl()).into(img);
    }

    private void setupID()
    {
        img = findViewById(R.id.img);
        name = findViewById(R.id.name);
        department = findViewById(R.id.department);
        ratingInfo = findViewById(R.id.ratingInfo);
        description  = findViewById(R.id.description);
        workingHour = findViewById(R.id.workingHours);
        workingExperience = findViewById(R.id.workingExperience);
        price = findViewById(R.id.price);
        chatBtn = findViewById(R.id.chatBtn);
        appointmentBtn = findViewById(R.id.appointmentBtn);
        viewReviewBtn = findViewById(R.id.viewAllCommentBtn);
        commentNull = findViewById(R.id.CommentNullTV);
        followBtn = findViewById(R.id.followBtn);

        rv_comment = findViewById(R.id.rv_comment);

        ratingBar = findViewById(R.id.ratingBar);

        back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        viewReviewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DoctorProfileView.this, ReviewView.class);
                intent.putExtra("doctorID",model.getDoctorID());
                startActivity(intent);
            }
        });

        appointmentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DoctorProfileView.this, AppointmentPickerView.class);
                intent.putExtra("model",model);
                startActivity(intent);
            }
        });

    }
    private void getIncomingIntent() { model = (DoctorsModel) getIntent().getSerializableExtra("model"); }

    private void setOnClickFollowing()
    {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Following").child(user.getUid()).child(model.getDoctorID());

        initializeFollowingBtn(user,ref);

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists())
                {
                    followBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            //data exist, if clicked, remove data from firebase, set normal button
                            Unfollow(ref);
                            setUnfollowBtn();
                        }
                    });
                }
                else
                {
                    followBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            //data not exist, ready to follow
                            Follow(user,ref);
                            setFollowingBtn();
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("db error",error.getMessage());
            }
        });


    }

    private void initializeFollowingBtn(FirebaseUser user, DatabaseReference ref)
    {
        //initialize button
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists())
                {
                    setFollowingBtn();
                }
                else
                {
                    setUnfollowBtn();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("db error", error.getMessage());
            }
        });
    }

    private void setFollowingBtn()
    {
        followBtn.setBackgroundColor(getResources().getColor(R.color.orange_yellow));
        followBtn.setTextColor(getResources().getColor(R.color.black));
        followBtn.setText(R.string.following_title);
    }

    private void setUnfollowBtn()
    {
        followBtn.setBackgroundColor(getResources().getColor(R.color.teal_700));
        followBtn.setTextColor(getResources().getColor(R.color.white));
        followBtn.setText(R.string.follow_title);
    }

    private void Unfollow(DatabaseReference ref)
    {
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                snapshot.getRef().removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(DoctorProfileView.this, "You have unfollowed this hospital.", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(DoctorProfileView.this, "Something went wrong, try again later.", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("db error",error.getMessage());
            }
        });
    }

    private void Follow(FirebaseUser user, DatabaseReference ref)
    {
        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("type","doctor");

        ref.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(DoctorProfileView.this, "You are following this hospital.", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(DoctorProfileView.this, "Something went wrong, try again later.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}