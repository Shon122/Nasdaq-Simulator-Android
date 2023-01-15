package com.gtappdevelopers.bankrehovot;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class InvestActivity extends AppCompatActivity {

    private ListView listView;
    private ArrayList<StockModel> stockList;
    private ArrayAdapter<StockModel> adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invest);

        listView = findViewById(R.id.list_view);

        //initialize the stockList array with sample data
        stockList = new ArrayList<>();
        stockList.add(new StockModel("Stock 1", new ArrayList<Double>(), new ArrayList<String>(), "5 minutes"));
        stockList.add(new StockModel("Stock 2", new ArrayList<Double>(), new ArrayList<String>(), "15 minutes"));
        stockList.add(new StockModel("Stock 3", new ArrayList<Double>(), new ArrayList<String>(), "30 minutes"));

        adapter = new ArrayAdapter<StockModel>(this, android.R.layout.simple_list_item_2, android.R.id.text1, stockList) {
            @Override
            public View getView(final int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView text1 = view.findViewById(android.R.id.text1);
                TextView text2 = view.findViewById(android.R.id.text2);

                text1.setText(stockList.get(position).name);
                text2.setText("Interval: " + stockList.get(position).timeInterval);

                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });

                return view;
            }
        };

        listView.setAdapter(adapter);
    }
}