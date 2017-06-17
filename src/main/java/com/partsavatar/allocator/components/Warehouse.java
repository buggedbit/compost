package com.partsavatar.allocator.components;

import com.partsavatar.allocator.estimates.Estimate;
import com.partsavatar.packer.components.Part;
import lombok.Getter;
import lombok.NonNull;

import java.util.*;


@Getter
public class Warehouse {

    @NonNull
    Address address;
    Map<String, PartInfo> inventory = new HashMap<>();
    List<String> available = new ArrayList<>();

    @Getter
    public class PartInfo {
        @NonNull
        private String sku;
        private double costPrice;
        private int cloneCount;

        private PartInfo(@NonNull final String sku, final double costPrice, final int cloneCount) {
            if (costPrice <= 0) throw new IllegalArgumentException();
            if (cloneCount < 0) throw new IllegalArgumentException();
            this.sku = sku;
            this.costPrice = costPrice;
            this.cloneCount = cloneCount;
        }
    }

    public Warehouse(@NonNull final Address address) {
        this.address = new Address(address);
    }

    /**
     * If there is no such part in this warehouse's inventory add this part and its info
     * Else pass
     * Assert costPrice > 0
     * Assert cloneCount >= 0
     */
    public void insertNewPart(@NonNull final String id, final double cost_price, final int clone_count) {

        // If no such part in inventory
        if (!this.inventory.containsKey(id)) {
            // Add
            this.inventory.put(id, new PartInfo(id, cost_price, clone_count));
        }

    }

    /**
     * Returns customerOrder taken by the warehouse
     * <br/>
     * Changes param customerOrder
     * For each part in customerOrder
     * If part is fulfilled removes the part from customerOrder
     * Else decreases clone count by existing amount in warehouse
     * order_taken is whatever removed or decreased from the param customerOrder
     * <br/>
     * Does not indicate the warehouse that customerOrder has been placed
     * Does not decrease in inventory
     */
    public Map<String, Integer> pipeOrderGreedily(@NonNull CustomerOrder customerOrder) {
        Map<String, Integer> order_taken = new HashMap<>();

        // For every part in the customerOrder
        for (Map.Entry<String, Integer> part_clone_count : customerOrder.getPartCloneCountMap().entrySet()) {
            String part_id = part_clone_count.getKey();

            // This warehouse has the part
            if (this.inventory.containsKey(part_id)) {
                int needed = part_clone_count.getValue();
                int existing = this.inventory.get(part_id).cloneCount;

                // If the cloneCount of the part is > 0
                if (existing > 0) {
                    // Part is fulfilled
                    if (needed <= existing) {
                        order_taken.put(part_id, needed);
                        customerOrder.getPartCloneCountMap().remove(part_id);
                    }
                    // Some more clones needed
                    else {
                        order_taken.put(part_id, existing);
                        customerOrder.getPartCloneCountMap().put(part_id, needed - existing);
                    }
                }

            }

        }

        return order_taken;
    }

    /**
     * Returns part customerOrder taken by the warehouse
     * <br/>
     * Changes the part with param partId in param customerOrder
     * If part is fulfilled removes the part from customerOrder
     * Else decreases clone count by existing amount in warehouse
     * part_order_taken is whatever removed or decreased from the param customerOrder
     * <br/>
     * Return value 0 can mean
     * 1.Warehouse does not contain the part (map has not such key)
     * 2.Warehouse contains 0 copies of that part (map has key, cloneCount in warehouse = 0)
     * <br/>
     * Does not indicate the warehouse that customerOrder has been placed
     * Does not decrease in inventory
     */
    public int pipePartGreedily(@NonNull CustomerOrder customerOrder, @NonNull final String partId) {
        int part_order_taken;

        // Warehouse has the part
        if (this.inventory.containsKey(partId)) {
            int needed = customerOrder.getPartCloneCountMap().get(partId);
            int existing = this.inventory.get(partId).cloneCount;

            // Part is fulfilled
            if (needed <= existing) {
                customerOrder.getPartCloneCountMap().remove(partId);
                part_order_taken = needed;
            }
            // Some more clones needed
            else {
                customerOrder.getPartCloneCountMap().put(partId, needed - existing);
                part_order_taken = existing;
            }
        }
        // Ware does not have the part
        else part_order_taken = 0;

        return part_order_taken;
    }

    public void addToAvailable(String s) {
        available.add(s);
    }

    public void removeFromAvailable(String s) {
        available.remove(s);
    }

    public void descendingSort() {
        Collections.sort(available, new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                Part p1 = Estimate.estimatePart(o1, 1);
                Part p2 = Estimate.estimatePart(o2, 1);
                return p2.volCompareTo(p1);
            }
        });
    }

    public void ascendingSort() {
        Collections.sort(available, new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                Part p1 = Estimate.estimatePart(o1, 1);
                Part p2 = Estimate.estimatePart(o2, 1);
                return p1.volCompareTo(p2);
            }
        });
    }
}
