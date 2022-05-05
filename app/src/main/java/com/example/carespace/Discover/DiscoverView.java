package com.example.carespace.Discover;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.carespace.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class DiscoverView extends AppCompatActivity implements PostAdapter.onItemClickedListener{

    //widget
    private RecyclerView rv_post;
    private ImageButton back,add;


    //post adapter
    private PostAdapter postAdapter;
    private ArrayList<PostModel> postList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discover_view);


        init();
        setUpView();
    }

    private void init()
    {
        rv_post = findViewById(R.id.rv_post);
        back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        add = findViewById(R.id.addPost);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(DiscoverView.this,AddPost.class));
            }
        });
    }

    private void setUpView()
    {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        postList = new ArrayList<>();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Post");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //clear the list when there are changes to data, so that there is no repetitive post
                postList.clear();

                for (DataSnapshot ds : snapshot.getChildren())
                {
                    PostModel postModel = ds.getValue(PostModel.class);
                    postList.add(postModel);
                }

                postAdapter = new PostAdapter(DiscoverView.this,postList,DiscoverView.this);
                rv_post.setAdapter(postAdapter);

                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("db error",error.getMessage());
            }
        });
    }

    @Override
    public void onItemClicked(int position) {
        Intent intent = new Intent(DiscoverView.this,PostCommentView.class);
        intent.putExtra("postID",postList.get(position).getPostID());
        startActivity(intent);
    }
}