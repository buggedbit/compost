package com.partsavatar.allocator.allocationtypes;

import com.partsavatar.allocator.components.CustomerOrder;
import com.partsavatar.allocator.components.warehouse.Warehouse;
import com.partsavatar.allocator.exceptions.OrderCannotBeFullfilledException;
import com.partsavatar.allocator.operations.Pipe;
import lombok.NonNull;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

public class OptimizeCostPrice {

    public static Map<Warehouse, Map<String, Integer>> allocate(@NonNull final CustomerOrder customerOrder, @NonNull final Vector<Warehouse> warehouses) throws OrderCannotBeFullfilledException {
        // Answer
        Map<Warehouse, Map<String, Integer>> allocation = new HashMap<>();

        // Make a copy of the customerOrder
        CustomerOrder order = new CustomerOrder(customerOrder);

        // Initialize customerOrder in all warehouses
        for (Warehouse warehouse : warehouses) {
            allocation.put(warehouse, new HashMap<>());
        }

        // For each ordered part
        for (Map.Entry<String, Integer> productCloneCount : order.getProductCloneCountMap().entrySet()) {
            String productSku = productCloneCount.getKey();

            // Sort warehouses according to cost price of this part
            warehouses.sort(new Comparator<Warehouse>() {
                /**
                 * Sort all warehouses wrt cost price of the ordered part
                 * A warehouse containing part < A warehouse NOT containing part
                 * If both warehouses does not contain the part they are equal
                 */
                @Override
                public int compare(Warehouse w1, Warehouse w2) {
                    boolean w1ContainsProduct = w1.containsProduct(productSku);
                    boolean w2ContainsProduct = w2.containsProduct(productSku);
                    // If both warehouses do not contain
                    if (!w1ContainsProduct &&
                            !w2ContainsProduct) {
                        // w1 == w2
                        return 0;
                    }
                    // w1 contains the part and w2 does not contain
                    else if (w1ContainsProduct &&
                            !w2ContainsProduct) {
                        // w1 < w2 -> return -1
                        return -1;
                    }
                    // w2 contains the part and w1 does not contain
                    else if (!w1ContainsProduct) {
                        // w1 > w2 -> return 1
                        return 1;
                    }
                    // Both contain the part
                    else {
                        double w1_cost_price = w1.getCostPrice(productSku);
                        double w2_cost_price = w2.getCostPrice(productSku);

                        if (w1_cost_price < w2_cost_price) return -1;
                        else if (w1_cost_price == w2_cost_price) return 0;
                        else return 1;
                    }

                }
            });

            // Pipe the part through the sorted warehouses greedily
            for (Warehouse warehouse : warehouses) {
                Pipe.PipedPart pipedPart = Pipe.pipeProductGreedily(warehouse, order, productSku);

                if (pipedPart == null) {
                    break;
                } else if (pipedPart.getProductTaken() > 0) {
                    int partOrderTaken = pipedPart.getProductTaken();
                    order = pipedPart.getOrderRemaining();

                    Map<String, Integer> whAllocation = allocation.get(warehouse);
                    // As all parts in an customerOrder are unique
                    // whAllocation will not previously have a key == productSku
                    whAllocation.put(productSku, partOrderTaken);
                }
            }

            // If still this part remains in the order -> customerOrder cannot be fulfilled
            if (order.getProductCloneCountMap().containsKey(productSku)) {
                throw new OrderCannotBeFullfilledException();
            }
        }

        // At this point customerOrder can definitely be fulfilled
        return allocation;
    }

}
