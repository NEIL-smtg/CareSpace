package com.example.carespace.ClinicHospitalView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.carespace.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class AdaptiveView extends Fragment implements recommendAdapter.onRecommendItemClickListener, topRatedAdapter.onTopRatedItemClickListener {

    //tab postion
    private int position;

    //widget
    private RecyclerView rv_recommend, rv_topRated;
    private ProgressDialog progressDialog;

    //rv view
    private ArrayList<ClinicHospitalModel> clinicRecommendList,clinicTopRatedList,hospitalRecommendList,hospitalTopRatedList;

    public AdaptiveView(int position) {
        this.position = position;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_adaptive_view, container, false);

        rv_recommend = view.findViewById(R.id.rv_recommend);
        rv_topRated = view.findViewById(R.id.rv_topRated);

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        if (position == 0)
        {
            setupClinicRV();
        }
        else
        {
            setupHospitalRV();
        }

        return view;
    }


    private void setupHospitalRV()
    {
        hospitalRecommendList = new ArrayList<>();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Recommend Hospital");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                hospitalRecommendList.clear();
                for (DataSnapshot ds : snapshot.getChildren())
                {
                    ClinicHospitalModel model = ds.getValue(ClinicHospitalModel.class);
                    hospitalRecommendList.add(model);
                }
                recommendAdapter recommendAdapter = new recommendAdapter(AdaptiveView.this,hospitalRecommendList,getActivity());
                rv_recommend.setAdapter(recommendAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(), ""+error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


        hospitalTopRatedList = new ArrayList<>();
        ref = FirebaseDatabase.getInstance().getReference("Top Rated Hospital");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                hospitalTopRatedList.clear();
                for (DataSnapshot ds : snapshot.getChildren())
                {
                    ClinicHospitalModel model = ds.getValue(ClinicHospitalModel.class);
                    hospitalTopRatedList.add(model);
                }
                topRatedAdapter topRatedAdapter = new topRatedAdapter(AdaptiveView.this,hospitalTopRatedList,getActivity());
                rv_topRated.setAdapter(topRatedAdapter);
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(), ""+error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }


    private void setupClinicRV()
    {
        clinicRecommendList = new ArrayList<>();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Recommend Clinic");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                clinicRecommendList.clear();
                for (DataSnapshot ds : snapshot.getChildren())
                {
                    ClinicHospitalModel model = ds.getValue(ClinicHospitalModel.class);
                    clinicRecommendList.add(model);
                }
                recommendAdapter recommendAdapter = new recommendAdapter(AdaptiveView.this,clinicRecommendList,getActivity());
                rv_recommend.setAdapter(recommendAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("db error", error.getMessage());
            }
        });


        clinicTopRatedList = new ArrayList<>();
        ref = FirebaseDatabase.getInstance().getReference("Top Rated Clinic");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                clinicTopRatedList.clear();
                for (DataSnapshot ds : snapshot.getChildren())
                {
                    ClinicHospitalModel model = ds.getValue(ClinicHospitalModel.class);
                    clinicTopRatedList.add(model);
                }
                topRatedAdapter topRatedAdapter = new topRatedAdapter(AdaptiveView.this,clinicTopRatedList,getActivity());
                rv_topRated.setAdapter(topRatedAdapter);
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(), ""+error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }


    @Override
    public void onRecommendItemClick(int position) {
        Intent intent = new Intent(getActivity(),ClinicHospitalProfile.class);
        if (this.position == 0)
        {
            intent.putExtra("list", clinicRecommendList.get(position));
        }
        else
        {
            intent.putExtra("list",hospitalRecommendList.get(position));
        }
        startActivity(intent);
    }

    @Override
    public void onTopRatedItemClick(int position) {
        Intent intent = new Intent(getActivity(),ClinicHospitalProfile.class);
        if (this.position == 0)
        {
            intent.putExtra("list", clinicTopRatedList.get(position));
        }
        else
        {
            intent.putExtra("list",hospitalTopRatedList.get(position));
        }
        startActivity(intent);
    }
}