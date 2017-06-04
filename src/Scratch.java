import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

public class Scratch {

    public static void main(String[] args) {
        Map<double[], String> map = new HashMap<>();
        map.put(new double[]{1, 2, 3, 5}, "Hello");
        map.put(new double[]{1, 2, 3, 5}, "World");

        System.out.println(map);
    }
}