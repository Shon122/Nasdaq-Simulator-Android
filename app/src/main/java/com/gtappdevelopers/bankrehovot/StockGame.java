package com.gtappdevelopers.bankrehovot;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Timer;
import java.util.TimerTask;


public class StockGame extends AppCompatActivity {
    private GraphView graphView;
    private Button buyButton;
    private Button sellButton;
    private float[] prices = {0, 10, 20, 30, 40, 50, 60, 70, 80, 90, 100};
    private int currentPriceIndex = 0;
    private Timer priceTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.stock_game);


        int size = MainActivity.viewingStock.priceList.size();

// Create a float array with the same size
        float[] floatArray = new float[size];

// Iterate through the list and add the double values to the float array
        for (int i = 0; i < size; i++) {
            floatArray[i] = MainActivity.viewingStock.priceList.get(i).floatValue();
        }
        prices = floatArray;
        graphView = findViewById(R.id.graph_view);
        buyButton = findViewById(R.id.buy_button);
        sellButton = findViewById(R.id.sell_button);

        buyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // handle buy button click
            }
        });

        sellButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // handle sell button click
            }
        });

        priceTimer = new Timer();
        priceTimer.scheduleAtFixedRate(new TimerTask() {

            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //priceTimer.cancel();
                        graphView.setPrice(prices[currentPriceIndex]);
                        currentPriceIndex++;
                        if (currentPriceIndex == prices.length) {
                            currentPriceIndex = 0;
                        }
                    }
                });
            }
        }, 0, 500);
    }
}