package friends.eevee.TimeWallUtil;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.TextView;

import friends.eevee.Calender.Constants;
import friends.eevee.Calender.Date;
import friends.eevee.Calender.DateTime;

/**
 * Represents the time divisions
 * \b DO NOT create big views of this class
 * They might give unexpected results, slow down the system or even crash the system due to high memory usage
 * Instead create a view of the screen size and re-draw every-time the user scrolls
 */
public class TimeDivisions extends TextView {

    /**
     * Scale 1 min = min_px_scale px
     * */
    protected float min_px_scale;

    /**
     * used for drawing rectangle shaped division marks
     */
    protected Rect div_separator;

    /**
     * used for drawing rectangles representing change in day
     */
    protected Rect day_bg_rect;

    /**
     * used for color and size
     */
    protected Paint div_paint;

    /**
     * used for color and size
     */
    protected Paint text_paint;

    /**
     * used for color and size
     */
    protected Paint pres_mark_paint;

    /**
     * used for color and size
     */
    protected Paint day_bg_paint;

    /**
     * holds the starting DateTime
     * from which the time divisions are drawn
     * */
    protected DateTime ref_date_time;

    /**
     *  used for iterating through times
     * and printing the time division marks
     */
    protected DateTime date_time_type_set;

    /**
     * stores present date time
     * */
    protected DateTime present_date_time;

    /**
     * px
     */
    protected int text_height;

    /**
     * px
     */
    protected int div_line_width;

    /**
     * px
     */
    protected int pres_mark_line_width;

    /**
     * min
     */
    protected int time_bw_div;

    /**
     * px
     */
    protected int px_bw_div;

    /**
     * min
     * */
    protected int past_time;

    /**
     * number of division marks to be printed
     * equals number of divisions + 1
     * @see TimeDivisions#noDivsBasedOnHeight()
     */
    protected int no_div;

    /**
     *  the amount of time_flow scroll view scrolled,
     * so as to correspondingly indicate time divisions
     * Initialized to zero
     */
    protected int px_offset;


    public TimeDivisions(Context context) {
        super(context);
        initializeFields(context);
    }

    public TimeDivisions(Context context, AttributeSet attrs) {
        super(context, attrs);
        initializeFields(context);
    }

    protected void initializeFields(Context context) {
        div_separator = new Rect();
        day_bg_rect = new Rect();

        div_paint = new Paint();
        div_paint.setAntiAlias(true);
        div_paint.setColor(Color.BLACK);

        text_paint = new Paint();
        text_paint.setAntiAlias(true);
        text_paint.setColor(Color.BLACK);

        pres_mark_paint = new Paint();
        pres_mark_paint.setAntiAlias(true);
        pres_mark_paint.setColor(Color.MAGENTA);

        day_bg_paint = new Paint();
        day_bg_paint.setAntiAlias(true);
        day_bg_paint.setColor(Color.parseColor("#33000000"));

        ref_date_time = new DateTime(new Date(true));
        date_time_type_set = new DateTime(ref_date_time);
        present_date_time = new DateTime(true);

        px_offset = 0;

        this.reloadUIPreferences();
    }

    /**
     * reloads the UI preferences from {@link UIPreferences} class
     * */
    public void reloadUIPreferences(){
        /* Min PX Scale */
        min_px_scale = UIPreferences.MINUTE_PX_SCALE;

        /* Time between divisions */
        time_bw_div = UIPreferences.TIME_DIVISIONS.MINUTES_BW_DIVISIONS;
        px_bw_div = (int) (time_bw_div * UIPreferences.MINUTE_PX_SCALE);

        /* Time Text size */
        text_height = UIPreferences.TIME_DIVISIONS.TIME_TEXT_SIZE;
        text_paint.setTextSize(text_height);

        /* Past Time */
        past_time = UIPreferences.PAST_TIME;

        /* Division Mark width */
        div_line_width = UIPreferences.TIME_DIVISIONS.DIV_MARK_WIDTH;

        /* Present Pointer width */
        pres_mark_line_width = UIPreferences.TIME_DIVISIONS.PRESENT_MARK_WIDTH;
    }

    /**
     * reloads UI preferences {@link TimeDivisions#reloadUIPreferences()} and {@link TimeDivisions#px_offset}
     * update the present time and then invalidate the view
     * */
    public void UITweaked(int px_offset) {
        this.reloadUIPreferences();
        this.px_offset = px_offset;
        present_date_time.toPresent();
        this.invalidate();
    }

    /**
     * updates  {@link TimeDivisions#px_offset}
     * update the present time and then invalidate the view
     * */
    public void scrollWithOffSet(int px_offset) {
        this.px_offset = px_offset;
        present_date_time.toPresent();
        this.invalidate();
    }

    public void showThisDay(Date date){
        ref_date_time = new DateTime(date);
        px_offset = 0;
        this.invalidate();
    }

