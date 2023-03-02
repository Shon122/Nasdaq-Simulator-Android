package com.gtappdevelopers.bankrehovot;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.stat.regression.OLSMultipleLinearRegression;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class StockGame extends AppCompatActivity {
    GraphView graphView;
    Button buyButton;
    Button sellButton;
    ArrayList<Double> prices;
    Double totalPNL;
    Double amountInvest;
    int currentPriceIndex = 0;
    int stockNumber;
    boolean ongoingGame = false;
    EditText amountInvestEditText;
    TextView predictionTextView;
    TextView totalPNLTextView;
    TextView balanceUser;
    TextView timeRemaining;
    MediaPlayer mediaPlayer;
    AudioManager audioManager;
    ArrayList<Double> sellPositions = new ArrayList<>();
    ArrayList<Double> buyPositions = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.stock_game);
        predictionTextView = findViewById(R.id.botPredictionTextView);
        amountInvestEditText = findViewById(R.id.userAmountInvestEditText);
        balanceUser = findViewById(R.id.balanceUser);
        timeRemaining = findViewById(R.id.timeRemaining);
        totalPNLTextView = findViewById(R.id.ProfitLossTextView);
        graphView = findViewById(R.id.graph_view);
        buyButton = findViewById(R.id.buy_button);
        sellButton = findViewById(R.id.sell_button);


        stockNumber = 0;
        randomStock(stockNumber);


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


    @Override //when user wants to go back
    public void onBackPressed() {
        // Handle back button press event here
        if (ongoingGame) {
            Toast.makeText(StockGame.this, "Cant go back while in a game", Toast.LENGTH_SHORT).show();

        } else {
            super.onBackPressed();
            //goes back regularly
        }

    }


    public void startGame(View view) {
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
            }

            //if game started so we initialize everything
            Intent intent = new Intent(this, MusicService.class);
            startService(intent);
            AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, maxVolume, 0);
            mediaPlayer = MediaPlayer.create(this, R.raw.dramamusic);
            mediaPlayer.start();
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, maxVolume/4, 1);

            //after music is done
            amountInvest = Double.parseDouble(String.valueOf(amountInvestEditText.getText()));
            totalPNL = 0.0;
            ongoingGame = true;
            currentPriceIndex = 0;
            sellPositions = new ArrayList<>();
            buyPositions = new ArrayList<>();
            randomStock(stockNumber);

            //start game and draw graph
            CountDownTimer countDownTimer = new CountDownTimer(15 * 1000, 1000) {

                @Override
                public void onTick(long millisUntilFinished) {
                    addPrice();
                    updateStats();
                    TextView predictionTextView1;
                    TextView totalPNLTextView1;
                    predictionTextView1 = findViewById(R.id.botPredictionTextView);
                    totalPNLTextView1 = findViewById(R.id.ProfitLossTextView);
                    ArrayList<Double> newPrices = new ArrayList<>();
                    for (int i = 0; i <= currentPriceIndex; i++) {
                        Double takeNumber = prices.get(i);
                        newPrices.add(takeNumber);
                    }
                    try {
                        NumberPrediction prediction = new NumberPrediction(newPrices);
                        Double nextPrice = roundToTwoDecimals(prediction.predictNextNumber());
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (nextPrice > prices.get(currentPriceIndex))
                                    predictionTextView1.setText("Bot Prediction: Price Going Down");
                                else
                                    predictionTextView1.setText("Bot Prediction: Price Going Up");
                            }
                        });
                    } catch (MathIllegalArgumentException e) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                predictionTextView1.setText("Bot Prediction: Price Going Up"); // just in case
                            }
                        });
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            totalPNLTextView1.setText("Total Profit/loss: $" + roundToTwoDecimals(totalPNL));
                        }
                    });


                    currentPriceIndex++;
                }

                @Override
                public void onFinish() {
                    // This method will be called when the countdown is finished or cancelled.
                    // Perform your desired action here.
                    currentPriceIndex = 0;
                    randomStock(stockNumber);
                    MainActivity.currentUser.balance += totalPNL;
                    MainActivity.users.set(MainActivity.currentUserIndex, MainActivity.currentUser);
                    MainActivity.uploadUsersToFirestore();
                    ongoingGame = false;
                    sellPositions = new ArrayList<>();
                    buyPositions = new ArrayList<>();
                    Toast.makeText(StockGame.this, "Game Over", Toast.LENGTH_LONG).show();

                    mediaPlayer.stop();
                    mediaPlayer.release();

                }
            };

            countDownTimer.start();


            // To cancel the timer after 5 seconds:
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    countDownTimer.cancel();
                }
            }, 16 * 1000);


        }

    }

    //other methods///////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    private void addPrice() {
        ArrayList<Double> shownPrices = new ArrayList<>();
        for (int i = 0; i <= currentPriceIndex; i++) {
            shownPrices.add(prices.get(i));
        }
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                graphView.setPrices(shownPrices);
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
        prices = MainActivity.viewingStock.priceList;
        Collections.reverse(prices); // becuase pricelist is from newest price to oldest


    }

    public Double profitLossCalculator(boolean longShort, Double startPrice) {
        //gives -+16.66% percent for example
        Double currentPrice = prices.get(currentPriceIndex);
        Double percentProfitLoss;
        Double totalProfitLoss;

        percentProfitLoss = ((currentPrice - startPrice) / startPrice) * 100;
        totalProfitLoss = (amountInvest + (percentProfitLoss / 100) * amountInvest) - amountInvest;
        if (longShort) {
            totalProfitLoss = roundToTwoDecimals(totalProfitLoss);
        } else {
            totalProfitLoss = -1 * roundToTwoDecimals(totalProfitLoss);
        }
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