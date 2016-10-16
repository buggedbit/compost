package friends.eevee.DB.Def;

import android.database.Cursor;
import android.util.Log;

import friends.eevee.DB.Helpers.Events;
import friends.eevee.Log.ZeroLog;

public class PersonalEventDef extends EventDef{

    public String $COMMENT;

    public PersonalEventDef(){

    }

    public PersonalEventDef(Cursor cursor){
        if(cursor != null){
            this.$PK  = cursor.getString(cursor.getColumnIndex(Events.PRIMARY_KEY));
            this.$NAME  = cursor.getString(cursor.getColumnIndex(Events.TABLES.PERSONAL_EVENTS_TABLE.NAME));
            this.$START = cursor.getString(cursor.getColumnIndex(Events.TABLES.PERSONAL_EVENTS_TABLE.START));
            this.$END   = cursor.getString(cursor.getColumnIndex(Events.TABLES.PERSONAL_EVENTS_TABLE.END));
            this.$COMMENT = cursor.getString(cursor.getColumnIndex(Events.TABLES.PERSONAL_EVENTS_TABLE.COMMENT));
        }
        else {
            Log.i(ZeroLog.TAG, "PersonalEventDef: Tried to initialize personal_event_def with null cursor");
        }
    }

    public String get(){
        return
                this.$PK + "  " +
                this.$NAME  + "  " +
                this.$START + "  " +
                this.$END   + "  " +
                this.$COMMENT + "  " ;
    }

    /* GETTER AND SETTER */

    public String get$COMMENT() {
        return $COMMENT;
    }

    public void set$COMMENT(String $COMMENT) {
        this.$COMMENT = $COMMENT;
    }
}
