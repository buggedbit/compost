package com.harsha.eevee;

import java.util.Calendar;

public class DateTime {

    public int YEAR;
    public int MONTH;
    public int DAY;
    public int HOUR;
    public int MINUTE;


    public static final int MINS_IN_HOUR = 60;
    public static final int HOURS_IN_DAY = 24;
    public static final int MINS_IN_DAY = 1440;

    public static final int[] DAYS_IN_MONTH_NLY = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
    public static final int[] DAYS_IN_MONTH_LY = {31, 29, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};

    public static final int DAYS_IN_NLY = 365;
    public static final int DAYS_IN_LY = 366;

    public static final int DAYS_IN_WEEK = 7;

    public DateTime() {
    }

    public DateTime(int YEAR, int MONTH, int DAY, int HOUR, int MINUTE) {
        this.YEAR = YEAR;
        this.MONTH = MONTH;
        this.DAY = DAY;
        this.HOUR = HOUR;
        this.MINUTE = MINUTE;
    }

    public DateTime(boolean setToPresent) {
        if (setToPresent == true) {
            Calendar currentTime = Calendar.getInstance();
            MINUTE = currentTime.get(Calendar.MINUTE);
            HOUR = currentTime.get(Calendar.HOUR_OF_DAY);
            DAY = currentTime.get(Calendar.DAY_OF_MONTH);
            MONTH = currentTime.get(Calendar.MONTH) + 1;
            YEAR = currentTime.get(Calendar.YEAR);
        } else {

        }
    }

    //String Format = YYYY:MonMon:DD:HH:MinMin
    public DateTime(String dateString) {
        if (dateString != null && !dateString.matches("")) {
            String[] dateComponents = dateString.split(":");
            if (dateComponents.length == 5) {
                YEAR = Integer.parseInt(dateComponents[0]);
                MONTH = Integer.parseInt(dateComponents[1]);
                DAY = Integer.parseInt(dateComponents[2]);
                HOUR = Integer.parseInt(dateComponents[3]);
                MINUTE = Integer.parseInt(dateComponents[4]);
            }
        } else {

        }
    }

    //String Format = YYYY-MonMon-DD HH:MinMin:SS
    public DateTime(String dateString , int type) {
        if (dateString != null && !dateString.matches("") && type == 0) {
            String[] space = dateString.split(" ");
            String[] hyphen = space[0].split("-");
            String[] colon = space[1].split(":");

            int total = hyphen.length + colon.length;

            if(total == 6){
                YEAR = Integer.parseInt(hyphen[0]);
                MONTH = Integer.parseInt(hyphen[1]);
                DAY = Integer.parseInt(hyphen[2]);
                HOUR = Integer.parseInt(colon[0]);
                MINUTE = Integer.parseInt(colon[1]);
            }
        }
        else {

        }
    }

    public DateTime(DateTime reference) {
        YEAR = reference.YEAR;
        MONTH = reference.MONTH;
        DAY = reference.DAY;
        HOUR = reference.HOUR;
        MINUTE = reference.MINUTE;
    }

    public String toString() {
        String dateTimeFormat = "";

        dateTimeFormat += String.valueOf(YEAR);
        dateTimeFormat += ":";
        dateTimeFormat += String.valueOf(MONTH);
        dateTimeFormat += ":";
        dateTimeFormat += String.valueOf(DAY);
        dateTimeFormat += ":";
        dateTimeFormat += String.valueOf(HOUR);
        dateTimeFormat += ":";
        dateTimeFormat += String.valueOf(MINUTE);

        return dateTimeFormat;
    }

    private boolean isLeapYear() {
        int P = YEAR % 400;
        int Q = P % 100;
        int R = Q % 4;

        if (P == 0) {
            return true;
        }
        if (Q == 0) {
            return false;
        }
        if (R == 0) {
            return true;
        }
        return false;
    }

    public int sinceStart() {
        int minSinceStart = 0;
        int daysSinceStart = 0;
        if (isLeapYear()) {
            for (int i = 1; i < MONTH; i++) {
                daysSinceStart += DAYS_IN_MONTH_LY[i - 1];
            }
        } else {
            for (int i = 1; i < MONTH; i++) {
                daysSinceStart += DAYS_IN_MONTH_NLY[i - 1];
            }
        }
        daysSinceStart += (DAY - 1);
        minSinceStart += (daysSinceStart * MINS_IN_DAY);
        minSinceStart += (HOUR * MINS_IN_HOUR);
        minSinceStart += MINUTE;

        return minSinceStart;
    }

    public static int differenceInMin(DateTime start, DateTime end) {
        if (start != null && end != null) {
            if (!(start.isSetByString() && end.isSetByString())) {
                return -2; // indicates wrong parameters
            }
        } else {
            return -3; // represents null pointers
        }

        int minDiff = 0;
        int dayDiff = 0;
        for (int i = start.YEAR; i < end.YEAR; i++) {
            boolean isLeap = false;
            int P = i % 400;
            int Q = P % 100;
            int R = Q % 4;

            if (P == 0) {
                isLeap = true;
            } else if (Q == 0) {
                isLeap = false;
            } else if (R == 0) {
                isLeap = true;
            }
            if (isLeap) {
                dayDiff += 366;
            } else {
                dayDiff += 365;
            }
        }
        minDiff += (dayDiff * MINS_IN_DAY);
        minDiff += end.sinceStart();
        minDiff -= start.sinceStart();

        if (minDiff < 0) {
            return -1;
        } else {
            return minDiff;
        }
    }

