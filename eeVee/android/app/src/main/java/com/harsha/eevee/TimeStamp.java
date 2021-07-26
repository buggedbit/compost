package com.harsha.eevee;

import java.util.Calendar;

public class TimeStamp {
    public int YEAR;
    public int MONTH;
    public int DAY;
    public int HOUR;
    public int MINUTE;
    public int SECOND;

    public TimeStamp() {
    }

    public TimeStamp(int YEAR, int MONTH, int DAY, int HOUR, int MINUTE, int SECOND) {
        this.YEAR = YEAR;
        this.MONTH = MONTH;
        this.DAY = DAY;
        this.HOUR = HOUR;
        this.MINUTE = MINUTE;
        this.SECOND = SECOND;
    }

    public TimeStamp(boolean setToPresent) {
        if (setToPresent == true) {
            Calendar currentTime = Calendar.getInstance();
            SECOND = currentTime.get(Calendar.SECOND);
            MINUTE = currentTime.get(Calendar.MINUTE);
            HOUR = currentTime.get(Calendar.HOUR_OF_DAY);
            DAY = currentTime.get(Calendar.DAY_OF_MONTH);
            MONTH = currentTime.get(Calendar.MONTH) + 1;
            YEAR = currentTime.get(Calendar.YEAR);
        } else {

        }
    }


    //String Format = YYYY-MonMon-DD HH:MinMin:SS
    public TimeStamp(String dateString) {
        if (dateString != null && !dateString.matches("")) {
            String[] space = dateString.split(" ");
            String[] hyphen = space[0].split("-");
            String[] colon = space[1].split(":");

            int total = hyphen.length + colon.length;

            if (total == 6) {
                YEAR = Integer.parseInt(hyphen[0]);
                MONTH = Integer.parseInt(hyphen[1]);
                DAY = Integer.parseInt(hyphen[2]);
                HOUR = Integer.parseInt(colon[0]);
                MINUTE = Integer.parseInt(colon[1]);
                SECOND = Integer.parseInt(colon[2]);
            }
        } else {

        }
    }

    public TimeStamp(TimeStamp reference) {
        YEAR = reference.YEAR;
        MONTH = reference.MONTH;
        DAY = reference.DAY;
        HOUR = reference.HOUR;
        MINUTE = reference.MINUTE;
        SECOND = reference.SECOND;
    }

    public static boolean isGreater(TimeStamp A, TimeStamp B) {
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

        if (A.SECOND > B.SECOND) return true;
        if (A.SECOND < B.SECOND) return false;

        return false;
    }

    public String toString() {
        String timeStampFormat = "";

        timeStampFormat += String.valueOf(YEAR);
        timeStampFormat += "-";
        timeStampFormat += String.valueOf(MONTH);
        timeStampFormat += "-";
        timeStampFormat += String.valueOf(DAY);
        timeStampFormat += " ";
        timeStampFormat += String.valueOf(HOUR);
        timeStampFormat += ":";
        timeStampFormat += String.valueOf(MINUTE);
        timeStampFormat += ":";
        timeStampFormat += String.valueOf(SECOND);

        return timeStampFormat;
    }
}
