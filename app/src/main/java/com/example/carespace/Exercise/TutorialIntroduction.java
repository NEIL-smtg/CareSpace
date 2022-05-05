package com.example.carespace.Exercise;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.carespace.Profile.FavouritesModel;
import com.example.carespace.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class TutorialIntroduction extends AppCompatActivity {

    //firebase
    private FirebaseUser user;

    //widgets
    private CircleImageView profilePic;
    private EditText commentET;
    private Button startBtn,publishBtn;
    private ImageButton back;
    private TextView description, title, level, pplTrained;
    private ImageView img;
    private RecyclerView rv_comment;
    private Button favouritesBtn;

    //comment session adapter
    private CommentAdapter adapter;
    private ArrayList<CommentModel> commentList;

    //incoming intent vars
    private int position, tabNum;

    //tick mark
    private String tickMark = "\u2713";

    //model
    private TutorialModel tutorialModel;
    private dbTutorialModel db;


    //constants
    private final String ABS = "abs";
    private final String ARM = "arm";
    private final String YOGA = "yoga";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial_introduction);

        getIncomingIntent();

        init();
        setupView();
        isTyping();
        startOnClick();
        publishOnClick();
        setupCommentSession();
    }

    private void init()
    {
        tutorialModel = new TutorialModel();

        user = FirebaseAuth.getInstance().getCurrentUser();

        profilePic = findViewById(R.id.profilePic);
        commentET = findViewById(R.id.comment_ET);
        startBtn = findViewById(R.id.startBtn);
        publishBtn = findViewById(R.id.publishBtn);
        description = findViewById(R.id.description);
        back = findViewById(R.id.btnback);
        title = findViewById(R.id.title);
        level = findViewById(R.id.level);
        img = findViewById(R.id.img);
        rv_comment = findViewById(R.id.rv_comment);
        favouritesBtn = findViewById(R.id.favouritesBtn);
        pplTrained = findViewById(R.id.pplTrained);


        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }

    private void isTyping()
    {
        commentET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                publishBtn.setClickable(false);
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                publishBtn.setBackgroundResource(R.drawable.rounded_edge);
                publishBtn.setClickable(true);
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (commentET.getText().toString().isEmpty())
                {
                    publishBtn.setClickable(false);
                    publishBtn.setBackgroundResource(R.drawable.rounded_edge_grey);
                }
                else
                {
                    publishBtn.setBackgroundResource(R.drawable.rounded_edge);
                    publishBtn.setClickable(true);
                }
            }
        });
    }

    private void setupCommentSession()
    {
        commentList = new ArrayList<>();

        String path = getPath();
        path += "/Comment";

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(path);

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                commentList.clear();
                for (DataSnapshot ds : snapshot.getChildren())
                {
                    CommentModel commentModel = ds.getValue(CommentModel.class);
                    commentList.add(commentModel);
                }
                Collections.reverse(commentList);
                adapter = new CommentAdapter(TutorialIntroduction.this,commentList, ref);
                rv_comment.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }

    private void publishOnClick()
    {
        publishBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //then upload the comment to firebase
                uploadComment();
            }
        });
    }

    private void uploadComment()
    {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setMessage("Posting Comment...");
        progressDialog.show();

        //get path, decide which path the comment is added to firebase
        String path = getPath();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(path + "/Comment");

        //put necessary data into hash map
        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("uid",user.getUid());
        hashMap.put("photoUrl",user.getPhotoUrl().toString());
        hashMap.put("timestamp", ""+System.currentTimeMillis());
        hashMap.put("name",user.getDisplayName());
        hashMap.put("comment",commentET.getText().toString());

        //update/add comment into firebase
        ref.push().setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                //if success, display toast and set edit text to empty
                progressDialog.dismiss();
                Toast.makeText(TutorialIntroduction.this, "Your comment is posted", Toast.LENGTH_SHORT).show();
                commentET.setText("");
                commentET.setClickable(false);
                commentET.setBackgroundResource(R.drawable.rounded_edge_grey);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //if failed, display a toast, comment still remain in edit text
                progressDialog.dismiss();
                Toast.makeText(TutorialIntroduction.this, "Your comment is not posted..", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String getPath()
    {
        String path = "Exercise Tutorial/";

        if (tabNum == 0)
        {
            path += "Abs/";
            switch (position)
            {
                case 0:
                    path += "level 1";
                    return path;
                case 1:
                    path += "level 2";
                    return path;
                default:
                    path += "level 3";
                    return path;
            }
        }
        else if (tabNum ==1)
        {
            path += "Arm/";
            switch (position)
            {
                case 0:
                    path += "level 1";
                    return path;
                case 1:
                    path += "level 2";
                    return path;
                default:
                    path += "level 3";
                    return path;
            }
        }
        else
        {
            path += "Yoga/";
            switch (position)
            {
                case 0:
                    path += "level 1";
                    return path;
                case 1:
                    path += "level 2";
                    return path;
                default:
                    path += "level 3";
                    return path;
            }
        }
    }

    private void startOnClick()
    {
        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                addPersonTrained();

                Intent intent = new Intent(TutorialIntroduction.this,TutorialView.class);
                if (tabNum == 0)
                {
                    switch (position)
                    {
                        case 0:
                            intent.putExtra("giflist",tutorialModel.abs_lvl1_gif);
                            intent.putExtra("instruction",tutorialModel.abs_lvl1_instruction);
                            break;
                        case 1:
                            intent.putExtra("giflist",tutorialModel.abs_lvl2_gif);
                            intent.putExtra("instruction",tutorialModel.abs_lvl2_instruction);
                            break;
                        default:
                            intent.putExtra("giflist",tutorialModel.abs_lvl3_gif);
                            intent.putExtra("instruction",tutorialModel.abs_lvl3_instruction);
                            break;
                    }
                }
                else if (tabNum ==1)
                {
                    intent.putExtra("instruction",tutorialModel.abs_lvl2_gif);
                }
                else
                {
                    intent.putExtra("instruction",tutorialModel.abs_lvl3_gif);
                }

                startActivity(intent);
            }
        });
    }

    private void addPersonTrained()
    {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Tutorial People Trained").child(db.getTutorialID());
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists())
                {
                    int ppl = Integer.parseInt(snapshot.getValue(String.class));
                    ppl++;
                    ref.setValue(ppl+"");
                }
                else
                {
                    ref.setValue("1");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("db error",error.getMessage());
            }
        });

    }

    private void setupView()
    {
        //user profile pic
        Glide
                .with(this)
                .load(user.getPhotoUrl())
                .into(profilePic);

        if (tabNum == 0)
        {
            loadData(ABS,position);
//            img.setImageResource(tutorialModel.abs_pic[position]);
//            title.setText(tutorialModel.abs_title[position]);
//            level.setText(tutorialModel.abs_info[position]);
//            description.setText(tutorialModel.abs_description[position]);
        }
        else if (tabNum ==1)
        {
            loadData(ARM,position);

//            img.setImageResource(tutorialModel.arm_pic[position]);
//            title.setText(tutorialModel.arm_title[position]);
//            level.setText(tutorialModel.arm_info[position]);
//            description.setText(tutorialModel.abs_description[position]);
        }
        else
        {
            loadData(YOGA,position);

//            img.setImageResource(tutorialModel.yoga_pic[position]);
//            title.setText(tutorialModel.yoga_title[position]);
//            level.setText(tutorialModel.yoga_info[position]);
//            description.setText(tutorialModel.abs_description[position]);
        }

    }

    private void loadData(String type, int position)
    {
        String levelstr = "level "+(position+1);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Exercise Tutorial").child(type).child(levelstr);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren())
                {
                    db = ds.getValue(dbTutorialModel.class);

                    Glide.with(TutorialIntroduction.this).load(db.getImgUrl()).into(img);
                    title.setText(db.getTitle());
                    level.setText(db.getLevelInfo());
                    description.setText(tutorialModel.abs_description[position]);
                    break;
                }

                setupPplTrained();
                setInitialFav();
                favouriteOnClick();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("db error",error.getMessage());
            }
        });
    }

    private void setupPplTrained() {
        //ppl trained
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Tutorial People Trained").child(db.getTutorialID());
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists())
                {
                    String ppl = snapshot.getValue(String.class) + " " +getResources().getString(R.string.people_trained_title);
                    pplTrained.setText(ppl);
                }
                else
                {
                    String ppl = "0 " + getResources().getString(R.string.people_trained_title);
                    pplTrained.setText(ppl);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("db error",error.getMessage());
            }
        });
    }

    private void setInitialFav()
    {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Favourites").child(user.getUid());
        ref.child(db.getTutorialID()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child("tutorialID").exists())  //already fav
                {
                    setFavourite();
                }
                else
                {
                    setUnfavoured();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }


    private void favouriteOnClick()
    {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Favourites").child(user.getUid());
        ref.child(db.getTutorialID()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                if (snapshot.child("tutorialID").exists())  //already fav
                {
                    //remove
                    favouritesBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            removeFavourite();
                            setUnfavoured();
                        }
                    });
                }
                else
                {
                    favouritesBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            //add fav
                            addFavourite();
                            setFavourite();
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });

    }


    private void addFavourite()
    {
        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("tutorialID",db.getTutorialID());

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Favourites").child(user.getUid());

        ref.child(db.getTutorialID()).setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(TutorialIntroduction.this, "Added to your favourites.", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(TutorialIntroduction.this, "Failed to add to favourites.", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void removeFavourite()
    {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Favourites").child(user.getUid());
        ref.child(db.getTutorialID()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                snapshot.child("tutorialID").getRef().removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(TutorialIntroduction.this, "Removed from your favourites.", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(TutorialIntroduction.this, "Something went wrong, try again later.", Toast.LENGTH_SHORT).show();
                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("db error",error.getMessage());
            }
        });
    }

    private void setUnfavoured()
    {
        favouritesBtn.setBackgroundColor(getResources().getColor(R.color.teal_700));
        String btnTxt = "+ " + getResources().getString(R.string.favourite_title);
        favouritesBtn.setText(btnTxt);
        favouritesBtn.setTextColor(getResources().getColor(R.color.white));
    }

    private void setFavourite() {
        favouritesBtn.setBackgroundColor(getResources().getColor(R.color.orange_yellow));
        String btnTxt = tickMark + getResources().getString(R.string.added_favourites_title);
        favouritesBtn.setText(btnTxt);
        favouritesBtn.setTextColor(getResources().getColor(R.color.black));
    }

    private void getIncomingIntent()
    {
        position = getIntent().getIntExtra("position",-1);
        tabNum = getIntent().getIntExtra("tabNum",-1);
    }
}