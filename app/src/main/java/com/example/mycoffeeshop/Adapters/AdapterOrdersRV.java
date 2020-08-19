package com.example.mycoffeeshop.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mycoffeeshop.Models.OrdersModel;
import com.example.mycoffeeshop.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class AdapterOrdersRV extends RecyclerView.Adapter<AdapterOrdersRV.ViewHolder> {

    static class ViewHolder extends RecyclerView.ViewHolder {

        private TextView quantity, totalPrice, date;

        public ViewHolder(View itemView) {
            super(itemView);

            quantity = itemView.findViewById(R.id.quantity);
            totalPrice = itemView.findViewById(R.id.totalPrice);
            date = itemView.findViewById(R.id.date);
        }

    }

    private List<OrdersModel> list_data; // holds all products
    private Context context;
    private final LayoutInflater mInflater;

    public AdapterOrdersRV(ArrayList<OrdersModel> list_data, Context context) {
        this.list_data = list_data;
        this.context = context;
        mInflater = LayoutInflater.from(this.context);
    }

    @NonNull
    @Override
    public AdapterOrdersRV.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.coffee_orders_adapter, parent, false);
        return new AdapterOrdersRV.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(AdapterOrdersRV.ViewHolder holder, final int position) {
        final OrdersModel ordersModel = list_data.get(position);
        holder.quantity.setText(String.valueOf(ordersModel.getQuantity()));
        holder.totalPrice.setText(String.valueOf(ordersModel.getTotalPrice()));
        holder.date.setText(getDateInStringFormat(ordersModel.getDate()));
    }

    @Override
    public int getItemCount() {
        return list_data.size();
    }

    private String getDateInStringFormat(Date date) {
        final String DATE_FORMAT = "dd-MM-yyyy\nHH:mm:ss";
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT+3"));
        return dateFormat.format(date);
    }

}