    /**
     * When there is a day change in the field of view then eevee
     * shows the change by shading the backgrounds of days
     * the colors are selected based on the week day of date
     *
     * @param date the date for which color is required
     * @see Date#getDay(String)
     *
     * You can change the colors and opacity for week days here
     * */
    public int get_bg_color(Date date){
        switch (date.getDay("EE")){
            case "Sun":
                return Color.argb(50,255,0,0);
            case "Mon":
                return Color.argb(50,0,255,0);
            case "Tue":
                return Color.argb(50,0,0,255);
            case "Wed":
                return Color.argb(50,255,0,0);
            case "Thu":
                return Color.argb(50,0,255,0);
            case "Fri":
                return Color.argb(50,0,0,255);
            case "Sat":
                return Color.argb(50,255,255,0);
            default:
                return Color.argb(50,0,0,0);
        }
    }

    /**
     * finds number of time division marks to be printed
     * using UI preferences
     * This function does not change with px_offset
     *
     * @return the number of division marks to be printed
     * @see UIPreferences.TIME_DIVISIONS TIME_BW_DIVISIONS
     * @see UIPreferences MINUTE_PX_SCALE
     */
    protected int noDivsBasedOnHeight() {
        int height = getHeight();
        float px_bw_div_float = (time_bw_div * min_px_scale);
        no_div = (int) (height / px_bw_div_float) + 1;
        return no_div;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        /**
         * There can be slight mis-placement of divisions from their actual places due to int float conversions
         * in below few lines of code, but the error doesn't increase with time, but it oscillates
         * bw 0 and ({@link UIPreferences.MINUTE_PX_SCALE}/2) minutes
         * So for small values of ({@link UIPreferences.MINUTE_PX_SCALE}/2) its okay to use this
         * */

        int ref_time = ref_date_time.$TIME.$MINUTE;
        int min_top_to_ref_time = (int) (ref_time + past_time - px_offset / UIPreferences.MINUTE_PX_SCALE);
        // so that the printed time is always of form xy:00:pq
        int px_top_to_ref_time = (int) (min_top_to_ref_time * UIPreferences.MINUTE_PX_SCALE);
        // initially to farthest past region
        int min_ref_time_to_top = -min_top_to_ref_time;

        /* setting to ref time */
        date_time_type_set.toThis(ref_date_time);
        /* going to top */
        date_time_type_set.addDaysSeconds(0, min_ref_time_to_top * Constants.SECONDS_IN_MINUTE);
        /* min at top */
        int min_at_top = date_time_type_set.$TIME.$MINUTE;
        /*
          modifying the date_time_type_set,
          so that time divisions are printed in good format
          i.e , 4:00 and not 4:04 or 4:11 or so on ...
          TODO fixme for general time bw div
          */

        int px_from_start = 0;

        switch (UIPreferences.TIME_DIVISIONS.MINUTES_BW_DIVISIONS) {
            case 30:
                if (min_at_top == 0 || min_at_top == 30) {
                    px_from_start = 0;
                } else if (min_at_top < 30) {
                    date_time_type_set.addDaysSeconds(0, (Constants.MINUTES_IN_HOUR / 2 - min_at_top) * Constants.SECONDS_IN_MINUTE);
                    px_from_start = (int) ((Constants.MINUTES_IN_HOUR / 2 - min_at_top) * UIPreferences.MINUTE_PX_SCALE);
                } else if (min_at_top > 30) {
                    date_time_type_set.addDaysSeconds(0, (Constants.MINUTES_IN_HOUR - min_at_top) * Constants.SECONDS_IN_MINUTE);
                    px_from_start = (int) ((Constants.MINUTES_IN_HOUR - min_at_top) * UIPreferences.MINUTE_PX_SCALE);
                }
                break;
            case 60:
                if (min_at_top == 0) {
                    px_from_start = 0;
                } else if (min_at_top < 60) {
                    date_time_type_set.addDaysSeconds(0, (Constants.MINUTES_IN_HOUR - min_at_top) * Constants.SECONDS_IN_MINUTE);
                    px_from_start = (int) ((Constants.MINUTES_IN_HOUR - min_at_top) * UIPreferences.MINUTE_PX_SCALE);
                }
                break;
        }

        String day = "", time;
        int no_div_local = noDivsBasedOnHeight();
        String prev_day;
        int flag = px_from_start;

        for (int div_iterator = 0; div_iterator < no_div_local; div_iterator++) {
            // Preparing shapes and text
            div_separator.set(0, px_from_start, getWidth(), px_from_start + div_line_width);
            time = date_time_type_set.$TIME.get24HrFormatWithoutSeconds();
            prev_day = day;
            day = date_time_type_set.getDay("EE");

            // Drawing them on canvas
            // drawing bg and day when day changes
            if(div_iterator < no_div_local - 1){
                if(!prev_day.matches(day)){
                    day_bg_rect.set(0, flag, getWidth(), px_from_start);
                    flag = px_from_start;
                    day_bg_paint.setColor(get_bg_color(date_time_type_set.$DATE));
                    canvas.drawRect(day_bg_rect,day_bg_paint);
                    canvas.drawText(day, getWidth() - text_height * 2, px_from_start + text_height / 2, text_paint);
                }
            }
            canvas.drawRect(div_separator, div_paint);
            canvas.drawText(time, 0, px_from_start + text_height / 2, text_paint);

            // Updating indices
            px_from_start = px_from_start + px_bw_div;
            date_time_type_set.addDaysSeconds(0, time_bw_div * Constants.SECONDS_IN_MINUTE);
        }

    }

}




