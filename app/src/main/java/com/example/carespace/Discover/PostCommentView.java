package com.example.carespace.Discover;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.carespace.Exercise.CommentAdapter;
import com.example.carespace.Exercise.CommentModel;
import com.example.carespace.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class PostCommentView extends AppCompatActivity {

    //widgets
    private ImageButton backbtn;
    private Button publishbtn;
    private EditText commentET;
    private CircleImageView profilePic;
    private TextView name,timestamp,title;
    private ImageView img;

    //get postID from incoming intent
    String postID;

    //recycler comment view
    RecyclerView recyclerView;
    ArrayList<CommentModel> commentList;
    CommentAdapter commentAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_comment_view);

        getIncomingIntent();

        backbtn = findViewById(R.id.back);
        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        publishbtn = findViewById(R.id.publishBtn);
        recyclerView = findViewById(R.id.rv);
        commentET = findViewById(R.id.comment_ET);
        profilePic = findViewById(R.id.profilePic);
        name = findViewById(R.id.name);
        timestamp = findViewById(R.id.timestamp);
        title = findViewById(R.id.title);
        img = findViewById(R.id.img);

        isTyping();
        publishOnClick();
        setupPost();
        setupCommentView();
    }

    private void publishOnClick()
    {
        publishbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                publishbtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ProgressDialog progressDialog = new ProgressDialog(PostCommentView.this);
                        progressDialog.setMessage("Posting your comment...");
                        progressDialog.setCanceledOnTouchOutside(false);
                        progressDialog.show();

                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("uid",user.getUid());
                        hashMap.put("photoUrl",user.getPhotoUrl().toString());
                        hashMap.put("timestamp",""+System.currentTimeMillis());
                        hashMap.put("name",user.getDisplayName());
                        hashMap.put("comment",commentET.getText().toString());


                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Post Comment").child(postID);

                        ref.push().setValue(hashMap)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        progressDialog.dismiss();
                                        Toast.makeText(PostCommentView.this, "Your comment is posted", Toast.LENGTH_SHORT).show();
                                        commentET.setText("");
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        progressDialog.dismiss();
                                        Toast.makeText(PostCommentView.this, "Error : Failed to post comment", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                });
            }
        });
    }

    private void setupCommentView()
    {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Post Comment").child(postID);
        commentList = new ArrayList<>();

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                //clear the list to avoid repetitive comment everytime where there is changes to comments
                commentList.clear();

                for (DataSnapshot ds : snapshot.getChildren())
                {
                    CommentModel commentModel = ds.getValue(CommentModel.class);
                    commentList.add(commentModel);
                }

                commentAdapter = new CommentAdapter(PostCommentView.this,commentList,ref);
                recyclerView.setAdapter(commentAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(PostCommentView.this, ""+error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupPost()
    {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Post").child(postID);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.child("profilePicUrl").exists())
                {
                    Glide
                            .with(PostCommentView.this)
                            .load(snapshot.child("profilePicUrl").getValue().toString())
                            .into(profilePic);
                }

                if (snapshot.child("name").exists())
                {
                    name.setText(snapshot.child("name").getValue().toString());
                }

                if (snapshot.child("timestamp").exists())
                {
                    //format timestamp
                    String time = snapshot.child("timestamp").getValue().toString();
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTimeInMillis(Long.parseLong(time));
                    String formattedDateTime = DateFormat.format("dd/MM/yyyy K:mm a", calendar).toString();
                    timestamp.setText(formattedDateTime);
                }

                if (snapshot.child("imgUrl").exists())
                {
                    Glide
                            .with(PostCommentView.this)
                            .load(snapshot.child("imgUrl").getValue().toString())
                            .into(img);
                }

                if (snapshot.child("title").exists())
                {
                    title.setText(snapshot.child("title").getValue().toString());
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(PostCommentView.this, ""+error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void isTyping()
    {
        commentET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                publishbtn.setBackgroundResource(R.drawable.rounded_edge_grey);
                publishbtn.setClickable(false);
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                publishbtn.setBackgroundResource(R.drawable.rounded_edge);
                publishbtn.setClickable(true);
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (commentET.getText().toString().isEmpty())
                {
                    publishbtn.setBackgroundResource(R.drawable.rounded_edge_grey);
                    publishbtn.setClickable(false);
                }
                else {
                    publishbtn.setBackgroundResource(R.drawable.rounded_edge);
                    publishbtn.setClickable(true);
                }
            }
        });
    }

    private void getIncomingIntent()
    {
        postID = getIntent().getStringExtra("postID");
    }
}