package com.example.carespace.Appointment;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.FontRes;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.carespace.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class AppointmentViewAdapter extends RecyclerView.Adapter<AppointmentViewAdapter.myViewHolder> {

    private Context context;
    private ArrayList<AppointmentModel> list;
    private onItemClickListener onItemClickListener;

    public interface onItemClickListener{
        void onAppointmentClicked(int position);
    }

    public AppointmentViewAdapter(Context context, ArrayList<AppointmentModel> list, onItemClickListener onItemClickListener) {
        this.context = context;
        this.list = list;
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public AppointmentViewAdapter.myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.appointment_item, parent, false);
        return new myViewHolder(view, onItemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull AppointmentViewAdapter.myViewHolder holder, int position) {

        AppointmentModel model = list.get(position);

        holder.name.setText(model.getDocName());
        holder.department.setText(model.getDoctorDepartment());
        Glide.with(context).load(model.getDoctorImgUrl()).into(holder.docImg);

        //appointment date
        String[] arr = model.getAppointmentDate().split("-"); // arr[0] = year, [1] = month, [2] = day
        String dateStr = arr[2] + " " + arr[1] + " " + arr[0] + ", " + model.getTimeSlot();
        holder.dateTime.setText(dateStr);

    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    public class myViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView dateTime,name,department;
        CircleImageView docImg;
        onItemClickListener onItemClickListener;

        public myViewHolder(@NonNull View itemView, AppointmentViewAdapter.onItemClickListener onItemClickListener) {
            super(itemView);
            itemView.setOnClickListener(this);

            dateTime = itemView.findViewById(R.id.dateTime);
            name = itemView.findViewById(R.id.docName);
            department = itemView.findViewById(R.id.docDepartment);
            docImg = itemView.findViewById(R.id.docImg);

            this.onItemClickListener = onItemClickListener;
        }

        @Override
        public void onClick(View view) {
            onItemClickListener.onAppointmentClicked(getAdapterPosition());
        }
    }
}
