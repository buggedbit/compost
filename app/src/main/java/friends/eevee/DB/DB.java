package friends.eevee.DB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.Vector;

import friends.eevee.Calender.Date;
import friends.eevee.Calender.DateTime;
import friends.eevee.Calender.DateTimeDelta;
import friends.eevee.Calender.Time;
import friends.eevee.Activities.TouchEvent.Def.EventDef;
import friends.eevee.Activities.TouchEvent.Def.PersonalEventDef;
import friends.eevee.Log.ZeroLog;
import friends.eevee.Activities.TimeWall.TimeWall.TimeWallUtil.UIPreferences;

/**
 * <p>
 * To add or remove columns in any of the table
 * 1. add/remove column names from their static table classes
 * 2. ||ly change the query for creating table
 * 3. Then add/remove the fields in respective def class
 * 4. add/remove getter setter to them and change the constructors and methods which use them
 * 5. do the same as #4 in the parent EventDef class also but with no implementation in them <b>for polymorphism purpose</b>
 * 6. add/remove the fields in ALL the corresponding methods of this class like <b>insert, update etc... </b>
 * 7. similarly make changes where ever the def objects are used in whole project
 * 8. re-install the app or more preferably TODO: upgrade the db
 * </p>
 */
public class DB extends Base {

    /**
     * DB name
     */
    public static final String DB_NAME = "eevee.db";
    /**
     * DB version
     */
    public static final int DB_VERSION = 1;


    public static final class TABLES {

        /**
         * does not close db connection
         */
        /* create table function*/
        public static void createTable(SQLiteDatabase db, String create_table_query) {
            db.execSQL(create_table_query);
            Log.i(ZeroLog.TAG, "created table with query <<< " + create_table_query);
        }

        /**
         * does not close db connection
         */
        /* drop table function*/
        public static void dropTable(SQLiteDatabase db, String table_name) {
            db.execSQL("DROP TABLE IF EXISTS " + table_name + ";");
            Log.i(ZeroLog.TAG, "dropped table with query <<< " + "DROP TABLE IF EXISTS " + table_name + ";");
        }

        /**
         * personal events table
         */
        public static final class PERSONAL_EVENTS {
            /* personal events table - name*/
            public static final String TABLE_NAME = "personal_events";

            /* personal events table - columns*/
            //PRIMARY KEY
            public static final String NAME = "name";
            public static final String START = "start";
            public static final String DURATION = "duration";
            public static final String COMMENT = "comment";

            /* create table query*/
            private static final String CREATE_TABLE_QUERY =
                    "CREATE TABLE "
                            + TABLE_NAME
                            + "("
                            + PRIMARY_KEY + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                            + START + " VARCHAR(255) NOT NULL,"
                            + DURATION + " VARCHAR(255) NOT NULL,"
                            + NAME + " VARCHAR(255) NOT NULL,"
                            + COMMENT + " VARCHAR(255) NOT NULL"
                            + ")";
        }
    }

