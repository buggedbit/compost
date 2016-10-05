package friends.eevee.TimeWall;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.TextView;

import java.util.Calendar;

import friends.eevee.Calender.Constants;
import friends.eevee.Calender.DateTime;

/**
 * Represents the time divisions
 * \b DO NOT create big views of this class
 * They might give unexpected results, slow down the system or even crash the system due to high memory usage
 * Instead create a view of the screen size and re-draw every-time the user scrolls
 *
 */
public class TimeDivisions extends TextView{

    protected Rect div_separator;       /**<used for color and size*/
    protected Paint div_paint;          /**<used for color and size*/
    protected Paint text_paint;         /**<used for color and size*/
    protected Paint pres_mark_paint;    /**<used for color and size*/

    protected DateTime date_time_object; /**< used for iterating through times
                                            and printing the time division marks*/

    protected int text_height;        /**<px*/
    protected int div_line_width = 1; /**<px*/
    protected int pres_mark_line_width = 5; /**<px*/

    protected int time_bw_div;        /**<min*/
    protected int px_bw_div;          /**<px*/

    protected int no_div;             /**<number of division marks to be printed
                                        equals number of divisions + 1 */

    protected int offset = 0;/**< the amount of time_flow scroll view scrolled,
                            so as to correspondingly indicate time divisions
                            Initialized to zero*/


    public TimeDivisions(Context context) {
        super(context);
        initializeFields(context);
    }

    public TimeDivisions(Context context, AttributeSet attrs) {
        super(context, attrs);
        initializeFields(context);
    }

    public TimeDivisions(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initializeFields(context);
    }

    protected void initializeFields(Context context) {
        div_separator = new Rect();

        div_paint = new Paint();
        div_paint.setAntiAlias(true);
        div_paint.setColor(Color.BLACK);

        text_paint = new Paint();
        text_paint.setAntiAlias(true);
        text_paint.setColor(Color.BLACK);
        text_height = UIPreferences.TIME_DIVISIONS.TIME_TEXT_SIZE;
        text_paint.setTextSize(text_height);

        pres_mark_paint = new Paint();
        pres_mark_paint.setAntiAlias(true);
        pres_mark_paint.setColor(Color.MAGENTA);

        time_bw_div = UIPreferences.TIME_DIVISIONS.TIME_BW_DIVISIONS;
        px_bw_div = (int) (time_bw_div * UIPreferences.MINUTE_PX_SCALE);

        div_line_width = 1;
        offset = 0;
        pres_mark_line_width = 5;

        date_time_object = new DateTime(true);
    }

    public void invalidateWithOffset(int offset){
        this.offset = offset;
        this.invalidate();
    }

    /**
     * finds number of time division marks to be printed
     * using UI preferences
     * This function does not change with offset
     *
     * @see UIPreferences.TIME_DIVISIONS TIME_BW_DIVISIONS
     * @see UIPreferences MINUTE_PX_SCALE
     * @return the number of division marks to be printed
     * */
    protected int noDivsBasedOnHeight(){
        int height = getHeight();
        float px_bw_div_float = ( UIPreferences.TIME_DIVISIONS.TIME_BW_DIVISIONS * UIPreferences.MINUTE_PX_SCALE);
        no_div = (int) (height / px_bw_div_float) + 1;
        return no_div;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        Calendar currentTime = Calendar.getInstance();
        int curr_minute = currentTime.get(Calendar.MINUTE);
        /**
         * There can be slight mis-placement of divisions from their actual places due to int float conversions
         * in below few lines of code, but the error doesn't increase with time, but it oscillates
         * bw 0 and (UIPreferences.MINUTE_PX_SCALE/2) minutes
         * So for small values of (UIPreferences.MINUTE_PX_SCALE/2) its okay to use this
         * */
        long min_top_to_pres_mark = (long) (curr_minute + UIPreferences.MINIMUM_PAST_TIME - offset / UIPreferences.MINUTE_PX_SCALE);
        // so that the printed time is always of form xy:00:pq
        long px_top_to_pres_mark = (long) (min_top_to_pres_mark * UIPreferences.MINUTE_PX_SCALE);
        // set the present marker only if it is in scope
        if(px_top_to_pres_mark >= 0){
            div_separator.set( 0 , (int)px_top_to_pres_mark , getWidth() , (int) px_top_to_pres_mark + pres_mark_line_width);
            canvas.drawRect(div_separator, pres_mark_paint);
        }
        long min_pres_mark_to_top = -min_top_to_pres_mark;// initially to farthest past region
        /**
         * converting min_pres_mark_to_top into days and min
         * to increase the range of time this view can cover before overflow
         * i.e if user scrolls very much to future end
         * then directly converting min to seconds can overflow the int data type,
         * so convert it into days and min
         * and use the addDaysSeconds method in DateTime instead
         * */
        long day_pres_mark_to_top = min_pres_mark_to_top / Constants.MINUTES_IN_DAY;
        min_pres_mark_to_top = min_pres_mark_to_top % Constants.MINUTES_IN_DAY;

        date_time_object.toPresent();
        date_time_object.addDaysSeconds(day_pres_mark_to_top , min_pres_mark_to_top * Constants.SECONDS_IN_MINUTE);
        int min_at_top = date_time_object.$TIME.$MINUTE;

        /**
         * modifying the date_time_object,
         * so that time divisions are printed in good format
         * i.e , 4:00 and not 4:04 or 4:11 or so on ...
         * */
        // TODO fixme for general time bw div
        int px_from_start = 0;
        if(min_at_top == 0 || min_at_top == 30){
            px_from_start = 0;
        }
        else if(min_at_top < 30){
            date_time_object.addDaysSeconds(0, (Constants.MINUTES_IN_HOUR / 2 - min_at_top) * Constants.SECONDS_IN_MINUTE);
            px_from_start = (int) ((Constants.MINUTES_IN_HOUR / 2 - min_at_top) * UIPreferences.MINUTE_PX_SCALE);
        }
        else if(min_at_top > 30){
            date_time_object.addDaysSeconds(0, (Constants.MINUTES_IN_HOUR - min_at_top) * Constants.SECONDS_IN_MINUTE);
            px_from_start = (int) ((Constants.MINUTES_IN_HOUR - min_at_top) * UIPreferences.MINUTE_PX_SCALE);
        }

        String day,time;
        int no_div_local = noDivsBasedOnHeight();
        for (int div_iterator = 0; div_iterator < no_div_local; div_iterator++) {
            // Preparing shapes and text
            div_separator.set(0 , px_from_start , getWidth(), px_from_start + div_line_width);
            time = date_time_object.$TIME.get24HrFormatWithoutSeconds();
            day = date_time_object.getDay("EE");

            // Drawing them on canvas
            canvas.drawRect(div_separator, div_paint);
            canvas.drawText(time, 0, px_from_start + text_height/2 , text_paint);
            canvas.drawText(day, getWidth() - text_height * 2, px_from_start + text_height/2 , text_paint);

            // Updating indices
            px_from_start = px_from_start + px_bw_div;
            date_time_object.addDaysSeconds(0, time_bw_div * Constants.SECONDS_IN_MINUTE);
        }
    }

}




