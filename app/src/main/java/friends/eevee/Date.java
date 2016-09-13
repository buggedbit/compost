package friends.eevee;

import android.util.Log;
import java.util.Calendar;

public class Date {

    public int $YEAR = -1;  // 1 - +inf
    public int $MONTH = -1; // 1 - 12
    public int $DAY = -1;   // 1 - 31

    public final String[] MONTH_NAMES = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
    public final String[] SUFFIXES = {"st", "nd", "rd", "th"};

    public Date() {
        UnsetDate();
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
                        this.UnsetDate();
                 Log.i(ZeroLog.TAG, error);
                    }
                }catch (Exception e){
                 Log.i(ZeroLog.TAG, error);
                    UnsetDate();
                }
            }
            else {
             Log.i(ZeroLog.TAG, error);
                UnsetDate();
            }
        }
        else {
         Log.i(ZeroLog.TAG, error);
            UnsetDate();
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
                        this.UnsetDate();
                    }
                }catch (Exception e){
                 Log.i(ZeroLog.TAG, error);
                    UnsetDate();
                }
            }
            else {
             Log.i(ZeroLog.TAG, error);
                UnsetDate();
            }
        }
        else {
         Log.i(ZeroLog.TAG, error);
            UnsetDate();
        }
    }

    public Date(Date reference){
        this.$YEAR = reference.$YEAR;
        this.$MONTH = reference.$MONTH;
        this.$DAY = reference.$DAY;

        String error = "Date: not a proper Date Object Initialization with reference object YYYY_MM_DD" + String.valueOf(reference.$YEAR) + " " + String.valueOf(reference.$MONTH) + " " + String.valueOf(reference.$DAY);
        if(!this.isValid()) {
            UnsetDate();
            Log.i(ZeroLog.TAG, error);
        }
    }

    // DEFAULT INITIALIZATION
    public void UnsetDate() {
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

//    public static void main(String[] args){
//        Date d1 = new Date(true);
//        String a = "400 2 29-";
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


