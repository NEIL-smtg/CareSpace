package com.example.carespace.ClinicHospitalView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;

import com.example.carespace.R;

public class DepartmentView extends AppCompatActivity implements miniExclusiveAdapter.onItemClickListener{

    //widgets
    private ImageButton back;
    private RecyclerView rv;

    //model
    private HospitalDepartmentModel model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_department_view);

        init();
        setupRV();
    }

    private void setupRV()
    {
        GridLayoutManager grid = new GridLayoutManager(this,2, GridLayoutManager.VERTICAL,false);
        rv.setLayoutManager(grid);

        miniExclusiveAdapter miniExclusiveAdapter = new miniExclusiveAdapter(this,this, model.getDepartment().length);
        rv.setAdapter(miniExclusiveAdapter);
    }

    private void init()
    {
        back = findViewById(R.id.back);
        rv = findViewById(R.id.rv);
        model = new HospitalDepartmentModel();
    }

    @Override
    public void onDepartmentOnClick(int position) {
        Intent intent = new Intent(this, DepartmentProfileView.class);
        intent.putExtra("position", position);
        intent.putExtra("department", model.getDepartment()[position]);
        startActivity(intent);
    }
}