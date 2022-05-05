package com.example.carespace.ClinicHospitalView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.carespace.DoctorView.DoctorProfileView;
import com.example.carespace.R;
import com.example.carespace.Shop.ItemModel;
import com.example.carespace.Shop.ItemView;
import com.example.carespace.Shop.ShopView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.util.ArrayList;

public class DepartmentProfileView extends AppCompatActivity implements miniShopAdapter.onItemClickListener, miniDoctorAdapter.onItemClickListener{

    //widgets
    private TextView name,description, workingHours, contactNum, address;
    private ImageView img;
    private ImageButton back;

    //recycler view
    private RecyclerView rvDoc;
    private ArrayList<DoctorsModel> docList;

    private RecyclerView rvShop;
    private ArrayList<ItemModel> itemList;

    //incoming vars
    private String hospitalID;
    private String department;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_department_profile_view);

        init();
        //setupView();
        setUpRVdoc();
        setupRVshop();
    }

    private void setupRVshop()
    {
        itemList = new ArrayList<>();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Shop").child(hospitalID);
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

                miniShopAdapter miniShopAdapter = new miniShopAdapter(DepartmentProfileView.this, itemList, DepartmentProfileView.this);
                rvShop.setAdapter(miniShopAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("db error",error.getMessage());
            }
        });
    }

    private void setUpRVdoc()
    {
        docList = new ArrayList<>();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Normal Doctor");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                docList.clear();

                for (DataSnapshot ds : snapshot.getChildren())
                {
                    if (ds.child("department").getValue(String.class).equals(department))
                    {
                        DoctorsModel doctorsModel = ds.getValue(DoctorsModel.class);
                        docList.add(doctorsModel);
                    }
                }

                miniDoctorAdapter adapter = new miniDoctorAdapter(DepartmentProfileView.this, DepartmentProfileView.this, docList);
                rvDoc.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("db error",error.getMessage());
            }
        });
    }

    private void setupView()
    {
        //TODO add department to db
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Department").child(hospitalID);
        ref.child(department).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                dbDepartmentModel model = snapshot.getValue(dbDepartmentModel.class);
                name.setText(model.getDepartmentName());
                description.setText(model.getDescription());
                workingHours.setText(model.getWorkingHours());
                contactNum.setText(model.getContactNum());
                address.setText(model.getAddress());
                Glide.with(DepartmentProfileView.this).load(model.getImgUrl()).into(img);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("db error",error.getMessage());
            }
        });
    }

    private void init()
    {
        hospitalID = getIntent().getStringExtra("hospitalID");
        department = getIntent().getStringExtra("department");
        name = findViewById(R.id.name);
        description = findViewById(R.id.description);
        workingHours = findViewById(R.id.workingHours);
        contactNum = findViewById(R.id.contactNum);
        address = findViewById(R.id.address);
        img = findViewById(R.id.img);
        rvDoc = findViewById(R.id.rv_doctor);
        rvShop = findViewById(R.id.rv_shop);
        back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        TextView browseShop = findViewById(R.id.browseShopTV);
        browseShop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(DepartmentProfileView.this, ShopView.class));
            }
        });

        TextView browseDoc = findViewById(R.id.browseDocTV);
        browseDoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DepartmentProfileView.this, DepartmentDoctorListView.class);
                intent.putExtra("department",department);
                intent.putExtra("hospitalID",hospitalID);
                intent.putExtra("docList",docList);
                startActivity(intent);
            }
        });
    }

    @Override
    public void ShopitemOnClick(int position) {
        Intent intent = new Intent(this, ItemView.class);
        intent.putExtra("model", itemList.get(position));
        startActivity(intent);
    }

    @Override
    public void doctorOnClick(int position) {
        Intent intent = new Intent(this, DoctorProfileView.class);
        intent.putExtra("model", docList.get(position));
        startActivity(intent);
    }
}