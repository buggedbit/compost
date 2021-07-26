package com.harsha.eevee;

public class DateInput {
    //public static int dateDispFormat = 1;
    public int selectedYear;
    public int selectedMonth;
    public int selectedDay;
    public boolean selectingDone;
    public final String[] MonthNames = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
    public final String[] Suffixes = {"st", "nd", "rd", "th"};

    public DateInput() {
        InitialiseDateVariables();
    }

    public DateInput(DateTime dateTime){
        selectedYear = dateTime.YEAR;
        selectedMonth = dateTime.MONTH;
        selectedDay = dateTime.DAY;
        selectingDone = false;
    }

    public String dateStoreString(){
        if(selectingDone) return ("" + selectedYear + ":" + selectedMonth + ":" + selectedDay);
        return "DATE_NOT_SET";
    }

    public String dateDispString(){
        if(selectingDone){
            String suffix;
            String MonthName;
            MonthName = MonthNames[selectedMonth-1];
            if ((selectedDay -1) % 10 < 3) suffix = Suffixes[(selectedDay -1) % 10];
            else suffix = Suffixes[3];
            if (selectedDay == 11 || selectedDay == 12 || selectedDay == 13)
                suffix = "th";
            return ("" + selectedDay + suffix + " " + MonthName + " " + selectedYear);
        }
        return "";
    }

    public void InitialiseDateVariables() {
        selectedYear = -1;
        selectedMonth = -1;
        selectedDay = -1;
        selectingDone = false;
    }

    public static boolean isGreater(DateInput A , DateInput B) {
        // returns A>B
        if(A.selectedYear > B.selectedYear) return true;
        if(A.selectedYear < B.selectedYear) return false;
        if(A.selectedMonth > B.selectedMonth) return true;
        if(A.selectedMonth < B.selectedMonth) return false;
        if(A.selectedDay > B.selectedDay) return true;
        if(A.selectedDay < B.selectedDay) return false;
        return false;
    }
}