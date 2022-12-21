package com.gtappdevelopers.bankrehovot;

import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
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
    String priceTaker = "price307";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //create a trade
//        Date c = Calendar.getInstance().getTime();
//        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
//        String formattedDate = df.format(c); //the string result is like "2022-12-10"
//        Trade trade1 = new Trade(formattedDate, "MSFT", 3561.0, 3561.0,
//                100.0, 3000.0, 4000.0, true);
//        String test = trade1.limitProfit.toString();
//        TextView textView = findViewById(R.id.txt1);
//        textView.setText(test);


        //this sets data
        //  setData();

        //this gets data
        //getData(); // got in in 'dataTaker' string

        //show it in chart
        //showGraph();


        //// get price of btc
        load();
//        TextView textView = findViewById(R.id.txt2);
//        String info = (String) textView.getText();
//        textView.setVisibility(View.INVISIBLE);
//
//        TextView textView1 = findViewById(R.id.txt1);
//        textView1.setText(info);
//        int dateIndex = info.indexOf("date");
//        ArrayList<String> dates = new ArrayList<>();
//        dates.add("first");
//        while (dateIndex != -1 && dates.size()<10) {
//            int tempIndex = info.indexOf('"', dateIndex + 1);
//
//            tempIndex = info.indexOf('"', tempIndex + 1);
//            String take = info.substring(tempIndex, info.indexOf('"', dateIndex + 2));
//            dates.add(take);
//
//            dateIndex = info.indexOf("date", dateIndex);
//
//
//        }
//        textView.setText(dates.get(0));


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

    public void setData() {
        docData.put("price", "1234");
        db.collection("Trades").add(docData); // this line creates a new document
        //
        //this will change a specific field inside an existing document
        docData.put("price", "111");
        db.collection("Trades").document("Prices").set(docData, SetOptions.merge());


    }

    public void getData() {
        db.collection("Trades").document("Prices").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();

                    TextView textView = findViewById(R.id.txt1);
                    textView.setText(document.getData() + "s");
                    //now taking it back to string type
                    dataTaker = textView.getText().toString(); //got it in string form
                    //and now reset the text view so it doesnt bother anything
                    textView.setText("");
                }
            }
        });


    }

    private void load() {
        //BPI_ENDPOINT = "https://api.coindesk.com/v1/bpi/historical/close.json?start=2019-08-10&end=2019-08-15&currency=zar";


        Request request = new Request.Builder().url(BPI_ENDPOINT).build();


        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response)
                    throws IOException {
                final String body = response.body().string();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        parseBpiResponse(body);
                    }
                });
            }
        });

    }

    private void parseBpiResponse(String body) {
        //get prices and dates and upload to firebase
        ArrayList<String> dates = new ArrayList<>();
        ArrayList<String> prices = new ArrayList<>();
        String saveString="";

        // TextView textView = findViewById(R.id.txt2);
        //textView.setText(body.substring(dateIndex+9,dateIndex+9+11+8));
        int tempIndex = body.indexOf("date");
        while (tempIndex != -1) {
            dates.add(body.substring(tempIndex + 9, tempIndex + 9 + 11 + 8));
            saveString+=dates.get(dates.size()-1);
            saveString+= ",";
            tempIndex = body.indexOf("date",tempIndex+1);
        }
        saveString=saveString.replaceAll("(\\r|\\n)", "");
        docData.put("dates", saveString);
        db.collection("Trades").document("Prices").set(docData, SetOptions.merge());

        //after we got dates now we get prices
        saveString="";
        tempIndex = body.indexOf("close");
        while (tempIndex != -1) {
            prices.add(body.substring(tempIndex + 9, tempIndex + 9 + 8));
            saveString+=prices.get(prices.size()-1);
            saveString+= ",";
            tempIndex = body.indexOf("close",tempIndex+1);
        }
        saveString=saveString.replaceAll("(\\r|\\n)", "");
        docData.put("prices", saveString);
        db.collection("Trades").document("Prices").set(docData, SetOptions.merge());




    }


    //end of main
}