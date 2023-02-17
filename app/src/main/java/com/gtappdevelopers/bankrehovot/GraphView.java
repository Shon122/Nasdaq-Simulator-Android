package com.gtappdevelopers.bankrehovot;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import java.util.ArrayList;

public class GraphView extends View {
    private Paint paint;
    private Path path;
    private ArrayList<Float> points;

    public GraphView(Context context) {
        super(context);
        init();
    }

    public GraphView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public GraphView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        paint = new Paint();
        paint.setColor(Color.RED);
        paint.setStrokeWidth(5f);
        paint.setStyle(Paint.Style.STROKE);
        path = new Path();
    }

    public void setPrices(ArrayList<Double> prices) {
        points = new ArrayList<>();
        for (Double price : prices) {
            points.add(price.floatValue());
        }
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int width = getWidth();
        int height = getHeight();
        if (points != null) {
            for (int i = 0; i < points.size(); i++) {
                float x;
                if (points.size() - 1 == 0)
                    x = width * i / 1;
                else
                    x = width * i / (points.size() - 1);
                float y = height - (points.get(i) * height / 100);
                if (i == 0) {
                    path.moveTo(x, y);
                } else {
                    path.lineTo(x, y);
                }
            }
            canvas.drawPath(path, paint);
        }
    }
}