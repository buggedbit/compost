package com.harsha.eevee;

import android.database.Cursor;

public class WebEventDetails {
    private String _TimeStamp;
    private String _eeVeeID;
    private String _EventName;
    private String _EventPlace;
    private String _StartDateTime;
    private String _EndDateTime;
    private String _Repetition;
    private String _DeadlineDateTime;
    private String _Regn_Place;
    private String _Website;
    private String _Comments;
    private String _Type;
    private String _Status;
    private String _ClubName;
    private String _StartsFromDailyOrWeekly;
    private String _EndsFromDailyOrWeekly;

    public WebEventDetails() {
    }

    public WebEventDetails(Cursor c) {
        if (c != null) {
            _TimeStamp = c.getString(c.getColumnIndex(WebEventDetailsDBHandler.COLUMN_TIME_STAMP));
            _eeVeeID = c.getString(c.getColumnIndex(WebEventDetailsDBHandler.COLUMN_EEVEE_ID));
            _EventName = c.getString(c.getColumnIndex(WebEventDetailsDBHandler.COLUMN_NAME));
            _EventPlace = c.getString(c.getColumnIndex(WebEventDetailsDBHandler.COLUMN_PLACE));
            _StartDateTime = c.getString(c.getColumnIndex(WebEventDetailsDBHandler.COLUMN_START_DATE_TIME));
            _EndDateTime = c.getString(c.getColumnIndex(WebEventDetailsDBHandler.COLUMN_END_DATE_TIME));
            _Repetition = c.getString(c.getColumnIndex(WebEventDetailsDBHandler.COLUMN_REPETITION));
            _DeadlineDateTime = c.getString(c.getColumnIndex(WebEventDetailsDBHandler.COLUMN_DEADLINE_DATE_TIME));
            _Regn_Place = c.getString(c.getColumnIndex(WebEventDetailsDBHandler.COLUMN_REGN_PLACE));
            _Website = c.getString(c.getColumnIndex(WebEventDetailsDBHandler.COLUMN_WEBSITE));
            _Comments = c.getString(c.getColumnIndex(WebEventDetailsDBHandler.COLUMN_COMMENTS));
            _Type = c.getString(c.getColumnIndex(WebEventDetailsDBHandler.COLUMN_TYPE));
            _Status = c.getString(c.getColumnIndex(WebEventDetailsDBHandler.COLUMN_STATUS));
            _ClubName = c.getString(c.getColumnIndex(WebEventDetailsDBHandler.COLUMN_CLUBNAME));
            _StartsFromDailyOrWeekly = c.getString(c.getColumnIndex(WebEventDetailsDBHandler.COLUMN_STARTS_FROM));
            _EndsFromDailyOrWeekly = c.getString(c.getColumnIndex(WebEventDetailsDBHandler.COLUMN_ENDS_ON));
        }
    }

    public WebEventDetails(OnlineEventDetails online) {
        if (online != null) {
            _TimeStamp = online.get_TimeStamp();
            _eeVeeID = online.get_eeVeeID();
            _EventName = online.get_EventName();
            _EventPlace = online.get_EventPlace();
            _StartDateTime = online.get_StartDateTime();
            _EndDateTime = online.get_EndDateTime();
            _Repetition = online.get_Repetition();
            _DeadlineDateTime = online.get_DeadlineDateTime();
            _Regn_Place = online.get_Regn_Place();
            _Website = online.get_Website();
            _Comments = online.get_Comments();
            _Type = online.get_Type();
            _Status = online.get_Status();
            _ClubName = online.get_ClubName();
            _StartsFromDailyOrWeekly = online.get_StartsFromDailyOrWeekly();
            _EndsFromDailyOrWeekly = online.get_EndsFromDailyOrWeekly();
        }
    }

    public String toString() {
        return _eeVeeID + _EventName + _EventPlace + _StartDateTime + _EndDateTime + _Repetition + _DeadlineDateTime + _Regn_Place + _Website + _Comments + _Type + _Status + _TimeStamp + _ClubName + _StartsFromDailyOrWeekly + _EndsFromDailyOrWeekly;
    }

    public String get_TimeStamp() {
        return _TimeStamp;
    }

    public void set_TimeStamp(String _TimeStamp) {
        this._TimeStamp = _TimeStamp;
    }

    public String get_EventName() {
        return _EventName;
    }

    public void set_EventName(String _EventName) {
        this._EventName = _EventName;
    }

    public String get_EventPlace() {
        return _EventPlace;
    }

    public void set_EventPlace(String _EventPlace) {
        this._EventPlace = _EventPlace;
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

    public String get_Regn_Place() {
        return _Regn_Place;
    }

    public void set_Regn_Place(String _Regn_Place) {
        this._Regn_Place = _Regn_Place;
    }

    public String get_Website() {
        return _Website;
    }

    public void set_Website(String _Website) {
        this._Website = _Website;
    }

    public String get_DeadlineDateTime() {
        return _DeadlineDateTime;
    }

    public void set_DeadlineDateTime(String _DeadlineDateTime) {
        this._DeadlineDateTime = _DeadlineDateTime;
    }

    public String get_Comments() {
        return _Comments;
    }

    public void set_Comments(String _Comments) {
        this._Comments = _Comments;
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

    public String get_Status() {
        return _Status;
    }

    public void set_Status(String _Status) {
        this._Status = _Status;
    }

    public String get_ClubName() {
        return _ClubName;
    }

    public void set_ClubName(String _ClubName) {
        this._ClubName = _ClubName;
    }

    public String get_eeVeeID() {
        return _eeVeeID;
    }

    public void set_eeVeeID(String _eeVeeID) {
        this._eeVeeID = _eeVeeID;
    }
}
