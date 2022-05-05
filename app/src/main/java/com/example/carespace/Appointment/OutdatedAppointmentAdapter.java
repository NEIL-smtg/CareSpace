package com.example.carespace.Appointment;

import android.content.Context;
import android.content.Intent;
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

public class OutdatedAppointmentAdapter extends RecyclerView.Adapter<OutdatedAppointmentAdapter.myViewHolder> {

    private Context context;
    private ArrayList<AppointmentModel> list;

    public OutdatedAppointmentAdapter(Context context, ArrayList<AppointmentModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public OutdatedAppointmentAdapter.myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.outdated_appointment_item,parent,false);
        return new myViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OutdatedAppointmentAdapter.myViewHolder holder, int position) {

        AppointmentModel model = list.get(position);

        holder.name.setText(model.getDocName());
        holder.department.setText(model.getDoctorDepartment());
        Glide.with(context).load(model.getDoctorImgUrl()).into(holder.docImg);

        //appointment date
        String[] arr = model.getAppointmentDate().split("-"); // arr[0] = year, [1] = month, [2] = day
        String dateStr = arr[2] + " " + arr[1] + " " + arr[0] + ", " + model.getTimeSlot();
        holder.dateTime.setText(dateStr);

        holder.reviewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context,addReview.class);
                intent.putExtra("doctorID",model.getDoctorID());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class myViewHolder extends RecyclerView.ViewHolder {

        TextView dateTime,name,department;
        CircleImageView docImg;
        TextView reviewBtn;

        public myViewHolder(@NonNull View itemView) {
            super(itemView);

            dateTime = itemView.findViewById(R.id.dateTime);
            name = itemView.findViewById(R.id.docName);
            department = itemView.findViewById(R.id.docDepartment);
            docImg = itemView.findViewById(R.id.docImg);
            reviewBtn = itemView.findViewById(R.id.reviewBtn);
        }
    }
}
