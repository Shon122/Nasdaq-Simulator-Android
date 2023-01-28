package com.gtappdevelopers.bankrehovot;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Handler;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.util.Random;

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.charts.Cartesian;
import com.anychart.core.Chart;
import com.anychart.core.cartesian.series.Line;
import com.anychart.data.Mapping;
import com.anychart.data.Set;
import com.anychart.enums.Anchor;
import com.anychart.enums.MarkerType;
import com.anychart.enums.TooltipPositionMode;
import com.anychart.graphics.vector.Stroke;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class StockGame extends AppCompatActivity {
    private static final int NUM_PRICES = 100;
    private static final int UPDATE_INTERVAL = 2000; // 2 seconds

    ArrayList<Double> reversedPriceList;
    ArrayList<String> reversedDateList;

    private ChartView mChartView;
    private float[] mPrices;
    private Random mRandom;
    private Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.stock_game);
//        reversedPriceList = MainActivity.viewingStock.priceList;
//        reversedDateList = MainActivity.viewingStock.dateList;
//
//        Collections.reverse(reversedDateList);
//        Collections.reverse(reversedPriceList);
        // Initialize buttons and set onClickListeners
        Button buyButton = findViewById(R.id.buy_button);
        buyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // buy();
            }
        });
        Button sellButton = findViewById(R.id.sell_button);
        sellButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //  sell();
            }
        });


        mPrices = new float[NUM_PRICES];
        mRandom = new Random();
        mHandler = new Handler();

        for (int i = 0; i < NUM_PRICES; i++) {
            mPrices[i] = mRandom.nextFloat() * 100;
        }

        mHandler.post(new Runnable() {
            @Override
            public void run() {
                updatePrices();
                mChartView.invalidate();
                mHandler.postDelayed(this, UPDATE_INTERVAL);
            }
        });
    }


    private void updatePrices() {
        for (int i = 0; i < NUM_PRICES - 1; i++) {
            mPrices[i] = mPrices[i + 1];
        }
        mPrices[NUM_PRICES - 1] = mRandom.nextFloat() * 100;
    }

    public class ChartView extends View {

        private Paint mPaint;

        public ChartView(Context context) {
            super(context);
            mPaint = new Paint();
            mPaint.setColor(Color.BLUE);
            mPaint.setStrokeWidth(3);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);

            float width = getWidth();
            float height = getHeight();

            for (int i = 0; i < NUM_PRICES - 1; i++) {
                float x1 = (i /




                (float) NUM_PRICES) * width;
                float y1 = height - (mPrices[i] / 100) * height;
                float x2 = ((i + 1) / (float) NUM_PRICES) * width;
                float y2 = height - (mPrices[i + 1] / 100) * height;
                canvas.drawLine(x1, y1, x2, y2, mPaint);
            }
        }
    }

}