package com.example.carespace.Exercise;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.carespace.R;


public class TutorialCardAdapter extends RecyclerView.Adapter<TutorialCardAdapter.MyViewHolder>{

    private TutorialModel tutorialModel;
    private Context context;
    private onItemClickedListener onItemClickedListener;
    private int tabNum;

    public interface onItemClickedListener{
        void onItemClicked(int position);
    }

    public TutorialCardAdapter(Context context, TutorialCardAdapter.onItemClickedListener onItemClickedListener, int tabNum) {
        tutorialModel = new TutorialModel();
        this.context = context;
        this.onItemClickedListener = onItemClickedListener;
        this.tabNum = tabNum;
    }

    @NonNull
    @Override
    public TutorialCardAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.exercise_tutorial_card, parent, false);
        return new MyViewHolder(view, onItemClickedListener);
    }

    @Override
    public void onBindViewHolder(@NonNull TutorialCardAdapter.MyViewHolder holder, int position)
    {
        switch (tabNum)
        {
            case 0:
                holder.title.setText(tutorialModel.abs_title[position]);
                holder.info.setText(tutorialModel.abs_info[position]);
                holder.img.setBackground(context.getDrawable(tutorialModel.abs_pic[position]));
                break;

            case 1:
                holder.title.setText(tutorialModel.arm_title[position]);
                holder.info.setText(tutorialModel.arm_info[position]);
                holder.img.setBackground(context.getDrawable(tutorialModel.arm_pic[position]));
                break;

            default:
                holder.title.setText(tutorialModel.yoga_title[position]);
                holder.info.setText(tutorialModel.yoga_info[position]);
                holder.img.setBackground(context.getDrawable(tutorialModel.yoga_pic[position]));
                break;
        }
    }

    @Override
    public int getItemCount() {

        switch (tabNum)
        {
            case 0:
                return tutorialModel.abs_title.length;
            case 1:
                return tutorialModel.arm_title.length;
            default:
                return tutorialModel.yoga_title.length;

        }
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView title,info;
        ImageView img;
        onItemClickedListener onItemClickedListener;

        public MyViewHolder(@NonNull View itemView, onItemClickedListener onItemClickedListener)
        {
            super(itemView);
            itemView.setOnClickListener(this);

            title = itemView.findViewById(R.id.tutorial_title);
            info = itemView.findViewById(R.id.tutorial_info);
            img = itemView.findViewById(R.id.tutorial_img);
            this.onItemClickedListener = onItemClickedListener;
        }

        @Override
        public void onClick(View view) {
            onItemClickedListener.onItemClicked(getAdapterPosition());
        }
    }
}
