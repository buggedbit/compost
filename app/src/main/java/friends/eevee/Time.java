package friends.eevee;

import android.util.Log;

import java.util.Calendar;

// by default it stores 24hr time
// starts from 00:00:00 ends at 23:59:59
// xy:pq:rs to xy:pq:rs with seconds difference of 86400 is defined A DAY
public class Time {
    public int $HOUR = -1;     // 0 - 23
    public int $MINUTE = -1;    // 0 - 59
    public int $SECOND = -1;    // 0 - 59

    public static final int MINUTES_IN_HOUR = 60;
    public static final int SECONDS_IN_MINUTE = 60;
    public static final int SECONDS_IN_HOUR = 3600;
    public static final int SECONDS_IN_DAY = 86400;
    public static final int HOURS_IN_DAY = 24;

    public Time() {
        unsetTime();
    }

    public Time(boolean setToPresent) {
        if (setToPresent) {
            Calendar currentTime = Calendar.getInstance();
            this.$HOUR = currentTime.get(Calendar.HOUR_OF_DAY);  // 24hr format
            this.$MINUTE = currentTime.get(Calendar.MINUTE);
            this.$SECOND = currentTime.get(Calendar.SECOND);
        }
    }

    //String Format = "HH<separator>MM<separator>SS<separator>...
    //String Format = "HH<separator>MM<separator>SS"
    //if not in any of these then the default initialization
    public Time(String timeString, String separator) {

        String error = "Time: not a proper Time object initialization with string " + timeString + " and with separator " + separator;

        if (timeString != null && !timeString.matches("") && separator != null && !separator.matches("")) {
            String[] timeComponents = timeString.split(separator);
            if (timeComponents.length >= 3) {
                try {
                    this.$SECOND = Integer.parseInt(timeComponents[2]);
                    this.$MINUTE = Integer.parseInt(timeComponents[1]);
                    this.$HOUR = Integer.parseInt(timeComponents[0]);
                    if (!this.isValid()) {
                        this.unsetTime();
                        Log.i(ZeroLog.TAG, error);
                    }
                } catch (Exception e) {
                    Log.i(ZeroLog.TAG, error);
                    unsetTime();
                }
            } else {
                Log.i(ZeroLog.TAG, error);
                unsetTime();
            }
        } else {
            Log.i(ZeroLog.TAG, error);
            unsetTime();
        }
    }

    //String Format = HH<IN-separator>MM<IN-separator>SS<OUT-separator>
    //if not in any of these then the default initialization
    public Time(String timeString, String inlineSeparator, String outlineSeparator) {

        String error = "Time: not a proper Time object initialization with string " + timeString + " and with inline-separator " + inlineSeparator + " and with outline-separator " + outlineSeparator;

        if (timeString != null && !timeString.matches("") && inlineSeparator != null && !inlineSeparator.matches("") && outlineSeparator != null && !outlineSeparator.matches("")) {
            String[] outlineFilter = timeString.split(outlineSeparator);
            String[] timeComponents = outlineFilter[0].split(inlineSeparator);
            if (timeComponents.length >= 3) {
                try {
                    this.$SECOND = Integer.parseInt(timeComponents[2]);
                    this.$MINUTE = Integer.parseInt(timeComponents[1]);
                    this.$HOUR = Integer.parseInt(timeComponents[0]);
                    if (!this.isValid()) {
                        Log.i(ZeroLog.TAG, error);
                        this.unsetTime();
                    }
                } catch (Exception e) {
                    Log.i(ZeroLog.TAG, error);
                    unsetTime();
                }
            } else {
                Log.i(ZeroLog.TAG, error);
                unsetTime();
            }
        } else {
            Log.i(ZeroLog.TAG, error);
            unsetTime();
        }
    }

    public Time(Time reference) {
        this.$HOUR = reference.$HOUR;
        this.$MINUTE = reference.$MINUTE;
        this.$SECOND = reference.$SECOND;

        if (!this.isValid()) {
            String error = "Time: not a proper Time Object Initialization with reference object " + reference.simpleRepresentation();
            unsetTime();
            Log.i(ZeroLog.TAG, error);
        }
    }

    // DEFAULT CONSTRUCTOR
    private void unsetTime() {
        this.$HOUR = -1;
        this.$MINUTE = -1;
        this.$SECOND = -1;
    }

    // 00:00:00 - 23:59:59 are supported
    public boolean isValid() {
        if (this.$HOUR < 0 || this.$HOUR > 23) return false;
        if (this.$MINUTE < 0 || this.$MINUTE > 59) return false;
        if (this.$SECOND < 0 || this.$SECOND > 59) return false;
        return true;
    }

    public boolean isSet() {
        if (this.$HOUR == -1) return false;
        if (this.$MINUTE == -1) return false;
        if (this.$SECOND == -1) return false;

        return true;
    }

    public String get12HrFormat(){
        if(this.isValid()){

            int hour = this.$HOUR;
            if(hour>12) hour -= 12;
            else if(hour==0) hour = 12;

            String _hour = String.valueOf(hour);
            if(hour < 10) _hour = "0" + _hour;

            String _min = String.valueOf(this.$MINUTE);
            if(this.$MINUTE < 10) _min = "0" + _min;

            String _sec = String.valueOf(this.$SECOND);
            if(this.$SECOND < 10) _sec = "0" + _sec;

            String AM_PM = "AM";
            if(this.$HOUR >= 12) AM_PM = "PM";

            return (_hour + ":" + _min + ":" + _sec + " " + AM_PM + "\n");
        }
        return "The time is not properly set \n";
    }

