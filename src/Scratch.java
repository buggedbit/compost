import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Scratch {

    public static void main(String[] args) {
        Map<Set<String>, Integer> a = new HashMap<>();

        Set<String> stringSet1 = new HashSet<>();
        stringSet1.add("World");
        stringSet1.add("Hello");

        Set<String> stringSet2 = new HashSet<>();
        stringSet2.add("Hello");
        stringSet2.add("World");

        a.put(stringSet1, 1);

        if (a.containsKey(stringSet2)) {
            System.out.println("true");
        }
    }
}
