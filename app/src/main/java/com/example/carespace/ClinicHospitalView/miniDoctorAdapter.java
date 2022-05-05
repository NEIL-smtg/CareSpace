package com.example.carespace.ClinicHospitalView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.carespace.R;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class miniDoctorAdapter extends RecyclerView.Adapter<miniDoctorAdapter.myViewHolder> {

    private Context context;
    private onItemClickListener onItemClickListener;
    private ArrayList<DoctorsModel> model;

    public miniDoctorAdapter(Context context, miniDoctorAdapter.onItemClickListener onItemClickListener, ArrayList<DoctorsModel> model) {
        this.context = context;
        this.onItemClickListener = onItemClickListener;
        this.model = model;
    }

    public interface onItemClickListener{
        void doctorOnClick(int position);
    }

    @NonNull
    @Override
    public miniDoctorAdapter.myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.mini_doctor_item, parent, false);
        return new myViewHolder(view, onItemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull miniDoctorAdapter.myViewHolder holder, int position) {

        DoctorsModel doctorsModel = model.get(position);

        holder.name.setText(doctorsModel.getName());
        holder.department.setText(doctorsModel.getDepartment());
        holder.rating.setText(doctorsModel.getRating());
        Glide.with(context).load(doctorsModel.getImgUrl()).into(holder.img);
    }

    @Override
    public int getItemCount() {
        return model.size();
    }

    public class myViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        onItemClickListener onItemClickListener;
        CircleImageView img;
        TextView department,name,rating;

        public myViewHolder(@NonNull View itemView, miniDoctorAdapter.onItemClickListener onItemClickListener) {
            super(itemView);
            itemView.setOnClickListener(this);

            img = itemView.findViewById(R.id.img);
            department = itemView.findViewById(R.id.department);
            name = itemView.findViewById(R.id.name);
            rating = itemView.findViewById(R.id.rating);

            this.onItemClickListener = onItemClickListener;
        }


        @Override
        public void onClick(View view) {
            onItemClickListener.doctorOnClick(getAdapterPosition());
        }
    }
}
