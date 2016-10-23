package friends.eevee.DB.Def;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Encapsulates the rows in tables of the events db
 * */
public class EventDef implements Parcelable {
    public String $PK;
    public String $NAME;
    public String $START;
    public String $DURATION;

    public EventDef(){}

    public EventDef(Parcel source){

    }

    public String get(){
        return "";
    }

    /* GETTER AND SETTER */

    public String get$NAME() {
        return $NAME;
    }

    public void set$NAME(String $NAME) {
        this.$NAME = $NAME;
    }

    public String get$START() {
        return $START;
    }

    public void set$START(String $START) {
        this.$START = $START;
    }

    public String get$DURATION() {
        return $DURATION;
    }

    public void set$DURATION(String $DURATION) {
        this.$DURATION = $DURATION;
    }

    public String get$COMMENT() {
        return "";
    }

    public void set$COMMENT(String $COMMENT) {

    }

    public String get$PK() {
        return $PK;
    }

    public void set$PK(String $PK) {
        this.$PK = $PK;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {

        @Override
        public EventDef createFromParcel(Parcel source) {
            return new EventDef(source);
        }

        @Override
        public EventDef[] newArray(int size) {
            return new EventDef[size];
        }
    };
}
