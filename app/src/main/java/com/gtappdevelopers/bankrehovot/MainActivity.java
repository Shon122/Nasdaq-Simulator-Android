package com.gtappdevelopers.bankrehovot;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.icu.text.IDNA;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import org.json.JSONObject;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class MainActivity extends AppCompatActivity {

    public static String BPI_ENDPOINT = "https://financialmodelingprep.com/api/v3/historical-chart/1min/AAPL?apikey=" + "f3366d9120bda80407791f48106ec000";
    private OkHttpClient okHttpClient = new OkHttpClient();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    Map<String, Object> docData = new HashMap<>();
    //String dataTaker = "s";
    String[] allNames = new String[]

            {

                    "ABNB", "ADBE", "ADI", "ADP", "AEP", "ALGN", "AMD", "AMGN",


            };
    final String[] priceTaker = new String[1];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        try {
//            InfoAll infoAll = new InfoAll(this);
//
//            infoAll.makeSingleString("1min");
//
//        } catch (ParseException | InterruptedException e) {
//            e.printStackTrace();
//        }
        TextView textview1 = findViewById(R.id.txt2);
        TextView textview2 = findViewById(R.id.txt3);
        new GetDataTask().execute();

//        for (int i = 0; i < allNames.length; i++) {
//
//            try {
//                TimeUnit.SECONDS.sleep(3);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//
//            try {
//                doOne(allNames[i]);
//            } catch (ParseException | InterruptedException e) {
//                e.printStackTrace();
//            }

//
//            SharedPreferences sharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE);
//            SharedPreferences.Editor myEdit = sharedPreferences.edit();
//            myEdit.putString("name1234", allNames[i]);
//            myEdit.apply();
//
//
//
//
//            Handler handler = new Handler();
//            handler.postDelayed(new Runnable() {
//                public void run() {
//                    try {
//                        SharedPreferences sharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE);
//                        String name = sharedPreferences.getString("name1234", "5");
//                        doOne(name);
//                    } catch (ParseException | InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }, 4000);
//


        //  }
//        try {
//            InfoAll infoAll = new InfoAll(this);
//            infoAll.updateAllPrices();
////            //StockModel[] stockModels = new StockModel[infoAll.allNames.length];
////            String currentName = infoAll.allNames[5];
////            StockModel stockModel = infoAll.getIndividualData(currentName);
////            //stockModels[1] = stockModel;
////
////
////            TextView textView = findViewById(R.id.txt1);
////            textView.setText(String.valueOf(stockModel.analysis));
////
////
////         Thread.sleep(10000);
////            infoAll = new InfoAll(this);
////            currentName = infoAll.allNames[5];
////            stockModel = infoAll.getIndividualData(currentName);
////            textView.setText(String.valueOf(stockModel.analysis));
//
//
//        } catch (ParseException | InterruptedException e) {
//            e.printStackTrace();
//        }


        //create a trade
//        Date c = Calendar.getInstance().getTime();
//        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
//        String formattedDate = df.format(c); //the string result is like "2022-12-10"
//        Trade trade1 = new Trade(formattedDate, "MSFT", 3561.0, 3561.0,
//                100.0, 3000.0, 4000.0, true);
//        String test = trade1.limitProfit.toString();
//        TextView textView = findViewById(R.id.txt1);
//        textView.setText(test);


        //show it in chart
        //showGraph();


        //end of oncreate
    }
//
//    public void allOneString(String timeInterval) {
//
//        for (int i = 0; i < allNames.length; i++) {
//        String    nameStock=allNames[i];
//            SharedPreferences sharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE);
//            String dataTaker = "";
//
//            if (timeInterval.equals("day")) {
//                apiLink = "https://financialmodelingprep.com/api/v3/historical-price-full/" + nameStock + "?apikey=" + apiList[apiIndex];
//            } else {
//                apiLink = "https://financialmodelingprep.com/api/v3/historical-chart/" + timeInterval + "/" + nameStock + "?apikey=" + apiList[apiIndex];
//            }
//            load(apiLink);
//            apiIndex++;
//            if (apiIndex >= apiList.length)
//                apiIndex = 0;
//            dataTaker = "";
//            dataTaker = sharedPreferences.getString("data", "none");
//            //now extract prices
//            ArrayList<Double> priceList = new ArrayList<>();
//            String saveString = "";
//            int count = 0;
//            int tempIndex = dataTaker.indexOf("close");
//            while (tempIndex > -1 && count < 51) {
//                count++;
//                String takeHere1 = (dataTaker.substring(tempIndex + 9, dataTaker.indexOf(',', tempIndex + 9)));
//                //here make sure there is no infinite number like 37.00000000
//                takeHere1 = removeInfiniteNumbers(takeHere1);
//                //now convert to Double
//                priceList.add(Double.valueOf(takeHere1));
//                saveString += priceList.get(priceList.size() - 1);
//                tempIndex = dataTaker.indexOf("close", tempIndex + 1);
//                if (tempIndex != -1)
//                    saveString += ",";
//            }
//            saveString = saveString.replaceAll("(\\r|\\n)", "");
//            String savePriceString = saveString; //this i will upload to firebase
//            ArrayList<String> dateList = new ArrayList<>();
//            saveString = ""; //!!IMPORTANT TO RESET THE SAVESTRING!!!!!!!!!!
//            tempIndex = dataTaker.indexOf("date");
//            count = 0;
//            while (tempIndex != -1 && count < 51) {
//                count++;
//                String takeDate = "";
//                if (timeInterval.equals("day")) {
//                    takeDate = dataTaker.substring(tempIndex + 9, tempIndex + 9 + 10);
//                } else {
//                    takeDate = dataTaker.substring(tempIndex + 9, tempIndex + 9 + 11 + 8);
//                    if (!takeDate.contains("o")) {
//                        SimpleDateFormat myDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//                        myDate.setTimeZone(TimeZone.getTimeZone("GMT-7:00"));
//                        Date newDate = null;
//                        newDate = myDate.parse(takeDate);
//                        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
//                        takeDate = df.format(newDate); //the string result is like "2022-12-10"
//                    }
//                }
//                dateList.add(takeDate);
//                saveString += dateList.get(dateList.size() - 1);
//                tempIndex = dataTaker.indexOf("date", tempIndex + 1);
//                if (tempIndex != -1)
//                    saveString += ",";
//            }
//            saveString = saveString.replaceAll("(\\r|\\n)", "");
//            String saveDatesString = saveString; // this i upload to firebase
//            StockModel stockModel = new StockModel(nameStock, priceList, dateList, timeInterval);
//        }
//    }


    public void doOne(String name) throws ParseException, InterruptedException {
        InfoAll infoAll = new InfoAll(this);
        infoAll.updateIndividualPrice(name, "1min");

        SharedPreferences sharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE);
        SharedPreferences.Editor myEdit = sharedPreferences.edit();
        myEdit.putString("name123", name);
        myEdit.apply();


        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                try {

                    String dataTaker = sharedPreferences.getString("name123", "5");
                    infoAll.updateIndividualPrice(dataTaker, "1min");
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }, 4000);

        handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                try {

                    String dataTaker = sharedPreferences.getString("name123", "5");
                    infoAll.updateIndividualPrice(dataTaker, "1min");
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }, 4000);


    }


    public void showGraph() {
        GraphView graph = (GraphView) findViewById(R.id.graph1);
        LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>(new DataPoint[]{
                new DataPoint(0, 1),
                new DataPoint(1, 5),
                new DataPoint(2, 3),
                new DataPoint(3, 2),
                new DataPoint(4, 6)
        });
        graph.addSeries(series);
    }


    public void do4hour(View view) throws ParseException, InterruptedException {
        InfoAll infoAll = new InfoAll(this);
        infoAll.updateIndividualPrice("AAPL", "4hour");
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                try {
                    infoAll.updateIndividualPrice("AAPL", "4hour");
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }, 4000);
    }

    public void do1hour(View view) throws ParseException {
//        InfoAll infoAll = new InfoAll(this);
//        infoAll.updateIndividualPrice("AAPL", "1min");
//
//        StockModel stockModel = getIndividualData("AAPL");
//
//
//        //  for (int i = 0; i < 1000; i++) {
//        TextView textView = findViewById(R.id.txt1);
//        textView.setText(stockModel.priceList.get(0).toString());
        // }
    }

    public void do1day(View view) throws ParseException {
//
//        StockModel stockModel = getIndividualData("ATVI");
//
//
//        // for (int i = 0; i < 1000; i++) {
//        TextView textView = findViewById(R.id.txt1);
//        textView.setText(stockModel.priceList.get(0).toString());
//        // }
//        InfoAll infoAll = new InfoAll(this);
//        infoAll.updateIndividualPrice("AAPL", "day");
    }

