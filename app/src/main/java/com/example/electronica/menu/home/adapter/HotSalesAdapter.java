package com.example.electronica.menu.home.adapter;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.electronica.R;
import com.example.electronica.menu.home.home_detail.DetailActivity;
import com.example.electronica.menu.home.model.HomeModel;

import java.util.List;

public class HotSalesAdapter extends RecyclerView.Adapter<HotSalesAdapter.ViewHolder> {

    private Context context;
    private final List<HomeModel> hotSalesList;

    public HotSalesAdapter(Context context, List<HomeModel> hotSalesList) {
        this.context = context;
        this.hotSalesList = hotSalesList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.items_hot_sales, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        HomeModel item = hotSalesList.get(position);

        if (context != null && holder.image != null) {
            Glide.with(context)
                    .load(item.getImageUrl()) // Replace with the actual image URL field
                    .placeholder(R.drawable.placeholder_img1)
                    .error(R.drawable.error_image)
                    .into(holder.image);
        }
        holder.textName.setText(item.getName());
        holder.textPrice.setText("₹ " + item.getPrice());
        holder.textDesc.setText(item.getDescription());

        holder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), DetailActivity.class);
                intent.putExtra("Image", hotSalesList.get(holder.getAdapterPosition()).getImageUrl());
                intent.putExtra("Name", hotSalesList.get(holder.getAdapterPosition()).getName());
                intent.putExtra("Price", hotSalesList.get(holder.getAdapterPosition()).getPrice());
                intent.putExtra("Description", hotSalesList.get(holder.getAdapterPosition()).getDescription());

                view.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return hotSalesList.size();
    }
    public static class ViewHolder extends RecyclerView.ViewHolder {
        CardView container;
        ImageView image;
        TextView textPrice, textName, textDesc;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            container = itemView.findViewById(R.id.container_hotSales);
            image     = itemView.findViewById(R.id.item_hotSales_image);
            textName  = itemView.findViewById(R.id.item_hotSales_name);
            textPrice = itemView.findViewById(R.id.item_hotSales_price);
            textDesc  = itemView.findViewById(R.id.item_hotSales_desc);
        }
    }
}
