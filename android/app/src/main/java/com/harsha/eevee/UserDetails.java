package com.harsha.eevee;

import android.database.Cursor;

public class UserDetails {

    public static String $USERNAME;
    public static boolean $IS_MALE;
    public static String $USERGROUP;

    private String _userName;
    private boolean _Male; // set to true if the input is male or false if not
    private String _Subscription;

    public UserDetails() {
    }

    public UserDetails(Cursor c) {
        _userName = c.getString(c.getColumnIndex(UserDetailsDBHandler.COLUMN_USER_NAME));
        _Subscription = c.getString(c.getColumnIndex(UserDetailsDBHandler.COLUMN_SUBSCRIPTION));
        if (c.getString(c.getColumnIndex(UserDetailsDBHandler.COLUMN_MALE)).matches("1"))
            _Male = true;
        else _Male = false;
    }

    public UserDetails(String _userName, boolean _Male, String _Subscription) {
        this._userName = _userName;
        this._Male = _Male;
        this._Subscription = _Subscription;
    }

    public String get_userName() {
        return _userName;
    }

    public void set_userName(String _userName) {
        this._userName = _userName;
    }

    public boolean is_Male() {
        return _Male;
    }

    public void set_Male(boolean _Male) {
        this._Male = _Male;
    }

    public String get_Subscription() {
        return _Subscription;
    }

    public void set_Subscription(String _Subscription) {
        this._Subscription = _Subscription;
    }
}