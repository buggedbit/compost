package org.yashasvi.calender4j.core.classes;

import static java.lang.Integer.parseInt;


// org.yashasvi.calender4j.core.classes.Duration class is explicitly defined for maintaining difference in dateTime
// So use develop it accordingly
public class Duration {
    // as there is no such thing as a VALID org.yashasvi.calender4j.core.classes.Duration
    // restrict with only a
    // public org.yashasvi.calender4j.core.classes.Duration(long $24hr,int $sec) constructor
    // i.e as there is no default constructor,
    // the org.yashasvi.calender4j.core.classes.Duration is always VALID and SET whenever it is initialized

    public long $24hr = 0L;
    public long $sec = 0L;

    public Duration(long $24hr, int $sec) {
        this.$24hr = $24hr;
        this.$sec = $sec;
    }

    public Duration(long $24hr, long $sec) {
        this.$24hr = $24hr;
        this.$sec = $sec;
    }

    //String format = x<space>hrs<space>y<space>min
    public Duration(String dateTimeDiffString) {
        String[] comp = dateTimeDiffString.split(Constants.SPACE_SEP);
        try {
            int hr = parseInt(comp[0]);
            int min = parseInt(comp[2]);

            this.$24hr = 0;
            this.$sec = hr * Constants.SECONDS_IN_HOUR + min * Constants.SECONDS_IN_MINUTE;
        } catch (Exception e) {
            // Bad Input
        }
    }

    //String Format = Days<sep>Seconds<sep>
    //String Format = Days<sep>Seconds
    public Duration(String dateTimeDiffString, String sep) {

        String error = "org.yashasvi.calender4j.core.classes.Duration: not a proper org.yashasvi.calender4j.core.classes.Duration object initialization with "
                + "\n string " + dateTimeDiffString
                + "\n and with separator " + sep;

        if (dateTimeDiffString != null && !dateTimeDiffString.matches("")
                && sep != null && !sep.matches("")
                ) {
            String[] day_sec = dateTimeDiffString.split(sep);
            if (day_sec.length == 2) {
                try {
                    this.$24hr = Long.parseLong(day_sec[0]);
                    this.$sec = Long.parseLong(day_sec[1]);
                } catch (Exception e) {
                    // Bad Input
                }
            } else {
                // Bad Input
            }
        } else {
            // Bad Input
        }
    }

    public Duration(long minutes) {
        this.$24hr = minutes / Constants.MINUTES_IN_DAY;
        this.$sec = (minutes % Constants.MINUTES_IN_DAY) * Constants.SECONDS_IN_MINUTE;
    }

    public long weeksDiff() {
        return this.$24hr / 7;
    }

    public long secondsDiff() {
        return this.$24hr * Constants.SECONDS_IN_DAY + this.$sec;
    }

    public long minutesDiff() {
        return this.$24hr * Constants.MINUTES_IN_DAY + this.$sec / Constants.SECONDS_IN_MINUTE;
    }

    public String daySecRepresentation() {
        return this.$24hr + Constants.SPACE_SEP + "day"
                + Constants.SPACE_SEP + this.$sec + Constants.SPACE_SEP + "second";
    }

    public String hourMinRepresentation() {
        long _hr = (int) (this.$24hr * Constants.HOURS_IN_DAY);
        _hr = _hr + this.$sec / Constants.SECONDS_IN_HOUR;
        int _min = (int) (this.$sec % Constants.SECONDS_IN_HOUR) / Constants.SECONDS_IN_MINUTE;

        return _hr + Constants.SPACE_SEP + "hr" + Constants.SPACE_SEP
                + _min + Constants.SPACE_SEP + "min";
    }

    public static boolean isValidHrMinRepresentation(String dateTimeDiffString) {
        String[] comp = dateTimeDiffString.split(Constants.SPACE_SEP);
        try {
            Integer.parseInt(comp[0]);
            Integer.parseInt(comp[2]);
        } catch (Exception e) {
            // Bad Input
        }
        return false;
    }
}
