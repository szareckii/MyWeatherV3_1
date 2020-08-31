package com.geekbrains.myweatherv3;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class RecyclerDataAdapterForHours extends RecyclerView.Adapter<RecyclerDataAdapterForHours.ViewHolder> {
    private ArrayList<DataClassOfHours> data;

    public RecyclerDataAdapterForHours(ArrayList<DataClassOfHours> data) {
        if(data != null) {
            this.data = data;
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();

        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_hours_rv_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.textHour.setText(data.get(position).textHour);
        holder.drawableHourImageView.setImageDrawable(data.get(position).drawableHourImageView);
        holder.texTempHour.setText(data.get(position).texTempHour);
    }

    @Override
    public int getItemCount() {
        return data == null ? 0 : data.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView textHour;
        ImageView drawableHourImageView;
        TextView texTempHour;

        ViewHolder(View view) {
            super(view);

            textHour = itemView.findViewById(R.id.itemHourTextView);
            drawableHourImageView = itemView.findViewById(R.id.typeHourImageView);
            texTempHour = itemView.findViewById(R.id.itemTempHourTextView);
        }
    }
}
