package com.example.carespace.TimerAlarm;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.carespace.R;

import java.util.ArrayList;

public class AlarmListAdapter extends RecyclerView.Adapter<AlarmListAdapter.MyViewHolder> {

    private ArrayList<String> db_id, db_title, db_time, db_desc;
    private Context context;

    public AlarmListAdapter(Context context,ArrayList<String> db_id, ArrayList<String> db_title, ArrayList<String> db_time, ArrayList<String> db_desc) {
        this.context = context;
        this.db_id = db_id;
        this.db_title = db_title;
        this.db_time = db_time;
        this.db_desc = db_desc;
    }

    @NonNull
    @Override
    public AlarmListAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.alarm_item,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        holder.alarmItem_title.setText(db_title.get(holder.getAdapterPosition()));
        if (db_time.get(holder.getAdapterPosition()).contains("AM") || db_time.get(holder.getAdapterPosition()).contains("PM"))
        {
            holder.alarmItem_time.setText(db_time.get(holder.getAdapterPosition()));
        }
        else
        {
            holder.alarmItem_time.setText("Repeat every "+ db_time.get(holder.getAdapterPosition()) + " minutes");
        }
        holder.alarmItem_desc.setText(db_desc.get(holder.getAdapterPosition()));

        holder.alarmItem_dlt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlarmDatabaseHelper myDB = new AlarmDatabaseHelper(context);
                myDB.deleteOneRow(db_id.get(holder.getAdapterPosition()));

                db_desc.remove(holder.getAdapterPosition());
                db_id.remove(holder.getAdapterPosition());
                db_time.remove(holder.getAdapterPosition());
                db_title.remove(holder.getAdapterPosition());

                notifyItemRemoved(holder.getAdapterPosition());

            }
        });
    }

    @Override
    public int getItemCount() {
        return db_id.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        TextView alarmItem_title,alarmItem_time,alarmItem_desc;
        ImageView alarmItem_dlt;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            alarmItem_title = itemView.findViewById(R.id.alarmItem_title);
            alarmItem_time = itemView.findViewById(R.id.alarmItem_time);
            alarmItem_desc = itemView.findViewById(R.id.alarmItem_desc);
            alarmItem_dlt = itemView.findViewById(R.id.alarmItem_dlt);

        }
    }

    public ArrayList<String> getDb_id() {
        return db_id;
    }

    public ArrayList<String> getDb_title() {
        return db_title;
    }

    public ArrayList<String> getDb_time() {
        return db_time;
    }

    public ArrayList<String> getDb_desc() {
        return db_desc;
    }
}
