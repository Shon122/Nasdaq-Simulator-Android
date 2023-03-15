package com.gtappdevelopers.bankrehovot;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashSet;

public class StockAdapter extends ArrayAdapter<StockModel> {
    private ArrayList<StockModel> stockList;
    private ArrayList<StockModel> originalList;


    public StockAdapter(Context context, ArrayList<StockModel> stockList) {
        super(context, 0, stockList);

        this.stockList = stockList;
        this.originalList = stockList;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        StockModel stock = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.stock_item, parent, false);
        }
        // Lookup view for data population
        ImageView imageViewdown = convertView.findViewById(R.id.imageViewdown);
        ImageView imageViewup = convertView.findViewById(R.id.imageViewup);
        TextView nameView = convertView.findViewById(R.id.name_view);
        TextView priceView = convertView.findViewById(R.id.price_view);
        TextView gainlossView = convertView.findViewById(R.id.gainloss_view);
        // Populate the data into the template view using the data object
        nameView.setText(stock.name);
        priceView.setText("" + roundToTwoDecimals(stock.priceList.get(0)));
        if (stock.gainLossPercent >= 0) {
            gainlossView.setText("+" + roundToTwoDecimals(stock.gainLossPercent) + "%");
            imageViewdown.setVisibility(View.INVISIBLE);
            imageViewup.setVisibility(View.VISIBLE);
            gainlossView.setTextColor(Color.parseColor("#FF148A45"));
        } else {
            imageViewdown.setVisibility(View.VISIBLE);
            imageViewup.setVisibility(View.INVISIBLE);
            gainlossView.setText(roundToTwoDecimals(stock.gainLossPercent) + "%");
            gainlossView.setTextColor(Color.parseColor("#D50000"));

        }
        // Return the completed view to render on screen
        return convertView;
    }


    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                ArrayList<StockModel> filteredList = new ArrayList<>();
                if (constraint == null || constraint.length() == 0) {
                    filteredList.addAll(originalList);
                } else {
                    String filterPattern = constraint.toString().toLowerCase().trim();
                    for (StockModel stock : originalList) {
                        if (stock.name.toLowerCase().startsWith(filterPattern)) {
                            filteredList.add(stock);
                        }
                    }
                }
                FilterResults results = new FilterResults();
                results.values = filteredList;
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                clear();
                addAll((ArrayList<StockModel>) results.values);
                notifyDataSetChanged();
            }
        };
    }


    public Double roundToTwoDecimals(Double value) {
        return (double) Math.round(value * 100) / 100;
    }
}