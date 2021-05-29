package app.nehc.batterytool;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

public class CirclePercentView extends View {
    private float circleWeight;
    private float percentTextSize;
    private int percent;

    public CirclePercentView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CirclePercentView);
        circleWeight = typedArray.getDimension(R.styleable.CirclePercentView_circleWeight, 0);
        percent = (int) typedArray.getFloat(R.styleable.CirclePercentView_circlePercent, 0);
        percentTextSize = typedArray.getDimension(R.styleable.CirclePercentView_percentTextSize, 50);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float toAngle = percent * 3.6f;
        float w = getWidth();
        float h = getHeight();
        float r = (w - circleWeight) / 2;
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(circleWeight);
        paint.setARGB(255, 181,201,232);
        canvas.drawCircle(w / 2, h / 2, r, paint);
        //
        RectF rectF = new RectF(circleWeight / 2, circleWeight / 2, w - circleWeight / 2, w - circleWeight / 2);
//        paint.setARGB(255, 0,87,208);
        paint.setARGB(255, 28,115,212);
        canvas.drawArc(rectF, -90, -toAngle, false, paint);
        //0处圆圈
        paint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(w / 2, circleWeight / 2, circleWeight / 2, paint);
        //end圆圈
        float c = r;
        float a = (float) (Math.sin(Math.toRadians(toAngle)) * c);
        float b = (float) (Math.cos(Math.toRadians(toAngle)) * c);
        canvas.drawCircle(r - a + circleWeight / 2, r - b + circleWeight / 2, circleWeight / 2, paint);
        //百分比数字
        paint.setColor(Color.BLACK);
        paint.setTextSize(percentTextSize);
        paint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText("" + percent + "%", w / 2+percentTextSize/4, w / 2 - percentTextSize / 4 + circleWeight, paint);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        heightMeasureSpec = widthMeasureSpec;
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    public void setPercent(int percent) {
        this.percent = percent;
        postInvalidate();
    }

    public void setCircleWeight(float circleWeight) {
        this.circleWeight = circleWeight;
        postInvalidate();
    }
}
