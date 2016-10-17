package friends.eevee.Activities;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;

import friends.eevee.Calender.Date;
import friends.eevee.DB.Helpers.Events;
import friends.eevee.R;
import friends.eevee.TimeWallUtil.TimeDivisions;
import friends.eevee.TimeWallUtil.UIPreferences;

public class TimeWall extends AppCompatActivity {

    TimeWallControlCenter timeWallControlCenter;
    TimeDivisionsManager timeDivisionsManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.time_wall);

        timeDivisionsManager = new TimeDivisionsManager();
        timeDivisionsManager.initTimeDivisions();

        timeWallControlCenter = new TimeWallControlCenter();
        timeWallControlCenter.initTimeWallControlCenter();
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


    class TimeDivisionsManager {

        ScrollView time_flow;
        TimeDivisions time_divisions;

        public TimeDivisionsManager() {
        }

        private void initTimeDivisions() {

            time_divisions = (TimeDivisions) findViewById(R.id.time_divisions);
            time_flow = (ScrollView) findViewById(R.id.time_flow);
            time_flow.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
                @Override
                public void onScrollChanged() {
                    scrollWithOffset();
                }
            });

        }

        private void UITweaked() {
            int y = time_flow.getScrollY();
            time_divisions.UITweaked(y);
        }

        private void scrollWithOffset() {
            int y = time_flow.getScrollY();
            time_divisions.scrollWithOffSet(y);
        }

        public void showThisDay(Date date) {
            time_divisions.showThisDay(date);
        }
    }

    class StubsStackManager {

        friends.eevee.DB.Helpers.Events eventsDB;
        ScrollView time_flow;
        RelativeLayout stubs_stack;

        public StubsStackManager(Date selected) {

        }

        public void initStubsStack() {
            eventsDB = new Events(TimeWall.this, Events.DB_NAME, null, Events.DB_VERSION);

        }
    }

    /**
     * UI tweak manager of TimeWall Activity
     */
    class TimeWallControlCenter {

        LinearLayout time_wall_control_center;
        SeekBar time_divisions_time_text_size;
        SeekBar time_divisions_time_bw_marks;
        SeekBar time_wall_minute_px_scale;
        SeekBar time_divisions_past_time;

        public TimeWallControlCenter() {

        }

        public void initTimeWallControlCenter() {
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
                        UIPreferences.PAST_TIME = UIPreferences.MINIMUM_PAST_TIME + UIPreferences.PAST_TIME_STEP * seekBar.getProgress();
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

                    Date present_date = new Date(true);
                    DatePickerDialog datePickerDialog = new DatePickerDialog(TimeWall.this,
                            new DatePickerDialog.OnDateSetListener() {
                                @Override
                                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                    Date selected = new Date(year,monthOfYear + 1, dayOfMonth);
                                    timeDivisionsManager.showThisDay(selected);
                                }
                            },
                            present_date.$YEAR, present_date.$MONTH - 1, present_date.$DAY);
                    datePickerDialog.show();

                    break;
                default:
                    break;
            }
        }
    }
}
