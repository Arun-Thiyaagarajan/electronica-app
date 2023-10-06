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

public class PopularProductsAdapter extends RecyclerView.Adapter<PopularProductsAdapter.ViewHolder> {

    private Context context;
    private final List<HomeModel> popularProductsList;

    public PopularProductsAdapter(Context context, List<HomeModel> popularProductsList) {
        this.context = context;
        this.popularProductsList = popularProductsList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.popular_products, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        HomeModel item = popularProductsList.get(position);

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
                intent.putExtra("Image", popularProductsList.get(holder.getAdapterPosition()).getImageUrl());
                intent.putExtra("Name", popularProductsList.get(holder.getAdapterPosition()).getName());
                intent.putExtra("Price", popularProductsList.get(holder.getAdapterPosition()).getPrice());
                intent.putExtra("Description", popularProductsList.get(holder.getAdapterPosition()).getDescription());

                view.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return popularProductsList.size();
    }
    public static class ViewHolder extends RecyclerView.ViewHolder {
        CardView container;
        ImageView image;
        TextView textPrice, textName, textDesc;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            container = itemView.findViewById(R.id.container_popular_products);
            image     = itemView.findViewById(R.id.popular_products_image);
            textName  = itemView.findViewById(R.id.popular_products_name);
            textPrice = itemView.findViewById(R.id.popular_products_price);
            textDesc  = itemView.findViewById(R.id.popular_products_desc);
        }
    }
}
