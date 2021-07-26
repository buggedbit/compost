package com.harsha.eevee;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import java.util.Vector;

public class Overlaps extends RelativeLayout {

    public Vector<DateTime> START = new Vector<>();
    public Vector<DateTime> END = new Vector<>();
    private Paint overlapPaint = new Paint();


    public Overlaps(Context context) {
        super(context);
        this.setWillNotDraw(false);
        initialize();
    }

    public Overlaps(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setWillNotDraw(false);
        initialize();
    }

    public Overlaps(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.setWillNotDraw(false);
        initialize();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public Overlaps(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.setWillNotDraw(false);
        initialize();
    }

    public void showOverlaps(Vector<DateTime> _START, Vector<DateTime> _END) {
        START = _START;
        END = _END;
        invalidate();
    }

    private void initialize() {
        overlapPaint.setAntiAlias(true);
        overlapPaint.setColor(Color.parseColor("#7bf42727"));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (START == null) return;

        int noOverLaps = START.size();

        DateTime present = new DateTime(true);

        for (int i = 0; i < noOverLaps; i++) {
            int DIFF_IN_MIN_LEFT_MARGIN = DateTime.differenceInMinWithSign(present, START.get(i));
            int LEFT_MARGIN = (int) ((DIFF_IN_MIN_LEFT_MARGIN + Constants.EVENT.PAST_DISPLAY_TIME_MIN) * Constants.SCALE);

            int DIFF_IN_MIN_WIDTH = DateTime.differenceInMinWithSign(START.get(i), END.get(i));
            int WIDTH = (int) (DIFF_IN_MIN_WIDTH * Constants.SCALE);

            Rect source = new Rect(LEFT_MARGIN, 0, LEFT_MARGIN + WIDTH, getHeight());
            canvas.drawRect(source, overlapPaint);

        }

    }


}
