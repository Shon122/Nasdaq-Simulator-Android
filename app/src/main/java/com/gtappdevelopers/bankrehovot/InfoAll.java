package com.gtappdevelopers.bankrehovot;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import android.app.Activity;
import android.content.SharedPreferences;
import android.widget.TextView;

import static android.content.Context.MODE_PRIVATE;

import androidx.annotation.NonNull;

public class InfoAll {
    public Long currentTime;
    public String news;
    public Long lastUpdateTime;
    public String[] apiList;
    public int apiIndex;
    public String[] stockNames;
    public String[] currenciesNames;
    public String[] cryptoNames;
    public String[] allNames;
    public StockModel[] stockModels;
    String apiLink;
    private OkHttpClient okHttpClient;
    private FirebaseFirestore db;
    Map<String, Object> docData;
    public Context mContext;

    public InfoAll(Context context) {
        mContext = context;
        stockNames = new String[]{"TSLA", "MSFT", "AMZN", "AAPL", "GOOGL", "NVDA", "META",
                "NFLX", "ADBE", "IBM", "WMT", "MMM", "NKE", "PYPL", "JPM"};
        cryptoNames = new String[]{"XRPUSD", "ETHUSD", "BTCUSD", "LUNAUSD", "DOGEUSD", "ADAUSD"
                , "LTCUSD", "SOLUSD", "SHIBUSD", "ETCUSD", "ALGOUSD", "CROUSD", "LUNCUSD", "AAVEUSD", "BTTUSD"};
        currenciesNames = new String[]{"EURUSD", "USDJPY", "GBPUSD", "ILSUSD", "AUDNOK", "CADBRL", "NZDTRY",
                "PLNILS", "NZDCZK", "AUDPLN", "MXNGBP", "CHFJPY", "TRYDKK", "CADZAR", "EURBRL"};

        int tempIndex = 0;
        allNames = new String[stockNames.length + cryptoNames.length + currenciesNames.length];
        for (int i = 0; i < stockNames.length; i++) {
            allNames[tempIndex] = stockNames[i];
            tempIndex++;
        }
        for (int i = 0; i < cryptoNames.length; i++) {
            allNames[tempIndex] = cryptoNames[i];
            tempIndex++;
        }
        for (int i = 0; i < currenciesNames.length; i++) {
            allNames[tempIndex] = currenciesNames[i];
            tempIndex++;
        }
        okHttpClient = new OkHttpClient();
        db = FirebaseFirestore.getInstance();
        docData = new HashMap<>();
        currentTime = System.currentTimeMillis();
        apiList = new String[]{"c42711901b00e79841bb71702345719e",
                "262483bd904a81b091b2e27cbcfc0655",
                "5e4573cba51e730e43abbfdf9ed9b975",
                "cccd134cea3374b1ec72c38c08c0b0b0",
                "5aa107ade26d9f4076b8d60f0020d49b",
                "19ba45233fe86671685bdf936a24b931",
                "f44e40de3e5c0c5b6ec60df0730c10d6",
                "21161f524ff705577169d62f61047ff7",
                "c0c89ef92565d4cf7c7ffa0a013e7313",
                "f3366d9120bda80407791f48106ec000",
                "f5eca2647dfd717ee3c6541b48950600",
                "f47d87d5284e9b73dfe85379526ba0c9",
                "9a6a270b61f40c0e58d160cbb1c57131",
                "02d49e539ff86d6fa9aa0f549efc93a3",
                "b050b1fd76d5fb561c1fa00deeeea4d5",};
        apiIndex = 0;
        apiLink = "https://financialmodelingprep.com/api/v3/historical-chart/1min/BTCUSD?apikey=" + apiList[apiIndex];
        stockModels = new StockModel[stockNames.length];
        updatePrices("1min");
    }

    public void updateAll() {
        //gets all the data and upload to firestore
        //now i need to check if the firebase data is older than 1 minute and
        //only then i will be able to get new data
        updateNews();
        updatePrices("1min");

    }

    public void updatePrices(String timeInterval) { //available time intervals: 1min,5min,15min,30min,1hour,4hour. for days its link /historical-price-full/AAPL
        //get all the info into the variables first
        for (int i = 0; i < stockModels.length; i++) {
            String nameStock=stockNames[i];
        //now extract prices
            ArrayList<Double> priceList= new ArrayList<>();



            ArrayList<Double> dateList= new ArrayList<>();
        }


        //upload variables info to firebase
        docData.put("price", "555");// creates an entirely new document with new field
        db.collection("Trades").document("stockInfo").collection("allStocks").document("ETHUSD").set(docData);

    }

    public void getIndividualPrice() {


    }