    public static int differenceInMinModulus(DateTime start, DateTime end) {
        if (start != null && end != null) {
            if (!(start.isSetByString() && end.isSetByString())) {
                return -2; // indicates wrong parameters
            }
        } else {
            return -3; // represents null pointers
        }

        int minDiff = 0;
        int dayDiff = 0;
        for (int i = start.YEAR; i < end.YEAR; i++) {
            boolean isLeap = false;
            int P = i % 400;
            int Q = P % 100;
            int R = Q % 4;

            if (P == 0) {
                isLeap = true;
            } else if (Q == 0) {
                isLeap = false;
            } else if (R == 0) {
                isLeap = true;
            }
            if (isLeap) {
                dayDiff += 366;
            } else {
                dayDiff += 365;
            }
        }
        minDiff += (dayDiff * MINS_IN_DAY);
        minDiff += end.sinceStart();
        minDiff -= start.sinceStart();

        if (minDiff < 0) {
            return -1 * minDiff;
        } else {
            return minDiff;
        }
    }

    public static int differenceInMinWithSign(DateTime start, DateTime end) {
        if (start != null && end != null) {
            if (!(start.isSetByString() && end.isSetByString())) {
                return -2; // indicates wrong parameters
            }
        } else {
            return -3; // represents null pointers
        }

        //TODO : check whether start and end are null or not
        int minDiff = 0;
        int dayDiff = 0;
        for (int i = start.YEAR; i < end.YEAR; i++) {
            boolean isLeap = false;
            int P = i % 400;
            int Q = P % 100;
            int R = Q % 4;

            if (P == 0) {
                isLeap = true;
            } else if (Q == 0) {
                isLeap = false;
            } else if (R == 0) {
                isLeap = true;
            }
            if (isLeap) {
                dayDiff += 366;
            } else {
                dayDiff += 365;
            }
        }
        minDiff += (dayDiff * MINS_IN_DAY);
        minDiff += end.sinceStart();
        minDiff -= start.sinceStart();

        return minDiff;
    }

    public boolean isSetByString() {
        if (YEAR == 0 && MONTH == 0 && DAY == 0 && HOUR == 0 && MINUTE == 0) {
            return false;
        }
        return true;
    }

    public static boolean isGreater(DateTime A, DateTime B) {
        // returns boolean A>B
        if (A.YEAR > B.YEAR) return true;
        if (A.YEAR < B.YEAR) return false;

        if (A.MONTH > B.MONTH) return true;
        if (A.MONTH < B.MONTH) return false;

        if (A.DAY > B.DAY) return true;
        if (A.DAY < B.DAY) return false;

        if (A.HOUR > B.HOUR) return true;
        if (A.HOUR < B.HOUR) return false;

        if (A.MINUTE > B.MINUTE) return true;
        if (A.MINUTE < B.MINUTE) return false;

        return false;
    }

    public void setTimeInput(TimeInput time) {
        time.selectedHour = HOUR;
        time.selectedMin = MINUTE;
        time.selectingDone = true;
    }

    public TimeInput getTimeInput() {
        TimeInput time = new TimeInput(this);
        return time;
    }

    public DateInput getDateInput() {
        DateInput date = new DateInput(this);
        return date;
    }

    public void setDateInput(DateInput date) {
        date.selectedDay = DAY;
        date.selectedMonth = MONTH;
        date.selectedYear = YEAR;
        date.selectingDone = true;
    }

    public void overflowConverter(){
        HOUR += MINUTE / DateTime.MINS_IN_HOUR;
        MINUTE = MINUTE % DateTime.MINS_IN_HOUR;
        DAY += HOUR / DateTime.HOURS_IN_DAY;
        HOUR = HOUR % DateTime.HOURS_IN_DAY;

        while (true) {
            if (isLeapYear()) {
                while (DAY > DateTime.DAYS_IN_MONTH_LY[MONTH - 1]) {
                    DAY -= DateTime.DAYS_IN_MONTH_LY[MONTH - 1];
                    MONTH += 1;
                    if (MONTH > 12) {
                        MONTH = 1;
                        YEAR += 1;
                        break;
                    }
                }
                if(!(DAY > DateTime.DAYS_IN_MONTH_LY[MONTH - 1])){
                    break;
                }
            }
            else {
                while (DAY > DateTime.DAYS_IN_MONTH_NLY[MONTH - 1]) {
                    DAY -= DateTime.DAYS_IN_MONTH_NLY[MONTH - 1];
                    MONTH += 1;
                    if (MONTH > 12) {
                        MONTH = 1;
                        YEAR += 1;
                        break;
                    }
                }
                if(!(DAY > DateTime.DAYS_IN_MONTH_NLY[MONTH - 1])){
                    break;
                }
            }

        }
    }

    public void underflowConverter() {
        while(!(MINUTE >= 0)){
            MINUTE += 60;
            HOUR--;
        }
        while(!(HOUR >= 0)){
            HOUR += 24;
            DAY--;
        }
        while(!(DAY > 0)){
            MONTH--;
            if(MONTH == 0){
                MONTH =12;
                YEAR--;
            }
            if(isLeapYear()) DAY += DAYS_IN_MONTH_LY[MONTH-1];
            else DAY += DAYS_IN_MONTH_NLY[MONTH-1];
        }
    }

    public static DateTime getDateTime(DateTime input, int minutes) {
        DateTime ret = new DateTime(input);
        ret.MINUTE += minutes;
        if(minutes >= 0)ret.overflowConverter();
        else ret.underflowConverter();
        return ret;
    }

    public static DateTime maximum(DateTime A,DateTime B) {
        if(DateTime.isGreater(A,B)) return A;
        else return B;
    }

    public static DateTime minimum(DateTime A,DateTime B) {
        if(DateTime.isGreater(A,B)) return B;
        else return A;
    }
}