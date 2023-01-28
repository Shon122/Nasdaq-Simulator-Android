package com.gtappdevelopers.bankrehovot;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Cartesian;
import com.anychart.core.cartesian.series.Line;
import com.anychart.data.Mapping;
import com.anychart.data.Set;
import com.anychart.enums.Anchor;
import com.anychart.enums.MarkerType;
import com.anychart.enums.TooltipPositionMode;
import com.anychart.graphics.vector.Stroke;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;


import org.apache.commons.math3.stat.regression.OLSMultipleLinearRegression;


public class CurrencyPage extends AppCompatActivity {
    //    EditText priceProfitEditText = findViewById(R.id.priceProfitEditText);
//    EditText ilsProfitEditText = findViewById(R.id.ilsProfitEditText);
//    EditText percentProfitEditText = findViewById(R.id.percentProfitEditText);
//    EditText priceStopEditText = findViewById(R.id.priceStopEditText);
//    EditText ilsStopEditText = findViewById(R.id.ilsStopEditText);
//    EditText percentStopEditText = findViewById(R.id.percentStopEditText);
//    CheckBox stopLossCheckBox = findViewById(R.id.stopLossCheckBox);
//    CheckBox takeProfitCheckBox = findViewById(R.id.takeProfitCheckBox);

    ArrayList<Double> reversedPriceList = MainActivity.viewingStock.priceList;
    ArrayList<String> reversedDateList = MainActivity.viewingStock.dateList;

    ArrayList<Double> priceList = MainActivity.viewingStock.priceList;
    ArrayList<String> dateList = MainActivity.viewingStock.dateList;
    EditText amountInvestEditText;
    EditText orderTradeEditText;
    CheckBox orderTradeCheckBox;
    TextView stockNameTextView;
    TextView predictionTextView;
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
        Collections.reverse(reversedDateList);
        Collections.reverse(reversedPriceList);
        changeTextView = findViewById(R.id.changeTextView);
        predictionTextView = findViewById(R.id.predictionTextView);
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


        NumberPrediction prediction = new NumberPrediction(priceList);
        double nextPrice = prediction.predictNextNumber();
        predictionTextView.setText("" + nextPrice);


        AnyChartView anyChartView = findViewById(R.id.any_chart_view);
        Cartesian cartesian = AnyChart.line();
        cartesian.animation(true);
        cartesian.padding(10d, 20d, 5d, 20d);
        cartesian.crosshair().enabled(true);
        cartesian.crosshair().yLabel(true).yStroke((Stroke) null, null, null, (String) null, (String) null);
        cartesian.tooltip().positionMode(TooltipPositionMode.POINT);
        cartesian.title().enabled(false);
        cartesian.xAxis(0).labels().padding(5d, 5d, 5d, 5d);
        cartesian.xAxis(0).labels().enabled(false);
        List<DataEntry> seriesData = new ArrayList<>();
        for (int i = 0; i < reversedDateList.size(); i++) {
            seriesData.add(new CustomDataEntry(reversedDateList.get(i), reversedPriceList.get(i)));
        }
        Set set = Set.instantiate();
        set.data(seriesData);
        Mapping series1Mapping = set.mapAs("{ x: 'x', value: 'value' }");
        Line series1 = cartesian.line(series1Mapping);
        series1.name(MainActivity.viewingStock.name);
        series1.hovered().markers().enabled(true);
        series1.hovered().markers().type(MarkerType.CIRCLE).size(4d);
        series1.tooltip().position("right").anchor(Anchor.LEFT_CENTER).offsetX(5d).offsetY(5d);
        cartesian.legend().enabled(true);
        cartesian.legend().fontSize(13d);
        cartesian.legend().padding(0d, 0d, 10d, 0d);
        anyChartView.setChart(cartesian);
        //end of oncreate
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////


    public static class CustomDataEntry extends ValueDataEntry {
        CustomDataEntry(String x, Number value) {
            super(x, value);
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


    public class NumberPrediction {
        private ArrayList<Double> data;
        private double[] predictors;
        private double[] parameters;
        private OLSMultipleLinearRegression regression;

        public NumberPrediction(ArrayList<Double> data) {
            this.data = data;
            predictors = new double[data.size()];
            for (int i = 0; i < data.size(); i++) {
                predictors[i] = i;
            }
            regression = new OLSMultipleLinearRegression();
            double[] dataArray = new double[data.size()];
            for (int i = 0; i < data.size(); i++) {
                dataArray[i] = data.get(i);
            }
            double[][] predictorsArray = new double[dataArray.length][1];
            for (int i = 0; i < dataArray.length; i++) {
                predictorsArray[i][0] = i;
            }

            regression.newSampleData(dataArray, predictorsArray);
            parameters = regression.estimateRegressionParameters();
        }

        public double predictNextNumber() {
            double nextIndex = data.size();
            return parameters[0] + parameters[1] * nextIndex;
        }
    }
}