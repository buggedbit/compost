package friends.eevee.Activities;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;

import java.util.Vector;

import friends.eevee.Calender.Constants;
import friends.eevee.Calender.Date;
import friends.eevee.Calender.DateTime;
import friends.eevee.Calender.DateTimeDiff;
import friends.eevee.Calender.Time;
import friends.eevee.DB.Def.EventDef;
import friends.eevee.DB.Helpers.Events;
import friends.eevee.R;
import friends.eevee.TimeWallUtil.EventStub;
import friends.eevee.TimeWallUtil.TimeDivisions;
import friends.eevee.TimeWallUtil.TimeFlow;
import friends.eevee.TimeWallUtil.UIPreferences;


public class TimeWall extends AppCompatActivity {

    TimeDivisionsManager timeDivisionsManager;
    StubsStackManager stubsStackManager;
    TimeWallControlCenter timeWallControlCenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.time_wall);

        timeDivisionsManager = new TimeDivisionsManager();
        stubsStackManager = new StubsStackManager();
        timeWallControlCenter = new TimeWallControlCenter();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.time_wall_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.time_wall_control_center_UI_Tweak_menu_button:
                timeWallControlCenter.onOptionsItemSelected(item);
                break;
            case R.id.time_wall_control_center_day_select_menu_button:
                timeWallControlCenter.onOptionsItemSelected(item);
                break;
            default:
                break;
        }
        return true;
    }

    public class TimeDivisionsManager {

        TimeFlow time_flow;
        TimeDivisions time_divisions;

        public TimeDivisionsManager() {
            this.initTimeDivisions();
        }

        private void initTimeDivisions() {
            /* getting time_divisions reference */
            time_divisions = (TimeDivisions) findViewById(R.id.time_divisions);
            /* getting time_flow reference */
            time_flow = (TimeFlow) findViewById(R.id.time_flow);
            time_flow.timeDivisionsManager = this;
        }

        private void UITweaked() {
            int y = time_flow.getScrollY();
            time_divisions.UITweaked(y);
        }

        public void scrollWithOffset() {
            int y = time_flow.getScrollY();
            time_divisions.scrollWithOffSet(y);
        }

        public void showThisDay(Date date) {
            time_divisions.showThisDay(date);
        }
    }

    public class StubsStackManager {

        friends.eevee.DB.Helpers.Events eventsDB;
        TimeFlow time_flow;
        RelativeLayout stubs_stack;

        public StubsStackManager() {
            this.initStubsStack();
        }

        public void initStubsStack() {
            /* creating a DB manager */
            eventsDB = new Events(TimeWall.this, Events.DB_NAME, null, Events.DB_VERSION);
            /* getting time_flow reference */
            time_flow = (TimeFlow) findViewById(R.id.time_flow);
            time_flow.stubsStackManager = this;
            /* getting stubs_stack reference */
            stubs_stack = (RelativeLayout) time_flow.findViewById(R.id.stubs_stack);

            /* setting stubs_stack height */
            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) stubs_stack.getLayoutParams();
            params.height = (int) (Constants.MINUTES_IN_DAY * UIPreferences.MINUTE_PX_SCALE);
            stubs_stack.setLayoutParams(params);
        }

        public void reloadThisDay(Date day){
            stubs_stack.removeAllViews();

            Vector<EventDef> personalEventDefs = eventsDB.getRelatedEvents(Events.TABLES.PERSONAL_EVENTS_TABLE.PERSONAL_EVENTS_TABLE_NAME, day);

            if(personalEventDefs.size() == 0){
                Snackbar.make(stubs_stack,"This day seems free",Snackbar.LENGTH_LONG).show();
            }

            for (int i = 0; i < personalEventDefs.size(); i++) {

                /* Getting Start and End of event */
                DateTime start = new DateTime(personalEventDefs.get(i).$START,Date.SIMPLE_REPR_SEPARATOR, Time.SIMPLE_REPR_SEPARATOR,DateTime.SIMPLE_REPR_SEPARATOR);
                int duration_min = (int) new DateTimeDiff(personalEventDefs.get(i).$DURATION).minutesDiff();
                DateTimeDiff duration = new DateTimeDiff(duration_min);
                DateTime end = new DateTime(start);
                end.addDateTimeDiff(duration);

                /* Getting Ref date time and +24 and -24 mark */
                DateTime ref_date_time = new DateTime(day, UIPreferences.START_OF_THE_DAY);
                DateTime plus_24hr = new DateTime(ref_date_time);
                plus_24hr.addDaysSeconds(1,0);

                /* Cases for events */

                /*      ref                  +24            */
                /*       |  <-------------->  |             */
                if(start.isFutureOrEqualTo(ref_date_time)
                        && start.isPastOrEqualTo(plus_24hr)
                        && end.isFutureOrEqualTo(ref_date_time)
                        && end.isPastOrEqualTo(plus_24hr)){

                    EventStub eventStub = new EventStub(TimeWall.this, start, duration_min, personalEventDefs.get(i).$NAME, Integer.parseInt(personalEventDefs.get(i).$PK));
                    stubs_stack.addView(eventStub);
                    eventStub.reloadStub();
                }

                /*      ref                  +24            */
                /*       |          <---------|----->       */
                else if(start.isFutureOrEqualTo(ref_date_time)
                        && start.isPastOrEqualTo(plus_24hr)
                        && end.isFutureOrEqualTo(plus_24hr)){

                    EventStub eventStub = new EventStub(TimeWall.this, start, (int) plus_24hr.dateTimeDifferenceFrom(start).minutesDiff(), personalEventDefs.get(i).$NAME, Integer.parseInt(personalEventDefs.get(i).$PK));
                    stubs_stack.addView(eventStub);
                    eventStub.reloadStub();
                }

                /*        ref                  +24            */
                /*  <------|-------->           |             */
                else if(start.isPastOrEqualTo(ref_date_time)
                        && end.isFutureOrEqualTo(ref_date_time)
                        && end.isPastOrEqualTo(plus_24hr)){

                    EventStub eventStub = new EventStub(TimeWall.this, ref_date_time, (int) end.dateTimeDifferenceFrom(ref_date_time).minutesDiff(), personalEventDefs.get(i).$NAME, Integer.parseInt(personalEventDefs.get(i).$PK));
                    stubs_stack.addView(eventStub);
                    eventStub.reloadStub();
                }
            }
        }

        public void UITweak(){
            this.reloadThisDay(UIPreferences.SHOWING_DATE);
            /* setting stubs_stack height */
            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) stubs_stack.getLayoutParams();
            params.height = (int) (Constants.MINUTES_IN_DAY * UIPreferences.MINUTE_PX_SCALE);
            stubs_stack.setLayoutParams(params);
        }
    }

    /**
     * Manager of TimeWall Activity
     */
    public class TimeWallControlCenter {

        LinearLayout time_wall_control_center;
        SeekBar time_divisions_time_text_size;
        SeekBar time_divisions_time_bw_marks;
        SeekBar time_wall_minute_px_scale;
        SeekBar time_divisions_past_time;

        public TimeWallControlCenter() {
            this.initTimeWallControlCenter();
        }

        public void initTimeWallControlCenter() {
            this.initInterfaceControl();
            UIPreferences.SHOWING_DATE = new Date(true);
            timeDivisionsManager.showThisDay(UIPreferences.SHOWING_DATE);
            stubsStackManager.reloadThisDay(UIPreferences.SHOWING_DATE);
        }

        private void initInterfaceControl(){
            //////////// initializes time wall control center

            time_wall_control_center = (LinearLayout) findViewById(R.id.time_wall_control_center);
            int max, progress;

            // initializing tweak for time_text_size
            time_divisions_time_text_size = (SeekBar) time_wall_control_center.findViewById(R.id.time_divisions_time_text_size);
            max = (UIPreferences.TIME_DIVISIONS.MAX_TEXT_SIZE - UIPreferences.TIME_DIVISIONS.MIN_TEXT_SIZE) / UIPreferences.TIME_DIVISIONS.TEXT_SIZE_STEP;
            time_divisions_time_text_size.setMax(max);
            progress = (UIPreferences.TIME_DIVISIONS.TIME_TEXT_SIZE - UIPreferences.TIME_DIVISIONS.MIN_TEXT_SIZE) / UIPreferences.TIME_DIVISIONS.TEXT_SIZE_STEP;
            time_divisions_time_text_size.setProgress(progress);
            time_divisions_time_text_size.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if (fromUser) {
                        UIPreferences.TIME_DIVISIONS.TIME_TEXT_SIZE = UIPreferences.TIME_DIVISIONS.MIN_TEXT_SIZE + UIPreferences.TIME_DIVISIONS.TEXT_SIZE_STEP * seekBar.getProgress();
                        timeDivisionsManager.UITweaked();
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });
            //

            // initializing tweak for time_bw_divs
            time_divisions_time_bw_marks = (SeekBar) time_wall_control_center.findViewById(R.id.time_divisions_time_bw_marks);
            max = (UIPreferences.TIME_DIVISIONS.MAX_MINUTES_BW_DIVISIONS - UIPreferences.TIME_DIVISIONS.MIN_MINUTES_BW_DIVISIONS) / UIPreferences.TIME_DIVISIONS.MINUTES_BW_DIVISIONS_STEP;
            time_divisions_time_bw_marks.setMax(max);
            progress = (UIPreferences.TIME_DIVISIONS.MINUTES_BW_DIVISIONS - UIPreferences.TIME_DIVISIONS.MIN_MINUTES_BW_DIVISIONS) / UIPreferences.TIME_DIVISIONS.MINUTES_BW_DIVISIONS_STEP;
            time_divisions_time_bw_marks.setProgress(progress);
            time_divisions_time_bw_marks.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if (fromUser) {
                        UIPreferences.TIME_DIVISIONS.MINUTES_BW_DIVISIONS = UIPreferences.TIME_DIVISIONS.MIN_MINUTES_BW_DIVISIONS + UIPreferences.TIME_DIVISIONS.MINUTES_BW_DIVISIONS_STEP * seekBar.getProgress();
                        timeDivisionsManager.UITweaked();
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });
            //

            // initializing tweak for past_time shown
            time_divisions_past_time = (SeekBar) time_wall_control_center.findViewById(R.id.time_divisions_past_time);
            max = (UIPreferences.MAXIMUM_PAST_TIME - UIPreferences.MINIMUM_PAST_TIME) / UIPreferences.PAST_TIME_STEP;
            time_divisions_past_time.setMax(max);
            progress = (UIPreferences.PAST_TIME - UIPreferences.MINIMUM_PAST_TIME) / UIPreferences.PAST_TIME_STEP;
            time_divisions_past_time.setProgress(progress);
            time_divisions_past_time.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if (fromUser) {
//                        UIPreferences.PAST_TIME = UIPreferences.MINIMUM_PAST_TIME + UIPreferences.PAST_TIME_STEP * seekBar.getProgress();
//                        timeDivisionsManager.UITweaked();
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });
            //

            // initializing tweak for minute_px_scale
            time_wall_minute_px_scale = (SeekBar) time_wall_control_center.findViewById(R.id.time_wall_minute_px_scale);
            max = (int) ((UIPreferences.MAX_MINUTE_PX_SCALE - UIPreferences.MIN_MINUTE_PX_SCALE) / UIPreferences.MINUTE_PX_SCALE_STEP);
            time_wall_minute_px_scale.setMax(max);
            progress = (int) ((UIPreferences.MINUTE_PX_SCALE - UIPreferences.MIN_MINUTE_PX_SCALE) / UIPreferences.MINUTE_PX_SCALE_STEP);
            time_wall_minute_px_scale.setProgress(progress);
            time_wall_minute_px_scale.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if (fromUser) {
                        float final_val = UIPreferences.MIN_MINUTE_PX_SCALE + UIPreferences.MINUTE_PX_SCALE_STEP * seekBar.getProgress();
                        final_val = final_val - final_val % UIPreferences.MINUTE_PX_SCALE_STEP;
                        UIPreferences.MINUTE_PX_SCALE = final_val;

                        timeDivisionsManager.UITweaked();
                        stubsStackManager.UITweak();
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });
            //

            /////////////
        }

        private void onOptionsItemSelected(MenuItem item) {

            switch (item.getItemId()) {
                case R.id.time_wall_control_center_UI_Tweak_menu_button:

                    switch (time_wall_control_center.getVisibility()) {
                        case View.GONE:
                            time_wall_control_center.setVisibility(View.VISIBLE);
                            break;
                        case View.VISIBLE:
                            time_wall_control_center.setVisibility(View.GONE);
                            break;
                        case View.INVISIBLE:
                            break;
                        default:
                            break;
                    }
                    break;
                case R.id.time_wall_control_center_day_select_menu_button:

                    DatePickerDialog datePickerDialog = new DatePickerDialog(TimeWall.this,
                            new DatePickerDialog.OnDateSetListener() {
                                @Override
                                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                    Date selected = new Date(year,monthOfYear + 1, dayOfMonth);
                                    UIPreferences.SHOWING_DATE = selected;
                                    timeDivisionsManager.showThisDay(UIPreferences.SHOWING_DATE);
                                    stubsStackManager.reloadThisDay(UIPreferences.SHOWING_DATE);
                                }
                            },
                            UIPreferences.SHOWING_DATE.$YEAR, UIPreferences.SHOWING_DATE.$MONTH - 1, UIPreferences.SHOWING_DATE.$DAY);
                    datePickerDialog.show();

                    break;
                default:
                    break;
            }
        }
    }
}
