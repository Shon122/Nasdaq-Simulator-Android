package com.gtappdevelopers.bankrehovot;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.HighLowDataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Cartesian;
import com.anychart.charts.Stock;
import com.anychart.core.stock.Plot;
import com.anychart.data.Table;
import com.anychart.data.TableMapping;
import com.anychart.enums.StockSeriesType;

import java.util.ArrayList;
import java.util.List;

public class GraphActivity extends AppCompatActivity {

    ArrayList<Double> priceList = MainActivity.viewingStock.priceList;
    ArrayList<String> dateList = MainActivity.viewingStock.dateList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);

        AnyChartView anyChartView = findViewById(R.id.any_chart_view);

        Cartesian cartesian = AnyChart.line();
        Table table = Table.instantiate("x");
        table.addData(getData());

        TableMapping mapping = table.mapAs("{open: 'open', high: 'high', low: 'low', close: 'close'}");

        Stock stock = AnyChart.stock();

        Plot plot = stock.plot(0);
        plot.yGrid(true)
                .xGrid(true)
                .yMinorGrid(true)
                .xMinorGrid(true);

        plot.ema(table.mapAs("{value: 'close'}"), 20d, StockSeriesType.LINE);

        plot.ohlc(mapping)
                .name("CSCO")
                .legendItem("{\n" +
                        "        iconType: 'rising-falling'\n" +
                        "      }");

        stock.scroller().ohlc(mapping);

        anyChartView.setChart(stock);
    }

    private List<DataEntry> getData() {
        List<DataEntry> data = new ArrayList<>();
        for (int i = 0; i < priceList.size(); i++) {
            //from date to long
            long end=0;
          //  data.add(new CustomDataEntry(end, priceList.get(i), priceList.get(i), priceList.get(i), priceList.get(i)));
        }

        return data;
    }

    private class CustomDataEntry extends ValueDataEntry {

        CustomDataEntry(String x, Number value) {
            super(x, value);

        }

    }
}