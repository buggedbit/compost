package friends.eevee.DB.Def;

/**
 * Encapsulates the rows in tables of the events db
 * */
public class EventDef {
    public String $PK;
    public String $NAME;
    public String $START;
    public String $DURATION;

    public String get(){
        return null;
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
}
