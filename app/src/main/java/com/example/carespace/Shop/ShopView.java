package com.example.carespace.Shop;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.carespace.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ShopView extends AppCompatActivity implements ShopAdapter.onItemClickListener{

    //widgets
    private ImageButton back;
    private TextView search;

    //recycler view
    private RecyclerView rv;
    private ArrayList<ItemModel> itemList;

    //incoming vars
    private String hospitalID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_view);

        hospitalID = getIntent().getStringExtra("hospitalID");
        init();
        Search();
        setupRV();
    }

    private void setupRV()
    {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        itemList = new ArrayList<>();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Shop").child(hospitalID);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                itemList.clear();

                for (DataSnapshot ds : snapshot.getChildren())
                {
                    ItemModel model = ds.getValue(ItemModel.class);
                    itemList.add(model);
                }

                ShopAdapter adapter = new ShopAdapter(ShopView.this,  itemList, ShopView.this);
                rv.setAdapter(adapter);
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("db error",error.getMessage());
            }
        });
    }

    private void Search()
    {
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ShopView.this, Search.class);
                intent.putExtra("hospitalID",hospitalID);
                startActivity(intent);
            }
        });
    }

    private void init()
    {
        back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        search = findViewById(R.id.search);
        rv = findViewById(R.id.rv);

        //2 items per row
        GridLayoutManager gridLayoutManager = new GridLayoutManager(ShopView.this ,2,GridLayoutManager.VERTICAL,false);
        rv.setLayoutManager(gridLayoutManager);
    }

    @Override
    public void onItemClicked(int position) {
        Intent intent = new Intent(this, ItemView.class);
        intent.putExtra("model",itemList.get(position));
        startActivity(intent);
    }
}