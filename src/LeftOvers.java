import java.util.Vector;

public class LeftOvers {
    private static Vector<SInequality> ALL = new Vector<>();

    public static void add(SInequality sInequality) {
        ALL.add(sInequality);
    }

    public static void add(Vector<SInequality> sInequalities) {
        ALL.addAll(sInequalities);
    }

    public static int size() {
        return ALL.size();
    }

    public static Vector<SInequality> getAll() {
        return ALL;
    }
}
