package com.geekbrains.myweatherv3;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class RecyclerDataAdapterForDays extends RecyclerView.Adapter<RecyclerDataAdapterForDays.ViewHolder> {
    private ArrayList<DataClassOfDays> data;

    public RecyclerDataAdapterForDays(ArrayList<DataClassOfDays> data) {
        if(data != null) {
            this.data = data;
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();

        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_days_rv_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.textDay.setText(data.get(position).textDay);
//        holder.textDay.setTextColor(Color.parseColor("#DC1919"));
        holder.texTemptDay.setText(data.get(position).texTemptDay);
        holder.texTemptNight.setText(data.get(position).texTemptNight);
        holder.drawableDayImageView.setImageDrawable(data.get(position).drawableDay);
    }

    @Override
    public int getItemCount() {
        return data == null ? 0 : data.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView textDay;
        TextView texTemptDay;
        ImageView drawableDayImageView;
        TextView texTemptNight;

        ViewHolder(View view) {
            super(view);

            textDay = itemView.findViewById(R.id.itemDayTextView);
            texTemptDay = itemView.findViewById(R.id.itemTempDayTextView);
            texTemptNight = itemView.findViewById(R.id.itemTempNightTextView);
            drawableDayImageView = itemView.findViewById(R.id.typeDayImageView);
        }
    }
}
