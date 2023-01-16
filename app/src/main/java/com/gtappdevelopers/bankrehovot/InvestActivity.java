package com.gtappdevelopers.bankrehovot;

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

    private ArrayList<StockModel> stockList;
    private StockAdapter adapter;
    private ListView listView;
    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invest);
        //initialize the arraylist and adapter
        stockList = MainActivity.stockModels;
        adapter = new StockAdapter(this, stockList);

        //initialize the ListView and set the adapter
        listView = findViewById(R.id.list_view);
        listView.setAdapter(adapter);

        //set onItemClickListener for the ListView
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                StockModel stock = stockList.get(position);
                //Do something with the clicked stock object
            }
        });

        //initialize the SearchView and set the listener
        searchView = findViewById(R.id.search_view);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return false;
            }
        });
    }

    //function to sort the list by name
    public void sortByName(View view) {
        Collections.sort(stockList, new Comparator<StockModel>() {
            @Override
            public int compare(StockModel o1, StockModel o2) {
                return o1.name.compareTo(o2.name);
            }
        });
        adapter.notifyDataSetChanged();
    }

    //function to sort the list by gain/loss percentage
    public void sortByGainLossPercent(View view) {
        Collections.sort(stockList, new Comparator<StockModel>() {
            @Override
            public int compare(StockModel o1, StockModel o2) {
                return Double.compare(o1.gainLossPercent, o2.gainLossPercent);
            }
        });
        adapter.notifyDataSetChanged();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }

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


