package com.harsha.eevee;

public class TaskDetails {
    private String _TimeStamp;
    private String _TaskDescription;
    private String _TaskTag;
    private String _StartDateTime;
    private String _EndDateTime;
    private String _Repetition;
    private String _Type;
    private String _StartsFromDailyOrWeekly;
    private String _EndsFromDailyOrWeekly;

    public TaskDetails() {
    }

    public String get_TimeStamp() {
        return _TimeStamp;
    }

    public void set_TimeStamp(String _TimeStamp) {
        this._TimeStamp = _TimeStamp;
    }

    public String get_TaskDescription() {
        return _TaskDescription;
    }

    public void set_TaskDescription(String _TaskDescription) {
        this._TaskDescription = _TaskDescription;
    }

    public String get_TaskTag() {
        return _TaskTag;
    }

    public void set_TaskTag(String _TaskTag) {
        this._TaskTag = _TaskTag;
    }

    public String get_StartDateTime() {
        return _StartDateTime;
    }

    public void set_StartDateTime(String _StartDateTime) {
        this._StartDateTime = _StartDateTime;
    }

    public String get_EndDateTime() {
        return _EndDateTime;
    }

    public void set_EndDateTime(String _EndDateTime) {
        this._EndDateTime = _EndDateTime;
    }

    public String get_Repetition() {
        return _Repetition;
    }

    public void set_Repetition(String _Repetition) {
        this._Repetition = _Repetition;
    }

    public String get_Type() {
        return _Type;
    }

    public void set_Type(String _Type) {
        this._Type = _Type;
    }

    public String get_StartsFromDailyOrWeekly() {
        return _StartsFromDailyOrWeekly;
    }

    public void set_StartsFromDailyOrWeekly(String _StartsFromDailyOrWeekly) {
        this._StartsFromDailyOrWeekly = _StartsFromDailyOrWeekly;
    }

    public String get_EndsFromDailyOrWeekly() {
        return _EndsFromDailyOrWeekly;
    }

    public void set_EndsFromDailyOrWeekly(String _EndsFromDailyOrWeekly) {
        this._EndsFromDailyOrWeekly = _EndsFromDailyOrWeekly;
    }
}