import org.omg.PortableInterceptor.INACTIVE;

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
    private class PartInfo {
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
     * Assert clone_count > 0
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
     * Changes param order, by changing the clone counts
     * If part is fulfilled removes the part from order
     * Else decreases clone count by existing amount in warehouse
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

        return order_taken;
    }

}
