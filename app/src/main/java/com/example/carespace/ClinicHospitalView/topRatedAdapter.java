package com.example.carespace.ClinicHospitalView;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.carespace.R;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class topRatedAdapter extends RecyclerView.Adapter<topRatedAdapter.myViewHolder> {

    private onTopRatedItemClickListener onTopRatedItemClickListener;
    private ArrayList<ClinicHospitalModel> list;
    private Context context;

    public interface onTopRatedItemClickListener {
        void onTopRatedItemClick(int position);
    }

    public topRatedAdapter(topRatedAdapter.onTopRatedItemClickListener onTopRatedItemClickListener, ArrayList<ClinicHospitalModel> list, Context context) {
        this.onTopRatedItemClickListener = onTopRatedItemClickListener;
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.clinic_hostpital_toprated_item,parent,false);
        return new myViewHolder(view, onTopRatedItemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull myViewHolder holder, int position) {

        ClinicHospitalModel model = list.get(position);

        Glide.with(context).load(Uri.parse(model.getPictures().get("imgUrl1").toString())).into(holder.img);
        holder.name.setText(model.getName());
        holder.city.setText(model.getCity());
        holder.rating.setText(model.getRating());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class myViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        onTopRatedItemClickListener onTopRatedItemClickListener;
        TextView name,rating,city;
        CircleImageView img;

        public myViewHolder(@NonNull View itemView, onTopRatedItemClickListener onTopRatedItemClickListener) {
            super(itemView);
            itemView.setOnClickListener(this);

            name = itemView.findViewById(R.id.name);
            rating = itemView.findViewById(R.id.rating);
            city = itemView.findViewById(R.id.city_name);
            img = itemView.findViewById(R.id.clinicHospitalImg);
            this.onTopRatedItemClickListener = onTopRatedItemClickListener;
        }

        @Override
        public void onClick(View view) {
            onTopRatedItemClickListener.onTopRatedItemClick(getAdapterPosition());
        }
    }
}