    public String get24HrFormat(){
        if(this.isValid()) {

            String hrPart , minPart , secPart;

            if (this.$HOUR < 10)hrPart = "0" + String.valueOf(this.$HOUR);
            else hrPart = String.valueOf(this.$HOUR);

            if (this.$MINUTE < 10)minPart = "0" + String.valueOf(this.$MINUTE);
            else minPart = String.valueOf(this.$MINUTE);

            if (this.$SECOND < 10)secPart = "0" + String.valueOf(this.$SECOND);
            else secPart = String.valueOf(this.$SECOND);

            return hrPart + ":" + minPart + ":" + secPart + "\n";
        }
        return "The time is not properly set \n";
    }

    public String simpleRepresentation() {
        if (this.isValid()) {
            return this.$HOUR + ":" + this.$MINUTE + ":" + this.$SECOND + "\n";
        }
        return "The time is not properly set \n";
    }

    public String getTime() {
        return this.$HOUR + ":" + this.$MINUTE + ":" + this.$SECOND;
    }

    // These functions compare without validity check
    public static boolean isGreater(Time A, Time B) {
        // returns A>B
        if (A.$HOUR > B.$HOUR) return true;
        else if (A.$HOUR < B.$HOUR) return false;
        if (A.$MINUTE > B.$MINUTE) return true;
        else if (A.$MINUTE < B.$MINUTE) return false;
        if (A.$SECOND > B.$SECOND) return true;
        else if (A.$SECOND < B.$SECOND) return false;
        return false;
    }

    public boolean isFutureTo(Time B) {
        // returns this>B
        return Time.isGreater(this,B);
    }

    public boolean isPastTo(Time B) {
        // returns this>B
        return Time.isGreater(B,this);
    }

    public boolean isEqualTo(Time B){
        return this.$HOUR == B.$HOUR && this.$MINUTE == B.$MINUTE && this.$SECOND == B.$SECOND;
    }
    //

    // return A - B in seconds with sign
    public static int timeDifferenceSecondToFirst(Time A, Time B){
        return (A.$HOUR - B.$HOUR) * Time.SECONDS_IN_HOUR + (A.$MINUTE - B.$MINUTE) * Time.SECONDS_IN_MINUTE + (A.$SECOND - B.$SECOND);
    }

    public int timeDifferenceFrom(Time B){
        return (this.$HOUR - B.$HOUR) * Time.SECONDS_IN_HOUR + (this.$MINUTE - B.$MINUTE) * Time.SECONDS_IN_MINUTE + (this.$SECOND - B.$SECOND);
    }

    public int timeDifferenceTo(Time A){
        return (A.$HOUR - this.$HOUR) * Time.SECONDS_IN_HOUR + (A.$MINUTE - this.$MINUTE) * Time.SECONDS_IN_MINUTE + (A.$SECOND - this.$SECOND);
    }

    // adds seconds to this time circularly and returns no days that have passed in bw
    private long add(long additional_seconds){
        if(additional_seconds < 0)return 0;

        //
        long days = (additional_seconds/Time.SECONDS_IN_DAY);
        int _day = (int) (additional_seconds%Time.SECONDS_IN_DAY);
        // _day [0,86399] therefore no extra days will come
        int hours = _day/Time.SECONDS_IN_HOUR;
        int _hour = _day%Time.SECONDS_IN_HOUR;
        int minutes = _hour/Time.SECONDS_IN_MINUTE;
        int seconds = _hour%Time.SECONDS_IN_MINUTE;

        int prev_sec = this.$SECOND;
        int prev_min = this.$MINUTE;
        int prev_hour= this.$HOUR;

        this.$SECOND =  (prev_sec + seconds)%Time.SECONDS_IN_MINUTE;
        int extra_minute = (prev_sec + seconds)/Time.SECONDS_IN_MINUTE;

        this.$MINUTE =  (prev_min + minutes + extra_minute)%Time.MINUTES_IN_HOUR;
        int extra_hour = (prev_min + minutes + extra_minute)/Time.MINUTES_IN_HOUR;

        this.$HOUR =  (prev_hour + hours + extra_hour)%Time.HOURS_IN_DAY;

        return (days);
    }

    private long subtract(long negative_seconds){
        if(negative_seconds < 0)return 0;
        //
        long days = negative_seconds/Time.SECONDS_IN_DAY;
        int remaining_seconds = (int) (negative_seconds%Time.SECONDS_IN_DAY);
        // remaining_seconds [0,86399] therefore no extra days will come
        days++;
        remaining_seconds = Time.SECONDS_IN_DAY - remaining_seconds;
        this.add(remaining_seconds);

        return (-days);
    }

    public long addTime(long seconds ){
        if(seconds == 0)return 0;
        else if(seconds > 0)return this.add(seconds);
        else return this.subtract(-seconds);
    }

//    public static void main(String[] args){
//    }



}



//    public Time(DateTime dateTime){
//        if (dateTime.isSet()){
//            HOUR = dateTime.HOUR;
//            MINUTE = dateTime.MINUTE;
//        }
//        else {
//            InitializeTimeVariables();
//        }
//    }

