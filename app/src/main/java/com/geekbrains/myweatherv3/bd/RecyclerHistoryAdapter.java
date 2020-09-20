package com.geekbrains.myweatherv3.bd;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.geekbrains.myweatherv3.IRVOnItemClick;
import com.geekbrains.myweatherv3.R;

import java.text.SimpleDateFormat;
import java.util.List;

public class RecyclerHistoryAdapter extends RecyclerView.Adapter<RecyclerHistoryAdapter.ViewHolder> {
    private List<CityWithHistory> citiesWithHistory;

    private IRVOnItemClick onItemClickCallback;
    private int selectedPos = 0;

    public RecyclerHistoryAdapter(List<CityWithHistory> citiesWithHistory, IRVOnItemClick onItemClickCallback) {
        this.citiesWithHistory = citiesWithHistory;
        this.onItemClickCallback = onItemClickCallback;
    }

    @NonNull
    @Override
    public RecyclerHistoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_rv_layout, parent,
                false);
        return new RecyclerHistoryAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerHistoryAdapter.ViewHolder holder, int position) {

        CityWithHistory cityWithHistory = citiesWithHistory.get(position);

        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat simpleDate =  new SimpleDateFormat("dd/MM/yyyy");
        String strDt = simpleDate.format(cityWithHistory.dateTemp);

        holder.cityNameTextView.setText(cityWithHistory.cityName);
        holder.dateHistoryTextView.setText(strDt);
        holder.temperTextView.setText(cityWithHistory.temper);

        String text = citiesWithHistory.get(position).cityName;
        float lon = citiesWithHistory.get(position).lon;
        float lat = citiesWithHistory.get(position).lat;
        holder.setOnClickForItem(text, lon, lat);
    }

    @Override
    public int getItemCount() {
        return citiesWithHistory == null ? 0 : citiesWithHistory.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView cityNameTextView;
        private TextView dateHistoryTextView;
        private TextView temperTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cityNameTextView = itemView.findViewById(R.id.itemCityNameTextView);
            dateHistoryTextView = itemView.findViewById(R.id.itemDateHistoryTextView);
            temperTextView = itemView.findViewById(R.id.itemTemperTextView);
        }

        void setOnClickForItem(final String text, final float lon, final float lat) {
            cityNameTextView.setOnClickListener(view -> {
                if(onItemClickCallback != null) {
                    if (getAdapterPosition() == RecyclerView.NO_POSITION)
                        return;
                    notifyItemChanged(selectedPos);
                    selectedPos = getAdapterPosition();
                    notifyItemChanged(selectedPos);
                    onItemClickCallback.onItemClicked(text, lon, lat);
                }
            });

            dateHistoryTextView.setOnClickListener(view -> {
                if(onItemClickCallback != null) {
                    if (getAdapterPosition() == RecyclerView.NO_POSITION)
                        return;
                    notifyItemChanged(selectedPos);
                    selectedPos = getAdapterPosition();
                    notifyItemChanged(selectedPos);
                    onItemClickCallback.onItemClicked(text, lon, lat);
                }
            });

            temperTextView.setOnClickListener(view -> {
                if(onItemClickCallback != null) {
                    if (getAdapterPosition() == RecyclerView.NO_POSITION)
                        return;
                    notifyItemChanged(selectedPos);
                    selectedPos = getAdapterPosition();
                    notifyItemChanged(selectedPos);
                    onItemClickCallback.onItemClicked(text, lon, lat);
                }
            });
        }
    }
}