    public DB(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        super.onCreate(db);
        // create tables
        TABLES.createTable(db, TABLES.PERSONAL_EVENTS.CREATE_TABLE_QUERY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        super.onUpgrade(db, oldVersion, newVersion);
        // drop tables
        TABLES.dropTable(db, TABLES.PERSONAL_EVENTS.TABLE_NAME);
        // create new ones
        onCreate(db);
    }

    /* CRUD operations on all tables */

    public void insert(EventDef eventDef, String table) {
        switch (table) {
            case TABLES.PERSONAL_EVENTS.TABLE_NAME:
                /* Personal events table */
                ContentValues values = new ContentValues();

                values.put(TABLES.PERSONAL_EVENTS.NAME, eventDef.get$NAME());
                values.put(TABLES.PERSONAL_EVENTS.START, eventDef.get$START());
                values.put(TABLES.PERSONAL_EVENTS.DURATION, eventDef.get$DURATION());
                values.put(TABLES.PERSONAL_EVENTS.COMMENT, eventDef.get$COMMENT());

                SQLiteDatabase db = getWritableDatabase();
                db.insert(table, null, values);
                db.close();

                break;
            default:
                break;
        }
    }

    public void updateEntryWithKeyValue(EventDef entry, String key, String value) {

        if (entry instanceof PersonalEventDef) {
            /* Personal events table */
            ContentValues values = new ContentValues();

            values.put(TABLES.PERSONAL_EVENTS.NAME, entry.get$NAME());
            values.put(TABLES.PERSONAL_EVENTS.START, entry.get$START());
            values.put(TABLES.PERSONAL_EVENTS.DURATION, entry.get$DURATION());
            values.put(TABLES.PERSONAL_EVENTS.COMMENT, entry.get$COMMENT());

            SQLiteDatabase db = getWritableDatabase();
            String where = key + " = '" + value + "' ";
            db.update(TABLES.PERSONAL_EVENTS.TABLE_NAME, values, where, null);
            db.close();

        } else {
        }
    }

    public EventDef getFirstEntryWithKeyValue(String table, String key, String value) {
        switch (table) {
            case TABLES.PERSONAL_EVENTS.TABLE_NAME:
                /* Personal events table */
                SQLiteDatabase db = getWritableDatabase();
                String query = "SELECT * FROM " + table + " WHERE " + key + " ='" + value + "';";

                Cursor c = db.rawQuery(query, null);
                c.moveToFirst();

                if (!c.isAfterLast()) {

                    EventDef ret = new PersonalEventDef(c);
                    c.close();
                    db.close();
                    return ret;
                } else {

                    c.close();
                    db.close();
                    return null;
                }

            default:
                return null;
        }
    }

    public Vector<EventDef> getAllEntryWithKeyValue(String table, String key, String value) {
        switch (table) {
            case TABLES.PERSONAL_EVENTS.TABLE_NAME:
                /* Personal events table */
                SQLiteDatabase db = getWritableDatabase();
                String query = "SELECT * FROM " + table + " WHERE " + key + " ='" + value + "';";

                Vector<EventDef> retVector = new Vector<>();

                Cursor c = db.rawQuery(query, null);
                c.moveToFirst();

                while (!c.isAfterLast()) {
                    EventDef row = new PersonalEventDef(c);
                    retVector.add(row);
                    c.moveToNext();
                }

                c.close();
                db.close();
                return retVector;

            default:
                return null;
        }

    }

    public Vector<EventDef> getRelatedEvents(String table, Date date) {

        Time ref_time = new Time(UIPreferences.START_OF_THE_DAY);

        DateTime ref_date_time = new DateTime(date, ref_time);
        DateTime plus_24hr = new DateTime(ref_date_time);
        plus_24hr.addDaysSeconds(1,0);

        switch (table) {
            case TABLES.PERSONAL_EVENTS.TABLE_NAME:
                /* Personal events table */
                SQLiteDatabase db = getWritableDatabase();
                String query = "SELECT * FROM " + table + ";";

                Vector<EventDef> retVector = new Vector<>();

                Cursor c = db.rawQuery(query, null);
                c.moveToFirst();

                while (!c.isAfterLast()) {
                    EventDef row = new PersonalEventDef(c);

                    DateTime start = new DateTime(row.$START,Date.SIMPLE_REPR_SEPARATOR,Time.SIMPLE_REPR_SEPARATOR,DateTime.SIMPLE_REPR_SEPARATOR);
                    DateTimeDelta duration = new DateTimeDelta(row.$DURATION);
                    DateTime end = new DateTime(start);
                    end.addDateTimeDiff(duration);

                    if(start.isFutureOrEqualTo(ref_date_time) && start.isPastOrEqualTo(plus_24hr)) {
                        retVector.add(row);
                    }
                    else if(end.isFutureOrEqualTo(ref_date_time) && end.isPastOrEqualTo(plus_24hr)) {
                        retVector.add(row);
                    }

                    c.moveToNext();
                }

                c.close();
                db.close();
                return retVector;

            default:
                return null;
        }

    }
}






