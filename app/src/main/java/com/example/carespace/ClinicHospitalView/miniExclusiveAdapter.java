package com.example.carespace.ClinicHospitalView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.carespace.R;

public class miniExclusiveAdapter extends RecyclerView.Adapter<miniExclusiveAdapter.myViewHolder> {

    private Context context;
    private onItemClickListener onItemClickListener;
    private HospitalDepartmentModel model;
    private int size;

    public miniExclusiveAdapter(Context context, miniExclusiveAdapter.onItemClickListener onItemClickListener, int size) {
        this.context = context;
        this.onItemClickListener = onItemClickListener;
        model = new HospitalDepartmentModel();
        this.size = size;
    }

    public interface onItemClickListener{
        void onDepartmentOnClick(int position);
    }


    @NonNull
    @Override
    public miniExclusiveAdapter.myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.department_item,parent,false);
        return new myViewHolder(view,onItemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull miniExclusiveAdapter.myViewHolder holder, int position) {
        Glide.with(context).load(model.getImg()[position]).into(holder.img);
    }

    @Override
    public int getItemCount() {
        return size;
    }

    public class myViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        onItemClickListener onItemClickListener;
        ImageView img;

        public myViewHolder(@NonNull View itemView, miniExclusiveAdapter.onItemClickListener onItemClickListener) {
            super(itemView);
            itemView.setOnClickListener(this);
            img = itemView.findViewById(R.id.img);

            this.onItemClickListener = onItemClickListener;
        }

        @Override
        public void onClick(View view) {
            onItemClickListener.onDepartmentOnClick(getAdapterPosition());
        }
    }
}
