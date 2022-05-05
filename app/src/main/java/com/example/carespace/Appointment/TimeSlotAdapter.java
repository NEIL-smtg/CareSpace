package com.example.carespace.Appointment;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.carespace.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class TimeSlotAdapter extends RecyclerView.Adapter<TimeSlotAdapter.myViewHolder> {

    private onItemClickListener onItemClickListener;
    private Context context;
    private ArrayList<String> timeslotList;
    private String doctorID;
    private Date currDate;
    private boolean selected = false;

    public TimeSlotAdapter(onItemClickListener onItemClickListener, Context context, ArrayList<String> timeslotList, String doctorID, Date currDate) {
        this.onItemClickListener = onItemClickListener;
        this.context = context;
        this.timeslotList = timeslotList;
        this.doctorID = doctorID;
        this.currDate = currDate;
    }

    public interface onItemClickListener{
        void onTimeSlotClicked(int position);
    }

    @NonNull
    @Override
    public TimeSlotAdapter.myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.time_slot_item, parent, false);
        return new myViewHolder(view , onItemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull TimeSlotAdapter.myViewHolder holder, int position) {

        String timeslot = timeslotList.get(position);
        holder.time.setText(timeslot);


        //TODO set unclickable to the timeslot that was taken by other patient
        checkTimeSlotAvailability(timeslot,holder);
    }

    private void checkTimeSlotAvailability(String timeslot, myViewHolder holder)
    {
        String date = new SimpleDateFormat("yyyy-MMMM-dd").format(currDate);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Appointment").child("Patient");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren())
                {
                    AppointmentModel am = ds.getValue(AppointmentModel.class);
                    if
                    (
                            am.getDoctorID().equals(doctorID) &&
                            am.getAppointmentDate().equals(date) &&
                            am.getTimeSlot().equals(timeslot)
                    )
                    {
                        holder.timeslot_card.setCardBackgroundColor(context.getResources().getColor(R.color.colorGrey));
                        holder.timeslot_card.setClickable(false);
                        holder.availability.setText(context.getResources().getText(R.string.unavailable_title));
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("databaseError", error.getMessage());
            }
        });
    }

    @Override
    public int getItemCount() {
        return timeslotList.size();
    }


    public class myViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        onItemClickListener onItemClickListener;
        TextView time,availability;
        CardView timeslot_card;
        ImageButton next;

        public myViewHolder(@NonNull View itemView, TimeSlotAdapter.onItemClickListener onItemClickListener) {
            super(itemView);
            itemView.setOnClickListener(this);

            time = itemView.findViewById(R.id.time);
            availability = itemView.findViewById(R.id.availability);
            timeslot_card = itemView.findViewById(R.id.timeslot_card);
            next= ((Activity) context).findViewById(R.id.nextBtn);

            this.onItemClickListener = onItemClickListener;
        }

        @Override
        public void onClick(View view)
        {
            onItemClickListener.onTimeSlotClicked(getAdapterPosition());
            if (!selected)
            {
                timeslot_card.setCardBackgroundColor(context.getResources().getColor(R.color.colorGrey));
                selected = true;
                next.setVisibility(View.VISIBLE);
            }
            else
            {
                timeslot_card.setCardBackgroundColor(context.getResources().getColor(R.color.white));
                selected = false;
                next.setVisibility(View.INVISIBLE);
            }
        }
    }
}
