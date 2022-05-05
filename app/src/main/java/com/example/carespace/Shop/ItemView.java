package com.example.carespace.Shop;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.example.carespace.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class ItemView extends AppCompatActivity {

    //widgets
    private TextView name,rating,description,price, buyBtn, sold;
    private RatingBar ratingBar;
    private ImageView img;
    private ImageButton back;

    //rv
    private RecyclerView reviewRV;
    private ArrayList<ItemReviewModel> reviewList;

    //incoming vars
    private ItemModel model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_view);

        model = (ItemModel) getIntent().getSerializableExtra("model");

        init();
        setupView();
        setupReviewRV();
    }

    private void setupReviewRV()
    {
        reviewList = new ArrayList<>();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Item Review");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                reviewList.clear();

                for (DataSnapshot ds : snapshot.getChildren())
                {
                    if (ds.child("itemID").getValue().toString().equals(model.getItemID()))
                    {
                        ItemReviewModel reviewModel = ds.getValue(ItemReviewModel.class);
                        reviewList.add(reviewModel);
                    }
                }

                ItemReviewAdapter adapter = new ItemReviewAdapter(ItemView.this, reviewList, ref);
                reviewRV.setAdapter(adapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("db error",error.getMessage());
            }
        });
    }

    private void setupView()
    {
        name.setText(model.getItemName());
        rating.setText(model.getItemRating() + "/5");
        description.setText(model.getItemDescription());
        price.setText("RM " + model.getItemPrice());
        sold.setText(model.getItemSold());

        ratingBar.setRating(Float.parseFloat(model.getItemRating()));

        Glide.with(this).asBitmap().load(model.getItemImg()).centerCrop().into(new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                Drawable dr = new BitmapDrawable(resource);
                img.setImageDrawable(dr);
                img.setScaleType(ImageView.ScaleType.CENTER_CROP);
            }
        });


        buyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ItemView.this, GetCustomerInfo.class);
                intent.putExtra("model", model);
                startActivity(intent);
            }
        });
    }

    private void init()
    {
        name = findViewById(R.id.name);
        rating = findViewById(R.id.rating);
        description = findViewById(R.id.description);
        price = findViewById(R.id.price);
        buyBtn = findViewById(R.id.buyBtn);
        sold = findViewById(R.id.sold);
        ratingBar = findViewById(R.id.ratingBar);
        img = findViewById(R.id.img);
        reviewRV = findViewById(R.id.rv);

        back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }
}