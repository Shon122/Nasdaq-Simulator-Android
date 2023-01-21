package com.gtappdevelopers.bankrehovot;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import com.google.firebase.database.collection.LLRBNode;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;

public class TradeAdapter extends ArrayAdapter<Trade> {
    private ArrayList<Trade> tradeList;
    private ArrayList<Trade> originalList;

    public TradeAdapter(Context context, ArrayList<Trade> tradeList) {
        super(context, 0, tradeList);
        this.tradeList = tradeList;
        this.originalList = new ArrayList<>(tradeList);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Trade currentTrade = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.trade_item, parent, false);
        }
        // Lookup view for data population
        TextView statusTextView = convertView.findViewById(R.id.statusTextView);
        TextView dateTextView = convertView.findViewById(R.id.dateTextView);
        TextView nameTextView = convertView.findViewById(R.id.nameTextView);
        TextView entryTextView = convertView.findViewById(R.id.entryTextView);
        TextView exitTextView = convertView.findViewById(R.id.exitTextView);
        TextView returnTextView = convertView.findViewById(R.id.returnTextView);
        // Populate the data into the template view using the data object
        if (currentTrade.orderHold) {
            statusTextView.setText("Order");

        } else {
            if (currentTrade.openClose)
                statusTextView.setText("Active");
            else
                statusTextView.setText("Closed");
        }
        DateFormat sdf2 = DateFormat.getDateInstance(DateFormat.MEDIUM);
        dateTextView.setText(sdf2.format(new Date()));
        nameTextView.setText(currentTrade.stockName);
        entryTextView.setText(String.valueOf(currentTrade.startPrice));

        if (currentTrade.openClose) {
            exitTextView.setText("---");
        } else {
            exitTextView.setText(String.valueOf(currentTrade.currentPrice));

        }
        if (currentTrade.totalProfitLoss < 0) {
            returnTextView.setText("-$" + String.valueOf(currentTrade.totalProfitLoss));
            returnTextView.setTextColor(Color.parseColor("#F15044"));
        } else {
            returnTextView.setText("+$" + String.valueOf(currentTrade.totalProfitLoss));
            returnTextView.setTextColor(Color.parseColor("#4CAF50"));

        }
        // Return the completed view to render on screen
        return convertView;
    }


    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                ArrayList<Trade> filteredList = new ArrayList<>();
                if (constraint == null || constraint.length() == 0) {
                    filteredList.addAll(originalList);
                } else {
                    String filterPattern = constraint.toString().toLowerCase().trim();
                    for (Trade currentTrade : originalList) {
                        if (currentTrade.stockName.toLowerCase().startsWith(filterPattern)) {
                            filteredList.add(currentTrade);
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
                addAll((ArrayList<Trade>) results.values);
                notifyDataSetChanged();
            }
        };
    }


}