package app.nehc.batterytool;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class StatisticsView extends View {
    private Context context;
    //
    private int width;
    private int height;
    private float fontSize;
    //
    private List<String> statsDataList = new ArrayList<>();
    private long headStatsTimeStamp;
    private String[] headStats;
    //
    private int showViewType;
    public static final int TYPE_01 = 0;
    public static final int TYPE_02 = 1;

    public StatisticsView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.StatisticsView);
        fontSize = typedArray.getDimension(R.styleable.StatisticsView_fontSize, 0);
        initStatsDataList();
    }

    public void setShowViewType(int showViewType) {
        this.showViewType = showViewType;
        postInvalidate();
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //
        width = getWidth();
        height = getHeight();
        //
        //确定边界
        Paint paint0 = new Paint();
        paint0.setARGB(0, 0, 0, 0);
        Rect rect = new Rect();
        rect.set(0, 0, width, height);
        canvas.drawRect(rect, paint0);
        //
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

    }

    private void initStatsDataList() {
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(context.openFileInput("stats_record")));
            String tmp;
            while ((tmp = br.readLine()) != null) {
                statsDataList.add(tmp);
            }
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        headStats = statsDataList.get(0).split(" ");
        headStatsTimeStamp = Long.valueOf(headStats[3]);
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
        paint2.setARGB(255, 0, 0, 0);
        //画坐标系
        for (int i = 0; i < 10; i += 2) {
            float textY = boundHeight / 10.0f * i + fontSize * 4 / 3;
            canvas.drawText((10 - i) + "0", 0, textY, paint2);
            float lineY = boundHeight / 10.0f * i + fontSize;
            canvas.drawLine(fontSize * 2, lineY, width, lineY, paint1);
        }
        //描点画笔
        Paint paint3 = new Paint();
        paint3.setAntiAlias(true);
        paint3.setARGB(255, 0, 0, 200);
        paint3.setStrokeWidth(8);
        //描点
        float lastPointY = boundHeight / 100.0f * (100 - Integer.valueOf(headStats[0])) + fontSize;
        float lastPointX = fontSize * 2;
        for (int i = 0; i < statsDataList.size(); i++) {
            String s = statsDataList.get(i);
            String[] str = s.split(" ");
            int batteryCapacity = Integer.valueOf(str[0]);
            long timeStamp = Long.valueOf(str[3]);
            float pointY = boundHeight / 100.0f * (100 - batteryCapacity) + fontSize;
            float pointX = (width - fontSize * 2) * (timeStamp - headStatsTimeStamp) / 43200000.0f + fontSize * 2;
            canvas.drawCircle(pointX, pointY, 4, paint3);
            canvas.drawLine(lastPointX, lastPointY, pointX, pointY, paint3);
            if (i < statsDataList.size() - 1) {
                lastPointX = pointX;
                lastPointY = pointY;
            }
        }

        //坐标底线
        Paint paint4 = new Paint();
        paint4.setAntiAlias(true);
        paint4.setARGB(200, 141, 185, 244);
        paint4.setStrokeWidth(2);
        canvas.drawLine(fontSize * 2, height - 1.0f - fontSize * 2, width, height - 1.0f - fontSize * 2, paint4);

        //底部时间画笔
        Paint paint5 = new Paint();
        paint5.setAntiAlias(true);
        paint5.setTextSize(fontSize);
        paint5.setTextAlign(Paint.Align.CENTER);
        for (int i = 0; i < 6; i++) {
            String timeStr = new SimpleDateFormat("HH:mm").format(new Date(headStatsTimeStamp + i * 7200000));
            float timeStrX = (width - fontSize * 2) / 6.0f * i + fontSize * 2;
            canvas.drawText(timeStr, timeStrX, height - 4, paint5);
            canvas.drawLine(timeStrX, height - 1.0f - fontSize * 2, timeStrX, height - 1.0f - fontSize * 2 - 10, paint4);
        }
        canvas.drawText("...", width - fontSize / 3, height - fontSize / 3 - 1.0f, paint5);
    }

}
