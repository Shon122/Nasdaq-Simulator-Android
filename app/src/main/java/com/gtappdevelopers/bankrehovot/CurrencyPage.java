package com.gtappdevelopers.bankrehovot;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Objects;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.HighLowDataEntry;
import com.anychart.charts.Stock;
import com.anychart.core.stock.Plot;
import com.anychart.data.Table;
import com.anychart.data.TableMapping;
import com.anychart.enums.StockSeriesType;

import java.util.ArrayList;
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





    ArrayList<Double> priceList = MainActivity.viewingStock.priceList;
    ArrayList<String> dateList = MainActivity.viewingStock.dateList;

    EditText amountInvestEditText;
    EditText orderTradeEditText;
    CheckBox orderTradeCheckBox;
    TextView stockNameTextView;
    TextView changeTextView;
    TextView userBalanceTextView;
    TextView priceTextView;
    StockModel currentStock;
    Double currentPrice;
    Double currentBalance;
    Button buysellButton;
    Double currentChange;
    boolean buysell;//true for buy, false for sell

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.currency_page);
        changeTextView = findViewById(R.id.changeTextView);
        amountInvestEditText = findViewById(R.id.amountInvestEditText);
        orderTradeEditText = findViewById(R.id.orderTradeEditText);
        orderTradeCheckBox = findViewById(R.id.orderTradeCheckBox);
        stockNameTextView = findViewById(R.id.stockName);
        userBalanceTextView = findViewById(R.id.userBalance);
        priceTextView = findViewById(R.id.currentPrice);
        currentStock = MainActivity.viewingStock;
        currentPrice = currentStock.priceList.get(0);
        currentChange = currentStock.gainLossPercent;
        currentBalance = MainActivity.currentUser.balance;
        buysellButton = findViewById(R.id.buysellButton);
        buysell = true;//true for buy, false for sell

        priceTextView.setText(String.valueOf(currentPrice));
        stockNameTextView.setText(currentStock.name);
        userBalanceTextView.setText("Your Balance: $" + currentBalance);
        orderTradeEditText.setVisibility(View.INVISIBLE);


        changeTextView.setText("" + currentChange + "%");
        if (currentChange < 0)
            changeTextView.setTextColor(Color.RED);
        else
            changeTextView.setTextColor(Color.GREEN);

        buysellButton.setTextColor(Color.GREEN);


        AnyChartView anyChartView = findViewById(R.id.any_chart_view);

        Table table = Table.instantiate("x");
        table.addData(getData());

        TableMapping mapping = table.mapAs("{open: 'open', high: 'high', low: 'low', close: 'close'}");

        Stock stock = AnyChart.stock();

        Plot plot = stock.plot(0);
        plot.yGrid(true)
                .xGrid(true)
                .yMinorGrid(true)
                .xMinorGrid(true);

        plot.ema(table.mapAs("{value: 'close'}"), 20d, StockSeriesType.LINE);

        plot.ohlc(mapping)
                .name("CSCO")
                .legendItem("{\n" +
                        "        iconType: 'rising-falling'\n" +
                        "      }");

        stock.scroller().ohlc(mapping);

        // set x axis as the datelist and y axis as the prices

        anyChartView.setChart(stock);

    }



    private List<DataEntry> getData() {
        List<DataEntry> data = new ArrayList<>();
        // get the date and prices from the dateList and priceList
        for (int i = 0; i < dateList.size(); i++) {
            data.add(new OHCLDataEntry(dateList.get(i), priceList.get(i),priceList.get(i),priceList.get(i),priceList.get(i)));
        }
        return data;
    }

    private static class OHCLDataEntry extends HighLowDataEntry {
        OHCLDataEntry(String x, Double open, Double high, Double low, Double close) {
            super(x, high, low);
            setValue("open", open);
            setValue("close", close);
        }
    }
    public long stringToMilliseconds(String dateString) {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss");
        try {
            Date date = format.parse(dateString);
            assert date != null;
            return date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
            return 641750400000L;
        }
    }

    @SuppressLint("SetTextI18n")
    public void createTrade(View view) {
        Double userAmount = -1.0;
        Double orderPrice = -1.0;
        String amountTake = String.valueOf(amountInvestEditText.getText());
        String orderTake = String.valueOf(orderTradeEditText.getText());
        if (amountTake.length() > 0)
            userAmount = Double.valueOf(String.valueOf(amountInvestEditText.getText()));
        if (orderTake.length() > 0)
            orderPrice = Double.valueOf(String.valueOf(orderTradeEditText.getText()));
        if (userAmount > currentBalance) {
            Toast.makeText(this, "You dont have enough money", Toast.LENGTH_SHORT).show();
            return;
        }
        if (userAmount <= 0) {
            Toast.makeText(this, "Amount has to be more than 0", Toast.LENGTH_SHORT).show();
            return;
        }

        if (orderTradeEditText.getVisibility() == View.VISIBLE && orderPrice <= 0) {
            Toast.makeText(this, "cant order price lower or equal to 0", Toast.LENGTH_SHORT).show();
            return;
        }
        Trade newTrade = new Trade();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String currentTime = sdf.format(new Date());

        if (orderTradeEditText.getVisibility() == View.INVISIBLE) {
            newTrade = new Trade(currentTime, currentStock.name, currentStock.priceList.get(0),
                    currentStock.priceList.get(0), userAmount, -1.0, -1.0, buysell, false, -1.0);
            Toast.makeText(this, "Trade Created", Toast.LENGTH_SHORT).show();
        } else {
            newTrade = new Trade(currentTime, currentStock.name, currentStock.priceList.get(0),
                    currentStock.priceList.get(0), userAmount, -1.0, -1.0, buysell, true, orderPrice);
            Toast.makeText(this, "Trade Ordered", Toast.LENGTH_SHORT).show();
        }
        userBalanceTextView.setText("Your Balance: $" + (MainActivity.currentUser.balance - userAmount));
        MainActivity.currentUser.balance = MainActivity.currentUser.balance - userAmount;
        MainActivity.currentUser.trades.add(newTrade);
        MainActivity.users.set(MainActivity.currentUserIndex, MainActivity.currentUser);

        MainActivity.uploadUsersToFirestore();
    }

    public void tradeCheckBoxChange(View view) {
        if (orderTradeEditText.getVisibility() == View.VISIBLE)
            orderTradeEditText.setVisibility(View.INVISIBLE);
        else
            orderTradeEditText.setVisibility(View.VISIBLE);
    }

    public void viewGraphIntent(View view) {
//        Intent intent = new Intent(this, GraphActivity.class);
//        startActivity(intent);

    }

    public void changebuysell(View view) {
        buysell = !buysell;
        if (buysell) {
            buysellButton.setText("buy");
            buysellButton.setTextColor(Color.GREEN);
        } else {
            buysellButton.setText("sell");
            buysellButton.setTextColor(Color.RED);
        }
    }



}