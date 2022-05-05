package com.example.carespace.ClinicHospitalView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.carespace.DoctorView.DoctorListView;
import com.example.carespace.DoctorView.DoctorProfileView;
import com.example.carespace.R;
import com.example.carespace.Shop.ItemModel;
import com.example.carespace.Shop.ItemView;
import com.example.carespace.Shop.ShopView;
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

public class ClinicHospitalProfile extends AppCompatActivity implements miniShopAdapter.onItemClickListener,miniExclusiveAdapter.onItemClickListener, miniDoctorAdapter.onItemClickListener{

    //clinic/hospital model
    private ClinicHospitalModel model;

    //widgets
    private TextView name,description,rating,city,address,workingHours,contactNum;
    private RecyclerView rv_doctor,rv_shop, rv_department;
    private ViewPager2 vp;
    private Button followBtn;

    //rv
    private ArrayList<DoctorsModel> doctorlist;
    private ArrayList<ItemModel> itemList;
    private HospitalDepartmentModel departmentModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clinic_hospital_profile);

        getIncomingIntent();
        init();
        setUpView();
        setOnClickFollowing();
    }

    private void setUpView()
    {
        ImageAdapter vpAdapter = new ImageAdapter(model.getPictures(),this);
        vp.setAdapter(vpAdapter);

        name.setText(model.getName());
        description.setText(model.getDescription());
        rating.setText("  "+model.getRating());
        city.setText("  "+model.getCity());
        address.setText(model.getAddress());
        workingHours.setText(model.getWorkingHours());
        contactNum.setText(model.getContactNum());

        setUpMiniShopRV();

        miniExclusiveAdapter miniExclusiveAdapter = new miniExclusiveAdapter(this,this, 5);
        rv_department.setAdapter(miniExclusiveAdapter);

        setupDoctorRV();
    }

    private void setUpMiniShopRV()
    {
        itemList = new ArrayList<>();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Shop").child(model.getHospitalID());
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                itemList.clear();

                for (DataSnapshot ds : snapshot.getChildren())
                {
                    if (itemList.size() == 5)
                    {
                        break;
                    }

                    ItemModel itemModel = ds.getValue(ItemModel.class);
                    itemList.add(itemModel);
                }

                miniShopAdapter miniShopAdapter = new miniShopAdapter(ClinicHospitalProfile.this, itemList, ClinicHospitalProfile.this);
                rv_shop.setAdapter(miniShopAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("db error",error.getMessage());
            }
        });
    }

    private void setupDoctorRV()
    {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        doctorlist = new ArrayList<>();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Recommend Doctor");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                doctorlist.clear();

                for (DataSnapshot ds : snapshot.getChildren())
                {
                    if (ds.child("hospitalID").getValue().toString().equals(model.getHospitalID()))
                    {
                        if (doctorlist.size() == 5)
                        {
                            break;
                        }
                        DoctorsModel doctorsModel = ds.getValue(DoctorsModel.class);
                        doctorlist.add(doctorsModel);
                    }
                }
                miniDoctorAdapter miniDoctorAdapter = new miniDoctorAdapter(ClinicHospitalProfile.this,ClinicHospitalProfile.this,doctorlist);
                rv_doctor.setAdapter(miniDoctorAdapter);
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ClinicHospitalProfile.this, ""+error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void init()
    {
        departmentModel = new HospitalDepartmentModel();

        name = findViewById(R.id.name);
        description =findViewById(R.id.description);
        rating = findViewById(R.id.rating);
        city = findViewById(R.id.city_name);
        address = findViewById(R.id.address);
        workingHours = findViewById(R.id.workingHours);
        contactNum = findViewById(R.id.contactNum);
        followBtn = findViewById(R.id.followBtn);


        //start maps intent
        address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent geoIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("geo:0,0?q=" +address.getText().toString()));
                startActivity(geoIntent);
            }
        });

        ImageButton back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        TextView browseDoctor = findViewById(R.id.browseDocTV);
        browseDoctor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ClinicHospitalProfile.this, DoctorListView.class);
                intent.putExtra("hospitalID",model.getHospitalID());
                startActivity(intent);
            }
        });

        TextView browseDepartment = findViewById(R.id.departmentTV);
        browseDepartment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ClinicHospitalProfile.this, DepartmentView.class);
                intent.putExtra("hospitalID",model.getHospitalID());
                startActivity(intent);
            }
        });

        TextView browseShop = findViewById(R.id.browseShopTV);
        browseShop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ClinicHospitalProfile.this, ShopView.class);
                intent.putExtra("hospitalID",model.getHospitalID());
                startActivity(intent);
            }
        });

        rv_doctor = findViewById(R.id.rv_doctor);
        rv_department = findViewById(R.id.rv_exclusive);
        rv_shop = findViewById(R.id.rv_shop);

        vp = findViewById(R.id.vp);
    }

    private void getIncomingIntent() {
        model = (ClinicHospitalModel)  getIntent().getSerializableExtra("list");
    }

    @Override
    public void ShopitemOnClick(int position) {
        Intent intent = new Intent(this, ItemView.class);
        intent.putExtra("model",itemList.get(position));
        startActivity(intent);
    }

    @Override
    public void onDepartmentOnClick(int position) {
        Intent intent = new Intent(this, DepartmentProfileView.class);
        intent.putExtra("position", position);
        intent.putExtra("hospitalID",model.getHospitalID());
        intent.putExtra("department",departmentModel.getDepartment()[position]);
        startActivity(intent);
    }

    @Override
    public void doctorOnClick(int position) {
        Intent intent = new Intent(this, DoctorProfileView.class);
        intent.putExtra("model",doctorlist.get(position));
        startActivity(intent);
    }

    private void setOnClickFollowing()
    {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Following").child(user.getUid()).child(model.getHospitalID());

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
                        Toast.makeText(ClinicHospitalProfile.this, "You have unfollowed this hospital.", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ClinicHospitalProfile.this, "Something went wrong, try again later.", Toast.LENGTH_SHORT).show();
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
        hashMap.put("type","hospital");

        ref.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(ClinicHospitalProfile.this, "You are following this hospital.", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ClinicHospitalProfile.this, "Something went wrong, try again later.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}