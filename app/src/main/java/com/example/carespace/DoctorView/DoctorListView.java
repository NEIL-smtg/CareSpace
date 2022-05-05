package com.example.carespace.DoctorView;

import androidx.annotation.FontRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.carespace.ClinicHospitalView.DoctorsModel;
import com.example.carespace.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class DoctorListView extends AppCompatActivity implements DoctorListRecommendAdapter.onItemClickListener, DoctorListNormalAdapter.onItemClickListener{

    //widgets
    private CircleImageView backBtn;
    private RecyclerView rv_recommend,rv_department;
    private Spinner spinner;

    //vars
    private String hospitalID;

    //rv arraylist
    private ArrayList<DoctorsModel> recommendList, departmentList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_list_view);

        getIncomingIntent();
        setID();
        setupRecommendRV();
        setupDepartmentRV();
    }

    private void setupDepartmentRV()
    {
        departmentList = new ArrayList<>();

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                String department = adapterView.getSelectedItem().toString();

                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Normal Doctor");
                ref.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        departmentList.clear();

                        for (DataSnapshot ds : snapshot.getChildren())
                        {
                            if (
                                    ds.child("hospitalID").getValue().toString().equals(hospitalID) &&
                                    ds.child("department").getValue().toString().equals(department)
                            )
                            {
                                DoctorsModel model = ds.getValue(DoctorsModel.class);
                                departmentList.add(model);
                            }
                        }

                        DoctorListNormalAdapter adapter = new DoctorListNormalAdapter(DoctorListView.this,DoctorListView.this,departmentList);
                        rv_department.setAdapter(adapter);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(DoctorListView.this, ""+error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) { }
        });
    }

    private void setupRecommendRV()
    {
        recommendList = new ArrayList<>();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Recommend Doctor");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                recommendList.clear();

                for (DataSnapshot ds: snapshot.getChildren())
                {
                    if (ds.child("hospitalID").getValue().toString().equals(hospitalID))
                    {
                        DoctorsModel doctorsModel = ds.getValue(DoctorsModel.class);
                        recommendList.add(doctorsModel);
                    }
                }

                DoctorListRecommendAdapter adapter = new DoctorListRecommendAdapter(DoctorListView.this, DoctorListView.this,recommendList);
                rv_recommend.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(DoctorListView.this, ""+error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setID()
    {
        backBtn = findViewById(R.id.back);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        spinner = findViewById(R.id.Department_spinner);

        rv_recommend = findViewById(R.id.rv_recommend);
        rv_department = findViewById(R.id.rv_department);
    }

    private void getIncomingIntent() {
        hospitalID = getIntent().getStringExtra("hospitalID");
    }

    @Override
    public void onRecommendItemClick(int position) {
        Intent intent = new Intent(this,DoctorProfileView.class);
        intent.putExtra("model",recommendList.get(position));
        startActivity(intent);
    }

    @Override
    public void onNormalDoctorClicked(int position) {
        Intent intent = new Intent(this,DoctorProfileView.class);
        intent.putExtra("model",departmentList.get(position));
        startActivity(intent);
    }
}