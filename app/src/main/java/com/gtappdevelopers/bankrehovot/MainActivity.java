package com.gtappdevelopers.bankrehovot;

import android.content.Intent;
import android.os.Bundle;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;

import androidx.appcompat.app.AppCompatActivity;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;


public class MainActivity extends AppCompatActivity {
    //user essentials saving his data
    public static ArrayList<StockModel> stockModels = null;
    public static String news = "";
    public static String username = "";
    public static String password = "";
    public static ArrayList<Trade> trades = new ArrayList<>();
    public static ArrayList<User> users = new ArrayList<>();
    public static int currentUserIndex = 0;
    //


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firstLoad();
  //      switchIntent();
        //end of oncreate
    }

    public void switchIntent() {
        //before switich i put all the current user stats in mainactivity vars

        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    public void firstLoad() {
        try {
            InfoAll info1 = new InfoAll(this);
            info1.getAllStockModels("day");
            info1.updateNews();
          //  info1.updateUsersFirebase();
            users = info1.users;
            news = info1.news;
           // stockModels = (ArrayList<StockModel>) Arrays.asList(info1.stockModels);
        } catch (ParseException e) {
            e.printStackTrace();
        }


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


    //end of main
}