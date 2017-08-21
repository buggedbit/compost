package org.yashasvi.calender4j.core.classes;

import java.util.Calendar;

// by default it stores 24hr time
// starts from 00:00:00 ends at 23:59:59
// xy:pq:rs to xy:pq:rs with seconds difference of 86400 is defined A DAY
public class Time {

    public int hour = -1;      // 0 - 23
    public int minute = -1;    // 0 - 59
    public int second = -1;    // 0 - 59

    // constructors
    public Time() {
        unsetTime();
    }

    public Time(boolean setToPresent) {
        if (setToPresent) {
            Calendar currentTime = Calendar.getInstance();
            this.hour = currentTime.get(Calendar.HOUR_OF_DAY);  // 24hr format
            this.minute = currentTime.get(Calendar.MINUTE);
            this.second = currentTime.get(Calendar.SECOND);
        }
    }

    public Time(int HOUR, int MINUTE, int SECOND) {
        this.hour = HOUR;
        this.minute = MINUTE;
        this.second = SECOND;
    }

    //String Format = "HH<separator>MM<separator>SS<separator>...
    //String Format = "HH<separator>MM<separator>SS"
    //if not in any of these then the default initialization
    public Time(String timeString, String separator) {

        String error = "org.yashasvi.calender4j.core.classes.Time: not a proper org.yashasvi.calender4j.core.classes.Time object initialization with string " + timeString + " and with separator " + separator;

        if (timeString != null && !timeString.matches("") && separator != null && !separator.matches("")) {
            String[] timeComponents = timeString.split(separator);
            if (timeComponents.length >= 3) {
                try {
                    this.second = Integer.parseInt(timeComponents[2]);
                    this.minute = Integer.parseInt(timeComponents[1]);
                    this.hour = Integer.parseInt(timeComponents[0]);
                    if (!this.isValid()) {
                        this.unsetTime();
                        // Bad Input
                    }
                } catch (Exception e) {
                    unsetTime();
                }
            } else {
                unsetTime();
            }
        } else {
            unsetTime();
        }
    }

    //String Format = HH<IN-separator>MM<IN-separator>SS<OUT-separator>
    //if not in any of these then the default initialization
    public Time(String timeString, String inlineSeparator, String outlineSeparator) {

        String error = "org.yashasvi.calender4j.core.classes.Time: not a proper org.yashasvi.calender4j.core.classes.Time object initialization with string " + timeString + " and with inline-separator " + inlineSeparator + " and with outline-separator " + outlineSeparator;

        if (timeString != null && !timeString.matches("") && inlineSeparator != null && !inlineSeparator.matches("") && outlineSeparator != null && !outlineSeparator.matches("")) {
            String[] outlineFilter = timeString.split(outlineSeparator);
            String[] timeComponents = outlineFilter[0].split(inlineSeparator);
            if (timeComponents.length >= 3) {
                try {
                    this.second = Integer.parseInt(timeComponents[2]);
                    this.minute = Integer.parseInt(timeComponents[1]);
                    this.hour = Integer.parseInt(timeComponents[0]);
                    if (!this.isValid()) {
                        unsetTime();
                    }
                } catch (Exception e) {
                    unsetTime();
                }
            } else {
                unsetTime();
            }
        } else {
            unsetTime();
        }
    }

    public Time(Time reference) {
        this.hour = reference.hour;
        this.minute = reference.minute;
        this.second = reference.second;

        if (!this.isValid()) {
            String error = "org.yashasvi.calender4j.core.classes.Time: not a proper org.yashasvi.calender4j.core.classes.Time Object Initialization with reference object " + reference.getTimeString();
            unsetTime();
        }
    }

    // DEFAULT INITIALIZATION
    private void unsetTime() {
        this.hour = -1;
        this.minute = -1;
        this.second = -1;
    }

