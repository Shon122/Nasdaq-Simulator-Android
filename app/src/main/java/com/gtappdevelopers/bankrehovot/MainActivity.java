package com.gtappdevelopers.bankrehovot;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.TimeZone;
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
    String dataTaker = "s";
    final String[] priceTaker = new String[1];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        try {
            InfoAll infoAll = new InfoAll(this);
            infoAll.updateAllPrices();
//            //StockModel[] stockModels = new StockModel[infoAll.allNames.length];
//            String currentName = infoAll.allNames[5];
//            StockModel stockModel = infoAll.getIndividualData(currentName);
//            //stockModels[1] = stockModel;
//
//
//            TextView textView = findViewById(R.id.txt1);
//            textView.setText(String.valueOf(stockModel.analysis));
//
//
//         Thread.sleep(10000);
//            infoAll = new InfoAll(this);
//            currentName = infoAll.allNames[5];
//            stockModel = infoAll.getIndividualData(currentName);
//            textView.setText(String.valueOf(stockModel.analysis));


        } catch (ParseException | InterruptedException e) {
            e.printStackTrace();
        }


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
        }, 3000);
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

    //end of main
}