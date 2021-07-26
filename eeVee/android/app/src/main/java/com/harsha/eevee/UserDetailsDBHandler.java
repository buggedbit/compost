package com.harsha.eevee;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class UserDetailsDBHandler extends SQLiteOpenHelper {
    public static String DATABASE_NAME = "eeVeeUser.db";
    public static int DATABASE_VERSION = 1;

    public static String TABLE_NAME = "usersData";
    public static String COLUMN_ID = "_id";
    public static String COLUMN_USER_NAME = "UserName";
    public static String COLUMN_MALE = "isMale";
    public static String COLUMN_SUBSCRIPTION = "UserSubscription";

    public UserDetailsDBHandler(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
    }

    /**
     * As you increase no Columns this function increases
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE " + TABLE_NAME + "(" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_USER_NAME + " TEXT NOT NULL, " +
                COLUMN_MALE + " BOOLEAN NOT NULL, " +
                COLUMN_SUBSCRIPTION + " TEXT NOT NULL " +
                ");";
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME + ";");
        onCreate(db);
    }

    /**
     * As you increase no Columns this function increases
     */
    public void insertRow(UserDetails user) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_NAME, user.get_userName());
        values.put(COLUMN_MALE, user.is_Male());
        values.put(COLUMN_SUBSCRIPTION, user.get_Subscription());

        SQLiteDatabase db = getWritableDatabase();
        db.insert(TABLE_NAME, null, values);
        setStaticUserDetails();
        db.close();
    }

    /**
     * As you increase no Columns this function increases
     */
    public void updateFirstRow(UserDetails user) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_NAME, user.get_userName());
        values.put(COLUMN_MALE, user.is_Male());
        values.put(COLUMN_SUBSCRIPTION, user.get_Subscription());

        db.update(TABLE_NAME, values, COLUMN_ID + " = 1", null);
        setStaticUserDetails();
        db.close();
    }

    /**
     * Applicable only to those columns with DataType String
     */
    public void updateThisInFirstRow(String NAME_OF_COLUMN, String Value) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(NAME_OF_COLUMN, Value);

        db.update(TABLE_NAME, values, COLUMN_ID + " = 1", null);
        setStaticUserDetails();
        db.close();
    }

    /**
     * As you increase no Columns this function increases
     */
    public void setStaticUserDetails() {
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME + ";";

        Cursor c = db.rawQuery(query, null);
        c.moveToFirst();

        UserDetails.$USERNAME = c.getString(c.getColumnIndex(COLUMN_USER_NAME));
        if (c.getString(c.getColumnIndex(COLUMN_MALE)) == "1") {
            UserDetails.$IS_MALE = true;
        } else {
            UserDetails.$IS_MALE = false;
        }
        UserDetails.$USERGROUP = c.getString(c.getColumnIndex(COLUMN_SUBSCRIPTION));

        c.close();
        db.close();
    }

    /**
     * As you increase no Columns this function increases
     */
    public boolean isAllSet() {
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME + ";";

        Cursor c = db.rawQuery(query, null);
        if (c.isAfterLast()) {
            c.close();
            db.close();
            return false;
        }

        c.moveToFirst();
        String userName = c.getString(c.getColumnIndex(COLUMN_USER_NAME));
        String isMale = c.getString(c.getColumnIndex(COLUMN_MALE));
        String userGroup = c.getString(c.getColumnIndex(COLUMN_SUBSCRIPTION));


        if (userName != null && isMale != null && userGroup != null && !userName.matches("") && !isMale.matches("") && !userGroup.matches("")) {
            c.close();
            db.close();
            return true;
        }

        c.close();
        db.close();
        return false;
    }

    /**
     * returns the first row of the table
     */
    public UserDetails getUserDetails() {
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME + ";";
        Cursor c = db.rawQuery(query, null);
        c.moveToFirst();
        if (!c.isAfterLast()) {
            UserDetails userDetails = new UserDetails(c);
            return userDetails;
        }
        c.close();
        db.close();
        return null;
    }

    public void deleteRow(String userName) {
        SQLiteDatabase db = getWritableDatabase();
        Cursor ptr = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_USER_NAME + "='" + userName + "'", null);

        if (ptr.moveToFirst()) {
            // Deleting record if found
            while (!ptr.isAfterLast()) {
                db.execSQL("DELETE FROM " + TABLE_NAME + " WHERE " + COLUMN_USER_NAME + "='" + userName + "';");
                ptr.moveToNext();
            }
            ptr.close();
        }

        db.close();
    }

    /**
     * As you increase no Columns this function increases
     */
    public String viewTable() {
        String dbString = "";
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME + ";";

        Cursor c = db.rawQuery(query, null);
        c.moveToFirst();
        int i = 1;
        while (!c.isAfterLast()) {
            if (c.getString(c.getColumnIndex(COLUMN_USER_NAME)) != null && c.getString(c.getColumnIndex(COLUMN_MALE)) != null
                    && c.getString(c.getColumnIndex(COLUMN_SUBSCRIPTION)) != null) {
                dbString += c.getString(c.getColumnIndex(COLUMN_ID));
                dbString += ",";
                dbString += c.getString(c.getColumnIndex(COLUMN_USER_NAME));
                dbString += ",";
                dbString += c.getString(c.getColumnIndex(COLUMN_MALE));
                dbString += ",";
                dbString += c.getString(c.getColumnIndex(COLUMN_SUBSCRIPTION));
                dbString += ".";
                dbString += "\n";
            }
            c.moveToNext();
            i++;
        }
        c.close();
        db.close();
        return dbString;
    }

    public int getCount() {
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME + ";";

        Cursor c = db.rawQuery(query, null);
        c.moveToFirst();
        int count = 0;
        while (!c.isAfterLast()) {
            count++;
            c.moveToNext();
        }
        c.close();
        db.close();
        return count;
    }

    public void dropTable() {
        SQLiteDatabase db = getWritableDatabase();
        String query = "DROP TABLE IF EXISTS " + TABLE_NAME + ";";
        db.execSQL(query);
        onCreate(db);
        db.close();
    }

}