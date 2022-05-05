package com.example.carespace.ClinicHospitalView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.carespace.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class DepartmentDoctorListView extends AppCompatActivity implements DepartmentDoctorsListAdapter.onItemClickListener{

    //widgets
    private TextView viewTitle;
    private ImageButton back;

    //recycler view
    private RecyclerView rv;
    private ArrayList<DoctorsModel> doctorList;

    //incoming vars
    private String department,hospitalID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_department_doctor_list_view);

        getIncomingIntent();
        init();
    }

    private void init(){
        viewTitle = findViewById(R.id.viewTitle);
        back = findViewById(R.id.back);
        rv = findViewById(R.id.rv);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        viewTitle.setText(department);

        DepartmentDoctorsListAdapter adapter = new DepartmentDoctorsListAdapter(this, doctorList,this);
        rv.setAdapter(adapter);
    }

    private void getIncomingIntent() {
        department = getIntent().getStringExtra("department");
        hospitalID = getIntent().getStringExtra("hospitalID");

        doctorList = new ArrayList<>();
        doctorList = (ArrayList<DoctorsModel>) getIntent().getSerializableExtra("docList");
    }

    @Override
    public void onItemClicked(int position) {
        Intent intent = new Intent(getApplicationContext(), DepartmentProfileView.class);
        intent.putExtra("model", doctorList.get(position));
        startActivity(intent);
    }
}
