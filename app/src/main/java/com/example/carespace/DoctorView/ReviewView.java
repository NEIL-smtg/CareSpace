package com.example.carespace.DoctorView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.carespace.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ReviewView extends AppCompatActivity {

    //widgets
    private ImageButton back;
    private RecyclerView rv_comment;
    private TextView comment_null;

    //rv vars
    private ArrayList<ReviewModel> reviewList;

    //incoming item
    private String doctorID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_view);

        doctorID = getIntent().getStringExtra("doctorID");

        back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        rv_comment = findViewById(R.id.rv_comment);
        comment_null = findViewById(R.id.CommentNullTV);

        setupRV();
    }

    private void setupRV()
    {
        reviewList = new ArrayList<>();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Doctor Review");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                reviewList.clear();

                for (DataSnapshot ds : snapshot.getChildren())
                {
                    if (ds.child("doctorID").getValue().toString().equals(doctorID))
                    {
                        rv_comment.setVisibility(View.VISIBLE);
                        comment_null.setVisibility(View.INVISIBLE);

                        ReviewModel reviewModel = ds.getValue(ReviewModel.class);
                        reviewList.add(reviewModel);
                    }

                    if (!ds.child("doctorID").exists())
                    {
                        comment_null.setVisibility(View.VISIBLE);
                        rv_comment.setVisibility(View.INVISIBLE);
                    }
                }

                ReviewAdapter adapter = new ReviewAdapter(ReviewView.this,reviewList,ref);
                rv_comment.setAdapter(adapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ReviewView.this, ""+error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}