package com.harsha.eevee;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.RelativeLayout;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class TimeDivisions extends RelativeLayout {

    private Paint divisions = new Paint();
    private Paint text = new Paint();

    public TimeDivisions(Context context) {
        super(context);
        this.setWillNotDraw(false);
    }

    public TimeDivisions(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setWillNotDraw(false);
    }

    public TimeDivisions(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.setWillNotDraw(false);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public TimeDivisions(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.setWillNotDraw(false);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Log.i("yash", "drawing");

        int ViewWidth = getWidth();
        Log.i("yash", String.valueOf(ViewWidth));

        int timeBwDiv = 30;//min
        float pxDiffFloat = (timeBwDiv * Constants.SCALE);
        int pxDiffInt = (int) pxDiffFloat;

        int noDiv = (int) (ViewWidth / pxDiffFloat) + 3;

        divisions.setAntiAlias(true);
        text.setAntiAlias(true);

        divisions.setColor(Color.BLACK);
        text.setColor(Color.BLACK);

        int textSize = Constants.TIME_TICKER_TEXT_SIZE;
        text.setTextSize(textSize);

        int x = 0;
        int verticalLineWidth = 1; // in px

        int timeFromStartToAPointWithSign = -Constants.EVENT.PAST_DISPLAY_TIME_MIN;
        String day;
        String timeAtAPoint;

        for (int i = 0; i < noDiv; i++) {
            Rect TimeDivision = new Rect(x, 2 * textSize, x + verticalLineWidth, getHeight());
            day = getDayInString(timeFromStartToAPointWithSign);
            timeAtAPoint = getTimeInString(timeFromStartToAPointWithSign);

            canvas.drawRect(TimeDivision, divisions);
            canvas.drawText(day, x, textSize, text);
            canvas.drawText(timeAtAPoint, x - (pxDiffInt) / 4, 2 * textSize, text);

            timeFromStartToAPointWithSign += timeBwDiv;
            x = x + pxDiffInt;
        }
    }

    private String getDayInString(int timeFromNow) {
        Calendar currentTime = Calendar.getInstance();
        int YEAR = currentTime.get(Calendar.YEAR);
        int MONTH = currentTime.get(Calendar.MONTH) + 1;
        int DAY = currentTime.get(Calendar.DAY_OF_MONTH);
        int HOUR = currentTime.get(Calendar.HOUR_OF_DAY);
        int MIN = currentTime.get(Calendar.MINUTE);

        DateTime reqDT = new DateTime(YEAR, MONTH, DAY, HOUR, MIN);
        reqDT = DateTime.getDateTime(reqDT, timeFromNow);

        SimpleDateFormat sdf = new SimpleDateFormat("EE");
        Date d = new Date(reqDT.YEAR - 1900, reqDT.MONTH - 1, reqDT.DAY);
        String dayOfTheWeek = sdf.format(d);

        return dayOfTheWeek;
    }

    private String getTimeInString(int timeFromNow) {
        Calendar currentTime = Calendar.getInstance();
        int HOUR = currentTime.get(Calendar.HOUR_OF_DAY);
        int MINUTE = currentTime.get(Calendar.MINUTE);

        int sum = (DateTime.MINS_IN_HOUR * HOUR) + MINUTE + timeFromNow;

        if (sum < 0) {
            sum = sum % DateTime.MINS_IN_DAY;
            if (sum != 0) {
                sum = sum + DateTime.MINS_IN_DAY;
            }
        } else if (sum >= DateTime.MINS_IN_DAY) {
            sum = sum % DateTime.MINS_IN_DAY;
        }

        TimeInput reqTime = new TimeInput();
        reqTime.selectedHour = sum / DateTime.MINS_IN_HOUR;
        reqTime.selectedMin = sum % DateTime.MINS_IN_HOUR;

        return reqTime.timeDispString();
    }
}