package com.gtappdevelopers.bankrehovot;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class GraphActivity extends AppCompatActivity {

    ArrayList<Double> priceList = MainActivity.viewingStock.priceList;
    ArrayList<String> dateList = MainActivity.viewingStock.dateList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);
        GraphView graph = (GraphView) findViewById(R.id.graph);
        if (priceList.size() > 0 && dateList.size() > 0) {
            ArrayList<String> sortedDateList = sortDateList(dateList);
            DataPoint[] dataPoints = new DataPoint[priceList.size()];
            SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            for (int i = 0; i < priceList.size(); i++) {
                try {
                    Date date = format.parse(sortedDateList.get(i));
                    dataPoints[i] = new DataPoint(date, priceList.get(priceList.size() - 1 - i));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            LineGraphSeries<DataPoint> series = new LineGraphSeries<>(dataPoints);
            graph.addSeries(series);
            graph.getViewport().setScalable(true);
            graph.getViewport().setScalableY(true);
            graph.getViewport().setScrollable(true);
            graph.getViewport().setScrollableY(true);
            graph.getViewport().setXAxisBoundsManual(true);
            graph.getViewport().setMinX(dataPoints[0].getX());
            graph.getViewport().setMaxX(dataPoints[dataPoints.length - dataPoints.length/2].getX());

        }


    }


    private ArrayList<String> sortDateList(ArrayList<String> dateList1) {
        List<Date> dateListDate = new ArrayList<>();
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        for (String date : dateList1) {
            try {
                dateListDate.add(format.parse(date));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        Collections.sort(dateListDate);
        dateList1.clear();
        for (Date date : dateListDate) {
            dateList1.add(format.format(date));
        }
        return dateList1;
    }
}