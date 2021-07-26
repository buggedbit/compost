package friends.eevee.Activities.TouchEvent.Def;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import friends.eevee.DB.DB;
import friends.eevee.Log.ZeroLog;

public class PersonalEventDef extends EventDef{

    public String $COMMENT;

    public PersonalEventDef(){}

    public PersonalEventDef(Cursor cursor){
        if(cursor != null){
            this.$PK  = Integer.parseInt(cursor.getString(cursor.getColumnIndex(DB.PRIMARY_KEY)));
            this.$NAME  = cursor.getString(cursor.getColumnIndex(DB.TABLES.PERSONAL_EVENTS.NAME));
            this.$START = cursor.getString(cursor.getColumnIndex(DB.TABLES.PERSONAL_EVENTS.START));
            this.$DURATION = cursor.getString(cursor.getColumnIndex(DB.TABLES.PERSONAL_EVENTS.DURATION));
            this.$COMMENT = cursor.getString(cursor.getColumnIndex(DB.TABLES.PERSONAL_EVENTS.COMMENT));
        }
        else {
            Log.i(ZeroLog.TAG, "PersonalEventDef: Tried to initialize personal_event_def with null cursor");
        }
    }

    public PersonalEventDef(Parcel source) {
        this.$PK = source.readInt();
        this.$NAME = source.readString();
        this.$START = source.readString();
        this.$DURATION = source.readString();
        this.$COMMENT = source.readString();
    }

    public String toString(){
        return
                this.$PK + "  " +
                this.$NAME  + "  " +
                this.$START + "  " +
                this.$DURATION + "  " +
                this.$COMMENT + "  " ;
    }

    /* GETTER AND SETTER */

    public String get$COMMENT() {
        return $COMMENT;
    }

    public void set$COMMENT(String $COMMENT) {
        this.$COMMENT = $COMMENT;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.$PK);
        dest.writeString(this.$NAME);
        dest.writeString(this.$START);
        dest.writeString(this.$DURATION);
        dest.writeString(this.$COMMENT);
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {

        @Override
        public EventDef createFromParcel(Parcel source) {
            return new PersonalEventDef(source);
        }

        @Override
        public EventDef[] newArray(int size) {
            return new EventDef[size];
        }
    };
}
