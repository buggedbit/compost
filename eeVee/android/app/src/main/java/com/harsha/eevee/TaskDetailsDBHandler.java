package com.harsha.eevee;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class TaskDetailsDBHandler extends SQLiteOpenHelper {
    public static String TABLE_NAME = "offlineTasksData";
    public static String COLUMN_ID = "id";
    public static String COLUMN_TIME_STAMP = "TimeStamp";
    public static String COLUMN_DESCRIPTION = "Description";
    public static String COLUMN_TAG = "TAG";
    public static String COLUMN_START_DATE_TIME = "StartDateTime";
    public static String COLUMN_END_DATE_TIME = "EndDateTime";
    public static String COLUMN_REPETITION = "Repetition";
    public static String COLUMN_TYPE = "Type";
    private static String DATABASE_NAME = "eeVeeOfflineTasks.db";
    private static int DATABASE_VERSION = 1;
    private static String COLUMN_STARTS_FROM = "StartsFromDailyOrWeekly";
    private static String COLUMN_ENDS_ON = "EndsFromDailyOrWeekly";

    public TaskDetailsDBHandler(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
//        getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE " + TABLE_NAME + "(" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_TIME_STAMP + " TEXT NOT NULL, " +
                COLUMN_DESCRIPTION + " TEXT NOT NULL, " +
                COLUMN_TAG + " TEXT NOT NULL, " +
                COLUMN_START_DATE_TIME + " TEXT NOT NULL, " +
                COLUMN_END_DATE_TIME + " TEXT NOT NULL, " +
                COLUMN_REPETITION + " TEXT NOT NULL, " +
                COLUMN_TYPE + " TEXT NOT NULL, " +
                COLUMN_STARTS_FROM + " TEXT NOT NULL, " +
                COLUMN_ENDS_ON + " TEXT NOT NULL " +
                ");";
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME + ";");
        onCreate(db);
    }

    public void insertRow(TaskDetails task) {
        ContentValues values = new ContentValues();

        values.put(COLUMN_TIME_STAMP, task.get_TimeStamp());
        values.put(COLUMN_DESCRIPTION, task.get_TaskDescription());
        values.put(COLUMN_TAG, task.get_TaskTag());
        values.put(COLUMN_START_DATE_TIME, task.get_StartDateTime());
        values.put(COLUMN_END_DATE_TIME, task.get_EndDateTime());
        values.put(COLUMN_REPETITION, task.get_Repetition());
        values.put(COLUMN_TYPE, task.get_Type());
        values.put(COLUMN_STARTS_FROM, task.get_StartsFromDailyOrWeekly());
        values.put(COLUMN_ENDS_ON, task.get_EndsFromDailyOrWeekly());

        SQLiteDatabase db = getWritableDatabase();
        db.insert(TABLE_NAME, null, values);
        db.close();
    }

    public String viewTable() {
        String dbString = "";
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME + ";";

        Cursor c = db.rawQuery(query, null);
        c.moveToFirst();
        int i = 1;
        while (!c.isAfterLast()) {
            dbString += String.valueOf(i);
            dbString += ",";
            dbString += c.getString(c.getColumnIndex(COLUMN_ID));
            dbString += ",";
            dbString += c.getString(c.getColumnIndex(COLUMN_TIME_STAMP));
            dbString += ",";
            dbString += c.getString(c.getColumnIndex(COLUMN_DESCRIPTION));
            dbString += ",";
            dbString += c.getString(c.getColumnIndex(COLUMN_TAG));
            dbString += ",";
            dbString += c.getString(c.getColumnIndex(COLUMN_START_DATE_TIME));
            dbString += ",";
            dbString += c.getString(c.getColumnIndex(COLUMN_END_DATE_TIME));
            dbString += ",";
            dbString += c.getString(c.getColumnIndex(COLUMN_REPETITION));
            dbString += ",";
            dbString += c.getString(c.getColumnIndex(COLUMN_TYPE));
            dbString += ",";
            dbString += c.getString(c.getColumnIndex(COLUMN_STARTS_FROM));
            dbString += ",";
            dbString += c.getString(c.getColumnIndex(COLUMN_ENDS_ON));
            dbString += ".";

            dbString += "\n";
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

    public String returnReqInfo() {
        String dbString = "";
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME + ";";

        Cursor c = db.rawQuery(query, null);
        c.moveToFirst();

        // The order is referred to in initializing TaskViewCompact Objects in TaskHome activity
        while (!c.isAfterLast()) {
            dbString += c.getString(c.getColumnIndex(COLUMN_ID));                 // 0
            dbString += ",";
            dbString += c.getString(c.getColumnIndex(COLUMN_TIME_STAMP));         // 1
            dbString += ",";
            dbString += c.getString(c.getColumnIndex(COLUMN_DESCRIPTION));        // 2
            dbString += ",";
            dbString += c.getString(c.getColumnIndex(COLUMN_TAG));                // 3
            dbString += ",";
            dbString += c.getString(c.getColumnIndex(COLUMN_START_DATE_TIME));    // 4
            dbString += ",";
            dbString += c.getString(c.getColumnIndex(COLUMN_END_DATE_TIME));      // 5
            dbString += ",";
            dbString += c.getString(c.getColumnIndex(COLUMN_REPETITION));         // 6
            dbString += ",";
            dbString += c.getString(c.getColumnIndex(COLUMN_STARTS_FROM));        // 7
            dbString += ",";
            dbString += c.getString(c.getColumnIndex(COLUMN_ENDS_ON));            // 8
            dbString += ",";
            dbString += TABLE_NAME;                                               // 9
            dbString += ",";
            dbString += c.getString(c.getColumnIndex(COLUMN_TYPE));               // 10

            dbString += "/";
            c.moveToNext();
        }
        c.close();
        db.close();
        return dbString;
    }

    public void deleteOutdatedTasks() {
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME + ";";

        Cursor c = db.rawQuery(query, null);
        c.moveToFirst();

        DateTime present = new DateTime(true);

        while (!c.isAfterLast()) {

            if (c.getString(c.getColumnIndex(COLUMN_REPETITION)).matches("0000000")) {

                DateTime END = new DateTime(c.getString(c.getColumnIndex(COLUMN_END_DATE_TIME)));

                if (END.isSetByString()) {
                    if (DateTime.differenceInMinWithSign(present, END) < -Constants.TASK.PAST_DISPLAY_TIME_MIN) {
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

    public String returnWithConstraint(String SEARCH, String MATCH, String RETURN) {
        String dbString = "";
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME + ";";

        Cursor c = db.rawQuery(query, null);
        c.moveToFirst();

        // The order is referred to in initializing TaskViewCompact Objects in TaskHome activity
        while (!c.isAfterLast()) {
            if (MATCH == c.getString(c.getColumnIndex(SEARCH))) {
                dbString += c.getString(c.getColumnIndex(RETURN));
                dbString += ",";
            }
            c.moveToNext();
        }
        c.close();
        db.close();
        return dbString;
    }

    public void deleteRow(int ID) {
        SQLiteDatabase db = getWritableDatabase();
        String query = "DELETE FROM " + TABLE_NAME + " WHERE " + COLUMN_ID + " = '" + String.valueOf(ID) + "'; ";
        db.execSQL(query);
        db.close();
    }

    public void dropTable() {
        SQLiteDatabase db = getWritableDatabase();
        String query = "DROP TABLE IF EXISTS " + TABLE_NAME + ";";
        db.execSQL(query);
        onCreate(db);
        db.close();
    }

    public String[] getTaskData(int id) {
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_ID + " ='" + String.valueOf(id) + "';";

        String[] taskColumnData = new String[8];
        Cursor c = db.rawQuery(query, null);
        c.moveToFirst();
        taskColumnData[0] = c.getString(c.getColumnIndex(COLUMN_DESCRIPTION));
        taskColumnData[1] = c.getString(c.getColumnIndex(COLUMN_TAG));
        taskColumnData[2] = c.getString(c.getColumnIndex(COLUMN_START_DATE_TIME));
        taskColumnData[3] = c.getString(c.getColumnIndex(COLUMN_END_DATE_TIME));
        taskColumnData[4] = c.getString(c.getColumnIndex(COLUMN_REPETITION));
        taskColumnData[5] = c.getString(c.getColumnIndex(COLUMN_TYPE));
        taskColumnData[6] = c.getString(c.getColumnIndex(COLUMN_STARTS_FROM));
        taskColumnData[7] = c.getString(c.getColumnIndex(COLUMN_ENDS_ON));

        c.close();
        db.close();
        return taskColumnData;
    }

    public void updateRow(TaskDetails task, int id) {
        ContentValues values = new ContentValues();

        values.put(COLUMN_TIME_STAMP, task.get_TimeStamp());
        values.put(COLUMN_DESCRIPTION, task.get_TaskDescription());
        values.put(COLUMN_TAG, task.get_TaskTag());
        values.put(COLUMN_START_DATE_TIME, task.get_StartDateTime());
        values.put(COLUMN_END_DATE_TIME, task.get_EndDateTime());
        values.put(COLUMN_REPETITION, task.get_Repetition());
        values.put(COLUMN_TYPE, task.get_Type());
        values.put(COLUMN_STARTS_FROM, task.get_StartsFromDailyOrWeekly());
        values.put(COLUMN_ENDS_ON, task.get_EndsFromDailyOrWeekly());

        SQLiteDatabase db = getWritableDatabase();
        String where = COLUMN_ID + "='" + id + "'";
        db.update(TABLE_NAME, values, where, null);
        db.close();
    }

    public void deleteTask(int id) {
        SQLiteDatabase db = getWritableDatabase();
        String delQuery = "DELETE FROM " + TABLE_NAME + " WHERE " + COLUMN_ID + " = '" + String.valueOf(id) + "';";
        db.execSQL(delQuery);
        db.close();
    }
}