    public void updateNews() {
        long lastUpdate = 0;
        //get lastUpdate data from firebase
        db.collection("Trades").document("newsAll").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();


                    String uploaderTaker = (document.get("lastNewsUpdate").toString());
                    SharedPreferences sharedPreferences = mContext.getSharedPreferences("MySharedPref", MODE_PRIVATE);
                    SharedPreferences.Editor myEdit = sharedPreferences.edit();
                    myEdit.putString("dataLastUpdate", uploaderTaker);
                    myEdit.apply();

                }
            }
        });
        SharedPreferences sharedPreferences = mContext.getSharedPreferences("MySharedPref", MODE_PRIVATE);
        String dataTaker = sharedPreferences.getString("dataLastUpdate", "5");
        lastUpdate = Long.parseLong((dataTaker));
        int diff = (int) (currentTime - lastUpdate); // 1 min = 60000 ms
        //make sure more than 2 minute has passed since last call


        if (diff > 120000) {
            apiLink = "https://financialmodelingprep.com/api/v3/fmp/articles?page=0&size=5&apikey=" + apiList[apiIndex];
            apiIndex++;
            load();
            String data = sharedPreferences.getString("data", "none");
            //after i get string upload to firebase
            data = data.replaceAll("(?s)<[^>]*>(\\s*<[^>]*>)*", " ");
            data = data.replaceAll("\\r\\n|\\r|\\n", " ");
            String temp = String.valueOf((char) 92);
            data = data.replace(temp + "n", "");
            //now i just filter so it will only show content
            ArrayList<String> allContent = new ArrayList<>();
            int contentIndex = data.indexOf((String.valueOf('"') + "content" + String.valueOf('"')), 25); // searching for " ("content")"
            while (contentIndex != -1) {
                String takePart = data.substring(contentIndex + 14, (data.indexOf("tickers", contentIndex + 9)) - 10);
                allContent.add(takePart);

                contentIndex = data.indexOf(String.valueOf('"') + "content" + String.valueOf('"'), contentIndex + 15);
            }
            data = "";
            for (int i = 0; i < allContent.size(); i++) {
                data += "" + allContent.get(i) + ". ";

            }
            docData.put("lastNewsUpdate", currentTime);
            db.collection("Trades").document("newsAll").set(docData, SetOptions.merge());

            docData.put("news", data);
            db.collection("Trades").document("newsAll").set(docData, SetOptions.merge());
            news = data;
        } else {
            db.collection("Trades").document("newsAll").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();


                        String uploaderTaker = (document.get("news").toString());
                        SharedPreferences sharedPreferences = mContext.getSharedPreferences("MySharedPref", MODE_PRIVATE);
                        SharedPreferences.Editor myEdit = sharedPreferences.edit();
                        myEdit.putString("dataNews", uploaderTaker);
                        myEdit.apply();

                    }
                }
            });
            String dataNewsTaker = sharedPreferences.getString("dataNews", "5");
            news = dataNewsTaker;
        }

    }

    public void load() {
        Request request = new Request.Builder().url(apiLink).build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
            }

            @Override
            public void onResponse(Call call, Response response)
                    throws IOException {
                final String body = response.body().string();

                SharedPreferences sharedPreferences = mContext.getSharedPreferences("MySharedPref", MODE_PRIVATE);
                SharedPreferences.Editor myEdit = sharedPreferences.edit();
                myEdit.putString("data", body);
                myEdit.apply();


            }
        });
    }


    public void parseBpiResponse(String body) throws ParseException {
        //get prices and dates and upload to firebase
        ArrayList<String> dates = new ArrayList<>();
        ArrayList<String> prices = new ArrayList<>();
        String saveString = "";

        // TextView textView = findViewById(R.id.txt2);
        //textView.setText(body.substring(dateIndex+9,dateIndex+9+11+8));
        int tempIndex = body.indexOf("date");
        while (tempIndex != -1) {
            String takeDate = body.substring(tempIndex + 9, tempIndex + 9 + 11 + 8);


            java.text.SimpleDateFormat myDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            myDate.setTimeZone(TimeZone.getTimeZone("GMT-7:00"));
            Date newDate = null;
            newDate = myDate.parse(takeDate);
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            takeDate = df.format(newDate); //the string result is like "2022-12-10"


            dates.add(takeDate);
            saveString += dates.get(dates.size() - 1);
            saveString += ",";
            tempIndex = body.indexOf("date", tempIndex + 1);
        }
        saveString = saveString.replaceAll("(\\r|\\n)", "");
        //now i change the time of date to UTC+2 israel time


        docData.put("dates", saveString);
        db.collection("Trades").document("Prices").set(docData, SetOptions.merge());

        //after we got dates now we get prices
        saveString = "";
        tempIndex = body.indexOf("close");
        while (tempIndex != -1) {
            prices.add(body.substring(tempIndex + 9, tempIndex + 9 + 8));
            saveString += prices.get(prices.size() - 1);
            saveString += ",";
            tempIndex = body.indexOf("close", tempIndex + 1);
        }
        saveString = saveString.replaceAll("(\\r|\\n)", "");
        docData.put("prices", saveString);
        db.collection("Trades").document("Prices").set(docData, SetOptions.merge());


    }

}
