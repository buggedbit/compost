package friends.eevee;

import android.util.Log;
import java.util.Calendar;

public class Date extends java.util.Date{

    public int $YEAR = -1;  // 1 - +inf
    public int $MONTH = -1; // 1 - 12
    public int $DAY = -1;   // 1 - 31

    /**
     * std form is such that if
     * std_year * 365 + std_month * 30 + std_day + leap_extras + month_extras
     * will give (no of days passed from 01 year 01 month 01 day) + 1
     * OR
     * (no of days passed from 01 year 01 month 01 day) including the given day
     * */
    // from 0001 . 01 . 01
    public int $LEAP_EXTRA_DAYS = -1;    // due to leap years
    public int $MONTH_EXTRA_DAYS = -1;   // due to varying no days in months , equals
    public int $STD_YEAR = -1;
    public int $STD_MONTH = -1;
    public int $STD_DAY = -1;

    public static final int DAYS_IN_STD_MONTH = 30;
    public static final int DAYS_IN_STD_YEAR = 365;
    public static final int[] STD_MONTH_EXTRAS_ARRAY = {1 , -1 , 0 , 0 , 1 , 1 , 2 , 3 , 3 , 4 , 4 , 5};

    public final String[] MONTH_NAMES = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
    public final String[] SUFFIXES = {"st", "nd", "rd", "th"};
    public static final int[] DAYS_IN_MONTH_NLY = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
    public static final int[] DAYS_IN_MONTH_LY = {31, 29, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};

    public Date() {
        unsetDate();
    }

    public Date(boolean setToPresent) {
        if (setToPresent) {
            Calendar currentTime = Calendar.getInstance();
            this.$DAY = currentTime.get(Calendar.DAY_OF_MONTH);
            this.$MONTH = currentTime.get(Calendar.MONTH) + 1;
            this.$YEAR = currentTime.get(Calendar.YEAR);
        }
    }

    //String Format = YYYY<separator>MonMon<separator>DD<separator>
    //if not in any of these then the default initialization
    public Date(String dateString , String separator) {

        String error = "Date: not a proper Date object initialization with string " + dateString + " and with separator " + separator;

        if (dateString != null && !dateString.matches("") && separator != null && !separator.matches("")) {
            String[] dateComponents = dateString.split(separator);
            if (dateComponents.length >= 3) {
                try {
                    this.$YEAR = Integer.parseInt(dateComponents[0]);
                    this.$MONTH = Integer.parseInt(dateComponents[1]);
                    this.$DAY = Integer.parseInt(dateComponents[2]);
                    if(!this.isValid()){
                        this.unsetDate();
                 Log.i(ZeroLog.TAG, error);
                    }
                }catch (Exception e){
                 Log.i(ZeroLog.TAG, error);
                    unsetDate();
                }
            }
            else {
             Log.i(ZeroLog.TAG, error);
                unsetDate();
            }
        }
        else {
         Log.i(ZeroLog.TAG, error);
            unsetDate();
        }
    }

    //String Format = YYYY<IN-separator>MonMon<IN-separator>DD<OUT-separator>
    //if not in any of these then the default initialization
    public Date(String dateString , String inlineSeparator , String outlineSeparator) {

        String error = "Date: not a proper Date object initialization with string " + dateString + " and with inline-separator " + inlineSeparator + " and with outline-separator " + outlineSeparator;

        if (dateString != null && !dateString.matches("")  && inlineSeparator != null && !inlineSeparator.matches("") && outlineSeparator != null && !outlineSeparator.matches("")) {
            String[] datePart = dateString.split(outlineSeparator);
            String[] dateComponents = datePart[0].split(inlineSeparator);
            if (dateComponents.length >= 3) {
                try {
                    this.$YEAR = Integer.parseInt(dateComponents[0]);
                    this.$MONTH = Integer.parseInt(dateComponents[1]);
                    this.$DAY = Integer.parseInt(dateComponents[2]);
                    if(!this.isValid()){
                     Log.i(ZeroLog.TAG, error);
                        this.unsetDate();
                    }
                }catch (Exception e){
                 Log.i(ZeroLog.TAG, error);
                    unsetDate();
                }
            }
            else {
             Log.i(ZeroLog.TAG, error);
                unsetDate();
            }
        }
        else {
         Log.i(ZeroLog.TAG, error);
            unsetDate();
        }
    }

    public Date(Date reference){
        this.$YEAR = reference.$YEAR;
        this.$MONTH = reference.$MONTH;
        this.$DAY = reference.$DAY;

        String error = "Date: not a proper Date Object Initialization with reference object YYYY_MM_DD" + String.valueOf(reference.$YEAR) + " " + String.valueOf(reference.$MONTH) + " " + String.valueOf(reference.$DAY);
        if(!this.isValid()) {
            unsetDate();
            Log.i(ZeroLog.TAG, error);
        }
    }

    // DEFAULT INITIALIZATION
    public void unsetDate() {
        this.$YEAR= -1;
        this.$MONTH = -1;
        this.$DAY = -1;
    }

