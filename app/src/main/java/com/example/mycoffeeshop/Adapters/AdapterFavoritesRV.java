package com.example.mycoffeeshop.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.mycoffeeshop.OthersPackage.MyApplication;
import com.example.mycoffeeshop.R;
import com.example.mycoffeeshop.RoomFavoritesPackage.CoffeeFavorites;
import com.example.mycoffeeshop.RoomFavoritesPackage.CoffeeViewModelFavorites;

import java.util.ArrayList;
import java.util.List;

public class AdapterFavoritesRV extends RecyclerView.Adapter<AdapterFavoritesRV.ViewHolder> {

    private List<CoffeeFavorites> list_data;
    private Context context;
    private final LayoutInflater mInflater;
    private CoffeeViewModelFavorites coffeeViewModelFavorites;

    static class ViewHolder extends RecyclerView.ViewHolder {

        private TextView title, description, price, weight;
        private ImageView imageCoffee;
        private ImageButton deleteBtn;

        public ViewHolder(View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.title);
            description = itemView.findViewById(R.id.description);
            price = itemView.findViewById(R.id.price);
            weight = itemView.findViewById(R.id.weight);
            imageCoffee = itemView.findViewById(R.id.imageCoffee);
            deleteBtn = itemView.findViewById(R.id.deleteBtn);
        }

    }

    public AdapterFavoritesRV(ArrayList<CoffeeFavorites> list_data, Context context) {
        this.list_data = list_data;
        this.context = context;
        mInflater = LayoutInflater.from(this.context);
    }

    @NonNull
    @Override
    public AdapterFavoritesRV.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.favorites_coffee_adapter, parent, false);
        return new AdapterFavoritesRV.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(AdapterFavoritesRV.ViewHolder holder, final int position) {
        final CoffeeFavorites coffeeFavorites = list_data.get(position);
        holder.title.setText(coffeeFavorites.getTitle());
        holder.description.setText(coffeeFavorites.getDescription());
        holder.price.setText(String.valueOf(coffeeFavorites.getPrice()));
        holder.weight.setText(String.valueOf(coffeeFavorites.getWeight_unit()));
        Glide.with(mInflater.getContext()).load(coffeeFavorites.getImg()).into(holder.imageCoffee);

        holder.deleteBtn.setOnClickListener(v -> {
            coffeeViewModelFavorites = new CoffeeViewModelFavorites(MyApplication.getApplication());
            coffeeViewModelFavorites.deleteCoffee(coffeeFavorites);
        });
    }

    public void setCoffee(List<CoffeeFavorites> list_data) {
        this.list_data = list_data;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return list_data.size();
    }

}
