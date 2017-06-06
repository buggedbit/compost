import java.util.HashMap;
import java.util.Map;

/**
 * Order
 */
public class Order {
    /**
     * Delivery address
     */
    Address delivery_address;
    /**
     * Ordered parts and their clone counts
     * map : part id -> clone count
     */
    Map<String, Integer> part_clone_count_map = new HashMap<>();

    public Order(Address delivery_address) {
        this.delivery_address = delivery_address;
    }

    public Order(Order order) {
        this.delivery_address = new Address(order.delivery_address);
        this.part_clone_count_map = new HashMap<>(order.part_clone_count_map);
    }

    /**
     * If the part already exists in the order, the final clone count is sum of previous and present clone counts
     * Else adds the part to the order with the given clone count
     */
    public void addPart(String part, int clone_count) {

        // Part exists in the list
        if (this.part_clone_count_map.containsKey(part)) {
            int prev_clone_count = this.part_clone_count_map.get(part);
            this.part_clone_count_map.put(part, prev_clone_count + clone_count);
        }
        // Part is not in list
        else {
            this.part_clone_count_map.put(part, clone_count);
        }

    }

}
