//package org.yashasvi.calender4j.core.classes;
//
//public class DateTime {
//    /**
//     * Separator for fields in simple representation
//     */
//    private static final String SIMPLE_REPR_SEPARATOR = " ";
//
//    public Date $DATE;  // date object
//    public Time $TIME;  // time object
//    // time is internally implemented in 24hr
//
//
//    // constructors
//    public DateTime() {
//        unsetDateTime();
//    }
//
//    public DateTime(boolean setToPresent) {
//        if (setToPresent) {
//            this.$DATE = new Date(true);
//            this.$TIME = new Time(true);
//        } else {
//            unsetDateTime();
//        }
//    }
//
//    public DateTime(Date date) {
//        if (date.isValid()) {
//            this.$DATE = new Date(date);
//            this.$TIME = new Time();
//            this.$TIME.toStartOfDay();
//        } else {
//            unsetDateTime();
//        }
//    }
//
//    public DateTime(Date date, Time time) {
//        if (date.isValid() && time.isValid()) {
//            this.$DATE = new Date(date);
//            this.$TIME = new Time(time);
//        } else {
//            unsetDateTime();
//        }
//    }
//
//    public DateTime(DateTime reference) {
//        if (reference.isValid()) {
//            this.$DATE = new Date(reference.$DATE);
//            this.$TIME = new Time(reference.$TIME);
//        } else {
//            unsetDateTime();
//        }
//    }
//
//    public void unsetDateTime() {
//        this.$DATE = new Date();
//        this.$TIME = new Time();
//    }
//
//    // identifiers
//    private boolean isValid() {
//        return this.$DATE.isValid() && this.$TIME.isValid();
//    }
//
//    // comparisons
//    // These functions compare without validity check
//    public static boolean isFuture(DateTime A, DateTime B) {
//        // returns A>B
//        if (Date.isFuture(A.$DATE, B.$DATE)) return true;
//        else if (Date.equals(A.$DATE, B.$DATE)) return Time.isFuture(A.$TIME, B.$TIME);
//        return false;
//    }
//
//    public static boolean isSame(DateTime A, DateTime B) {
//        // returns A==B
//        return Date.equals(A.$DATE, B.$DATE) && Time.isSame(A.$TIME, B.$TIME);
//    }
//
//    public static boolean isPast(DateTime A, DateTime B) {
//        // returns boolean A<B
//        return DateTime.isFuture(B, A);
//    }
//
//    public boolean greaterThan(DateTime B) {
//        // returns this>B
//        return DateTime.isFuture(this, B);
//    }
//
//    public boolean lessThan(DateTime B) {
//        // returns this<B
//        return DateTime.isPast(this, B);
//    }
//
//    public boolean equalTo(DateTime B) {
//        // returns this==B
//        return DateTime.isSame(this, B);
//    }
//
//    public boolean isFutureOrEqualTo(DateTime B) {
//        return DateTime.isSame(this, B) || DateTime.isFuture(this, B);
//    }
//
//    public boolean isPastOrEqualTo(DateTime B) {
//        return DateTime.isSame(this, B) || DateTime.isPast(this, B);
//    }
//    //
//
//    // difference
//    // return A - B in org.yashasvi.calender4j.core.classes.Duration with sign
//    public static Duration dateTimeDifferenceSecondToFirst(DateTime A, DateTime B) {
//        return new Duration(Date.difference(A.$DATE, B.$DATE), Time.timeDifferenceSecondToFirst(A.$TIME, B.$TIME));
//    }
//
//    public Duration dateTimeDifferenceFrom(DateTime B) {
//        return DateTime.dateTimeDifferenceSecondToFirst(this, B);
//    }
//
//    public Duration dateTimeDifferenceTo(DateTime A) {
//        return DateTime.dateTimeDifferenceSecondToFirst(A, this);
//    }
//    //
//
//    // modifiers
//
//    public void toPresent() {
//        this.$DATE.toPresent();
//        this.$TIME.toPresent();
//    }
//
//    public void toThis(DateTime dateTime) {
//        if (!dateTime.isValid()) unsetDateTime();
//        else {
//            this.$DATE.toThis(dateTime.$DATE);
//            this.$TIME.toThis(dateTime.$TIME);
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
//        long days_due_to_seconds = this.$TIME.addSeconds(seconds);
//        boolean returnValue = this.$DATE.addDays(days + days_due_to_seconds);
//
//        if (!returnValue)
//            this.$TIME.addSeconds(-seconds);   // this is for the corner case where change in time reduces date below
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
//
//
////    public static void main(String[] args){
////    }
//
//
//}
//
//
////public class org.yashasvi.calender4j.core.classes.DateTime {
//
////    public org.yashasvi.calender4j.core.classes.DateTime(int YEAR, int MONTH, int DAY, int HOUR, int MINUTE) {
////        this.YEAR = YEAR;
////        this.MONTH = MONTH;
////        this.DAY = DAY;
////        this.HOUR = HOUR;
////        this.MINUTE = MINUTE;
////    }
//
////    // constructors ..................................................................
//
////    public org.yashasvi.calender4j.core.classes.Time getTime() {
////        org.yashasvi.calender4j.core.classes.Time time = new org.yashasvi.calender4j.core.classes.Time(this);
////        return time;
////    }
////
////    public org.yashasvi.calender4j.core.classes.Date getDateString() {
////        org.yashasvi.calender4j.core.classes.Date date = new org.yashasvi.calender4j.core.classes.Date(this);
////        return date;
////    }
////
////    public TimeInput getTimeInput() {
////        TimeInput time = new TimeInput(this);
////        return time;
////    }
////
////    public DateInput getDateInput() {
////        DateInput date = new DateInput(this);
////        return date;
////    }
////
////    // Getters and Setters ....................................................................
