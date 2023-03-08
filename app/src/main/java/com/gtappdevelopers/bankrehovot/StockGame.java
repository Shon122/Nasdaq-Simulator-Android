package com.gtappdevelopers.bankrehovot;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.stat.regression.OLSMultipleLinearRegression;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class StockGame extends AppCompatActivity {
    GraphView graphView;
    Button buyButton;
    Button sellButton;
    Button okayButton;
    Button startGamebutton;
    ArrayList<Double> prices;
    Double totalPNL;
    Double amountInvest;
    int currentPriceIndex = 0;
    int stockNumber;
    boolean ongoingGame = false;
    EditText amountInvestEditText;
    TextView predictionTextView;
    TextView finalMessage;
    TextView totalPNLTextView;
    TextView balanceUser;
    TextView timeRemaining;
    MediaPlayer mediaPlayer;
    AudioManager audioManager;
    ArrayList<Double> sellPositions = new ArrayList<>();
    ArrayList<Double> buyPositions = new ArrayList<>();
    int time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.stock_game);
        predictionTextView = findViewById(R.id.botPredictionTextView);
        finalMessage = findViewById(R.id.finalMessage);
        amountInvestEditText = findViewById(R.id.userAmountInvestEditText);
        balanceUser = findViewById(R.id.balanceUser);
        timeRemaining = findViewById(R.id.timeRemaining);
        totalPNLTextView = findViewById(R.id.ProfitLossTextView);
        graphView = findViewById(R.id.graph_view);
        buyButton = findViewById(R.id.buy_button);
        okayButton = findViewById(R.id.okay_button);
        sellButton = findViewById(R.id.sell_button);
        startGamebutton = findViewById(R.id.startGamebutton);
        time = 15;
        stockNumber = 0;
        randomStock(stockNumber);

        okayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                regularVisibility();


            }
        });

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

        //here i set everything before starting game
        regularVisibility();
        resetText();


    }


