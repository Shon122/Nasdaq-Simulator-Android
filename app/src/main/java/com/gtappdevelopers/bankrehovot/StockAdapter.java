package com.gtappdevelopers.bankrehovot;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import java.util.ArrayList;

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
        TextView nameView = convertView.findViewById(R.id.name_view);
        TextView priceView = convertView.findViewById(R.id.price_view);
        TextView gainlossView = convertView.findViewById(R.id.gainloss_view);
        // Populate the data into the template view using the data object
        nameView.setText(stock.name);
        priceView.setText("Price: " + stock.priceList.get(0));
        gainlossView.setText("Change: " + stock.gainLossPercent + "%");
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


}