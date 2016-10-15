package friends.eevee.DB.Def;

/**
 * Encapsulates the rows in tables of the events db
 * */
public class EventDef {
    public String $NAME;
    public String $START;
    public String $END;

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

    public String get$END() {
        return $END;
    }

    public void set$END(String $END) {
        this.$END = $END;
    }

    public String get$COMMENT() {
        return "";
    }

    public void set$COMMENT(String $COMMENT) {

    }
}
