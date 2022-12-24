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
import java.util.Objects;
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


    /////////////////////////////////////////////////
    public InfoAll(Context context) throws ParseException {
        okHttpClient = new OkHttpClient();
        mContext = context;
        db = FirebaseFirestore.getInstance();
        docData = new HashMap<>();
        //get api index from firebase
        db.collection("Trades").document("indexapi").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    String uploaderTaker = String.valueOf(document.get("indexnumber"));
                    SharedPreferences sharedPreferences = mContext.getSharedPreferences("MySharedPref", MODE_PRIVATE);
                    SharedPreferences.Editor myEdit = sharedPreferences.edit();
                    myEdit.putInt("dataIndexApi", Integer.parseInt((uploaderTaker)));
                    myEdit.apply();
                }
            }
        });
        SharedPreferences sharedPreferences = mContext.getSharedPreferences("MySharedPref", MODE_PRIVATE);
        apiIndex = sharedPreferences.getInt("dataIndexApi", 0);
        //apiIndex = 0;
        stockNames = new String[]{"TSLA", "MSFT", "AMZN", "AAPL", "GOOGL", "NVDA", "META",
                "NFLX", "ADBE", "IBM", "WMT", "MMM", "NKE", "PYPL", "JPM"};
        cryptoNames = new String[]{"XRPUSD", "ETHUSD", "BTCUSD", "LUNAUSD", "DOGEUSD", "ADAUSD"
                , "LTCUSD", "SOLUSD", "SHIBUSD", "ALGOUSD", "BTTUSD", "CROUSD", "LUNCUSD"};
        currenciesNames = new String[]{"EURUSD", "USDJPY", "GBPUSD", "ILSUSD", "AUDNOK", "CADBRL", "NZDTRY",
                "PLNILS", "NZDCZK", "AUDPLN", "MXNGBP", "CHFJPY", "TRYDKK", "CADZAR", "EURBRL"};

        int tempIndex = -1;
        allNames = new String[stockNames.length + cryptoNames.length + currenciesNames.length];
        for (String stockName : stockNames) {
            tempIndex++;
            allNames[tempIndex] = stockName;
        }
        for (String cryptoName : cryptoNames) {
            tempIndex++;
            allNames[tempIndex] = cryptoName;
        }
        for (String currenciesName : currenciesNames) {
            tempIndex++;
            allNames[tempIndex] = currenciesName;
        }

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
                "b050b1fd76d5fb561c1fa00deeeea4d5",
                "8d30c21d048073535bd26cefff977b0c",
                "e3d886b55e36cf01a11c0c15b62353eb",
                "8902fd4b5be3cdf6f457d9404d52c16a",
                "07fef516f7504f5b2781c5c58b75a63d",
                "af6063b9faed6beb8bb0fec11951feaa",
                "03e7915a807c174eabdc070225bd7997",
                "97a895c7e212f394201ce0b775894703",
                "f46c28ada7aa5eb18007371f7c19bd41",
                "1abbcbbb11ed8fbb27bc3d71e698b76d",
                "b384bd91fb7fcb3a9657beac393cc9db",
                "7ddd34443e97cc9ad5d4e6fe6d2d5502",
                "c5492f9f3334045292514ace4409bf35",
                "9826c9f4968f4726aff2e843db493424"};
        apiLink = "https://financialmodelingprep.com/api/v3/historical-chart/1min/BTCUSD?apikey=" + apiList[apiIndex];
        stockModels = new StockModel[allNames.length];
    }

    public void updatePrices(String timeInterval) throws ParseException {
        // 1min,5min,15min,30min,1hour,4hour. for days its link /historical-price-full/AAPL so write day
        //get all the info into the variables first

        for (int i = 0; i < stockModels.length; i++) {
            String nameStock = allNames[i];
            //check if the name of the stock was recently updated or not
            //
            db.collection("Trades").document("stockInfo").collection("allStocks").document(nameStock).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        String uploaderTaker = (String) document.get("timeinterval");
                        SharedPreferences sharedPreferences = mContext.getSharedPreferences("MySharedPref", MODE_PRIVATE);
                        SharedPreferences.Editor myEdit = sharedPreferences.edit();
                        myEdit.putString("dataLastInterval", uploaderTaker);
                        myEdit.apply();
                    }
                }
            });
            SharedPreferences sharedPreferences = mContext.getSharedPreferences("MySharedPref", MODE_PRIVATE);
            String lastTimeInterval = sharedPreferences.getString("dataLastInterval", "none");

            db.collection("Trades").document("stockInfo").collection("allStocks").document(nameStock).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        Object nowTake = document.get("lastUpdate");
                        long uploaderTaker = (long) nowTake;
                        SharedPreferences sharedPreferences = mContext.getSharedPreferences("MySharedPref", MODE_PRIVATE);
                        SharedPreferences.Editor myEdit = sharedPreferences.edit();
                        myEdit.putString("dataLastUpdate", String.valueOf(uploaderTaker));
                        myEdit.apply();
                    }
                }
            });
            long lastTimeUpdate = Long.parseLong(sharedPreferences.getString("dataLastUpdate", "1671869153213"));
            long diff = (currentTime - lastTimeUpdate);
            //////
            if (diff > 1 || !Objects.equals(timeInterval, lastTimeInterval)) {
                if (Objects.equals(timeInterval, "day")) {
                    apiLink = "https://financialmodelingprep.com/api/v3/historical-price-full/" + nameStock + "?serietype=line&apikey=" + apiList[apiIndex];
                } else {
                    apiLink = "https://financialmodelingprep.com/api/v3/historical-chart/" + timeInterval + "/" + nameStock + "?apikey=" + apiList[apiIndex];

                }
                load(apiLink);
                apiIndex++;
                if (apiIndex >= apiList.length)
                    apiIndex = 0;
                String dataTaker = sharedPreferences.getString("data", "none");
                //now extract prices
                ArrayList<Double> priceList = new ArrayList<>();
                String saveString = "";
                int tempIndex = dataTaker.indexOf("close");
                while (tempIndex != -1) {
                    String takeHere1 = (dataTaker.substring(tempIndex + 9, tempIndex + 9 + 8));
                    if (takeHere1.contains("E"))
                        takeHere1 = "0";
                    priceList.add(Double.valueOf(takeHere1));
                    saveString += priceList.get(priceList.size() - 1);
                    saveString += ",";
                    tempIndex = dataTaker.indexOf("close", tempIndex + 1);
                }
                saveString = saveString.replaceAll("(\\r|\\n)", "");
                String savePriceString = saveString; //this i will upload to firebase
                ArrayList<String> dateList = new ArrayList<>();

                tempIndex = dataTaker.indexOf("date");
                while (tempIndex != -1) {
                    String takeDate = dataTaker.substring(tempIndex + 9, tempIndex + 9 + 11 + 8);


                    java.text.SimpleDateFormat myDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    myDate.setTimeZone(TimeZone.getTimeZone("GMT-7:00"));
                    Date newDate = null;
                    newDate = myDate.parse(takeDate);
                    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                    takeDate = df.format(newDate); //the string result is like "2022-12-10"


                    dateList.add(takeDate);
                    saveString += dateList.get(dateList.size() - 1);
                    saveString += ",";
                    tempIndex = dataTaker.indexOf("date", tempIndex + 1);
                }
                saveString = saveString.replaceAll("(\\r|\\n)", "");
                String saveDatesString = saveString; // this i upload to firebase

                stockModels[i] = new StockModel(nameStock, priceList, dateList, timeInterval);

                //now upload priceList to firebase
                docData.put("analysis", stockModels[i].analysis);// creates an entirely new document with new field
                db.collection("Trades").document("stockInfo").collection("allStocks").document(nameStock).set(docData);

                docData.put("dateList", dateList);// creates an entirely new document with new field
                db.collection("Trades").document("stockInfo").collection("allStocks").document(nameStock).set(docData);

                docData.put("gainLossPercent", stockModels[i].gainLossPercent);// creates an entirely new document with new field
                db.collection("Trades").document("stockInfo").collection("allStocks").document(nameStock).set(docData);

                docData.put("lastUpdate", currentTime);// creates an entirely new document with new field
                db.collection("Trades").document("stockInfo").collection("allStocks").document(nameStock).set(docData);

                docData.put("priceList", priceList);// creates an entirely new document with new field
                db.collection("Trades").document("stockInfo").collection("allStocks").document(nameStock).set(docData);

                docData.put("timeinterval", timeInterval);// creates an entirely new document with new field
                db.collection("Trades").document("stockInfo").collection("allStocks").document(nameStock).set(docData);

            }


        }

        //
        //
        //update the index number in firebase
        docData.put("indexnumber", apiIndex);
        db.collection("Trades").document("indexapi").set(docData, SetOptions.merge());
    }


    public void updateIndividualPrice(String nameStock, String timeInterval) throws ParseException {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences("MySharedPref", MODE_PRIVATE);
        docData.put("analysis", "changed");// creates an entirely new document with new field
        db.collection("Trades").document("stockInfo").collection("allStocks").document(nameStock).set(docData);

//        //make sure it did update price
//        db.collection("Trades").document("stockInfo").collection("allStocks").document(nameStock).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                if (task.isSuccessful()) {
//                    DocumentSnapshot document = task.getResult();
//                    String uploaderTaker = (String) document.get("dateList");
//                    SharedPreferences sharedPreferences = mContext.getSharedPreferences("MySharedPref", MODE_PRIVATE);
//                    SharedPreferences.Editor myEdit = sharedPreferences.edit();
//                    myEdit.putString("data1", uploaderTaker);
//                    myEdit.apply();
//                    uploaderTaker = (String) document.get("priceList");
//                    myEdit = sharedPreferences.edit();
//                    myEdit.putString("data2", uploaderTaker);
//                    myEdit.apply();
//
//                }
//            }
//        });
//        SharedPreferences sharedPreferences = mContext.getSharedPreferences("MySharedPref", MODE_PRIVATE);
//        String lastDateFirebase = sharedPreferences.getString("data1", "none");
//        String lastPriceFirebase = sharedPreferences.getString("data2", "none");
//        String takenPriceFirebase = lastPriceFirebase;
//        String takenDateFirebase = lastDateFirebase;
//        while (takenPriceFirebase.equals(lastPriceFirebase) || takenDateFirebase.equals(lastDateFirebase)) {
        //check if the name of the stock was recently updated or not
        //


        //
//        SharedPreferences sharedPreferences = mContext.getSharedPreferences("MySharedPref", MODE_PRIVATE);
//        db.collection("Trades").document("stockInfo").collection("allStocks").document(nameStock).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                if (task.isSuccessful()) {
//                    DocumentSnapshot document = task.getResult();
//                    String uploaderTaker = (String) document.get("timeinterval");
//                    SharedPreferences sharedPreferences = mContext.getSharedPreferences("MySharedPref", MODE_PRIVATE);
//                    SharedPreferences.Editor myEdit = sharedPreferences.edit();
//                    myEdit.putString("dataLastInterval", uploaderTaker);
//                    myEdit.apply();
//                }
//            }
//        });
//        String lastTimeInterval = sharedPreferences.getString("dataLastInterval", "none");
//
//        db.collection("Trades").document("stockInfo").collection("allStocks").document(nameStock).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                if (task.isSuccessful()) {
//                    DocumentSnapshot document = task.getResult();
//                    Object nowTake = document.get("lastUpdate");
//                    long uploaderTaker = (long) nowTake;
//                    SharedPreferences sharedPreferences = mContext.getSharedPreferences("MySharedPref", MODE_PRIVATE);
//                    SharedPreferences.Editor myEdit = sharedPreferences.edit();
//                    myEdit.putString("dataLastUpdate", String.valueOf(uploaderTaker));
//                    myEdit.apply();
//                }
//            }
//        });
//        long lastTimeUpdate = Long.parseLong(sharedPreferences.getString("dataLastUpdate", "1671869153213"));
//        long diff = (currentTime - lastTimeUpdate);
        //////
        if (timeInterval.equals("day")) {
            apiLink = "https://financialmodelingprep.com/api/v3/historical-price-full/" + nameStock + "?apikey=" + apiList[apiIndex];
        } else {
            apiLink = "https://financialmodelingprep.com/api/v3/historical-chart/" + timeInterval + "/" + nameStock + "?apikey=" + apiList[apiIndex];
        }
        docData.put("analysis", apiLink);// creates an entirely new document with new field
        db.collection("Trades").document("stockInfo").collection("allStocks").document(nameStock).set(docData);
        load(apiLink);
        apiIndex++;
        if (apiIndex >= apiList.length)
            apiIndex = 0;
        String dataTaker = "";
        dataTaker = sharedPreferences.getString("data", "none");
        docData.put("analysis", dataTaker);// creates an entirely new document with new field
        db.collection("Trades").document("stockInfo").collection("allStocks").document(nameStock).set(docData);
        //now extract prices
        ArrayList<Double> priceList = new ArrayList<>();
        String saveString = "";
        int tempIndex = dataTaker.indexOf("close");
        while (tempIndex > -1) {
            String takeHere1 = (dataTaker.substring(tempIndex + 9, dataTaker.indexOf(',', tempIndex + 9)));

            priceList.add(Double.valueOf(takeHere1));
            saveString += priceList.get(priceList.size() - 1);
            tempIndex = dataTaker.indexOf("close", tempIndex + 1);
            if (tempIndex != -1)
                saveString += ",";
        }
        saveString = saveString.replaceAll("(\\r|\\n)", "");
        String savePriceString = saveString; //this i will upload to firebase
        ArrayList<String> dateList = new ArrayList<>();
        saveString = ""; //!!IMPORTANT TO RESET THE SAVESTRING!!!!!!!!!!
        tempIndex = dataTaker.indexOf("date");
        while (tempIndex != -1) {
            String takeDate = "";
            if (timeInterval.equals("day")) {
                takeDate = dataTaker.substring(tempIndex + 9, tempIndex + 9 + 10);
            } else {
                takeDate = dataTaker.substring(tempIndex + 9, tempIndex + 9 + 11 + 8);
                if (!takeDate.contains("o")) {
                    SimpleDateFormat myDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    myDate.setTimeZone(TimeZone.getTimeZone("GMT-7:00"));
                    Date newDate = null;
                    newDate = myDate.parse(takeDate);
                    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                    takeDate = df.format(newDate); //the string result is like "2022-12-10"
                }
            }
            dateList.add(takeDate);
            saveString += dateList.get(dateList.size() - 1);
            tempIndex = dataTaker.indexOf("date", tempIndex + 1);
            if (tempIndex != -1)
                saveString += ",";
        }
        saveString = saveString.replaceAll("(\\r|\\n)", "");
        String saveDatesString = saveString; // this i upload to firebase
        StockModel adir = new StockModel(nameStock, priceList, dateList, timeInterval);
        //now upload priceList to firebase
//        docData.put("analysis", adir.analysis);// creates an entirely new document with new field
//        db.collection("Trades").document("stockInfo").collection("allStocks").document(nameStock).set(docData);
        docData.put("dateList", saveDatesString);// creates an entirely new document with new field
        db.collection("Trades").document("stockInfo").collection("allStocks").document(nameStock).set(docData);

        docData.put("gainLossPercent", adir.gainLossPercent);// creates an entirely new document with new field
        db.collection("Trades").document("stockInfo").collection("allStocks").document(nameStock).set(docData);

        docData.put("lastUpdate", currentTime);// creates an entirely new document with new field
        db.collection("Trades").document("stockInfo").collection("allStocks").document(nameStock).set(docData);

        docData.put("priceList", savePriceString);// creates an entirely new document with new field
        db.collection("Trades").document("stockInfo").collection("allStocks").document(nameStock).set(docData);

        docData.put("timeinterval", timeInterval);// creates an entirely new document with new field
        db.collection("Trades").document("stockInfo").collection("allStocks").document(nameStock).set(docData);
        //            db.collection("Trades").document("stockInfo").collection("allStocks").document(nameStock).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//                @Override
//                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                    if (task.isSuccessful()) {
//                        DocumentSnapshot document = task.getResult();
//                        String uploaderTaker = (String) document.get("dateList");
//                        SharedPreferences sharedPreferences = mContext.getSharedPreferences("MySharedPref", MODE_PRIVATE);
//                        SharedPreferences.Editor myEdit = sharedPreferences.edit();
//                        myEdit.putString("data1", uploaderTaker);
//                        myEdit.apply();
//                        uploaderTaker = (String) document.get("priceList");
//                        myEdit = sharedPreferences.edit();
//                        myEdit.putString("data2", uploaderTaker);
//                        myEdit.apply();
//
//                    }
//                }
//            });
//            lastDateFirebase = sharedPreferences.getString("data1", "none");
//            lastPriceFirebase = sharedPreferences.getString("data2", "none");
//            if (!(takenPriceFirebase.equals(lastPriceFirebase)) || !(takenDateFirebase.equals(lastDateFirebase)))
//                break
//                ;
        //}
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
            load(apiLink);
            apiIndex++;
            if (apiIndex > apiList.length)
                apiIndex = 0;
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


    public void load(String apiLink1) {
        Request request = new Request.Builder().url(apiLink1).build();
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


}
