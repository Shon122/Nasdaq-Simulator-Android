package com.gtappdevelopers.bankrehovot;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.*;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Pie;

import java.util.ArrayList;
import java.util.List;
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

        Pie pie = AnyChart.pie();

        List<DataEntry> data = new ArrayList<>();
        data.add(new ValueDataEntry("John", 10000));
        data.add(new ValueDataEntry("Jake", 12000));
        data.add(new ValueDataEntry("Peter", 18000));

        pie.data(data);

        AnyChartView anyChartView = (AnyChartView) findViewById(R.id.any_chart_view);
        anyChartView.setChart(pie);



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