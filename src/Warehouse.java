import java.util.HashMap;
import java.util.Map;

/**
 * Warehouse
 */
public class Warehouse {
    /**
     * Its address
     */
    Address address;
    /**
     * Inventory of this warehouse
     * Map : part id -> PartInfo instance
     */
    Map<String, PartInfo> inventory = new HashMap<>();

    /**
     * All the information about a part in the inventory of a warehouse
     */
    public class PartInfo {
        String id;
        double cost_price;
        int clone_count;

        /**
         * Assert cost_price > 0
         * Assert clone_count > 0
         */
        public PartInfo(String id, double cost_price, int clone_count) {
            this.id = id;
            this.cost_price = cost_price;
            this.clone_count = clone_count;
        }
    }

    public Warehouse(Address address) {
        this.address = address;
    }

    /**
     * If there is no such part in this warehouse's inventory add this part and its info
     * Else pass
     * Assert cost_price > 0
     * Assert clone_count >= 0
     */
    public void insertNewPart(String id, double cost_price, int clone_count) {

        // If no such part in inventory
        if (!this.inventory.containsKey(id)) {
            // Add
            this.inventory.put(id, new PartInfo(id, cost_price, clone_count));
        }

    }

    /**
     * Returns order taken by the warehouse
     * <br/>
     * Changes param order
     * For each part in order
     * If part is fulfilled removes the part from order
     * Else decreases clone count by existing amount in warehouse
     * order_taken is whatever removed or decreased from the param order
     * <br/>
     * Does not indicate the warehouse that order has been placed
     * Does not decrease in inventory
     */
    public Map<String, Integer> pipeOrderGreedily(Order order) {
        Map<String, Integer> order_taken = new HashMap<>();

        // For every part in the order
        for (Map.Entry<String, Integer> part_clone_count : order.part_clone_count_map.entrySet()) {
            String part_id = part_clone_count.getKey();

            // This warehouse has the part
            if (this.inventory.containsKey(part_id)) {
                int needed = part_clone_count.getValue();
                int existing = this.inventory.get(part_id).clone_count;

                // If the clone_count of the part is > 0
                if (existing > 0) {
                    // Part is fulfilled
                    if (needed <= existing) {
                        order_taken.put(part_id, needed);
                        order.part_clone_count_map.remove(part_id);
                    }
                    // Some more clones needed
                    else {
                        order_taken.put(part_id, existing);
                        order.part_clone_count_map.put(part_id, needed - existing);
                    }
                }

            }

        }

        return order_taken;
    }

    /**
     * Returns part order taken by the warehouse
     * <br/>
     * Changes the part with param part_id in param order
     * If part is fulfilled removes the part from order
     * Else decreases clone count by existing amount in warehouse
     * part_order_taken is whatever removed or decreased from the param order
     * <br/>
     * Return value 0 can mean
     * 1.Warehouse does not contain the part (map has not such key)
     * 2.Warehouse contains 0 copies of that part (map has key, clone_count in warehouse = 0)
     * <br/>
     * Does not indicate the warehouse that order has been placed
     * Does not decrease in inventory
     */
    public int pipePartGreedily(Order order, String part_id) {
        int part_order_taken;

        // Warehouse has the part
        if (this.inventory.containsKey(part_id)) {
            int needed = order.part_clone_count_map.get(part_id);
            int existing = this.inventory.get(part_id).clone_count;

            // Part is fulfilled
            if (needed <= existing) {
                order.part_clone_count_map.remove(part_id);
                part_order_taken = needed;
            }
            // Some more clones needed
            else {
                order.part_clone_count_map.put(part_id, needed - existing);
                part_order_taken = existing;
            }
        }
        // Ware does not have the part
        else part_order_taken = 0;

        return part_order_taken;
    }

}
