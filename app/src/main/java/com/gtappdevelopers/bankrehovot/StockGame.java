package com.gtappdevelopers.bankrehovot;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class StockGame extends AppCompatActivity {
    private GraphView graphView;
    private Button buyButton;
    private Button sellButton;
    private ArrayList<Double> prices;
    private Timer timer;
    private int timeRemaining;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.stock_game);

        graphView = findViewById(R.id.graph_view);
        buyButton = findViewById(R.id.buy_button);
        sellButton = findViewById(R.id.sell_button);

        prices = new ArrayList<>();
        prices.add(100.0);
        prices.add(110.0);
        prices.add(120.0);
        prices.add(100.0);
        prices.add(110.0);
        prices.add(120.0);
        prices.add(100.0);


        graphView.setPrices(prices);

        buyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: Handle buy button click
            }
        });

        sellButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: Handle sell button click
            }
        });

        timeRemaining = 120;

        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (timeRemaining > 0) {
                    addPrice();
                    timeRemaining -= 2;
                } else {
                    stopTimer();
                }
            }
        }, 0, 2000);
    }

    private void addPrice() {
        double lastPrice = prices.get(prices.size() - 1);
        double newPrice = lastPrice + Math.random() * 10 - 5;
        prices.add(newPrice);
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                graphView.setPrices(prices);
            }
        });
    }

    private void stopTimer() {
        timer.cancel();
        timer.purge();
    }
}