package com.example.carespace.DoctorView;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.carespace.Appointment.AppointmentPickerView;
import com.example.carespace.ClinicHospitalView.DoctorsModel;
import com.example.carespace.R;

import java.util.ArrayList;

public class DoctorListRecommendAdapter extends RecyclerView.Adapter<DoctorListRecommendAdapter.myViewHolder> {

    private Context context;
    private onItemClickListener onItemClickListener;
    private ArrayList<DoctorsModel> doctorList;

    public DoctorListRecommendAdapter(Context context, DoctorListRecommendAdapter.onItemClickListener onItemClickListener, ArrayList<DoctorsModel> doctorList) {
        this.context = context;
        this.onItemClickListener = onItemClickListener;
        this.doctorList = doctorList;
    }

    public interface onItemClickListener{
        void onRecommendItemClick(int position);
    }

    @NonNull
    @Override
    public DoctorListRecommendAdapter.myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.doctor_recommend_item, parent, false);
        return new myViewHolder(view, onItemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull DoctorListRecommendAdapter.myViewHolder holder, int position) {
        DoctorsModel model = doctorList.get(position);

        Glide.with(context).load(model.getImgUrl()).into(holder.img);
        holder.name.setText(model.getName());
        holder.department.setText(model.getDepartment());
        holder.rating.setText(model.getRating());
        holder.workingHours.setText(model.getWorkingHours());
        holder.workingExperience.setText(model.getWorkingExperience() + " years");
        holder.description.setText(model.getDescription());
        holder.price.setText("RM "+model.getPrice());

        holder.appointmentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, AppointmentPickerView.class);
                intent.putExtra("model",model);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return doctorList.size();
    }

    public class myViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        ImageView img;
        TextView name,department,rating,workingExperience,description,workingHours,price, appointmentBtn;
        onItemClickListener onItemClickListener;

        public myViewHolder(@NonNull View itemView, DoctorListRecommendAdapter.onItemClickListener onItemClickListener) {
            super(itemView);
            itemView.setOnClickListener(this);

            img = itemView.findViewById(R.id.img);
            name = itemView.findViewById(R.id.name);
            department = itemView.findViewById(R.id.department);
            rating = itemView.findViewById(R.id.rating);
            workingExperience = itemView.findViewById(R.id.workingExperience);
            description = itemView.findViewById(R.id.description);
            workingHours = itemView.findViewById(R.id.workingHours);
            price = itemView.findViewById(R.id.price);
            appointmentBtn = itemView.findViewById(R.id.appointmentBtn);

            this.onItemClickListener = onItemClickListener;
        }

        @Override
        public void onClick(View view) {
            onItemClickListener.onRecommendItemClick(getAdapterPosition());
        }
    }
}
