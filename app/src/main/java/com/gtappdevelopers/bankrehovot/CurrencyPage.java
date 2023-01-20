package com.gtappdevelopers.bankrehovot;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import com.jjoe64.graphview.series.BarGraphSeries;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class CurrencyPage extends AppCompatActivity {
    //    EditText priceProfitEditText = findViewById(R.id.priceProfitEditText);
//    EditText ilsProfitEditText = findViewById(R.id.ilsProfitEditText);
//    EditText percentProfitEditText = findViewById(R.id.percentProfitEditText);
//    EditText priceStopEditText = findViewById(R.id.priceStopEditText);
//    EditText ilsStopEditText = findViewById(R.id.ilsStopEditText);
//    EditText percentStopEditText = findViewById(R.id.percentStopEditText);
//    CheckBox stopLossCheckBox = findViewById(R.id.stopLossCheckBox);
//    CheckBox takeProfitCheckBox = findViewById(R.id.takeProfitCheckBox);
    EditText amountInvestEditText;
    EditText orderTradeEditText;
    CheckBox orderTradeCheckBox;
    TextView stockNameTextView;
    TextView userBalanceTextView;
    TextView priceTextView;
    StockModel currentStock;
    Double currentPrice;
    Double currentBalance;
    Button longShortButton;
    boolean longShort;//true for long, false for short

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.currency_page);
        amountInvestEditText = findViewById(R.id.amountInvestEditText);
        orderTradeEditText = findViewById(R.id.orderTradeEditText);
        orderTradeCheckBox = findViewById(R.id.orderTradeCheckBox);
        stockNameTextView = findViewById(R.id.stockName);
        userBalanceTextView = findViewById(R.id.userBalance);
        priceTextView = findViewById(R.id.currentPrice);
        currentStock = MainActivity.viewingStock;
        currentPrice = currentStock.priceList.get(0);
        currentBalance = MainActivity.currentUser.balance;
        longShortButton = findViewById(R.id.longShortButton);
        longShort = true;//true for long, false for short

        priceTextView.setText(String.valueOf(currentPrice));
        stockNameTextView.setText(currentStock.name);
        userBalanceTextView.setText("Your Balance: " + currentBalance);
orderTradeEditText.setVisibility(View.INVISIBLE);
    }

    @SuppressLint("SetTextI18n")
    public void createTrade(View view) {
        Double userAmount = Double.valueOf(String.valueOf(amountInvestEditText.getText()));
        Double orderPrice = Double.valueOf(String.valueOf(orderTradeEditText.getText()));
        if (userAmount <= 0 || userAmount > currentBalance) {
            Toast.makeText(this, "You dont have enough money", Toast.LENGTH_SHORT).show();
            return;
        }
        if (orderTradeEditText.getVisibility() == View.VISIBLE && orderPrice <= 0) {
            Toast.makeText(this, "cant order price lower or equal to 0", Toast.LENGTH_SHORT).show();
            return;
        }
        Trade newTrade = new Trade();
        if (orderTradeEditText.getVisibility() == View.INVISIBLE) {
            newTrade = new Trade(currentStock.dateList.get(0), currentStock.name, currentStock.priceList.get(0),
                    currentStock.priceList.get(0), userAmount, -1.0, -1.0, longShort, false, -1.0);
        } else {
            newTrade = new Trade(currentStock.dateList.get(0), currentStock.name, currentStock.priceList.get(0),
                    currentStock.priceList.get(0), userAmount, -1.0, -1.0, longShort, true, orderPrice);

        }
        userBalanceTextView.setText("Your Balance: " + (MainActivity.currentUser.balance - userAmount));
        MainActivity.currentUser.balance = MainActivity.currentUser.balance - userAmount;
        MainActivity.currentUser.trades.add(newTrade);
        MainActivity.users.set(MainActivity.currentUserIndex, MainActivity.currentUser);
        Toast.makeText(this, "Trade Created1", Toast.LENGTH_SHORT).show();
        MainActivity.uploadUsersToFirestore();
    }

    public void tradeCheckBoxChange(View view) {
        if (orderTradeEditText.getVisibility() == View.VISIBLE)
            orderTradeEditText.setVisibility(View.INVISIBLE);
        else
            orderTradeEditText.setVisibility(View.VISIBLE);
    }

    public void viewGraphIntent(View view) {
        Intent intent = new Intent(this, GraphActivity.class);
        startActivity(intent);

    }

    public void changeLongShort(View view) {
        longShort = !longShort;
        if (longShort) {
            longShortButton.setText("Long");
        } else {
            longShortButton.setText("Short");
        }
    }

}