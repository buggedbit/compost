package com.harsha.eevee;

public class DurationInput {
    public int day;
    public int hour;
    public int min;
    public int minDays;

    public DurationInput() {
        initialiseVariables();
    }

    public void initialiseVariables() {
        day = 0;
        hour = 2;
        min = 0;
        minDays = 0;
    }

    public String durationDispString() {
        return ("" + day + "d " + hour + "h " + min + "m");
    }
}