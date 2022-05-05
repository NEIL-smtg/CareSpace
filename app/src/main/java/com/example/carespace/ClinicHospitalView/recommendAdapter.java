package com.example.carespace.ClinicHospitalView;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.carespace.R;

import java.util.ArrayList;

public class recommendAdapter extends RecyclerView.Adapter<recommendAdapter.myViewHolder> {

    private onRecommendItemClickListener onRecommendItemClickListener;
    private ArrayList<ClinicHospitalModel> list;
    private Context context;


    public interface onRecommendItemClickListener {
        void onRecommendItemClick(int position);
    }

    public recommendAdapter(recommendAdapter.onRecommendItemClickListener onRecommendItemClickListener, ArrayList<ClinicHospitalModel> list, Context context) {
        this.onRecommendItemClickListener = onRecommendItemClickListener;
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.clinic_hospital_recommend_item,parent,false);
        return new myViewHolder(view, onRecommendItemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull myViewHolder holder, int position) {
        ClinicHospitalModel model = list.get(position);

        String imgurl = model.getPictures().get("imgUrl1");

        Glide.with(context).asBitmap().load(imgurl).centerCrop().into(new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                Drawable dr = new BitmapDrawable(resource);
                holder.img.setImageDrawable(dr);
                holder.img.setScaleType(ImageView.ScaleType.CENTER_CROP);
            }
        });
        holder.name.setText(model.getName());
        holder.city.setText(model.getCity());
        holder.rating.setText(model.getRating());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class myViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        onRecommendItemClickListener onRecommendItemClickListener;
        TextView name,rating,city;
        ImageView img;

        public myViewHolder(@NonNull View itemView, onRecommendItemClickListener onRecommendItemClickListener) {
            super(itemView);
            itemView.setOnClickListener(this);

            name = itemView.findViewById(R.id.name);
            rating = itemView.findViewById(R.id.rating);
            city = itemView.findViewById(R.id.city_name);
            img = itemView.findViewById(R.id.clinicHospitalImg);
            this.onRecommendItemClickListener = onRecommendItemClickListener;
        }

        @Override
        public void onClick(View view) {
            onRecommendItemClickListener.onRecommendItemClick(getAdapterPosition());
        }
    }
}