    // identifiers
    // 00:00:00 - 23:59:59 are supported
    public boolean isValid() {
        if (this.hour < 0 || this.hour > 23) return false;
        if (this.minute < 0 || this.minute > 59) return false;
        if (this.second < 0 || this.second > 59) return false;
        return true;
    }

    // formatter
    public String get12HrFormat() {
        if (this.isValid()) {

            int hour = this.hour;
            if (hour > 12) hour -= 12;
            else if (hour == 0) hour = 12;

            String _hour = String.valueOf(hour);
            if (hour < 10) _hour = "0" + _hour;

            String _min = String.valueOf(this.minute);
            if (this.minute < 10) _min = "0" + _min;

            String _sec = String.valueOf(this.second);
            if (this.second < 10) _sec = "0" + _sec;

            String AM_PM = "AM";
            if (this.hour >= 12) AM_PM = "PM";

            return (_hour + ":" + _min + ":" + _sec + " " + AM_PM);
        }
        return "The time is not properly set ";
    }

    public String get24HrFormat() {
        if (this.isValid()) {

            String hrPart, minPart, secPart;

            if (this.hour < 10) hrPart = "0" + String.valueOf(this.hour);
            else hrPart = String.valueOf(this.hour);

            if (this.minute < 10) minPart = "0" + String.valueOf(this.minute);
            else minPart = String.valueOf(this.minute);

            if (this.second < 10) secPart = "0" + String.valueOf(this.second);
            else secPart = String.valueOf(this.second);

            return hrPart + ":" + minPart + ":" + secPart;
        }
        return "The time is not properly set ";
    }

    public String get24HrFormatWithoutSeconds() {
        if (this.isValid()) {

            String hrPart, minPart, secPart;

            if (this.hour < 10) hrPart = "0" + String.valueOf(this.hour);
            else hrPart = String.valueOf(this.hour);

            if (this.minute < 10) minPart = "0" + String.valueOf(this.minute);
            else minPart = String.valueOf(this.minute);

            if (this.second < 10) secPart = "0" + String.valueOf(this.second);
            else secPart = String.valueOf(this.second);

            return hrPart + ":" + minPart;
        }
        return "The time is not properly set ";
    }

    public String simpleRepresentation() {
        if (this.isValid()) {
            String hour_part, min_part, sec_part;
            if (this.hour < 10) hour_part = "0" + this.hour;
            else hour_part = String.valueOf(this.hour);
            if (this.minute < 10) min_part = "0" + this.minute;
            else min_part = String.valueOf(this.minute);
            if (this.second < 10) sec_part = "0" + this.second;
            else sec_part = String.valueOf(this.second);

            return hour_part + " " + min_part + " " + sec_part;
        }
        return "The time is not properly set ";
    }

    public String getTimeString() {
        return this.hour + ":" + this.minute + ":" + this.second;
    }

    // comparisons
    // These functions compare without validity check
    // the time is considered to be in one day only
    public static boolean isFuture(Time A, Time B) {
        // returns A>B
        if (A.hour > B.hour) return true;
        else if (A.hour < B.hour) return false;
        if (A.minute > B.minute) return true;
        else if (A.minute < B.minute) return false;
        if (A.second > B.second) return true;
        else if (A.second < B.second) return false;
        return false;
    }

    public static boolean isSame(Time A, Time B) {
        return A.second == B.second && A.minute == B.minute && A.hour == B.hour;
    }

    public static boolean isPast(Time A, Time B) {
        return Time.isFuture(B, A);
    }

    public boolean isFutureTo(Time B) {
        // returns this>B
        return Time.isFuture(this, B);
    }

    public boolean isPastTo(Time B) {
        // returns this<B
        return Time.isPast(this, B);
    }

    public boolean isEqualTo(Time B) {
        return Time.isSame(this, B);
    }

