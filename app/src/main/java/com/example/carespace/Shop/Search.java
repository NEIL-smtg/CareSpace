package com.example.carespace.Shop;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SearchView;

import com.example.carespace.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Search extends AppCompatActivity {

    //widgets
    private SearchView searchView;
    private ListView listView;
    private ProgressDialog progressDialog;
    private ImageButton back;

    //model
    private ArrayList<String> itemNameList;
    private ArrayList<ItemModel> itemList;
    private ArrayAdapter<String> adapter;

    //incoming intent
    private String hospitalID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        hospitalID = getIntent().getStringExtra("hospitalID");
        init();
        getDataFromDB();
    }

    private void getDataFromDB()
    {
        progressDialog = new ProgressDialog(this);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Shop").child(hospitalID);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren())
                {
                    ItemModel model = ds.getValue(ItemModel.class);
                    itemList.add(model);
                }

                progressDialog.dismiss();
                //setup search filter
                setupSearch();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("db error", error.getMessage());
            }
        });
    }


    private void setupSearch()
    {
        for (ItemModel item : itemList) {
            itemNameList.add(item.getItemName());
        }

        adapter = new ArrayAdapter<>(this, R.layout.search_item, itemNameList);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                ItemModel model = itemList.get(i);

                Intent intent = new Intent(Search.this, ItemView.class);
                intent.putExtra("model",model);
                startActivity(intent);
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Search.this.adapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                Search.this.adapter.getFilter().filter(query);
                return false;
            }
        });

    }

    private void init()
    {
        searchView = findViewById(R.id.searchView);
        listView = findViewById(R.id.listView);
        itemNameList = new ArrayList<>();
        itemList = new ArrayList<>();
        back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}