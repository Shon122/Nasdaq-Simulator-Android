package com.gtappdevelopers.bankrehovot;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.PopupMenu;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class InvestActivity extends AppCompatActivity {
    private ArrayList<StockModel> saveStockList;
    private ArrayList<StockModel> stockList;
    private StockAdapter adapter;
    private ListView listView;
    private SearchView searchView;
    int gainSort = 0; // 0 = not sorted this way ,1= sorted this way
    int nameSort = 0; // 0 = not sorted this way ,1= sorted this way

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invest);
        //initialize the arraylist and adapter
        stockList = MainActivity.stockModels;
        saveStockList=MainActivity.stockModels;
        adapter = new StockAdapter(this, stockList);

        //initialize the ListView and set the adapter
        listView = findViewById(R.id.list_view_invest);
        listView.setAdapter(adapter);

        //set onItemClickListener for the ListView
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MainActivity.viewingStock = stockList.get(position);
                Intent intent = new Intent(InvestActivity.this, CurrencyPage.class);
                startActivity(intent);
            }
        });

        //initialize the SearchView and set the listener
        searchView = findViewById(R.id.search_view_invest);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filter(newText);
                return true;
            }
        });
        sortByName(null);
    }
    public void filter(String text) {
        ArrayList<StockModel> filteredList = new ArrayList<>();
        if (text.isEmpty()) {
            filteredList = saveStockList;
        } else {
            for (StockModel stockModel : saveStockList) {
                if (stockModel.name.toLowerCase().contains(text.toLowerCase())) {
                    filteredList.add(stockModel);
                }
            }
        }
        stockList = filteredList;
        adapter = new StockAdapter(this, stockList);
        listView.setAdapter(adapter);
    }

    //function to sort the list by name
    public void sortByName(View view) {
        gainSort = 0;
        Collections.sort(stockList, new Comparator<StockModel>() {
            @Override
            public int compare(StockModel o1, StockModel o2) {
                return o1.name.compareTo(o2.name);
            }
        });

        if (nameSort != 0 && nameSort % 2 != 0) {
            Collections.reverse(stockList);
        }
        nameSort++;
        adapter.notifyDataSetChanged();
    }

    //function to sort the list by gain/loss percentage
    public void sortByGainLossPercent(View view) {
        nameSort = 0;
        Collections.sort(stockList, new Comparator<StockModel>() {
            @Override
            public int compare(StockModel o1, StockModel o2) {
                return Double.compare(o1.gainLossPercent, o2.gainLossPercent);
            }
        });

        if (gainSort != 0 && gainSort % 2 != 0) {
            Collections.reverse(stockList);
        }
        gainSort++;
        adapter.notifyDataSetChanged();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sort_by_name:
                sortByName(null);
                return true;
            case R.id.sort_by_gainlosspercent:
                sortByGainLossPercent(null);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


}


