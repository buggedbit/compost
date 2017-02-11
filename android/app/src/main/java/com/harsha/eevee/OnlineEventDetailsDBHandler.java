package com.harsha.eevee;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.Vector;

public class OnlineEventDetailsDBHandler extends SQLiteOpenHelper {

    public static String DATABASE_NAME = "eeVeeOnlineEvents.db";
    public static int DATABASE_VERSION = 1;

    public static String TABLE_NAME = "onlineEventsData";

    public static String COLUMN_ID = "_id";
    public static String COLUMN_EEVEE_ID = "eeVeeID";
    public static String COLUMN_NAME = "Name";
    public static String COLUMN_PLACE = "Place";
    public static String COLUMN_START_DATE_TIME = "StartDateTime";
    public static String COLUMN_END_DATE_TIME = "EndDateTime";
    public static String COLUMN_REPETITION = "Repetition";
    public static String COLUMN_DEADLINE_DATE_TIME = "DeadlineDateTime";
    public static String COLUMN_REGN_PLACE = "RegnPlace";
    public static String COLUMN_WEBSITE = "RegnWebsite";
    public static String COLUMN_COMMENTS = "Comments";
    public static String COLUMN_TYPE = "Type";
    public static String COLUMN_STATUS = "Status";
    public static String COLUMN_TIME_STAMP = "TimeStamp";
    public static String COLUMN_CLUBNAME = "ClubName";
    public static String COLUMN_APPROVAL = "Approval";
    public static String COLUMN_STARTS_FROM = "StartsFromDailyOrWeekly";
    public static String COLUMN_ENDS_ON = "EndsFromDailyOrWeekly";

    public OnlineEventDetailsDBHandler(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE " + TABLE_NAME + "(" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_EEVEE_ID + " TEXT NOT NULL, " +
                COLUMN_TIME_STAMP + " TEXT NOT NULL, " +
                COLUMN_NAME + " TEXT NOT NULL, " +
                COLUMN_PLACE + " TEXT NOT NULL, " +
                COLUMN_START_DATE_TIME + " TEXT NOT NULL, " +
                COLUMN_END_DATE_TIME + " TEXT NOT NULL, " +
                COLUMN_REPETITION + " TEXT NOT NULL, " +
                COLUMN_TYPE + " TEXT NOT NULL, " +
                COLUMN_REGN_PLACE + " TEXT NOT NULL, " +
                COLUMN_WEBSITE + " TEXT NOT NULL, " +
                COLUMN_DEADLINE_DATE_TIME + " TEXT NOT NULL, " +
                COLUMN_COMMENTS + " TEXT NOT NULL, " +
                COLUMN_STARTS_FROM + " TEXT NOT NULL, " +
                COLUMN_ENDS_ON + " TEXT NOT NULL, " +
                COLUMN_STATUS + " TEXT NOT NULL, " +
                COLUMN_CLUBNAME + " TEXT NOT NULL, " +
                COLUMN_APPROVAL + " TEXT NOT NULL " +
                ");";
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME + ";");
        onCreate(db);
    }

    public void insertRow(OnlineEventDetails event) {
        ContentValues values = new ContentValues();

        values.put(COLUMN_TIME_STAMP, event.get_TimeStamp());
        values.put(COLUMN_EEVEE_ID, event.get_eeVeeID());
        values.put(COLUMN_NAME, event.get_EventName());
        values.put(COLUMN_PLACE, event.get_EventPlace());
        values.put(COLUMN_START_DATE_TIME, event.get_StartDateTime());
        values.put(COLUMN_END_DATE_TIME, event.get_EndDateTime());
        values.put(COLUMN_REPETITION, event.get_Repetition());
        values.put(COLUMN_TYPE, event.get_Type());
        values.put(COLUMN_REGN_PLACE, event.get_Regn_Place());
        values.put(COLUMN_WEBSITE, event.get_Website());
        values.put(COLUMN_DEADLINE_DATE_TIME, event.get_DeadlineDateTime());
        values.put(COLUMN_COMMENTS, event.get_Comments());
        values.put(COLUMN_STARTS_FROM, event.get_StartsFromDailyOrWeekly());
        values.put(COLUMN_ENDS_ON, event.get_EndsFromDailyOrWeekly());
        values.put(COLUMN_STATUS, event.get_Status());
        values.put(COLUMN_CLUBNAME, event.get_ClubName());
        values.put(COLUMN_APPROVAL, event.get_Approval());

        SQLiteDatabase db = getWritableDatabase();
        db.insert(TABLE_NAME, null, values);
        db.close();
    }

