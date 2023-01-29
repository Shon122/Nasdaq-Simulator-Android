package com.gtappdevelopers.bankrehovot;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class GraphView extends View {
    private float price;
    private List<Float> prices = new ArrayList<>();
    private Paint paint = new Paint();

    public GraphView(Context context) {
        super(context);
    }

    public GraphView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public GraphView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setPrice(float price) {
        this.price = price;
        prices.add(price);
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        paint.setColor(Color.GREEN);
        paint.setStrokeWidth(5);

        float x = getWidth();
        float y = getHeight();
        float xIncrement = x/prices.size();
        float yScale = y/100;

        for (int i = 0; i < prices.size(); i++) {
            if (i == 0) {
                canvas.drawPoint(i * xIncrement, y - (prices.get(i) * yScale), paint);
            } else {
                canvas.drawLine(
                        (i - 1) * xIncrement,
                        y - (prices.get(i - 1) * yScale),
                        i * xIncrement,
                        y - (prices.get(i) * yScale),
                        paint
                );
            }
        }
    }
}