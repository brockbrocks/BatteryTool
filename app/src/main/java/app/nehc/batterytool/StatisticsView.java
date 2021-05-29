package app.nehc.batterytool;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;

public class StatisticsView extends View {
    private int width;
    private int height;
    private float fontSize;

    public StatisticsView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.StatisticsView);
        fontSize = typedArray.getDimension(R.styleable.StatisticsView_fontSize, 0);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //
        width = getWidth();
        height = getHeight();
        //
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStrokeWidth(1);
        //
        paint.setTextSize(fontSize);
        paint.setTextAlign(Paint.Align.RIGHT);
        for (int i = 0; i <= 10; i += 2) {
            float cy = height * (i / 10.f) + fontSize;
            paint.setARGB(180, 0, 0, 0);
            canvas.drawText(10 - i + "0", fontSize * 5 / 3, cy, paint);
            paint.setARGB(255, 230, 230, 230);
            canvas.drawLine(fontSize * 2, cy - fontSize / 3, width, cy - fontSize / 3, paint);
        }
        paint.setARGB(200, 141, 185, 244);
        paint.setStrokeWidth(4);
        canvas.drawLine(fontSize * 2, height, width, height, paint);
    }

}
