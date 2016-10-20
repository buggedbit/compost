package friends.eevee.TimeWallUtil;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import java.util.Random;

import friends.eevee.Calender.Date;
import friends.eevee.Calender.DateTime;
import friends.eevee.Calender.DateTimeDiff;
import friends.eevee.Calender.Time;
import friends.eevee.Log.ZeroLog;

public class EventStub extends Button {

    /** from the activity in which renders this view */
    Context CONTEXT;
    /** px */
    int HEIGHT = 0;
    /** px */
    int WIDTH = 0;
    /** px */
    int TOP_MARGIN = 0;
    /** px */
    int LEFT_MARGIN = 0;

    DateTime START;
    /** min */
    int DURATION;

    String NAME;
    /** pk value in Personal Events Table */
    int PK;

    public EventStub(Context context, DateTime start, int duration, String name,int pk ) {
        super(context);
        this.initFields(context, start, duration, name, pk);
    }

    private void initFields(Context context, DateTime start, int duration, String name, int pk){
        this.CONTEXT = context;
        this.START = start;
        this.DURATION = duration;
        this.NAME = name;
        this.PK = pk;
    }

    public void reloadStub(){
        Log.i(ZeroLog.TAG, this.__str__());
        /* ref date time */
        Date ref_date = UIPreferences.SHOWING_DATE;
        Time ref_time = UIPreferences.START_OF_THE_DAY;
        DateTime ref_date_time = new DateTime(ref_date, ref_time);

        /* TOP_MARGIN */
        DateTimeDiff start_to_ref = this.START.dateTimeDifferenceFrom(ref_date_time);
        int start_to_ref_min = (int) start_to_ref.minutesDiff();
        this.TOP_MARGIN = (int) (start_to_ref_min * UIPreferences.MINUTE_PX_SCALE);

        /* HEIGHT */
        this.HEIGHT = (int) (this.DURATION * UIPreferences.MINUTE_PX_SCALE);

        /* WIDTH */
        this.WIDTH = UIPreferences.EVENT_STUB.EVENT_WIDTH;

        /* LEFT_MARGIN */
        this.LEFT_MARGIN = 200;

        /* Re - setting Layout params */
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) this.getLayoutParams();
        params.setMargins(this.LEFT_MARGIN , this.TOP_MARGIN , 0 , 0);
        params.width = this.WIDTH;
        params.height = this.HEIGHT;
        this.setLayoutParams(params);

        /* Background color */
        this.setBackgroundColor(getBgColor());

        /* Text Size */
        this.setTextSize(10f);

        /* Text Color */
        this.setTextColor(Color.BLACK);

        /* Display Text */
        String display_text = "";
        String[] parts = this.NAME.split(" ");
        if(parts.length == 1){
            display_text = String.valueOf(parts[0].charAt(0)) + String.valueOf(parts[0].charAt(1));
        }
        else if(parts.length >=2){
            display_text = String.valueOf(parts[0].charAt(0)) + String.valueOf(parts[1].charAt(0));
        }
        this.setText(display_text);

        /* onClick listener */
        this.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(EventStub.this, NAME, Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //Save the current matrix
        canvas.save();
        //Rotate this View at its center
        canvas.rotate(UIPreferences.EVENT_STUB.ANGLE_OF_TEXT, this.getWidth() / 2, this.getHeight() / 2);
        //Draw it
        super.onDraw(canvas);
        //Restore to the previous matrix
        canvas.restore();
    }

    public String __str__(){
        return " Name = " + this.NAME + " Start = " + this.START.formal12Representation() + " Duration = " + String.valueOf(this.DURATION);
    }

    public int getBgColor(){
        Random random = new Random();
        return Color.argb(180,random.nextInt(256),random.nextInt(256),random.nextInt(256));
    }

}
