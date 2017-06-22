package com.partsavatar.allocator.operations;

import com.partsavatar.allocator.components.CustomerOrder;
import com.partsavatar.allocator.components.warehouse.Warehouse;
import lombok.NonNull;

import java.util.HashMap;
import java.util.Map;

public class Pipe {

    // DO NOT finalize param customerOrder
    // todo : fix simultaneous change exception
    // todo : make params final
    public static Map<String, Integer> pipeOrderGreedily(@NonNull final Warehouse warehouse, @NonNull CustomerOrder customerOrder) {
        Map<String, Integer> order_taken = new HashMap<>();

        // For every product in the customerOrder
        for (Map.Entry<String, Integer> productCloneCount : customerOrder.getProductCloneCountMap().entrySet()) {
            String productSku = productCloneCount.getKey();

            // This warehouse has the product
            if (warehouse.containsProduct(productSku)) {
                int needed = productCloneCount.getValue();
                int existing = warehouse.getCloneCount(productSku);

                // If the skuCloneCount of the product is > 0
                if (existing > 0) {
                    // order is fulfilled
                    if (needed <= existing) {
                        order_taken.put(productSku, needed);
                        customerOrder.getProductCloneCountMap().remove(productSku);
                    }
                    // Some more clones needed
                    else {
                        order_taken.put(productSku, existing);
                        customerOrder.getProductCloneCountMap().put(productSku, needed - existing);
                    }
                }

            }

        }

        return order_taken;
    }

    // DO NOT finalize param customerOrder
    // todo : fix simultaneous change exception
    // todo : make params final
    public static int pipeProductGreedily(@NonNull final Warehouse warehouse, @NonNull CustomerOrder customerOrder, @NonNull final String productSku) {
        if (customerOrder.getProductCloneCountMap().get(productSku) == null) return -1;

        int productOrderTaken;

        // Warehouse has the product
        if (warehouse.containsProduct(productSku)) {
            int needed = customerOrder.getProductCloneCountMap().get(productSku);
            int existing = warehouse.getCloneCount(productSku);

            // order is fulfilled
            if (needed <= existing) {
                customerOrder.getProductCloneCountMap().remove(productSku);
                productOrderTaken = needed;
            }
            // Some more clones needed
            else {
                customerOrder.getProductCloneCountMap().put(productSku, needed - existing);
                productOrderTaken = existing;
            }
        }
        // Ware does not have the product
        else productOrderTaken = 0;

        return productOrderTaken;
    }

}
