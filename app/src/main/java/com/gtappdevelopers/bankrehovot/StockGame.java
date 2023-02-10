package com.gtappdevelopers.bankrehovot;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.apache.commons.math3.stat.regression.OLSMultipleLinearRegression;

import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;


public class StockGame extends AppCompatActivity {
    GraphView graphView;
    Button buyButton;
    Button sellButton;
    float[] prices = {0, 10, 20, 30, 40, 50, 60, 70, 80, 90, 100};
    int currentPriceIndex = 0;
    Timer priceTimer;
    ArrayList<Double> sellPositions = new ArrayList<>();
    ArrayList<Double> buyPositions = new ArrayList<>();
    ArrayList<Double> originalPrices;
    Double amountInvest;
    int countTimes = -1;
    Double totalPNL;
    EditText amountInvestEditText;
    TextView predictionTextView;
    TextView totalPNLTextView;
    int stockNumber;
    boolean ongoingGame = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.stock_game);
        predictionTextView = findViewById(R.id.botPredictionTextView);
        amountInvestEditText = findViewById(R.id.userAmountInvestEditText);
        totalPNLTextView = findViewById(R.id.ProfitLossTextView);
        stockNumber = -1;
        randomStock(stockNumber);

        graphView = findViewById(R.id.graph_view);
        buyButton = findViewById(R.id.buy_button);
        sellButton = findViewById(R.id.sell_button);

        buyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buyPositions.add((double) prices[currentPriceIndex]);
            }
        });

        sellButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sellPositions.add((double) prices[currentPriceIndex]);
            }
        });


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
        originalPrices = MainActivity.viewingStock.priceList;
        int size = MainActivity.viewingStock.priceList.size();
        float[] floatArray = new float[size];
        for (int i = 0; i < size; i++) {
            floatArray[i] = MainActivity.viewingStock.priceList.get(i).floatValue();
        }
        prices = floatArray;

    }

    public Double profitLossCalculator(boolean longShort, Double startPrice) {
        //gives -+16.66% percent for example
        Double currentPrice = (double) prices[currentPriceIndex];
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
        Double currentPrice = (double) prices[currentPriceIndex];
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
                startGame();
            }

        }

    }

    public void startGame() {
        totalPNL = 0.0;
        countTimes = -1;
        ongoingGame = true;
        priceTimer = new Timer();
        priceTimer.scheduleAtFixedRate(new TimerTask() {

            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        countTimes++;
                        //here check if game should be stopped
                        graphView.setPrice(prices[currentPriceIndex]);
                        currentPriceIndex++;
                        if (currentPriceIndex == prices.length || countTimes == 120) {
                            currentPriceIndex = 0;
                            priceTimer.cancel();

                        }
                        //now continue


                        updateStats();

                        ArrayList<Double> convertedList = new ArrayList<Double>();
                        for(int i = 0; i < currentPriceIndex; i++){
                            convertedList.add(originalPrices.get(i));
                        }
                        NumberPrediction prediction = new NumberPrediction(convertedList);
                        double nextPrice = prediction.predictNextNumber();
                        predictionTextView.setText("Bot Prediction: " + nextPrice);
                        totalPNLTextView.setText("Total Profit/loss: $" + totalPNL);

                    }
                });
            }
        }, 0, 500);

        //after game is over change to a different stock, show results, update balance
        randomStock(stockNumber);
        MainActivity.currentUser.balance += totalPNL;
        MainActivity.users.set(MainActivity.currentUserIndex, MainActivity.currentUser);
        MainActivity.uploadUsersToFirestore();
        ongoingGame = false;
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