package friends.eevee.TimeWall;

public class UIPreferences {

    public static int MINIMUM_PAST_TIME = 120;                  /**< min*/
    public static float MINUTE_PX_SCALE = 5.0f;                 /**< 1 MINUTE = (this many) px */

    public static class TIME_DIVISIONS{

        public static int MINUTES_BW_DIVISIONS = 30;           /**< min multiples of 30*/
        public static int MIN_MINUTES_BW_DIVISIONS = 30;       /**< min*/
        public static int MAX_MINUTES_BW_DIVISIONS = 60;       /**< min*/

        public static int TIME_TEXT_SIZE = 30;              /**< px*/
        public static int MIN_TEXT_SIZE = 20;               /**< px*/
        public static int MAX_TEXT_SIZE = 150;              /**< px*/
    }

    public static void setDeviceDependentValues(int WIDTH , int HEIGHT){
        GESTURES.GESTURE_AREA_MIN = HEIGHT / 20;
        GESTURES.GESTURE_AREA_MAX = HEIGHT / 4;
        EVENT.EVENT_WIDTH = WIDTH/4;
    }

    public static class GESTURES{
        public static float FINGER_STROKE_WIDTH = 25;           // px
        public static String FINGER_TOUCH_COLOR = "#FFFFFF";    // px

        public static int GESTURE_AREA_MIN ;                 // px
        public static int GESTURE_AREA_MAX ;                 // px
    }

    public static class EVENT{
        public static int EVENT_WIDTH = 200;                // px

        public static int PADDING_LEFT = 0;
        public static int PADDING_TOP = 0;
        public static int PADDING_RIGHT = 0;
        public static int PADDING_BOTTOM = 0;

    }
}