    // difference
    // return A - B in seconds with sign
    // the time is considered to be in one day only
    public static long timeDifferenceSecondToFirst(Time A, Time B) {
        return (A.hour - B.hour) * Constants.SECONDS_IN_HOUR + (A.minute - B.minute) * Constants.SECONDS_IN_MINUTE + (A.second - B.second);
    }

    public long timeDifferenceFrom(Time B) {
        return Time.timeDifferenceSecondToFirst(this, B);
    }

    public long timeDifferenceTo(Time A) {
        return Time.timeDifferenceSecondToFirst(A, this);
    }

    //modifiers
    public void toPresent() {
        Calendar currentTime = Calendar.getInstance();
        this.hour = currentTime.get(Calendar.HOUR_OF_DAY);  // 24hr format
        this.minute = currentTime.get(Calendar.MINUTE);
        this.second = currentTime.get(Calendar.SECOND);
    }

    public void toStartOfDay() {
        this.toThis(Constants.START_OF_THE_DAY);
    }

    public void toThis(Time time) {
        if (!time.isValid()) unsetTime();
        else {
            this.hour = time.hour;
            this.minute = time.minute;
            this.second = time.second;
        }
    }

    //adds seconds to this time circularly
    //returns a value(initially 0) which is incremented by 1 every time the time crosses (or reaches) 00:00:00
    private long add(long additional_seconds) {
        if (additional_seconds < 0) return 0;

        long days = (additional_seconds / Constants.SECONDS_IN_DAY);
        int _day = (int) (additional_seconds % Constants.SECONDS_IN_DAY);
        // _day [0,86399] therefore no extra days will come
        int hours = _day / Constants.SECONDS_IN_HOUR;
        int _hour = _day % Constants.SECONDS_IN_HOUR;
        int minutes = _hour / Constants.SECONDS_IN_MINUTE;
        int seconds = _hour % Constants.SECONDS_IN_MINUTE;

        int prev_sec = this.second;
        int prev_min = this.minute;
        int prev_hour = this.hour;

        this.second = (prev_sec + seconds) % Constants.SECONDS_IN_MINUTE;
        int extra_minute = (prev_sec + seconds) / Constants.SECONDS_IN_MINUTE;

        this.minute = (prev_min + minutes + extra_minute) % Constants.MINUTES_IN_HOUR;
        int extra_hour = (prev_min + minutes + extra_minute) / Constants.MINUTES_IN_HOUR;

        this.hour = (prev_hour + hours + extra_hour) % Constants.HOURS_IN_DAY;

        if (this.hour < prev_hour) {
            days++;// due to day change
        } else if (prev_hour == this.hour && this.minute < prev_min) {
            days++;// due to day change
        } else if (prev_hour == this.hour && prev_min == this.minute && this.second < prev_sec) {
            days++;// due to day change
        }
        return (days);
    }

    //subtracts seconds to this time circularly
    //returns a value(initially 0) which is decremented by 1 every time the time crosses 00:00:00
    private long subtract(long negative_seconds) {
        if (negative_seconds < 0) return 0;
        //
        long days = negative_seconds / Constants.SECONDS_IN_DAY;
        int remaining_seconds = (int) (negative_seconds % Constants.SECONDS_IN_DAY);

        // instead of subtracting add the 1 day - remaining_seconds
        // remaining_seconds [0,86399]
        remaining_seconds = Constants.SECONDS_IN_DAY - remaining_seconds;
        days++;

        days -= this.add(remaining_seconds); // as we are returning -days

        return (-days);
    }

    /**
     * does its operation only if
     * the present object is valid &&
     * the object after algebraic addition is valid
     * otherwise
     * they do nothing to the object
     */
    //adds algebraic seconds to this time circularly
    //returns a value which is no of changes in the dates occurred (00:00:00 is considered the start of a day)
    public long addSecondsReturnChangeInDates(long seconds) {
        if (!this.isValid()) return 0;
        if (seconds == 0) return 0;
        else if (seconds > 0) return this.add(seconds);
        else return this.subtract(-seconds);
    }

}

