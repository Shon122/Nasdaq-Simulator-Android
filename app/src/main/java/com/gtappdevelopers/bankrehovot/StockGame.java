package com.gtappdevelopers.bankrehovot;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.stat.regression.OLSMultipleLinearRegression;
import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;


public class StockGame extends AppCompatActivity {
    GraphView graphView;
    Button buyButton;
    Button sellButton;
    ArrayList<Double> prices = new ArrayList<>();
    int currentPriceIndex = 0;
    Timer priceTimer;
    ArrayList<Double> sellPositions = new ArrayList<>();
    ArrayList<Double> buyPositions = new ArrayList<>();
    Double amountInvest;
    int countTimes = -1;
    Double totalPNL;
    EditText amountInvestEditText;
    TextView predictionTextView;
    TextView totalPNLTextView;
    int stockNumber;
    boolean ongoingGame = false;
    private TextView priceView;
    private LinearLayout graphLayout;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.stock_game);
        predictionTextView = findViewById(R.id.botPredictionTextView);
        amountInvestEditText = findViewById(R.id.userAmountInvestEditText);
        totalPNLTextView = findViewById(R.id.ProfitLossTextView);
        stockNumber = -1;
        randomStock(stockNumber);
        //now i got prices
        //graphView = (GraphView) findViewById(R.id.graph_view);
        buyButton = findViewById(R.id.buy_button);
        sellButton = findViewById(R.id.sell_button);
        graphLayout = findViewById(R.id.graph_layout);
        handler = new Handler();
        buyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buyPositions.add((double) prices.get(currentPriceIndex));
            }
        });

        sellButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sellPositions.add((double) prices.get(currentPriceIndex));
            }
        });


    }

    @SuppressLint("SetTextI18n")
    private void updatePrice() {
        if (currentPriceIndex < prices.size() && currentPriceIndex < 60) { //so it will be 2 minutes
            double currentPrice = prices.get(currentPriceIndex);
            priceView.setText(""+String.valueOf(currentPrice));

            // Add a new point to the graph
            View point = new View(this);
            point.setBackgroundColor(getResources().getColor(R.color.green));
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(20, 20);
            params.leftMargin = 20 * currentPriceIndex;
            point.setLayoutParams(params);
            graphLayout.addView(point);
            updateStats();

            ArrayList<Double> newPrices = new ArrayList<>();
            for (int i = 0; i <= currentPriceIndex; i++) {
                Double takeNumber = prices.get(i);
                newPrices.add(takeNumber);
            }
            try {
                NumberPrediction prediction = new NumberPrediction(newPrices);
                Double nextPrice = roundToTwoDecimals(prediction.predictNextNumber());
                if (nextPrice > prices.get(currentPriceIndex))
                    predictionTextView.setText("Bot Prediction: Price Going Down");
                else
                    predictionTextView.setText("Bot Prediction: Price Going Up");
            } catch (MathIllegalArgumentException e) {
                predictionTextView.setText("Bot Prediction: Price Going Up"); // just in case
            }

            totalPNLTextView.setText("Total Profit/loss: $" + totalPNL);

            currentPriceIndex++;
        } else {
            // All prices have been added

            randomStock(stockNumber);
            MainActivity.currentUser.balance += totalPNL;
            MainActivity.users.set(MainActivity.currentUserIndex, MainActivity.currentUser);
            MainActivity.uploadUsersToFirestore();
            ongoingGame = false;
            sellPositions = new ArrayList<>();
            buyPositions = new ArrayList<>();
            return;
        }

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                updatePrice();
            }
        }, 2000);
    }


    public void startGame() {
        totalPNL = 0.0;
        countTimes = -1;
        ongoingGame = true;
        currentPriceIndex = 0;
        ArrayList<Double> takeThis = new ArrayList<>();
        takeThis.add(prices.get(0));
        graphView.setPrices(takeThis);
        priceTimer = new Timer();
        priceTimer.scheduleAtFixedRate(new TimerTask() {

            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void run() {
                        countTimes++;
                        //here check if game should be stopped
                        currentPriceIndex++;
                        if (currentPriceIndex == prices.size() || countTimes == 120) {
                            currentPriceIndex = 0;
                            priceTimer.cancel();

                        }
                        updatePrices();
                        //now continue


                        updateStats();


                        ArrayList<Double> newPrices = new ArrayList<>();
                        for (int i = 0; i <= currentPriceIndex; i++) {
                            Double takeNumber = prices.get(i);
                            newPrices.add(takeNumber);
                        }
                        try {
                            NumberPrediction prediction = new NumberPrediction(newPrices);
                            Double nextPrice = roundToTwoDecimals(prediction.predictNextNumber());
                            if (nextPrice > prices.get(currentPriceIndex))
                                predictionTextView.setText("Bot Prediction: Price Going Down");
                            else
                                predictionTextView.setText("Bot Prediction: Price Going Up");
                        } catch (MathIllegalArgumentException e) {
                            predictionTextView.setText("Bot Prediction: Price Going Up"); // just in case
                        }

                        totalPNLTextView.setText("Total Profit/loss: $" + totalPNL);

                    }
                });
            }
        }, 0, 2000);

        //after game is over change to a different stock, show results, update balance

        randomStock(stockNumber);
        MainActivity.currentUser.balance += totalPNL;
        MainActivity.users.set(MainActivity.currentUserIndex, MainActivity.currentUser);
        MainActivity.uploadUsersToFirestore();
        ongoingGame = false;
        sellPositions = new ArrayList<>();
        buyPositions = new ArrayList<>();
    }

    public void updatePrices() {
        ArrayList<Double> convertList = new ArrayList<>();
        for (int i = 0; i <= currentPriceIndex; i++) {
            convertList.add(prices.get(i));
        }


        graphView.setPrices(convertList);
    }

    public void randomStock(int lastStockNumber) {
        //make sure the stock isnt the same as the last one displayed
        Random random = new Random();
        int randomNumber = lastStockNumber;
        while (randomNumber == lastStockNumber) {
            randomNumber = random.nextInt(MainActivity.stockModels.size());
        }
        stockNumber = randomNumber;
        MainActivity.viewingStock = MainActivity.stockModels.get(randomNumber);
        prices = MainActivity.viewingStock.priceList;

    }

    public Double profitLossCalculator(boolean longShort, Double startPrice) {
        //gives -+16.66% percent for example
        Double currentPrice = prices.get(currentPriceIndex);
        Double percentProfitLoss;
        Double totalProfitLoss;
        if (longShort) {
            percentProfitLoss = ((currentPrice - startPrice) / startPrice) * 100;
            totalProfitLoss = (amountInvest + (percentProfitLoss / 100) * amountInvest) - amountInvest;
        } else {
            percentProfitLoss = -1 * (((currentPrice - startPrice) / startPrice) * 100);
            totalProfitLoss = -1 * ((amountInvest + (percentProfitLoss / 100) * amountInvest) - amountInvest);

        }
        totalProfitLoss = roundToTwoDecimals(totalProfitLoss);
        return totalProfitLoss;

    }

    public Double roundToTwoDecimals(Double value) {
        return (double) Math.round(value * 100) / 100;
    }

    public void updateStats() {
        totalPNL = 0.0;
        for (int i = 0; i < sellPositions.size(); i++) {
            totalPNL += profitLossCalculator(false, sellPositions.get(i));
        }
        for (int i = 0; i < buyPositions.size(); i++) {
            totalPNL += profitLossCalculator(true, buyPositions.get(i));
        }
    }

    public void startGameCheck(View view) {
        if (!ongoingGame) {
            //check if amount is under startBalance and above 0
            if (String.valueOf(amountInvestEditText.getText()).length() == 0) {
                Toast.makeText(StockGame.this, "Please Enter Amount", Toast.LENGTH_SHORT).show();
                return;
            } else {
                if (Double.parseDouble(String.valueOf(amountInvestEditText.getText())) <= 0.0) {
                    Toast.makeText(StockGame.this, "Amount has to be above 0", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (Double.parseDouble(String.valueOf(amountInvestEditText.getText())) > MainActivity.currentUser.balance) {
                    Toast.makeText(StockGame.this, "Amount has to be below your balance", Toast.LENGTH_SHORT).show();
                    return;
                }
                //if amount is ok
                amountInvest = Double.parseDouble(String.valueOf(amountInvestEditText.getText()));
                totalPNL = 0.0;
                countTimes = -1;
                ongoingGame = true;
                currentPriceIndex = 0;
                //startGame();
                updatePrice();
            }

        }

    }

    private class NumberPrediction {
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