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

public class UserAdapter extends ArrayAdapter<User> {
    private ArrayList<User> stockList;
    private ArrayList<User> originalList;


    public UserAdapter(Context context, ArrayList<User> stockList) {
        super(context, 0, stockList);
        this.stockList = stockList;
        this.originalList = stockList;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        User stock = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.user_item, parent, false);
        }
        // Lookup view for data population
//        TextView nameView = convertView.findViewById(R.id.name_view);
//        TextView priceView = convertView.findViewById(R.id.price_view);
//        TextView gainlossView = convertView.findViewById(R.id.gainloss_view);
        // Populate the data into the template view using the data object
        //nameView.setText(stock.username);
//        priceView.setText("Price: " + stock.priceList.get(0));
//        gainlossView.setText("Change: " + stock.gainLossPercent + "%");
        // Return the completed view to render on screen
        return convertView;
    }


    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                ArrayList<User> filteredList = new ArrayList<>();
                if (constraint == null || constraint.length() == 0) {
                    filteredList.addAll(originalList);
                } else {
                    String filterPattern = constraint.toString().toLowerCase().trim();
                    for (User stock : originalList) {
                        if (stock.username.toLowerCase().startsWith(filterPattern)) {
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
                addAll((ArrayList<User>) results.values);
                notifyDataSetChanged();
            }
        };
    }


}