//
//    public void changeVisibilityAll() {
//        buyButton.setVisibility(buyButton.getVisibility() == View.VISIBLE ? View.INVISIBLE : View.VISIBLE);
//        graphView.setVisibility(graphView.getVisibility() == View.VISIBLE ? View.INVISIBLE : View.VISIBLE);
//        sellButton.setVisibility(sellButton.getVisibility() == View.VISIBLE ? View.INVISIBLE : View.VISIBLE);
//        startGamebutton.setVisibility(startGamebutton.getVisibility() == View.VISIBLE ? View.INVISIBLE : View.VISIBLE);
//        amountInvestEditText.setVisibility(amountInvestEditText.getVisibility() == View.VISIBLE ? View.INVISIBLE : View.VISIBLE);
//        predictionTextView.setVisibility(predictionTextView.getVisibility() == View.VISIBLE ? View.INVISIBLE : View.VISIBLE);
//        totalPNLTextView.setVisibility(totalPNLTextView.getVisibility() == View.VISIBLE ? View.INVISIBLE : View.VISIBLE);
//        balanceUser.setVisibility(balanceUser.getVisibility() == View.VISIBLE ? View.INVISIBLE : View.VISIBLE);
//        timeRemaining.setVisibility(timeRemaining.getVisibility() == View.VISIBLE ? View.INVISIBLE : View.VISIBLE);
//        finalMessage.setVisibility(finalMessage.getVisibility() == View.VISIBLE ? View.INVISIBLE : View.VISIBLE);
//
//
//    }


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
            duringGameVisibility();
            finalMessage.setVisibility(View.INVISIBLE);
            Intent intent = new Intent(this, MusicService.class);
            startService(intent);

            AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
           // audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, maxVolume / 4, 1);
            mediaPlayer = MediaPlayer.create(this, R.raw.dramamusic);
            mediaPlayer.start();

            //after music is done
            amountInvest = Double.parseDouble(String.valueOf(amountInvestEditText.getText()));
            totalPNL = 0.0;
            time = 15;
            ongoingGame = true;
            currentPriceIndex = 0;
            sellPositions = new ArrayList<>();
            buyPositions = new ArrayList<>();
            randomStock(stockNumber);

            //start game and draw graph
            CountDownTimer countDownTimer = new CountDownTimer(15 * 1000, 1000) {

                @Override
                public void onTick(long millisUntilFinished) {
                    timeRemaining.setText(time + "s");
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
                            totalPNLTextView1.setText("Profit/loss: $" + roundToTwoDecimals(totalPNL));
                        }
                    });


                    currentPriceIndex++;
                    time--;
                }

                @SuppressLint("SetTextI18n")
                @Override
                public void onFinish() {
                    // This method will be called when the countdown is finished or cancelled.
                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd:MM:yyyy HH:mm:ss");
                    Date currentDate = new Date();
                    String formattedDate = dateFormat.format(currentDate);
                    boolean win= totalPNL >= 0;
                    Game game1= new Game(formattedDate,totalPNL,win);
                    currentPriceIndex = 0;
                    randomStock(stockNumber);
                    MainActivity.currentUser.balance += totalPNL;
                    MainActivity.currentUser.games.add(game1);
                    MainActivity.users.set(MainActivity.currentUserIndex, MainActivity.currentUser);
                    MainActivity.uploadUsersToFirestore();
                    ongoingGame = false;
                    sellPositions = new ArrayList<>();
                    buyPositions = new ArrayList<>();
                    Toast.makeText(StockGame.this, "Game Over", Toast.LENGTH_LONG).show();
                    time = 15;
                    mediaPlayer.stop();
                    mediaPlayer.release();
                    afterGameVisibility();
                    resetText();
                    //now show only the final message
                    if (totalPNL >= 0)
                        finalMessage.setText("You Won +" + roundToTwoDecimals(totalPNL) + "$");
                    else
                        finalMessage.setText("You Lost " + roundToTwoDecimals(totalPNL) + "$");


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

    public void regularVisibility() {
        okayButton.setVisibility(View.INVISIBLE);
        buyButton.setVisibility(View.INVISIBLE);
        graphView.setVisibility(View.INVISIBLE);
        sellButton.setVisibility(View.INVISIBLE);
        timeRemaining.setVisibility(View.INVISIBLE);
        finalMessage.setVisibility(View.INVISIBLE);
        predictionTextView.setVisibility(View.INVISIBLE);
        totalPNLTextView.setVisibility(View.INVISIBLE);
        //
        startGamebutton.setVisibility(View.VISIBLE);
        amountInvestEditText.setVisibility(View.VISIBLE);
        balanceUser.setVisibility(View.VISIBLE);


    }

    public void duringGameVisibility() {
        okayButton.setVisibility(View.INVISIBLE);
        finalMessage.setVisibility(View.INVISIBLE);
        startGamebutton.setVisibility(View.INVISIBLE);
        balanceUser.setVisibility(View.INVISIBLE);
        amountInvestEditText.setVisibility(View.INVISIBLE);
        //
        predictionTextView.setVisibility(View.VISIBLE);
        totalPNLTextView.setVisibility(View.VISIBLE);
        buyButton.setVisibility(View.VISIBLE);
        graphView.setVisibility(View.VISIBLE);
        sellButton.setVisibility(View.VISIBLE);
        timeRemaining.setVisibility(View.VISIBLE);


    }

    public void afterGameVisibility() {
        startGamebutton.setVisibility(View.INVISIBLE);
        balanceUser.setVisibility(View.INVISIBLE);
        amountInvestEditText.setVisibility(View.INVISIBLE);
        predictionTextView.setVisibility(View.INVISIBLE);
        totalPNLTextView.setVisibility(View.INVISIBLE);
        buyButton.setVisibility(View.INVISIBLE);
        graphView.setVisibility(View.INVISIBLE);
        sellButton.setVisibility(View.INVISIBLE);
        timeRemaining.setVisibility(View.INVISIBLE);
        //
        okayButton.setVisibility(View.VISIBLE);
        finalMessage.setVisibility(View.VISIBLE);

    }


    public void resetText() {
        timeRemaining.setText("15s");
        balanceUser.setText("Your Balance: " + roundToTwoDecimals(MainActivity.currentUser.balance) + "$");
        totalPNLTextView.setText("Profit/loss: " + 0 + "$");
        predictionTextView.setText("Bot Prediction: Price Going Up");
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