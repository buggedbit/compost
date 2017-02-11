package com.harsha.eevee;

public class TimeInput {
    //public static int timeDispFormat = 1;
    public int selectedHour;
    public int selectedMin;
    public boolean selectingDone;

    public TimeInput() {
        InitialiseTimeVariables();
    }

    public TimeInput(DateTime dateTime){
        selectedHour = dateTime.HOUR;
        selectedMin = dateTime.MINUTE;
        selectingDone = false;
    }

    public String timeDispString(){
        if(selectedHour!=-1 && selectedMin!=-1){
            int Hour = selectedHour;
            if(Hour>12) Hour -= 12;
            if(Hour==0) Hour += 12;
            String hour = "" + Hour;
            if(Hour < 10) hour = "0" + hour;
            String min = "" + selectedMin;
            if(selectedMin < 10) min = "0" + min;
            String AM_PM = "AM";
            if(selectedHour >= 12) AM_PM = "PM";
            return (hour + ":" + min + " " + AM_PM);
        }
        return "";
    }

    public String timeStoreString() {
        if(selectedHour!=-1 && selectedMin!=-1) return (selectedHour + ":" + selectedMin);
        return "TIME_NOT_SET";
    }

    public void InitialiseTimeVariables() {
        selectedHour = -1;
        selectedMin = -1;
        selectingDone = false;
    }

    public static boolean isGreater(TimeInput A , TimeInput B) {
        // returns A>B
        if(A.selectedHour > B.selectedHour) return true;
        if(A.selectedHour < B.selectedHour) return false;
        if(A.selectedMin > B.selectedMin) return true;
        if(A.selectedMin < B.selectedMin) return false;
        return false;
    }
}