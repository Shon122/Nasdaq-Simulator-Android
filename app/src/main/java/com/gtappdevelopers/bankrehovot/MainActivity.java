package com.gtappdevelopers.bankrehovot;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.TimeZone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;

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

                    "ABNB", "ADBE", "ADI"


            };
    final String[] priceTaker = new String[1];
    String temp11;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        StockModel s1 = null;
        try {
            InfoAll info1 = new InfoAll(this);
            s1 = info1.GetOnePriceModel("ABNB", "1min", true);
            TextView textView = findViewById(R.id.txt1);
            textView.setText(String.valueOf(info1.stockModels[0].priceList.get(0)));
        } catch (ParseException e) {
            e.printStackTrace();
        }
//        TextView textView = findViewById(R.id.txt1);
//        assert s1 != null;
//        textView.setText(String.valueOf(s1.priceList.get(0)));


//        TextView textView = findViewById(R.id.txt1);
//        textView.setText("before");
//        TextView textView = findViewById(R.id.txt1);
//        textView.setText("before");
//        for (int i = 0; i < 35; i++) {
//            docData.put("" + i, i);
//            db.collection("Trades").document("stockInfo").collection("allStocks").document("" + i).set(docData, SetOptions.merge());
//        }
//
//        textView.setText("after");


//
//        docData.put("infoString", textview.getText());// creates an entirely new document with new field
//        db.collection("Trades").document("stockInfo").set(docData);
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


    private class GetDataTask extends AsyncTask<Void, Void, String> {


        @Override
        protected String doInBackground(Void... params) {
            String resultEnd = "";
            String[] allNames = MainActivity.this.allNames;
            String stockName = allNames[0];
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder().url("https://financialmodelingprep.com/api/v3/historical-price-full/" + stockName + "?timeseries=1&apikey=07fef516f7504f5b2781c5c58b75a63d").build();


            try {
                Response response = client.newCall(request).execute();
                String responseString = response.body().string();
                ArrayList<Double> priceList1 = extractPrices(responseString);
                ArrayList<String> dateList1 = extractDates(responseString, "day");
                String timeInterval = "1min";
                StockModel s1 = new StockModel(stockName, priceList1, dateList1, timeInterval);


                temp11 += "|||" + s1.name + " ? " + s1.priceList + " ? " + s1.dateList + " ? " +/* s1.analysis */ " ? " + s1.gainLossPercent + " ? " + s1.timeInterval + "|||" + "\n";
            } catch (IOException | ParseException e) {
                // return null;
            }

            return resultEnd;
        }

        @SuppressLint("SetTextI18n")
        @Override
        protected void onPostExecute(String result) {
            // Update the UI with the result
//            TextView textView = findViewById(R.id.txt1);
//            textView.setText(textView.getText() + String.valueOf(result));

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
        return priceList;
    }


    public ArrayList<String> extractDates(String dataTaker, String timeInterval) throws ParseException {
        String saveString = "";
        int count = 0;
        int tempIndex = 0;

        ArrayList<String> dateList = new ArrayList<>();
        saveString = ""; //!!IMPORTANT TO RESET THE SAVESTRING!!!!!!!!!!
        tempIndex = dataTaker.indexOf("date");
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
        return dateList;
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

    public void do4hour(View view) {
        TextView textView = findViewById(R.id.txt1);
        textView.setText("before");
//        for (int i = 0; i < 35; i++) {
//            docData.put("" + i, i);
//            db.collection("Trades").document("stockInfo").collection("allStocks").document("" + i).set(docData, SetOptions.merge());
//        }

//        docData.put("newString", "httpshhttps://console.firebase.google.com/u/0/project/bank-rehovot/firestore/data/~2FTrades~2FstockInfo~2FallStocks~2F15https://console.firebase.google.com/u/0/project/bank-rehovot/firestore/data/~2FTrades~2FstockInfo~2FallStocks~2F15https://console.firebase.google.com/u/0/project/bank-rehovot/firestore/data/~2FTrades~2FstockInfo~2FallStocks~2F15https://console.firebase.google.com/u/0/project/bank-rehovot/firestore/data/~2FTrades~2FstockInfo~2FallStocks~2F15https://console.firebase.google.com/u/0/project/bank-rehovot/firestore/data/~2FTrades~2FstockInfo~2FallStocks~2F15ttps://console.firebase.google.com/u/0/project/bank-rehovot/firestore/data/~2FTrades~2FstockInfo~2FallStocks~2F15//console.firebase.google.com/u/0/project/bank-rehovot/firestore/data/~2FTrades~2FstockInfo~2FallStocks~2F15https://console.firebase.google.com/u/0/project/bank-rehovot/firestore/data/~2FTrades~2FstockInfo~2FallStocks~2F15https://console.firebase.google.com/u/0/project/bank-rehovot/firestore/data/~2FTrades~2FstockInfo~2FallStocks~2F15https://console.firebase.google.com/u/0/project/bank-rehovot/firestore/data/~2FTrades~2FstockInfo~2FallStocks~2F15https://console.firebase.google.com/u/0/project/bank-rehovot/firestore/data/~2FTrades~2FstockInfo~2FallStocks~2F15https://console.firebase.google.com/u/0/project/bank-rehovot/firestore/data/~2FTrades~2FstockInfo~2FallStocks~2F15https://console.firebase.google.com/u/0/project/bank-rehovot/firestore/data/~2FTrades~2FstockInfo~2FallStocks~2F15https://console.firebase.google.com/u/0/project/bank-rehovot/firestore/data/~2FTrades~2FstockInfo~2FallStocks~2F15https://console.firebase.google.com/u/0/project/bank-rehovot/firestore/data/~2FTrades~2FstockInfo~2FallStocks~2F15https://console.firebase.google.com/u/0/project/bank-rehovot/firestore/data/~2FTrades~2FstockInfo~2FallStocks~2F15");
//        db.collection("Trades").document("stockInfo").collection("allStocks").document("" + "newString").set(docData, SetOptions.merge());

        db.collection("Trades").document("stockInfo").collection("allStocks").document("" + "newString").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    String uploaderTaker = String.valueOf(document.get("newString"));
                    SharedPreferences sharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE);
                    SharedPreferences.Editor myEdit = sharedPreferences.edit();
                    myEdit.putString("newString", uploaderTaker);
                    myEdit.apply();
                }
            }
        });
        SharedPreferences sharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE);
        String taker1 = sharedPreferences.getString("newString", "");


        textView.setText("after");


    }

    //end of main
}