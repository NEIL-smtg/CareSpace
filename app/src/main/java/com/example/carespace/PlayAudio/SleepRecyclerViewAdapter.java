package com.example.carespace.PlayAudio;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.carespace.R;

//audio recycler view
public class SleepRecyclerViewAdapter extends RecyclerView.Adapter<SleepRecyclerViewAdapter.MyViewHolder> {

    AudioAdapter audioAdapter;
    Context context;
    onItemClickListener onItemClickListener;

    public SleepRecyclerViewAdapter(Context context, onItemClickListener onItemClickListener) {
        audioAdapter = new AudioAdapter(context);
        this.context = context;
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.audio_card, parent, false);
        return new MyViewHolder(view, onItemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        holder.name.setText(audioAdapter.audioNameList[holder.getAdapterPosition()]);
        holder.category.setText(audioAdapter.Category[holder.getAdapterPosition()]);
        holder.duration.setText(audioAdapter.audioDuration[position] + "min");

        Glide
                .with(context)
                .load(audioAdapter.audioImages[position])
                .into(holder.card_background);

    }


    @Override
    public int getItemCount() {
        return audioAdapter.audioNameList.length;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView name,duration,category;
        ImageView card_background;
        onItemClickListener onItemClickListener;

        public MyViewHolder(@NonNull View itemView, onItemClickListener onItemClickListener) {
            super(itemView);

            itemView.setOnClickListener(this);

            name = (TextView) itemView.findViewById(R.id.audio_name);
            duration = (TextView) itemView.findViewById(R.id.audio_duration);
            category = (TextView) itemView.findViewById(R.id.audio_category);
            card_background = (ImageView) itemView.findViewById(R.id.audio_background);
            this.onItemClickListener = onItemClickListener;
        }

        @Override
        public void onClick(View view) {
            onItemClickListener.onItemClicked(getAdapterPosition());
        }
    }

    public interface onItemClickListener{
        void onItemClicked(int position);
    }

}
