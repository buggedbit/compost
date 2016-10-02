package friends.eevee.Calender;

import android.util.Log;

import java.util.Calendar;

import friends.eevee.ZeroLog;

// 01/01/0001 A.D. is the least date supported
public class Date {

    public int $YEAR = -1;  // 1 - +inf
    public int $MONTH = -1; // 1 - 12
    public int $DAY = -1;   // 1 - 31

    // Epoch is 01-01-0001
    // Do not access dates before the Epoch
    /**
     * std form is such that if
     * std_year * 365 + std_month * 30 + std_day + leap_extras + month_extras
     * will give (no of days passed from 01 year 01 month 01 day) + 1
     */
    // from 01 . 01 . 0001
    public int $LEAP_EXTRA_DAYS = -1;    // due to leap years
    public int $MONTH_EXTRA_DAYS = -1;   // due to varying no days in months , equals
    public int $STD_YEAR = -1;
    public int $STD_MONTH = -1;
    public int $STD_DAY = -1;

    // constructors
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

    //String Format = "DD<separator>MonMon<separator>YYYY<separator>...
    //String Format = "DD<separator>MonMon<separator>YYYY"
    //if not in any of these then the default initialization
    //NOTE : preferably do not use unknown separators DEFINITELY NOT "."
    public Date(String dateString, String separator) {

        Log.i(ZeroLog.TAG, dateString + "==>" + separator);

        String error = "Date: not a proper Date object initialization with string " + dateString + " and with separator " + separator;

        if (dateString != null && !dateString.matches("") && separator != null && !separator.matches("")) {
            String[] dateComponents = dateString.split(separator);
            if (dateComponents.length >= 3) {
                try {
                    this.$YEAR = Integer.parseInt(dateComponents[2]);
                    this.$MONTH = Integer.parseInt(dateComponents[1]);
                    this.$DAY = Integer.parseInt(dateComponents[0]);
                    if (!this.isValid()) {
                        this.unsetDate();
                        Log.i(ZeroLog.TAG, error);
                    }
                } catch (Exception e) {
                    Log.i(ZeroLog.TAG, error);
                    unsetDate();
                }
            } else {
                Log.i(ZeroLog.TAG, error);
                unsetDate();
            }
        } else {
            Log.i(ZeroLog.TAG, error);
            unsetDate();
        }
    }

    //String Format = DD<IN-separator>MonMon<IN-separator>YYYY<OUT-separator>
    //if not in any of these then the default initialization
    public Date(String dateString, String inlineSeparator, String outlineSeparator) {

        String error = "Date: not a proper Date object initialization with string " + dateString + " and with inline-separator " + inlineSeparator + " and with outline-separator " + outlineSeparator;

        if (dateString != null && !dateString.matches("") && inlineSeparator != null && !inlineSeparator.matches("") && outlineSeparator != null && !outlineSeparator.matches("")) {
            String[] outlineFilter = dateString.split(outlineSeparator);
            String[] dateComponents = outlineFilter[0].split(inlineSeparator);
            if (dateComponents.length >= 3) {
                try {
                    this.$YEAR = Integer.parseInt(dateComponents[2]);
                    this.$MONTH = Integer.parseInt(dateComponents[1]);
                    this.$DAY = Integer.parseInt(dateComponents[0]);
                    if (!this.isValid()) {
                        Log.i(ZeroLog.TAG, error);
                        this.unsetDate();
                    }
                } catch (Exception e) {
                    Log.i(ZeroLog.TAG, error);
                    unsetDate();
                }
            } else {
                Log.i(ZeroLog.TAG, error);
                unsetDate();
            }
        } else {
            Log.i(ZeroLog.TAG, error);
            unsetDate();
        }
    }

