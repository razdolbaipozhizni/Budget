package com.example.myapplication;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import androidx.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class PieChartView extends View {
    private List<PieEntry> entries = new ArrayList<>();
    private Paint paint = new Paint();
    private RectF rectF;

    public PieChartView(Context context) {
        super(context);
        init(null);
    }

    public PieChartView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public PieChartView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(@Nullable AttributeSet set) {
        paint.setAntiAlias(true);
        rectF = new RectF();
    }

    public void setEntries(List<PieEntry> entries) {
        this.entries = entries;
        invalidate(); // Перерисовываем View при изменении данных
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (entries.isEmpty()) return;

        float total = 0;
        for (PieEntry entry : entries) {
            total += entry.getValue();
        }

        int width = getWidth();
        int height = getHeight();
        int minSize = Math.min(width, height);
        float radius = minSize / 2f * 0.8f; // Уменьшаем радиус для отступов

        rectF.set(width / 2 - radius, height / 2 - radius, width / 2 + radius, height / 2 + radius);

        float currentAngle = 0;
        for (PieEntry entry : entries) {
            float sweepAngle = (entry.getValue() / total) * 360;
            paint.setColor(entry.getColor());
            canvas.drawArc(rectF, currentAngle, sweepAngle, true, paint);
            currentAngle += sweepAngle;
        }
    }

    public static class PieEntry {
        private final String label;
        private final float value;
        private final int color;

        public PieEntry(String label, float value, int color) {
            this.label = label;
            this.value = value;
            this.color = color;
        }

        public String getLabel() {
            return label;
        }

        public float getValue() {
            return value;
        }

        public int getColor() {
            return color;
        }
    }
}