    public void updateRow(OnlineEventDetails event, int eeVeeID) {
        ContentValues values = new ContentValues();

        values.put(COLUMN_TIME_STAMP, event.get_TimeStamp());
        values.put(COLUMN_EEVEE_ID, event.get_eeVeeID());
        values.put(COLUMN_NAME, event.get_EventName());
        values.put(COLUMN_PLACE, event.get_EventPlace());
        values.put(COLUMN_START_DATE_TIME, event.get_StartDateTime());
        values.put(COLUMN_END_DATE_TIME, event.get_EndDateTime());
        values.put(COLUMN_REPETITION, event.get_Repetition());
        values.put(COLUMN_TYPE, event.get_Type());
        values.put(COLUMN_REGN_PLACE, event.get_Regn_Place());
        values.put(COLUMN_WEBSITE, event.get_Website());
        values.put(COLUMN_DEADLINE_DATE_TIME, event.get_DeadlineDateTime());
        values.put(COLUMN_COMMENTS, event.get_Comments());
        values.put(COLUMN_STARTS_FROM, event.get_StartsFromDailyOrWeekly());
        values.put(COLUMN_ENDS_ON, event.get_EndsFromDailyOrWeekly());
        values.put(COLUMN_STATUS, event.get_Status());
        values.put(COLUMN_CLUBNAME, event.get_ClubName());
        values.put(COLUMN_APPROVAL, event.get_Approval());

        SQLiteDatabase db = getWritableDatabase();
        String where = COLUMN_EEVEE_ID + " ='" + eeVeeID + "' ";
        db.update(TABLE_NAME, values, where, null);
        db.close();
    }

    /**
     * The SEARCH KEY VALUE SHOULD BE SUCH THAT AT MOST ONE ROW SATISFYING THAT CONDITION EXISTS
     */
    public void setThisHere(String NAME_OF_COLUMN_SEARCH, String Value_SEARCH, String NAME_OF_COLUMN_PLACE, String Value_PLACE) {
        ContentValues values = new ContentValues();
        values.put(NAME_OF_COLUMN_PLACE, Value_PLACE);

        SQLiteDatabase db = getWritableDatabase();
        String where = NAME_OF_COLUMN_SEARCH + " ='" + Value_SEARCH + "' ";
        db.update(TABLE_NAME, values, where, null);

        db.close();
    }

    /**
     * The SEARCH KEY VALUE SHOULD BE SUCH THAT AT MOST ONE ROW SATISFYING THAT CONDITION EXISTS
     */
    public OnlineEventDetails getObjectWith(String NAME_OF_COLUMN, String Value) {
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + NAME_OF_COLUMN + " ='" + Value + "';";

        Cursor c = db.rawQuery(query, null);
        c.moveToFirst();
        if (!c.isAfterLast()) {
            OnlineEventDetails ret = new OnlineEventDetails(c);
            c.close();
            db.close();
            return ret;
        }

        c.close();
        db.close();

        return null;
    }

    public Vector<OnlineEventDetails> getAllObjectsWith(String NAME_OF_COLUMN, String Value) {
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + NAME_OF_COLUMN + " ='" + Value + "';";

        Vector<OnlineEventDetails> retVector = new Vector<>();

        Cursor c = db.rawQuery(query, null);
        c.moveToFirst();
        while (!c.isAfterLast()) {
            OnlineEventDetails row = new OnlineEventDetails(c);
            retVector.add(row);
            c.moveToNext();
        }

        c.close();
        db.close();

        return retVector;
    }

    public boolean areObjectWith(String NAME_OF_COLUMN, String Value) {
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + NAME_OF_COLUMN + " ='" + Value + "';";
        ;

        Cursor c = db.rawQuery(query, null);
        c.moveToFirst();
        if (!c.isAfterLast()) {
            c.close();
            db.close();
            return true;
        }

        c.close();
        db.close();

        return false;
    }

    public String viewTable() {
        String dbString = "";
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME + ";";

        Cursor c = db.rawQuery(query, null);
        c.moveToFirst();
        while (!c.isAfterLast()) {
            dbString += c.getString(c.getColumnIndex(COLUMN_ID));
            dbString += ",";
            dbString += c.getString(c.getColumnIndex(COLUMN_EEVEE_ID));
            dbString += ",";
            dbString += c.getString(c.getColumnIndex(COLUMN_TIME_STAMP));
            dbString += ",";
            dbString += c.getString(c.getColumnIndex(COLUMN_NAME));
            dbString += ",";
            dbString += c.getString(c.getColumnIndex(COLUMN_PLACE));
            dbString += ",";
            dbString += c.getString(c.getColumnIndex(COLUMN_START_DATE_TIME));
            dbString += ",";
            dbString += c.getString(c.getColumnIndex(COLUMN_END_DATE_TIME));
            dbString += ",";
            dbString += c.getString(c.getColumnIndex(COLUMN_REPETITION));
            dbString += ",";
            dbString += c.getString(c.getColumnIndex(COLUMN_TYPE));
            dbString += ",";
            dbString += c.getString(c.getColumnIndex(COLUMN_REGN_PLACE));
            dbString += ",";
            dbString += c.getString(c.getColumnIndex(COLUMN_WEBSITE));
            dbString += ",";
            dbString += c.getString(c.getColumnIndex(COLUMN_DEADLINE_DATE_TIME));
            dbString += ",";
            dbString += c.getString(c.getColumnIndex(COLUMN_COMMENTS));
            dbString += ",";
            dbString += c.getString(c.getColumnIndex(COLUMN_STARTS_FROM));
            dbString += ",";
            dbString += c.getString(c.getColumnIndex(COLUMN_ENDS_ON));
            dbString += ",";
            dbString += c.getString(c.getColumnIndex(COLUMN_STATUS));
            dbString += ",";
            dbString += c.getString(c.getColumnIndex(COLUMN_CLUBNAME));
            dbString += ",";
            dbString += c.getString(c.getColumnIndex(COLUMN_APPROVAL));
            dbString += ".";

            dbString += "\n";
            c.moveToNext();
        }
        c.close();
        db.close();
        return dbString;
    }