    public Date(Date reference) {
        this.$YEAR = reference.$YEAR;
        this.$MONTH = reference.$MONTH;
        this.$DAY = reference.$DAY;

        this.$LEAP_EXTRA_DAYS = reference.$LEAP_EXTRA_DAYS;     // due to leap years
        this.$MONTH_EXTRA_DAYS = reference.$MONTH_EXTRA_DAYS;   // due to varying no days in months , equals
        this.$STD_YEAR = reference.$STD_YEAR;
        this.$STD_MONTH = reference.$STD_MONTH;
        this.$STD_DAY = reference.$STD_DAY;

        if (!this.isValid()) {
            String error = "Date: not a proper Date Object Initialization with reference object DD/MM/YYYY " + reference.getDateString();
            unsetDate();
            Log.i(ZeroLog.TAG, error);
        }
    }

    // DEFAULT INITIALIZATION
    public void unsetDate() {
        this.$YEAR = -1;
        this.$MONTH = -1;
        this.$DAY = -1;

        this.$LEAP_EXTRA_DAYS = -1;    // due to leap years
        this.$MONTH_EXTRA_DAYS = -1;   // due to varying no days in months , equals
        this.$STD_YEAR = -1;
        this.$STD_MONTH = -1;
        this.$STD_DAY = -1;
    }
    //

    // identifiers
    // 01/01/0001 A.D. is the least date supported
    public boolean isValid() {
        if (this.$YEAR < 1) return false;
        if (this.$MONTH < 1 || this.$MONTH > 12) return false;
        switch (this.$MONTH % 2) {
            case 1:
                if (this.$MONTH >= 9) return !(this.$DAY < 1 || this.$DAY > 30);
                else return !(this.$DAY < 1 || this.$DAY > 31);
            case 0:
                if (this.$MONTH == 2) {
                    if (this.isLeapYear()) return !(this.$DAY < 1 || this.$DAY > 29);
                    else {
                        return !(this.$DAY < 1 || this.$DAY > 28);
                    }
                } else if (this.$MONTH >= 8) return !(this.$DAY < 1 || this.$DAY > 31);
                else if (this.$MONTH < 8) return !(this.$DAY < 1 || this.$DAY > 30);
            default:
                return false;
        }
    }

