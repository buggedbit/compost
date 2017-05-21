package Physical;

import Abstract.SInequality;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * A box and items in it
 */
public class Shipment {

    // Relational fields
    /**
     * It's id
     */
    String id;
    /**
     * It's order's id
     */
    String order_id;
    /**
     * Items as parts and their clone counts
     * Map : Id of part in the box -> Its quantity
     */
    public Map<String, Integer> part_clone_count_map = new HashMap<>();

    // Box fields
    // length >= breadth >= height > 0
    // weight > 0
    float length;
    float breadth;
    float height;
    float weight;

    /**
     * Assert length, breadth, height > 0
     * Assert weight > 0
     */
    public Shipment(String order_id, String id, float length, float breadth, float height, float weight) {
        this.order_id = order_id;
        this.id = id;

        float hbl[] = {
                height,
                breadth,
                length
        };
        Arrays.sort(hbl);
        this.length = hbl[2];
        this.breadth = hbl[1];
        this.height = hbl[0];

        this.weight = weight;
    }

    /**
     * Assert quantity > 0
     */
    public void addPart(String part, int quantity) {
        // Old part
        if (this.part_clone_count_map.containsKey(part)) {
            int prev_quantity = this.part_clone_count_map.get(part);
            this.part_clone_count_map.put(part, quantity + prev_quantity);
        }
        // New part
        else {
            this.part_clone_count_map.put(part, quantity);
        }
    }

    public SInequality extractSInequality() {
        SInequality sInequality = new SInequality(this.length, this.breadth, this.height, this.weight);
        for (Map.Entry<String, Integer> entry : this.part_clone_count_map.entrySet()) {
            sInequality.addTerm(entry.getValue(), entry.getKey());
        }
        return sInequality;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("length = ")
                .append(this.length)
                .append(" breadth = ")
                .append(this.breadth)
                .append(" height = ")
                .append(this.height)
                .append(" weight = ")
                .append(this.weight);
        for (Map.Entry<String, Integer> entry : this.part_clone_count_map.entrySet()) {
            sb.append("\n").append(entry.getKey()).append(" -> ").append(entry.getValue());
        }
        sb.append("\n");
        return sb.toString();
    }

}
