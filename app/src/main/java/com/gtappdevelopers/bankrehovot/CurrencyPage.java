package com.gtappdevelopers.bankrehovot;

import android.os.Bundle;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import com.jjoe64.graphview.series.BarGraphSeries;
import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class CurrencyPage extends AppCompatActivity {


    ArrayList<Double> priceList = MainActivity.viewingStock.priceList;
    ArrayList<String> dateList = MainActivity.viewingStock.dateList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.currency_page);

        // Create a new graph
        GraphView graph = (GraphView) findViewById(R.id.graph);


        // check if the size of both lists greater than 0
        if (priceList.size() > 0 && dateList.size() > 0) {
            // sort the dateList by date
            ArrayList<String> sortedDateList = sortDateList(dateList);

            // Create a new series with data points
            DataPoint[] dataPoints = new DataPoint[priceList.size()];
            SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            for (int i = 0; i < priceList.size(); i++) {
                try {
                    Date date = format.parse(dateList.get(i));
                    dataPoints[i] = new DataPoint(date, priceList.get(priceList.size() - 1 - i));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            LineGraphSeries<DataPoint> series = new LineGraphSeries<>(dataPoints);

            // Add the series to the graph
            graph.addSeries(series);

            // Enable panning and zooming
            graph.getViewport().setScalable(true);
            graph.getViewport().setScalableY(true);
            graph.getViewport().setScrollable(true);
            graph.getViewport().setScrollableY(true);

            // Set the viewport to only show a portion of the data at a time
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