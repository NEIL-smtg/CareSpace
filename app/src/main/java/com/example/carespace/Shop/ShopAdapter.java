package com.example.carespace.Shop;

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

//TODO CHECK IF SHOP WORKS
public class ShopAdapter extends RecyclerView.Adapter<ShopAdapter.myViewHolder> {

    private Context context;
    private ArrayList<ItemModel> itemList;
    private onItemClickListener onItemClickListener;

    public interface onItemClickListener{
        void onItemClicked(int position);
    }

    public ShopAdapter(Context context, ArrayList<ItemModel> itemList, ShopAdapter.onItemClickListener onItemClickListener) {
        this.context = context;
        this.itemList = itemList;
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public ShopAdapter.myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.shop_item, parent, false);
        return new myViewHolder(view, onItemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ShopAdapter.myViewHolder holder, int position) {

        ItemModel model = itemList.get(position);
        holder.name.setText(model.getItemName());
        holder.price.setText(model.getItemPrice());
        holder.sold.setText(model.getItemSold());
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

        public myViewHolder(@NonNull View itemView, ShopAdapter.onItemClickListener onItemClickListener) {
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
            onItemClickListener.onItemClicked(getAdapterPosition());
        }
    }
}
