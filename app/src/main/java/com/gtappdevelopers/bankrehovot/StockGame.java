package com.gtappdevelopers.bankrehovot;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Cartesian;
import com.anychart.core.Chart;
import com.anychart.core.cartesian.series.Line;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Stock;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


public class StockGame extends AppCompatActivity {

    private GraphView stockGraph;
    private LineGraphSeries<DataPoint> stockSeries;
    private ArrayList<Double> priceList;
    private int currentIndex;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.stock_game);

        stockGraph = findViewById(R.id.stock_graph);
        stockSeries = new LineGraphSeries<>();
        stockGraph.addSeries(stockSeries);
        stockGraph.getViewport().setYAxisBoundsManual(true);
        stockGraph.getViewport().setMinY(50);
        stockGraph.getViewport().setMaxY(500);
        // Initialize the priceList and set the currentIndex
        priceList = MainActivity.stockModels.get(0).priceList;
        // add prices to the list
        currentIndex = 0;

        // Update stock price every second
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (currentIndex < priceList.size()) {
                    stockSeries.appendData(new DataPoint(stockSeries.getHighestValueX() + 1, priceList.get(currentIndex)), true, 100);
                    currentIndex++;
                }
                handler.postDelayed(this, 1000);
            }
        }, 1000);


        //rest of the code

        Button buyButton = findViewById(R.id.buy_button);
        buyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkGuess(true);
            }
        });
        Button sellButton = findViewById(R.id.sell_button);
        sellButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkGuess(false);
            }
        });
    }

    private void checkGuess(boolean isBuy) {
        if (currentIndex > 0) {
            double currentPrice = priceList.get(currentIndex - 1);
            double nextPrice;
            if (currentIndex < priceList.size()) {
                nextPrice = priceList.get(currentIndex);
            } else {
                nextPrice = priceList.get(priceList.size() - 1);
            }
            boolean correctGuess = (isBuy && nextPrice > currentPrice) || (!isBuy && nextPrice < currentPrice);
            if (correctGuess) {
                Toast.makeText(this, "Correct!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Incorrect!", Toast.LENGTH_SHORT).show();
            }
        }
    }

}