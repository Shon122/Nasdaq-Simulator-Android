package com.gtappdevelopers.bankrehovot;

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
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {


    public static final String BPI_ENDPOINT = "https://api.coindesk.com/v1/bpi/currentprice.json";
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
//        Date date = new Date();
//        Timestamp timestamp = (Timestamp) date;
//        Trade trade1 = new Trade(timestamp, "MSFT", 3561, 3561,
//                100, 3000, 4000, true);
//        String test = trade1.limitProfit;
//


        //this sets data
        //  setData();

        //this gets data
        //getData(); // got in in 'dataTaker' string


        //show it in chart
        //showGraph();


        //get price of btc
        //load();

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
        Request request = new Request.Builder()
                .url(BPI_ENDPOINT)
                .build();


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
        try {
            StringBuilder builder = new StringBuilder();

            JSONObject jsonObject = new JSONObject(body);
            JSONObject timeObject = jsonObject.getJSONObject("time");
            builder.append(timeObject.getString("updated")).append("\n\n");

            JSONObject bpiObject = jsonObject.getJSONObject("bpi");
            JSONObject usdObject = bpiObject.getJSONObject("USD");
            builder.append(usdObject.getString("rate")).append("$").append("\n");

            JSONObject gbpObject = bpiObject.getJSONObject("GBP");
            builder.append(gbpObject.getString("rate")).append("£").append("\n");

            JSONObject euroObject = bpiObject.getJSONObject("EUR");
            builder.append(euroObject.getString("rate")).append("€").append("\n");
            TextView textView = findViewById(R.id.txt2);
            textView.setText(builder.toString());


        } catch (Exception e) {

        }
    }

    //end of main
}