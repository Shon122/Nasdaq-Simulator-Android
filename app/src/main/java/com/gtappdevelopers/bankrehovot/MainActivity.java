package com.gtappdevelopers.bankrehovot;

import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
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

    private static final String TAG = "FinancialDataDownloader";
    private static final String STOCKS_API = "pk_e5565284d93b4484a43fc6765f5bc6dd";
    private static final String STOCKS_URL = "https://cloud.iexapis.com/stable/stock/";
    public static String BPI_ENDPOINT = "https://financialmodelingprep.com/api/v3/historical-chart/1min/AAPL?apikey=" + "c42711901b00e79841bb71702345719e";
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


        // get price of btc
        load();

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
//
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

        TextView textView = findViewById(R.id.txt2);
        textView.setText(body);
//
//        try {
//            StringBuilder builder = new StringBuilder();
//
//            JSONObject jsonObject = new JSONObject(body);
//            JSONObject timeObject = jsonObject.getJSONObject("time");
//            builder.append(timeObject.getString("updated")).append("\n\n");
//
////            JSONObject bpiObject = jsonObject.getJSONObject("bpi");
////            JSONObject usdObject = bpiObject.getJSONObject("USD");
////            builder.append(usdObject.getString("rate")).append("$").append("\n");
////
////            JSONObject gbpObject = bpiObject.getJSONObject("GBP");
////            builder.append(gbpObject.getString("rate")).append("£").append("\n");
////
////            JSONObject euroObject = bpiObject.getJSONObject("EUR");
////            builder.append(euroObject.getString("rate")).append("€").append("\n");
//
//
//            JSONObject bpiObject = jsonObject.getJSONObject("bpi");
//            builder.append(bpiObject);
//
//            TextView textView = findViewById(R.id.txt2);
//            textView.setText(builder.toString());
//
//
//        } catch (Exception e) {
//
//        }
    }


    //end of main
}