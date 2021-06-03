package app.nehc.batterytool;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import app.nehc.batterytool.bean.BatteryStatsBean;
import app.nehc.batterytool.utils.DBUtil;

public class StatisticsView extends View {
    private Context context;
    //
    private int width;
    private int height;
    private float fontSize;
    //
    private List<BatteryStatsBean> statsDataList = new ArrayList<>();
    //
    private int showViewType;
    public static final int TYPE_01 = 0;
    public static final int TYPE_02 = 1;

    public StatisticsView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.StatisticsView);
        fontSize = typedArray.getDimension(R.styleable.StatisticsView_fontSize, 0);
        statsDataList.addAll(DBUtil.parseToStatsDataList());
    }

    public void setShowViewType(int showViewType) {
        this.showViewType = showViewType;
        postInvalidate();
    }


    public void setHeight(int newHeight) {
        height = newHeight;
        ViewGroup.LayoutParams layoutParams = getLayoutParams();
        layoutParams.height = newHeight;
        setLayoutParams(layoutParams);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //设置宽高
        width = getWidth();
        height = getHeight();
        //
        //确定边界
        Paint paint0 = new Paint();
        paint0.setARGB(0, 0, 0, 0);
        Rect rect = new Rect();
        rect.set(0, 0, width, height);
        canvas.drawRect(rect, paint0);
        //按所要展示的视图分类
        switch (showViewType) {
            case TYPE_01:
                drawTYPE_01(canvas);
                break;
            case TYPE_02:
                drawTYPE_02(canvas);
                break;
        }
    }

    private void drawTYPE_02(Canvas canvas) {
        //真正作图范围
        float boundHeight = height - fontSize - 5 - fontSize * 2;
        //虚线画笔
        Paint paint1 = new Paint();
        paint1.setAntiAlias(true);
        paint1.setStrokeWidth(1);
        paint1.setARGB(25, 0, 0, 0);
        //文字画笔
        Paint paint2 = new Paint();
        paint2.setAntiAlias(true);
        paint2.setTextSize(fontSize);
        paint2.setARGB(120, 0, 0, 0);
        //画坐标系
        for (int i = 0; i < 10; i += 2) {
            float textY = boundHeight / 10.0f * i + fontSize * 4 / 3;
            canvas.drawText((10 - i) + "0", 0, textY, paint2);
            float lineY = boundHeight / 10.0f * i + fontSize;
            canvas.drawLine(fontSize * 2, lineY, width, lineY, paint1);
        }
        //坐标底线
        Paint paint4 = new Paint();
        paint4.setAntiAlias(true);
        paint4.setARGB(200, 141, 185, 244);
        float lineWidth = 2;
        paint4.setStrokeWidth(lineWidth);
        canvas.drawLine(fontSize * 2, height - lineWidth / 2 - fontSize * 2, width, height - lineWidth / 2 - fontSize * 2, paint4);

        //test
        Paint paint5 = new Paint();
        paint5.setAntiAlias(true);
        paint5.setARGB(255, 28, 113, 227);

        float cy = 0;
        for (int i = 0; i <= 100; i++) {
            cy = boundHeight / 100.0f * i + fontSize;
            canvas.drawCircle(fontSize * 2, cy, 2, paint5);
            canvas.drawLine(fontSize * 2, cy, fontSize * 2 + 200, cy, paint5);
        }

    }

    private void drawTYPE_01(Canvas canvas) {
        //真正作图范围
        float boundHeight = height - fontSize - 5 - fontSize * 2;
        //虚线画笔
        Paint paint1 = new Paint();
        paint1.setAntiAlias(true);
        paint1.setStrokeWidth(1);
        paint1.setARGB(25, 0, 0, 0);
        //文字画笔
        Paint paint2 = new Paint();
        paint2.setAntiAlias(true);
        paint2.setTextSize(fontSize);
        paint2.setARGB(120, 0, 0, 0);
        //画坐标系
        for (int i = 0; i < 10; i += 2) {
            float textY = boundHeight / 10.0f * i + fontSize * 4 / 3;
            canvas.drawText((10 - i) + "0", 0, textY, paint2);
            float lineY = boundHeight / 10.0f * i + fontSize;
            canvas.drawLine(fontSize * 2, lineY, width, lineY, paint1);
        }
        //----------------------------------------------------------
        //描点画笔
        Paint paint3 = new Paint();
        paint3.setAntiAlias(true);
        paint3.setARGB(255, 28, 113, 227);
        paint3.setStrokeWidth(8);
        //渐变线，path画笔
        Paint paint6 = new Paint();
        //path上边缘粗线画笔
        Paint paint7 = new Paint();
        paint7.setAntiAlias(true);
        paint7.setARGB(255, 28, 113, 227);
        paint7.setStyle(Paint.Style.STROKE);
        paint7.setStrokeJoin(Paint.Join.ROUND);
        paint7.setStrokeWidth(8);
        //画笔设置渐变color
        int[] colorList = new int[]{0x851C71E3, 0x108DB9F4};
        LinearGradient linearGradient = new LinearGradient(0, 0, 0, height, colorList, null, Shader.TileMode.CLAMP);
        paint6.setShader(linearGradient);
        //描点
        float X = fontSize * 2;
        float Y = boundHeight / 100.0f * (100 - statsDataList.get(0).getCapacity()) + fontSize;
        float lastY = 0;
        Path path = new Path();
        Path path1 = new Path();
        path.setLastPoint(X, Y);
        path1.setLastPoint(X, Y);
        canvas.drawCircle(X, Y, 4, paint3);//开端的圆点
        //path上边界加粗
        float pointY = 0;
        float pointX = 0;
        for (int i = 0; i < statsDataList.size(); i++) {
            int batteryCapacity = statsDataList.get(i).getCapacity();
            long timeStamp = statsDataList.get(i).getTimeStamp();
            pointY = boundHeight / 100.0f * (100 - batteryCapacity) + fontSize;
            pointX = (width - fontSize * 2) * (timeStamp - statsDataList.get(0).getTimeStamp()) / 43200000.0f + fontSize * 2;
            //渐变线
            path.lineTo(pointX, pointY);
            path1.lineTo(pointX, pointY);
            lastY = pointY;
        }
        path.rLineTo(0, (height - fontSize * 2) - lastY);
        path.lineTo(fontSize * 2, (height - fontSize * 2));
        canvas.drawPath(path, paint6);
        canvas.drawPath(path1, paint7);
        canvas.drawCircle(pointX, pointY, 4, paint3);//结束端的圆点
        //坐标底线
        Paint paint4 = new Paint();
        paint4.setAntiAlias(true);
        paint4.setARGB(200, 141, 185, 244);
        float lineWidth = 2;
        paint4.setStrokeWidth(lineWidth);
        canvas.drawLine(fontSize * 2, height - lineWidth / 2 - fontSize * 2, width, height - lineWidth / 2 - fontSize * 2, paint4);

        //底部时间画笔
        Paint paint5 = new Paint();
        paint5.setAntiAlias(true);
        paint5.setARGB(120, 0, 0, 0);
        paint5.setTextSize(fontSize);
        paint5.setTextAlign(Paint.Align.CENTER);
        for (int i = 0; i < 6; i++) {
            String timeStr = new SimpleDateFormat("HH:mm").format(new Date(statsDataList.get(0).getTimeStamp() + i * 7200000));
            float timeStrX = (width - fontSize * 2) / 6.0f * i + fontSize * 2;
            canvas.drawText(timeStr, timeStrX, height - 4, paint5);
            canvas.drawLine(timeStrX, height - lineWidth / 2 - fontSize * 2, timeStrX, height - lineWidth / 2 - fontSize * 2 - 10, paint4);
        }
        canvas.drawText("...", width - fontSize / 3, height - fontSize / 3 - lineWidth / 2, paint5);
    }

}
