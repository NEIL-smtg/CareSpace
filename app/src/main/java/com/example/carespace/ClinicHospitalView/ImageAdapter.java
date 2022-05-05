package com.example.carespace.ClinicHospitalView;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.carespace.R;

import java.util.HashMap;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.MyViewHolder> {

    private HashMap<String,String> imgMap;
    private Context context;

    public ImageAdapter(HashMap<String, String> imgMap, Context context) {
        this.imgMap = imgMap;
        this.context = context;
    }

    @NonNull
    @Override
    public ImageAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.img_item,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageAdapter.MyViewHolder holder, int position) {

        String key = "imgUrl" + (position+1);

        //Glide.with(context).load(imgMap.get(key).toString()).into(holder.img);

        Glide.with(context).asBitmap().load(imgMap.get(key).toString()).centerCrop().into(new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                Drawable dr = new BitmapDrawable(resource);
                holder.img.setImageDrawable(dr);
                holder.img.setScaleType(ImageView.ScaleType.CENTER_CROP);
            }
        });
    }

    @Override
    public int getItemCount() {
        return imgMap.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView img;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            img = itemView.findViewById(R.id.img);
        }
    }
}
