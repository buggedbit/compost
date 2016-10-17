package friends.eevee.Calender;

import android.util.Log;

import friends.eevee.Log.ZeroLog;

import static java.lang.Integer.parseInt;

// DateTimeDiff class is explicitly defined for maintaining difference in dateTime
// So use develop it accordingly
public class DateTimeDiff {
    // as there is no such thing as a VALID DateTimeDiff
    // restrict with only a
    // public DateTimeDiff(long $24hr,int $sec) constructor
    // i.e as there is no default constructor,
    // the DateTimeDiff is always VALID and SET whenever it is initialized

    public long $24hr = 0L;
    public long $sec = 0L;

    public DateTimeDiff(long $24hr,int $sec){
        this.$24hr = $24hr;
        this.$sec = $sec;
    }

    public DateTimeDiff(long $24hr,long $sec){
        this.$24hr = $24hr;
        this.$sec = $sec;
    }

    //String format = x<space>hrs<space>y<space>min
    // TODO : improve this function
    public DateTimeDiff(String dateTimeDiffString){
        String[] comp = dateTimeDiffString.split(Constants.SPACE_SEP);
        try{
            int hr = parseInt(comp[0]);
            int min = Integer.parseInt(comp[2]);

            this.$24hr = 0;
            this.$sec = hr * Constants.SECONDS_IN_HOUR + min * Constants.SECONDS_IN_MINUTE;
        }catch (Exception e){
            Log.i(ZeroLog.TAG, "DateTimeDiff: not a proper DateTimeDiff object initialization with " + dateTimeDiffString);
        }
    }

    //String Format = Days<sep>Seconds<sep>
    //String Format = Days<sep>Seconds
    public DateTimeDiff(String dateTimeDiffString, String sep) {

        String error = "DateTimeDiff: not a proper DateTimeDiff object initialization with "
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
                    Log.i(ZeroLog.TAG, error);
                }
            } else {
                Log.i(ZeroLog.TAG, error);
            }
        } else {
            Log.i(ZeroLog.TAG, error);
        }
    }


    public long weeksDiff(){
        return this.$24hr / 7;
    }

    public long secondsDiff(){
        return this.$24hr * Constants.SECONDS_IN_DAY + this.$sec;
    }

    public String daySecRepresentation(){
        return this.$24hr + " day(s) " + this.$sec + " second(s) ";
    }
}