    public boolean isValid(){
        if(this.$YEAR < 1)return false;
        if(this.$MONTH < 1 || this.$MONTH > 12)return false;
        switch (this.$MONTH%2){
            case 1:
                if(this.$MONTH >= 9) return !(this.$DAY < 1 || this.$DAY > 30);
                else return !(this.$DAY < 1 || this.$DAY > 31);
            case 0:
                if (this.$MONTH == 2){
                    if(this.$YEAR%400 == 0)return !(this.$DAY < 1 || this.$DAY > 29);
                    else if(this.$YEAR%100 == 0)return !(this.$DAY < 1 || this.$DAY > 28);
                    else if(this.$YEAR%4 == 0)return !(this.$DAY < 1 || this.$DAY > 29);
                }
                else if(this.$MONTH >= 8)return !(this.$DAY < 1 || this.$DAY > 31);
                else if(this.$MONTH < 8) return !(this.$DAY < 1 || this.$DAY > 30);
            default:
                return false;
        }
    }

    public boolean isSet(){
        if(this.$YEAR == -1)return false;
        if(this.$MONTH == -1)return false;
        if(this.$DAY == -1)return false;

        return true;
    }

    private boolean isLeapYear() {
        int P = this.$YEAR % 400;
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

    public String formalRepresentation(){
        if(isSet()){
            String suffix;
            String MonthName;
            MonthName = MONTH_NAMES[this.$MONTH -1];
            if ((this.$DAY -1) % 10 < 3) suffix = SUFFIXES[(this.$DAY -1) % 10];
            else suffix = SUFFIXES[3];
            if (this.$DAY == 11 || this.$DAY == 12 || this.$DAY == 13)
                suffix = "th";
            return (this.$DAY + suffix + " " + MonthName + " " + this.$YEAR);
        }
        return "The date is not properly set \n";
    }

    public static boolean isGreater(Date A , Date B) {
        // returns A>B
        if(A.$YEAR > B.$YEAR) return true;
        else if(A.$YEAR < B.$YEAR) return false;
        if(A.$MONTH > B.$MONTH) return true;
        else if(A.$MONTH < B.$MONTH) return false;
        if(A.$DAY > B.$DAY) return true;
        else if(A.$DAY < B.$DAY) return false;
        return false;
    }

    public boolean makeStdForm(){
        if(!this.isValid())return false;

        // valid Date
        this.$STD_DAY = this.$DAY;
        // month related issues
        this.$STD_MONTH = this.$MONTH - 1;
        if(this.$MONTH > 1) this.$MONTH_EXTRA_DAYS = Date.STD_MONTH_EXTRAS_ARRAY[this.$MONTH - 2];//-2 bcz   // 1 for array start
        else this.$MONTH_EXTRA_DAYS = 0;                                                                     // 1 for previous month
        // year related issues
        this.$STD_YEAR = this.$YEAR - 1;
        this.$LEAP_EXTRA_DAYS = this.$YEAR / 4 - this.$YEAR / 100 + this.$YEAR / 400;
        if(this.$MONTH < 3 && this.$LEAP_EXTRA_DAYS > 0)this.$LEAP_EXTRA_DAYS--;

        return true;
    }

    public void addDays(int noDays){

        boolean toTheFuture = true;
        if(noDays == 0)return;
        else if(noDays < 0)toTheFuture = false;

        if(toTheFuture){

        }
        else {

        }
    }

    // returns A - B in days with sign
    public static long daysFromSecondToFirst(Date A , Date B){
        if(!A.isValid() || !B.isValid())return 0;
        A.makeStdForm();
        B.makeStdForm();
        return (A.$STD_YEAR - B.$STD_YEAR) * Date.DAYS_IN_STD_YEAR + (A.$STD_MONTH - B.$STD_MONTH) * Date.DAYS_IN_STD_MONTH + (A.$STD_DAY - B.$STD_DAY) + (A.$LEAP_EXTRA_DAYS - B.$LEAP_EXTRA_DAYS) + (A.$MONTH_EXTRA_DAYS - B.$MONTH_EXTRA_DAYS);
    }

//    public static void main(String[] args){
//        Date d1 = new Date(true);
//        Date d2 = new Date("2017 09 14-" , " " ,"-");
//        System.out.print(Date.daysFromSecondToFirst(d1,d2));

//        String b = "100 2 29-";
//        String c = "4 2 29-";
//        String d = "1 2 29-";
//        Date e = new Date(a , " " , "-");
//        Date f = new Date(b , " " , "-");
//        Date g = new Date(c , " " , "-");
//        Date h = new Date(d , " " , "-");
//        System.out.print(e.isValid());
//        System.out.print(f.isValid());
//        System.out.print(g.isValid());
//        System.out.print(h.isValid());
//        Date d2 = new Date("2016:09:14");
//
//        System.out.print(d2.formalRepresentation());
//        System.out.print(d2.formalRepresentation());
//        System.out.print("hello world\n");
//        System.out.print(Date.isGreater(d1,d2));
//    }




}


