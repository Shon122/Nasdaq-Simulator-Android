package com.gtappdevelopers.bankrehovot;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import java.util.ArrayList;

public class GraphView extends View {
    private ArrayList<Double> prices;
    private int width;
    private int height;
    private Paint paint;

    public GraphView(Context context, AttributeSet attrs) {
        super(context, attrs);
        prices = new ArrayList<>();
        paint = new Paint();
        paint.setColor(Color.BLUE);
        paint.setStrokeWidth(5);
    }

    public void setPrices(ArrayList<Double> prices) {
        this.prices = prices;
        invalidate();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width = w;
        height = h;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (prices.size() < 2) {
            return;
        }

        double maxPrice = prices.get(0);
        double minPrice = prices.get(0);

        for (double price : prices) {
            if (price > maxPrice) {
                maxPrice = price;
            }
            if (price < minPrice) {
                minPrice = price;
            }
        }

        double priceRange = maxPrice - minPrice;
        int numPoints = prices.size();
        double xStep = (double) width / (numPoints - 1);
        double yStep = (double) height / priceRange;

        for (int i = 0; i < numPoints - 1; i++) {
            double x1 = i * xStep;
            double y1 = (maxPrice - prices.get(i)) * yStep;
            double x2 = (i + 1) * xStep;
            double y2 = (maxPrice - prices.get(i + 1)) * yStep;

            canvas.drawLine((float) x1, (float) y1, (float) x2, (float) y2, paint);
        }
    }
}