    public boolean isSet() {
        if (this.$YEAR == -1) return false;
        if (this.$MONTH == -1) return false;
        if (this.$DAY == -1) return false;

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
    //

    // formatter
    public String formalRepresentation() {
        if (isValid()) {
            String suffix;
            String MonthName;
            MonthName = Constants.MONTH_NAMES[this.$MONTH - 1];
            if ((this.$DAY - 1) % 10 < 3) suffix = Constants.SUFFIXES[(this.$DAY - 1) % 10];
            else suffix = Constants.SUFFIXES[3];
            if (this.$DAY == 11 || this.$DAY == 12 || this.$DAY == 13)
                suffix = "th";
            return (this.$DAY + suffix + " " + MonthName + " " + this.$YEAR);
        }
        return "The date is not properly set ";
    }

    public String simpleRepresentation() {
        if (this.isValid()) {
            return this.$DAY + "//" + this.$MONTH + "//" + this.$YEAR;
        }
        return "The date is not properly set ";
    }

    public String getDateString() {
        return this.$DAY + "//" + this.$MONTH + "//" + this.$YEAR;
    }
    //

    public void printState() {
        System.out.print("\n year : ");
        System.out.print(this.$YEAR);
        System.out.print("\n month : ");
        System.out.print(this.$MONTH);
        System.out.print("\n day : ");
        System.out.print(this.$DAY);
        System.out.print("\n std_year : ");
        System.out.print(this.$STD_YEAR);
        System.out.print("\n std_month : ");
        System.out.print(this.$STD_MONTH);
        System.out.print("\n std_day : ");
        System.out.print(this.$STD_DAY);
        System.out.print("\n leap_extras : ");
        System.out.print(this.$LEAP_EXTRA_DAYS);
        System.out.print("\n month_extras : ");
        System.out.print(this.$MONTH_EXTRA_DAYS);
        System.out.print("\n");

    }

    public void printDate() {
        System.out.print("\n");
        System.out.print(this.$DAY);
        System.out.print("/");
        System.out.print(this.$MONTH);
        System.out.print("/");
        System.out.print(this.$YEAR);
        System.out.print("\n");
    }

    // comparisons
    // These functions compare without validity check
    public static boolean isFuture(Date A, Date B) {
        // returns A>B
        if (A.$YEAR > B.$YEAR) return true;
        else if (A.$YEAR < B.$YEAR) return false;
        if (A.$MONTH > B.$MONTH) return true;
        else if (A.$MONTH < B.$MONTH) return false;
        if (A.$DAY > B.$DAY) return true;
        else if (A.$DAY < B.$DAY) return false;
        return false;
    }

    public static boolean isSame(Date A, Date B) {
        return A.$DAY == B.$DAY && A.$MONTH == B.$MONTH && A.$YEAR == B.$YEAR;
    }

    public static boolean isPast(Date A, Date B) {
        return Date.isFuture(B, A);
    }

    public boolean isFutureTo(Date B) {
        // returns this>B
        return Date.isFuture(this, B);
    }

    public boolean isPastTo(Date B) {
        // returns this<B
        return Date.isPast(this, B);
    }

    public boolean isEqualTo(Date B) {
        // returns this==B
        return Date.isSame(this, B);
    }
    //

    // Helpers
    private boolean prepareStdForm() {
        if (!this.isValid()) return false;
        // valid Date
        // day related issues
        this.$STD_DAY = this.$DAY;
        // month related issues
        this.$STD_MONTH = this.$MONTH - 1;
        if (this.$MONTH > 1)
            this.$MONTH_EXTRA_DAYS = Constants.STD_MONTH_EXTRAS_ARRAY[this.$MONTH - 2];//-2 bcz   // 1 for array start
        else this.$MONTH_EXTRA_DAYS = 0;
        // year related issues
        this.$STD_YEAR = this.$YEAR - 1;
        this.$LEAP_EXTRA_DAYS = this.$YEAR / 4 - this.$YEAR / 100 + this.$YEAR / 400;
        if (this.isLeapYear() && this.$MONTH < 3 && this.$LEAP_EXTRA_DAYS > 0)
            this.$LEAP_EXTRA_DAYS--;

        return true;
    }

    // Epoch is 0001-01-01
    // Do not access dates before the Epoch
    // if the Date is 0001-01-01 the result is 0
    public long daysFromEpoch() {
        if (this.prepareStdForm())
            return (this.$STD_YEAR) * Constants.DAYS_IN_STD_YEAR + (this.$STD_MONTH) * Constants.DAYS_IN_STD_MONTH + (this.$STD_DAY) + (this.$LEAP_EXTRA_DAYS) + (this.$MONTH_EXTRA_DAYS) - 1;// refer to std form definition
        // else return -1 , means invalid Date
        return -1;
    }

    private void cloneFromStdForm() {
        this.$YEAR = this.$STD_YEAR + 1;
        this.$MONTH = this.$STD_MONTH + 1;
        this.$DAY = this.$STD_DAY;

        if (!this.isValid()) this.unsetDate();
    }
    //

    // difference
    // returns A - B in days with sign
    public static long dayDifferenceSecondToFirst(Date A, Date B) {
        if (!A.isValid() || !B.isValid()) return 0;
        A.prepareStdForm();
        B.prepareStdForm();
        return (A.$STD_YEAR - B.$STD_YEAR) * Constants.DAYS_IN_STD_YEAR + (A.$STD_MONTH - B.$STD_MONTH) * Constants.DAYS_IN_STD_MONTH + (A.$STD_DAY - B.$STD_DAY) + (A.$LEAP_EXTRA_DAYS - B.$LEAP_EXTRA_DAYS) + (A.$MONTH_EXTRA_DAYS - B.$MONTH_EXTRA_DAYS);
    }

    // returns PARAM TO this in days with sign
    public long dayDifferenceFrom(Date param) {
        return Date.dayDifferenceSecondToFirst(this,param);
    }

    // returns this TO PARAM in days with sign
    public long dayDifferenceTo(Date param) {
        return Date.dayDifferenceSecondToFirst(param,this);
    }
    //

    // modifiers
    // do not access dates before 01/01/0001
    public boolean toTomorrow() {
        if (!this.isValid()) return false;
        if (this.isLeapYear()) {
            if (this.$DAY + 1 > Constants.DAYS_IN_MONTH_LY[this.$MONTH - 1]) {
                if (this.$MONTH + 1 > 12) {
                    this.$YEAR += 1;
                    this.$MONTH = 1;
                    this.$DAY = 1;
                } else {
                    this.$MONTH += 1;
                    this.$DAY = 1;
                    return true;
                }
            } else {
                this.$DAY += 1;
                return true;
            }
        } else {
            if (this.$DAY + 1 > Constants.DAYS_IN_MONTH_NLY[this.$MONTH - 1]) {
                if (this.$MONTH + 1 > 12) {
                    this.$YEAR += 1;
                    this.$MONTH = 1;
                    this.$DAY = 1;
                } else {
                    this.$MONTH += 1;
                    this.$DAY = 1;
                    return true;
                }
            } else {
                this.$DAY += 1;
                return true;
            }
        }
        return false;
    }

    // do not access dates before 01/01/0001
    public boolean toYesterday() {
        if (!this.isValid()) return false;
        if (this.isLeapYear()) {
            if (this.$DAY - 1 < 1) {
                if (this.$MONTH - 1 < 1) {
                    this.$YEAR -= 1;
                    this.$MONTH = 12;
                    this.$DAY = 31;
                } else {
                    this.$MONTH -= 1;
                    this.$DAY = Constants.DAYS_IN_MONTH_LY[this.$MONTH - 1];
                    return true;
                }
            } else {
                this.$DAY -= 1;
                return true;
            }
        } else {
            if (this.$DAY - 1 < 1) {
                if (this.$MONTH - 1 < 1) {
                    this.$YEAR -= 1;
                    this.$MONTH = 12;
                    this.$DAY = 31;
                } else {
                    this.$MONTH -= 1;
                    this.$DAY = Constants.DAYS_IN_MONTH_NLY[this.$MONTH - 1];
                    return true;
                }
            } else {
                this.$DAY -= 1;
                return true;
            }
        }
        return false;
    }

    // adds no days to this date
    // do not access dates before 01/01/0001
    // time complexity is LINEAR in no days to be added
    private boolean add(long noDays) {
        if (noDays < 0) return false;
        if (!this.isValid()) return false;
        for (int i = 0; i < noDays; i++) {
            this.toTomorrow();
        }
        return true;
    }

    // subtracts algebraic no days to this date
    // do not access dates before 01/01/0001
    // time complexity is LINEAR in no days to be subtracted
    private boolean subtract(long noDays) {
        if (noDays < 0) return false;
        if (!this.isValid()) return false;
        for (int i = 0; i < noDays; i++) {
            this.toYesterday();
        }
        return true;
    }

    /**
     * does its operation only if
     * the present object is valid &&
     * the object after algebraic addition is valid
     * otherwise
     * they do nothing to the object
     */
    // adds algebraic no days to this date
    // do not access dates before 01/01/0001
    // time complexity is LINEAR in no days to be added
    public boolean addDays(long noDays) {
        if (!this.isValid()) return false;
        if (this.daysFromEpoch() + noDays < 0) return false;
        if (noDays == 0) return true;
        else if (noDays > 0) return this.add(noDays);
        else return this.subtract(-noDays);
    }
    //


//    public static void main(String[] args){
//
//    }


}
