package com.example.carespace.ClinicHospitalView;

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
import com.example.carespace.Shop.ItemModel;

import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class miniShopAdapter extends RecyclerView.Adapter<miniShopAdapter.myViewHolder>{

    private Context context;
    private ArrayList<ItemModel> itemList;
    private onItemClickListener onItemClickListener;


    public miniShopAdapter(Context context, ArrayList<ItemModel> itemList, miniShopAdapter.onItemClickListener onItemClickListener) {
        this.context = context;
        this.itemList = itemList;
        this.onItemClickListener = onItemClickListener;
    }


    public interface onItemClickListener{
        void ShopitemOnClick(int position);
    }

    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.mini_shop_item,parent,false);
        return new myViewHolder(view, onItemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull myViewHolder holder, int position) {

        ItemModel model = itemList.get(position);

        holder.price.setText("RM " + model.getItemPrice());
        holder.name.setText(model.getItemName());
        holder.sold.setText(model.getItemSold() + "Sold");
        Glide.with(context).load(model.getItemImg()).into(holder.img);
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }


    public class myViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        onItemClickListener onItemClickListener;
        TextView name,price,sold;
        CircleImageView img;

        public myViewHolder(@NonNull View itemView, miniShopAdapter.onItemClickListener onItemClickListener) {
            super(itemView);
            itemView.setOnClickListener(this);

            name = itemView.findViewById(R.id.name);
            price = itemView.findViewById(R.id.price);
            sold = itemView.findViewById(R.id.sold);
            img = itemView.findViewById(R.id.img);

            this.onItemClickListener = onItemClickListener;
        }

        @Override
        public void onClick(View view) {
            onItemClickListener.ShopitemOnClick(getAdapterPosition());
        }
    }
}
