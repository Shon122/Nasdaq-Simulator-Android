package com.gtappdevelopers.bankrehovot;

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

public class InfoAll {
    public Long currentTime;
    public String news;
    public Long lastUpdateTime;
    public String[] apiList;
    public int apiIndex;
    public String[] stockNames;
    public String[] currenciesNames;
    public String[] cryptoNames;
    public StockModel[] stockModels;
    String ApiLink;
    private OkHttpClient okHttpClient;
    private FirebaseFirestore db;
    Map<String, Object> docData;


    public InfoAll() {
        stockNames = new String[]{"TSLA", "MSFT", "AMZN", "AAPL", "GOOGL", "NVDA", "META",
                "NFLX", "ADBE", "IBM", "WMT", "MMM", "NKE", "PYPL", "JPM"};
        cryptoNames = new String[]{"XRPUSD", "ETHUSD", "BTCUSD", "LUNAUSD", "DOGEUSD", "ADAUSD"
                , "LTCUSD", "SOLUSD", "SHIBUSD", "ETCUSD", "ALGOUSD", "CROUSD", "LUNCUSD", "AAVEUSD", "BTTUSD"};
        currenciesNames = new String[]{"EURUSD", "USDJPY", "GBPUSD", "ILSUSD", "AUDNOK", "CADBRL", "NZDTRY",
                "PLNILS", "NZDCZK", "AUDPLN", "MXNGBP", "CHFJPY", "TRYDKK", "CADZAR", "EURBRL"};
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
        ApiLink = "https://financialmodelingprep.com/api/v3/historical-chart/1min/BTCUSD?apikey=" + apiList[apiIndex];
        load();
    }

    public void updateAll() {
        //gets all the data and upload to firestore
        getNews();
        getPrices();

    }

    public void getPrices() {


    }

    public void getIndividualPrice() {


    }
    public void getNews() {


    }

    private void load() {
        Request request = new Request.Builder().url(ApiLink).build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
            }

            @Override
            public void onResponse(Call call, Response response)
                    throws IOException {
                final String body = response.body().string();
                try {
                    parseBpiResponse(body);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    private void parseBpiResponse(String body) throws ParseException {
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
