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

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class TradesActivity extends AppCompatActivity {
    private ArrayList<Trade> saveTradeList;
    private ArrayList<Trade> tradeList;
    private TradeAdapter adapter;
    private ListView listView;
    private SearchView searchView;
    int gainSort = 0; // 0 = not sorted this way ,1= sorted this way
    int nameSort = 0; // 0 = not sorted this way ,1= sorted this way

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.trades_activity);
        //initialize the arraylist and adapter
        tradeList = MainActivity.trades;

        int size = tradeList.size();
        for (int i = 0; i < size - 1; i++) {
            Trade user = tradeList.get(i);
            for (int j = i + 1; j < size; j++) {
                if (user.equals(tradeList.get(j))) {
                    tradeList.remove(j);
                    size--;
                    j--;
                }
            }
        }

        saveTradeList=tradeList;
        adapter = new TradeAdapter(this, tradeList);

        //initialize the ListView and set the adapter
        listView = findViewById(R.id.list_view_trades);
        listView.setAdapter(adapter);

        //set onItemClickListener for the ListView
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MainActivity.viewingTrade = MainActivity.trades.get(MainActivity.trades.indexOf(tradeList.get(position)));
                String currentName = tradeList.get(position).stockName;
                for (int i = 0; i < MainActivity.stockModels.size(); i++) {
                    if (MainActivity.stockModels.get(i).name.equals(currentName)) {
                        MainActivity.viewingStock = MainActivity.stockModels.get(i);
                        break;
                    }
                }

            }
        });

        //initialize the SearchView and set the listener
        searchView = findViewById(R.id.search_view_trades);
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
        ArrayList<Trade> filteredList = new ArrayList<>();
        if (text.isEmpty()) {
            filteredList = saveTradeList;
        } else {
            for (Trade trade : saveTradeList) {
                if (trade.stockName.toLowerCase().contains(text.toLowerCase())) {
                    filteredList.add(trade);
                }
            }
        }
        tradeList = filteredList;
        adapter = new TradeAdapter(this, tradeList);
        listView.setAdapter(adapter);
    }

    //function to sort the list by name
    public void sortByName(View view) {
        gainSort = 0;
        Collections.sort(tradeList, new Comparator<Trade>() {
            @Override
            public int compare(Trade o1, Trade o2) {
                return o1.stockName.compareTo(o2.stockName);
            }
        });

        if (nameSort != 0 && nameSort % 2 != 0) {
            Collections.reverse(tradeList);
        }
        nameSort++;
        adapter = new TradeAdapter(this, tradeList);
        listView.setAdapter(adapter);
    }

    //function to sort the list by gain/loss percentage
    public void sortByProfitLoss(View view) {

        nameSort = 0;
        Collections.sort(tradeList, new Comparator<Trade>() {
            @Override
            public int compare(Trade o1, Trade o2) {
                return Double.compare(o1.totalProfitLoss, o2.totalProfitLoss);
            }
        });

        if (gainSort != 0 && gainSort % 2 != 0) {
            Collections.reverse(tradeList);
        }
        gainSort++;
        adapter = new TradeAdapter(this, tradeList);
        listView.setAdapter(adapter);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.trades_menu, menu);
        return true;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sort_by_name:
                sortByName(null);
                return true;
            case R.id.sort_by_profitLoss:
                sortByProfitLoss(null);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override //when user wants to go back
    public void onBackPressed() {
        // Handle back button press event here
        goBackTradesList(null);
    }

    public void goBackTradesList(View view) {
        Intent intent = new Intent(this, HomePage.class);


        startActivity(intent);
        finish();
    }

}


