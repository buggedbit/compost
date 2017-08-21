package org.yashasvi.calender4j.core.classes;

import lombok.NonNull;
import org.yashasvi.calender4j.core.exceptions.InvalidDateException;
import org.yashasvi.calender4j.core.exceptions.InvalidTimeException;

import java.util.Calendar;

public class DateTime {
    private Date date;
    private Time time;

    public static DateTime now() {
        Calendar currentTime = Calendar.getInstance();
        DateTime dateTime = new DateTime();
        dateTime.date = Date.now();
        dateTime.time = Time.now();
        return dateTime;
    }

    public static DateTime of(final int year,
                              final int month,
                              final int day,
                              final int hour,
                              final int minute,
                              final int second,
                              final int microsecond) throws InvalidDateException, InvalidTimeException {
        DateTime dateTime = new DateTime();
        dateTime.date = Date.of(year, month, day);
        dateTime.time = Time.of(hour, minute, second, microsecond);

        return dateTime;
    }

    public DateTime(@NonNull final DateTime b) {
        this(b.date, b.time);
    }

    public DateTime(@NonNull final Date date, @NonNull final Time time) {
        this.date = new Date(date);
        this.time = new Time(time);
    }

    public DateTime(@NonNull final Date date) {
        this.date = new Date(date);
        this.time = Time.now();
    }

    public DateTime(@NonNull final Time time) {
        this.date = Date.now();
        this.time = new Time(time);
    }

    private DateTime() {
    }

    private boolean isValid() {
        return this.date.isValid() && this.time.isValid();
    }


    public boolean greaterThan(DateTime B) {
        if (this.date.greaterThan(B.date))
            return true;
        else if (this.date.equalTo(B.date))
            return this.time.greaterThan(B.time);
        return false;
    }

    public boolean equalTo(DateTime B) {
        return this.date.equalTo(B.date) && this.time.equalTo(B.time);
    }

    public boolean lessThan(DateTime B) {
        return B.greaterThan(this);
    }

    public boolean greaterThanOrEqualTo(DateTime B) {
        return this.greaterThan(B) || this.equalTo(B);
    }

    public boolean lessThanOrEqualTo(DateTime B) {
        return this.lessThan(B) || this.equalTo(B);
    }

    // difference
//    // return A - B in org.yashasvi.calender4j.core.classes.Duration with sign
//    public static Duration dateTimeDifferenceSecondToFirst(DateTime A, DateTime B) {
//        return new Duration(Date.difference(A.date, B.date), Time.timeDifferenceSecondToFirst(A.time, B.time));
//    }
//
//    public Duration dateTimeDifferenceFrom(DateTime B) {
//        return DateTime.dateTimeDifferenceSecondToFirst(this, B);
//    }
//
//    public Duration dateTimeDifferenceTo(DateTime A) {
//        return DateTime.dateTimeDifferenceSecondToFirst(A, this);
//    }
//
//    // modifiers
//    public void toPresent() {
//        this.date.toPresent();
//        this.time.toPresent();
//    }
//
//    public void toThis(DateTime dateTime) {
//        if (!dateTime.isValid()) unsetDateTime();
//        else {
//            this.date.toThis(dateTime.date);
//            this.time.toThis(dateTime.time);
//        }
//    }
//
//    /**
//     * does its operation only if
//     * the present object is valid &&
//     * the object after algebraic addition is valid
//     * otherwise
//     * they do nothing to the object
//     */
//    public boolean addDaysSeconds(long days, long seconds) {
//        if (!this.isValid()) return false;
//
//        long days_due_to_seconds = this.time.addSeconds(seconds);
//        boolean returnValue = this.date.addDays(days + days_due_to_seconds);
//
//        if (!returnValue)
//            this.time.addSeconds(-seconds);   // this is for the corner case where change in time reduces date below
//        // 01 01 0001
//        // this preserves the INVARIANT mentioned above in /***/ comment
//        return returnValue;
//    }
//
//    public boolean addDateTimeDiff(Duration dateTimeDelta) {
//        return this.addDaysSeconds(dateTimeDelta.$24hr, dateTimeDelta.$sec);
//    }
//    //
//
//    // max min
//    public static DateTime maximum(DateTime A, DateTime B) {
//        if (DateTime.isFuture(A, B)) return A;
//        else return B;
//    }
//
//    public static DateTime minimum(DateTime A, DateTime B) {
//        if (DateTime.isFuture(A, B)) return B;
//        else return A;
//    }
//    //


//    public static void main(String[] args){
//    }


}
