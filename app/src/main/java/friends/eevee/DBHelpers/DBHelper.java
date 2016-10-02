package friends.eevee.DBHelpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    public final String PRIMARY_KEY = "_id";


    public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO : implement migration code
    }

    public void dropTable(String TABLE_NAME) {
        SQLiteDatabase db = getWritableDatabase();
        String query = "DROP TABLE IF EXISTS " + TABLE_NAME + ";";
        db.execSQL(query);
        onCreate(db);
    }


    public void pointUpdate(String NAME_OF_COLUMN_PLACE, String VALUE_PLACE, String NAME_OF_COLUMN_SEARCH, String VALUE_SEARCH, String TABLE_NAME) {
        ContentValues values = new ContentValues();
        values.put(NAME_OF_COLUMN_PLACE, VALUE_PLACE);

        SQLiteDatabase db = getWritableDatabase();
        String where = NAME_OF_COLUMN_SEARCH + " ='" + VALUE_SEARCH + "' ";
        db.update(TABLE_NAME, values, where, null);

        db.close();
    }

    public boolean existsEntry(String NAME_OF_COLUMN, String Value, String TABLE_NAME) {
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + NAME_OF_COLUMN + " ='" + Value + "';";

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

    public int numberOfEntries(String TABLE_NAME) {
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

}



