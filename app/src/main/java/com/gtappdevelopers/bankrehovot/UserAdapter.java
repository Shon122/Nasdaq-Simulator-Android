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
    private ArrayList<User> userList;
    private ArrayList<User> originalList;


    public UserAdapter(Context context, ArrayList<User> userList) {
        super(context, 0, userList);
        this.userList = userList;
        this.originalList = MainActivity.users;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        User user = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.user_item, parent, false);
        }
        // Lookup view for data population
        TextView nameView = convertView.findViewById(R.id.nameOfUser);
        TextView balanceView = convertView.findViewById(R.id.balanceOfUser);

        // Populate the data into the template view using the data object
        nameView.setText(user.username);
        balanceView.setText(user.balance + "$");

        // Return the completed view to render on screen
        return convertView;
    }


    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                ArrayList<User> filteredList = new ArrayList<>();
                originalList=MainActivity.users;
                filteredList.addAll(originalList);
                if (constraint == null || constraint.length() == 0) {
                   // filteredList.addAll(originalList);
                } else {
                    String filterPattern = constraint.toString().toLowerCase().trim();
                    for (User user : originalList) {
                        if (!user.username.toLowerCase().startsWith(filterPattern)) {
                            filteredList.remove(user);
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