//
//    public StockModel getIndividualData(String nameStock) {
//        SharedPreferences sharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE);
//        //get everything first and then put it in stockmodel
//        db.collection("Trades").document("stockInfo").collection("allStocks").document(nameStock).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                if (task.isSuccessful()) {
//                    DocumentSnapshot document = task.getResult();
//
//
//                    String dateList = String.valueOf((document.get("dateList")));
//                    String priceList = String.valueOf((document.get("priceList")));
//                    String timeinterval = String.valueOf((document.get("timeinterval")));
//
//                    SharedPreferences sharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE);
//                    SharedPreferences.Editor myEdit = sharedPreferences.edit();
//                    myEdit.putString("dateList", dateList);
//                    myEdit.putString("priceList", priceList);
//                    myEdit.putString("timeinterval", timeinterval);
//                    myEdit.apply();
//
//                }
//            }
//        });
//        String dateListString = sharedPreferences.getString("dateList", "5");
//        String priceListString = sharedPreferences.getString("priceList", "5");
//        String timeinterval = sharedPreferences.getString("timeinterval", "5");
//        //now make it double for model
//        ArrayList<Double> priceList = new ArrayList<>();
//        ArrayList<String> dateList = new ArrayList<>();
//
//
//        String[] splitNumbers = dateListString.split(",");
//        Collections.addAll(dateList, splitNumbers);
//
//
//        splitNumbers = priceListString.split(",");
//
//        for (String number : splitNumbers) {
//            priceList.add(Double.valueOf(number));
//        }
//
//
//        return new StockModel(nameStock, priceList, dateList, timeinterval);
//
//    }


    private class GetDataTask extends AsyncTask<Void, Void, String> {


        TextView textview1 = findViewById(R.id.txt2);
        TextView textview2 = findViewById(R.id.txt3);

        @Override
        protected String doInBackground(Void... params) {
            String resultEnd = "";
            //  String stockName = String.valueOf(textview1.getText());
            String[] allNames = MainActivity.this.allNames;
            // String timeInterval = String.valueOf(textview2.getText());
            for (int i = 0; i < allNames.length; i++) {
                String stockName = allNames[i];
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder().url("https://financialmodelingprep.com/api/v3/historical-price-full/" + stockName + "?timeseries=3&apikey=b050b1fd76d5fb561c1fa00deeeea4d5").build();


                try {
                    Response response = client.newCall(request).execute();
                    String responseString = response.body().string();
                    resultEnd += responseString;
                } catch (IOException e) {
                    // return null;
                }
            }
            return resultEnd;
        }

        @SuppressLint("SetTextI18n")
        @Override
        protected void onPostExecute(String result) {
            // Update the UI with the result
            //ArrayList<Double> priceList1 = extractPrices(result);
            TextView textView = findViewById(R.id.txt1);
            textView.setText(textView.getText() + String.valueOf(result));

        }
    }

    public ArrayList<Double> extractPrices(String info) {

        //now extract prices
        ArrayList<Double> priceList = new ArrayList<>();
        String saveString = "";
        int count = 0;
        int tempIndex = info.indexOf("close");
        while (tempIndex > -1 && count < 51) {
            count++;
            String takeHere1 = (info.substring(tempIndex + 9, info.indexOf(',', tempIndex + 9)));
            //here make sure there is no infinite number like 37.00000000
            takeHere1 = removeInfiniteNumbers(takeHere1);
            //now convert to Double
            priceList.add(Double.valueOf(takeHere1));
            saveString += priceList.get(priceList.size() - 1);
            tempIndex = info.indexOf("close", tempIndex + 1);
            if (tempIndex != -1)
                saveString += ",";
        }
        saveString = saveString.replaceAll("(\\r|\\n)", "");
        String savePriceString = saveString; //this i will upload to firebase
        return priceList;
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


    //end of main


}