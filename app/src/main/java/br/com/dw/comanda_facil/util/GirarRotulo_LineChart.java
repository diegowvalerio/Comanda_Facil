package br.com.dw.comanda_facil.util;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;

import com.github.mikephil.charting.animation.ChartAnimator;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.interfaces.dataprovider.LineDataProvider;
import com.github.mikephil.charting.renderer.LineChartRenderer;
import com.github.mikephil.charting.utils.MPPointF;
import com.github.mikephil.charting.utils.Utils;
import com.github.mikephil.charting.utils.ViewPortHandler;

public class GirarRotulo_LineChart extends LineChartRenderer {

        public GirarRotulo_LineChart(LineDataProvider chart, ChartAnimator animator, ViewPortHandler viewPortHandler) {
            super(chart, animator, viewPortHandler);
        }

        @Override
        public void drawValue(Canvas c, String valueText, float x, float y, int color) {
           mValuePaint.setColor(color);
            //c.drawText(valueText, x, y, mValuePaint);
            Paint paint = super.mDrawPaint;
            paint.setTextSize(22f);
            paint.setTextAlign(Paint.Align.CENTER);
            paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
            Utils.drawXAxisValue(c, valueText + "", x, y, paint, MPPointF.getInstance(), -90);
        }
}

