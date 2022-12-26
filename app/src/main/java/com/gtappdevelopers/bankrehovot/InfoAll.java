package com.gtappdevelopers.bankrehovot;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.CountDownTimer;
import android.os.Handler;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
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
    public String[] apiList;
    public int apiIndex;
    public String[] allNames;
    public StockModel[] stockModels;
    int stockModelIndex;
    String apiLink;
    private OkHttpClient okHttpClient;
    private FirebaseFirestore db;
    Map<String, Object> docData;
    public Context mContext;
    String result;
    int iNames;

    /////////////////////////////////////////////////
    public InfoAll(Context context) throws ParseException {
        result = "";
        iNames = 0;
        stockModelIndex = 0;
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
        allNames = new String[]{

                "ABNB", "ADBE", "ADI", "ADP", "AEP", "ALGN", "AMD", "AMGN",
                "AMZN", "AAPL", "ATVI", "AUDNOK", "AUDPLN", "BNBUSD", "BTCUSD"
//                ,
//                "CADBRL", "CADZAR", "CHFJPY", "ETHUSD", "EURBRL", "EURUSD", "GILD",
//                "GOOGL", "IBM", "INTC", "ILSUSD", "KO", "LTCUSD", "META",
//                "MMM", "MSFT", "NKE", "NFLX", "NZDCZK", "NZDTRY", "PYPL", "PLNILS",
//                "SOLUSD", "TSLA", "TRYDKK", "USDJPY", "WMT", "XRPUSD"
        };


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
                "9826c9f4968f4726aff2e843db493424",
                "2ccc83581cab1580034bd1d43219f421",
                "36c1d526b5750ae07a2de23109bedcda",
                "dda76b57b63a5a86e18a5e94e619a370",
                "0f569e49ec24d01e6abef9bd7f3aa3d2",
                "9b5236ded34b6ec08e2baf996f2e2604"


        };
        apiLink = "https://financialmodelingprep.com/api/v3/historical-chart/1min/BTCUSD?apikey=" + apiList[apiIndex];
        stockModels = new StockModel[allNames.length];
    }


    public void updateAllPrices() throws ParseException, InterruptedException {
        //update all timeInterval will allways be 1 minute
        //get last Big Update All and make sure more than 2 minutes have passed since that
        long lastUpdate = 0;
        //get lastUpdate data from firebase
        db.collection("Trades").document("indexapi").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();


                    String uploaderTaker = (String.valueOf(document.get("lastUpdateAll")));
                    SharedPreferences sharedPreferences = mContext.getSharedPreferences("MySharedPref", MODE_PRIVATE);
                    SharedPreferences.Editor myEdit = sharedPreferences.edit();
                    myEdit.putString("dataLastBigUpdate", uploaderTaker);
                    myEdit.apply();

                }
            }
        });
        SharedPreferences sharedPreferences = mContext.getSharedPreferences("MySharedPref", MODE_PRIVATE);
        String dataTaker = sharedPreferences.getString("dataLastBigUpdate", "5");
        lastUpdate = Long.parseLong((dataTaker));
        int diff = (int) (currentTime - lastUpdate); // 1 min = 60000 ms
        //make sure more than 1 day has passed since last call
        diff = 1111111111;
        if (diff > 604800000) {
            for (int i = 0; i < allNames.length; i++) {
                SharedPreferences.Editor myEdit = sharedPreferences.edit();
                myEdit.putString("name55", allNames[i]);
                myEdit.apply();


                updateIndividualPrice(allNames[i], "1min");

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        try {
                            String name = sharedPreferences.getString("name55", "5");
                            updateIndividualPrice(name, "1min");
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                }, 4000);
                Thread.sleep(4001);
                stockModelIndex++;
            }


            docData.put("lastUpdateAll", currentTime);
            db.collection("Trades").document("indexapi").set(docData, SetOptions.merge());
            docData.put("indexnumber", apiIndex);
            db.collection("Trades").document("indexapi").set(docData, SetOptions.merge());
        } else {
            for (int i = 0; i < allNames.length; i++) {
                String currentName = allNames[i];
                StockModel stockModel = getIndividualData(currentName);
                stockModels[i] = stockModel;
                stockModelIndex++;
            }

        }

        stockModelIndex = 0;
    }


    public StockModel getIndividualData(String nameStock) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences("MySharedPref", MODE_PRIVATE);
        //get everything first and then put it in stockmodel
        db.collection("Trades").document("stockInfo").collection("allStocks").document(nameStock).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();


                    String dateList = String.valueOf((document.get("dateList")));
                    String priceList = String.valueOf((document.get("priceList")));
                    String timeinterval = String.valueOf((document.get("timeinterval")));

                    SharedPreferences sharedPreferences = mContext.getSharedPreferences("MySharedPref", MODE_PRIVATE);
                    SharedPreferences.Editor myEdit = sharedPreferences.edit();
                    myEdit.putString("dateList", dateList);
                    myEdit.putString("priceList", priceList);
                    myEdit.putString("timeinterval", timeinterval);
                    myEdit.apply();

                }
            }
        });
        String dateListString = sharedPreferences.getString("dateList", "5");
        String priceListString = sharedPreferences.getString("priceList", "5");
        String timeinterval = sharedPreferences.getString("timeinterval", "5");
        //now make it double for model
        ArrayList<Double> priceList = new ArrayList<>();
        ArrayList<String> dateList = new ArrayList<>();


        String[] splitNumbers = dateListString.split(",");
        Collections.addAll(dateList, splitNumbers);


        splitNumbers = priceListString.split(",");

        for (String number : splitNumbers) {
            priceList.add(Double.valueOf(number));
        }


        return new StockModel(nameStock, priceList, dateList, timeinterval);

    }


    public void makeSingleString(String timeInterval) throws ParseException, InterruptedException {
        result = "";
        SharedPreferences sharedPreferences = mContext.getSharedPreferences("MySharedPref", MODE_PRIVATE);
        SharedPreferences.Editor myEdit = sharedPreferences.edit();
        myEdit.putString("result1","");
        myEdit.apply();

        for (int i = 0; i < allNames.length; i++) {
            iNames = i;


            String nameStock = allNames[iNames];
            String dataTaker = "";
            if (timeInterval.equals("day")) {
                apiLink = "https://financialmodelingprep.com/api/v3/historical-price-full/" + nameStock + "?apikey=" + apiList[apiIndex];
            } else {
                apiLink = "https://financialmodelingprep.com/api/v3/historical-chart/" + timeInterval + "/" + nameStock + "?apikey=" + apiList[apiIndex];
            }
            load(apiLink);
            apiIndex++;
            if (apiIndex >= apiList.length)
                apiIndex = 0;
            dataTaker = "";
            dataTaker = sharedPreferences.getString("data", "none");
            //now extract prices
            ArrayList<Double> priceList = new ArrayList<>();
            String saveString = "";
            int count = 0;
            int tempIndex = dataTaker.indexOf("close");
            while (tempIndex > -1 && count < 51) {
                count++;
                String takeHere1 = (dataTaker.substring(tempIndex + 9, dataTaker.indexOf(',', tempIndex + 9)));
                //here make sure there is no infinite number like 37.00000000
                takeHere1 = removeInfiniteNumbers(takeHere1);
                //now convert to Double
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
            count = 0;
            while (tempIndex != -1 && count < 51) {
                count++;
                String takeDate = "";
                if (timeInterval.equals("day")) {
                    takeDate = dataTaker.substring(tempIndex + 9, tempIndex + 9 + 10);
                } else {
                    takeDate = dataTaker.substring(tempIndex + 9, tempIndex + 9 + 11 + 8);
                    if (!takeDate.contains("o")) {
                        SimpleDateFormat myDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        myDate.setTimeZone(TimeZone.getTimeZone("GMT-7:00"));
                        Date newDate = null;
                        try {
                            newDate = myDate.parse(takeDate);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
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
            StockModel stockModel = new StockModel(nameStock, priceList, dateList, timeInterval);

            String result1 = "|||" + nameStock + " " + String.valueOf(priceList.get(0));

            String takenData = sharedPreferences.getString("result1", "");
            myEdit.putString("result1", takenData + result1);
            myEdit.apply();

        }


        result = sharedPreferences.getString("result1", "");
        docData.put("indexnumber", apiIndex);
        db.collection("Trades").document("indexapi").set(docData, SetOptions.merge());
        docData.put("infoString", result);// creates an entirely new document with new field
        db.collection("Trades").document("stockInfo").set(docData);
        result = "";

    }


    public void updateIndividualPrice(String nameStock, String timeInterval) throws ParseException {
        // 1min,5min,15min,30min,1hour,4hour. for days its link /historical-price-full/AAPL so write day
        //get all the info into the variables first


        SharedPreferences sharedPreferences = mContext.getSharedPreferences("MySharedPref", MODE_PRIVATE);
        String dataTaker = "";

        if (timeInterval.equals("day")) {
            apiLink = "https://financialmodelingprep.com/api/v3/historical-price-full/" + nameStock + "?apikey=" + apiList[apiIndex];
        } else {
            apiLink = "https://financialmodelingprep.com/api/v3/historical-chart/" + timeInterval + "/" + nameStock + "?apikey=" + apiList[apiIndex];
        }
        load(apiLink);
        apiIndex++;
        if (apiIndex >= apiList.length)
            apiIndex = 0;
        dataTaker = "";
        dataTaker = sharedPreferences.getString("data", "none");
        //now extract prices
        ArrayList<Double> priceList = new ArrayList<>();
        String saveString = "";
        int count = 0;
        int tempIndex = dataTaker.indexOf("close");
        while (tempIndex > -1 && count < 51) {
            count++;
            String takeHere1 = (dataTaker.substring(tempIndex + 9, dataTaker.indexOf(',', tempIndex + 9)));
            //here make sure there is no infinite number like 37.00000000
            takeHere1 = removeInfiniteNumbers(takeHere1);
            //now convert to Double
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
        count = 0;
        while (tempIndex != -1 && count < 51) {
            count++;
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
        stockModels[stockModelIndex] = new StockModel(nameStock, priceList, dateList, timeInterval);

        //now upload priceList to firebase
        docData.put("analysis", stockModels[stockModelIndex].analysis);// creates an entirely new document with new field
        db.collection("Trades").document("stockInfo").collection("allStocks").document(nameStock).set(docData);
        docData.put("dateList", saveDatesString);// creates an entirely new document with new field
        db.collection("Trades").document("stockInfo").collection("allStocks").document(nameStock).set(docData);

        docData.put("gainLossPercent", stockModels[stockModelIndex].gainLossPercent);// creates an entirely new document with new field
        db.collection("Trades").document("stockInfo").collection("allStocks").document(nameStock).set(docData);

        docData.put("lastUpdate", currentTime);// creates an entirely new document with new field
        db.collection("Trades").document("stockInfo").collection("allStocks").document(nameStock).set(docData);

        docData.put("priceList", savePriceString);// creates an entirely new document with new field
        db.collection("Trades").document("stockInfo").collection("allStocks").document(nameStock).set(docData);

        docData.put("timeinterval", timeInterval);// creates an entirely new document with new field
        db.collection("Trades").document("stockInfo").collection("allStocks").document(nameStock).set(docData);

        docData.put("indexnumber", apiIndex);
        db.collection("Trades").document("indexapi").set(docData, SetOptions.merge());
        //    stockModelIndex++;
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
        //make sure more than 1 day has passed since last call


        if (diff > 604800000) {
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


    public String removeInfiniteNumbers(String price) {
        //make sure the price is small and compact like 302.3656 and not 302.363573895
        //0.12345678 ---> 0.12345
        //1.12345678 ---> 1.1234
        //12.12345678 --->12.123
        //123.12345678 --->123.123
        //1234.12345678 --->1234.123
        //12345.12345678 --->12345.123

        //first of all cut all the zeros at the end


        int take1 = price.length();
        while (price.charAt(take1 - 1) == '0') {
            price = price.substring(0, price.length() - 1);
            take1 = price.length();
        }
        if (price.charAt(price.length() - 1) == '.') {
            price = price.substring(0, price.length() - 1);
            return price;
        }
        //now dealing with prices who are not "37.00000"

        String beforePoint = price.substring(0, price.indexOf("."));
        String afterPoint = price.substring(price.indexOf(".") + 1);


        if (afterPoint.length() > 5)
            afterPoint = afterPoint.substring(0, 5);
        String result = beforePoint + "." + afterPoint;


        return result;

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