    public void deleteOutdatedEvents() {
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME + ";";

        Cursor c = db.rawQuery(query, null);
        c.moveToFirst();

        DateTime present = new DateTime(true);

        while (!c.isAfterLast()) {

            if (c.getString(c.getColumnIndex(COLUMN_REPETITION)).matches("0000000")) {

                DateTime END = new DateTime(c.getString(c.getColumnIndex(COLUMN_END_DATE_TIME)));

                if (END.isSetByString()) {
                    if (DateTime.differenceInMinWithSign(present, END) < -Constants.EVENT.PAST_DISPLAY_TIME_MIN) {
                        String delQuery = "DELETE FROM " + TABLE_NAME + " WHERE " + COLUMN_ID + " = '" + c.getString(c.getColumnIndex(COLUMN_ID)) + "'; ";
                        db.execSQL(delQuery);
                    }
                }
            }
            c.moveToNext();
        }
        c.close();
        db.close();
    }

    public String returnReqInfo() {
        String dbString = "";
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME + ";";

        Cursor c = db.rawQuery(query, null);
        c.moveToFirst();

        // The order is referred to in initializing EventViewCompact Objects in EventHome activity

        while (!c.isAfterLast()) {
            dbString += c.getString(c.getColumnIndex(COLUMN_ID));                   // 0
            dbString += ",";
            dbString += c.getString(c.getColumnIndex(COLUMN_TIME_STAMP));           // 1
            dbString += ",";
            dbString += c.getString(c.getColumnIndex(COLUMN_NAME));                 // 2
            dbString += ",";
            dbString += c.getString(c.getColumnIndex(COLUMN_PLACE));                // 3
            dbString += ",";
            dbString += c.getString(c.getColumnIndex(COLUMN_START_DATE_TIME));      // 4
            dbString += ",";
            dbString += c.getString(c.getColumnIndex(COLUMN_END_DATE_TIME));        // 5
            dbString += ",";
            dbString += c.getString(c.getColumnIndex(COLUMN_REPETITION));           // 6
            dbString += ",";
            dbString += c.getString(c.getColumnIndex(COLUMN_DEADLINE_DATE_TIME));   // 7
            dbString += ",";
            dbString += c.getString(c.getColumnIndex(COLUMN_STARTS_FROM));          // 8
            dbString += ",";
            dbString += c.getString(c.getColumnIndex(COLUMN_ENDS_ON));              // 9
            dbString += ",";
            dbString += TABLE_NAME;                                                 // 10
            dbString += ",";
            dbString += c.getString(c.getColumnIndex(COLUMN_APPROVAL));             // 11
            dbString += ",";
            dbString += c.getString(c.getColumnIndex(COLUMN_STATUS));               // 12
            dbString += ",";
            dbString += c.getString(c.getColumnIndex(COLUMN_TYPE));                 // 13
            dbString += ",";
            dbString += c.getString(c.getColumnIndex(COLUMN_CLUBNAME));             // 14

            dbString += "/";
            c.moveToNext();
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
    }

    public void ignoreEvent(int SQLiteID) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_APPROVAL, Constants.APPROVAL_REJECTED);
        db.update(TABLE_NAME, values, COLUMN_ID + "='" + SQLiteID + "'", null);
        db.close();
    }

    public void acceptEvent(int SQLiteID) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_APPROVAL, Constants.APPROVAL_ACCEPTED);
        db.update(TABLE_NAME, values, COLUMN_ID + "='" + SQLiteID + "'", null);
        db.close();
    }

    public int getSQLiteID(int eeVeeID) {
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_EEVEE_ID + " ='" + eeVeeID + "';";

        int SQLiteID = 0;
        Cursor c = db.rawQuery(query, null);
        c.moveToFirst();
        if (!c.isAfterLast()) {
            SQLiteID = Integer.parseInt(c.getString(c.getColumnIndex(COLUMN_ID)));
        }
        c.close();
        db.close();
        return SQLiteID;
    }
}