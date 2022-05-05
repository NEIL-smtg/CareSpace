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

public class DepartmentDoctorsListAdapter extends RecyclerView.Adapter<DepartmentDoctorsListAdapter.myViewHolder> {

    private Context context;
    private ArrayList<DoctorsModel> docList;
    private onItemClickListener onItemClickListener;

    public DepartmentDoctorsListAdapter(Context context, ArrayList<DoctorsModel> docList, DepartmentDoctorsListAdapter.onItemClickListener onItemClickListener) {
        this.context = context;
        this.docList = docList;
        this.onItemClickListener = onItemClickListener;
    }

    public interface onItemClickListener{
        void onItemClicked(int position);
    }

    @NonNull
    @Override
    public DepartmentDoctorsListAdapter.myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.doctor_row_item, parent, false);
        return new myViewHolder(view, onItemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull DepartmentDoctorsListAdapter.myViewHolder holder, int position) {
        DoctorsModel model = docList.get(position);

        holder.name.setText(model.getName());
        Glide.with(context).load(model.getImgUrl()).into(holder.img);
    }

    @Override
    public int getItemCount() {
        return docList.size();
    }

    public class myViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView name;
        CircleImageView img;
        onItemClickListener onItemClickListener;

        public myViewHolder(@NonNull View itemView, DepartmentDoctorsListAdapter.onItemClickListener onItemClickListener) {
            super(itemView);
            itemView.setOnClickListener(this);
            this.onItemClickListener = onItemClickListener;
            name = itemView.findViewById(R.id.name);
            img = itemView.findViewById(R.id.img);
        }

        @Override
        public void onClick(View view) {
            onItemClickListener.onItemClicked(getAdapterPosition());
        }
    }
}
