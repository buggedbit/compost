package com.partsavatar.allocator.operations;

import com.partsavatar.allocator.components.CustomerOrder;
import com.partsavatar.allocator.components.warehouse.Warehouse;
import lombok.Getter;
import lombok.NonNull;

import java.util.HashMap;
import java.util.Map;

public class Pipe {

    @Getter
    public static class PipedOrder {
        @NonNull
        private CustomerOrder orderRemaining;
        private Map<String, Integer> orderTaken = new HashMap<>();

        public PipedOrder(@NonNull final CustomerOrder orderRemaining, @NonNull final Map<String, Integer> orderTaken) {
            this.orderRemaining = new CustomerOrder(orderRemaining);
            this.orderTaken = new HashMap<>(orderTaken);
        }

    }

    @Getter
    public static class PipedPart {
        @NonNull
        private CustomerOrder orderRemaining;
        @NonNull
        private String productSku;
        private int productTaken;

        public PipedPart(@NonNull final CustomerOrder orderRemaining, @NonNull final String productSku, @NonNull final int productTaken) {
            if (productTaken < 0) throw new IllegalArgumentException();

            this.orderRemaining = new CustomerOrder(orderRemaining);
            this.productSku = productSku;
            this.productTaken = productTaken;
        }

    }

    // DO NOT finalize param initial
    // todo : fix simultaneous change exception
    // todo : make params final
    public static PipedOrder pipeOrderGreedily(@NonNull final Warehouse warehouse, @NonNull final CustomerOrder initial) {
        CustomerOrder orderRemaining = new CustomerOrder(initial);
        Map<String, Integer> orderTaken = new HashMap<>();

        // For every product in the initial
        for (Map.Entry<String, Integer> productCloneCount : initial.getProductCloneCountMap().entrySet()) {
            String productSku = productCloneCount.getKey();

            // This warehouse has the product
            if (warehouse.containsProduct(productSku)) {
                int needed = productCloneCount.getValue();
                int existing = warehouse.getCloneCount(productSku);

                // If the skuCloneCount of the product is > 0
                if (existing > 0) {
                    // order is fulfilled
                    if (needed <= existing) {
                        orderTaken.put(productSku, needed);
                        orderRemaining.getProductCloneCountMap().remove(productSku);
                    }
                    // Some more clones needed
                    else {
                        orderTaken.put(productSku, existing);
                        orderRemaining.getProductCloneCountMap().put(productSku, needed - existing);
                    }
                }

            }

        }

        return new PipedOrder(orderRemaining, orderTaken);
    }

    // DO NOT finalize param initial
    // todo : fix simultaneous change exception
    // todo : make params final
    public static PipedPart pipeProductGreedily(@NonNull final Warehouse warehouse, @NonNull final CustomerOrder initial, @NonNull final String productSku) {
        CustomerOrder orderRemaining = new CustomerOrder(initial);
        int productTaken;

        // No such product in param initial
        if (initial.getProductCloneCountMap().get(productSku) == null) {
            return null;
        }

        // Warehouse has the product
        if (warehouse.containsProduct(productSku)) {
            int needed = initial.getProductCloneCountMap().get(productSku);
            int existing = warehouse.getCloneCount(productSku);

            // order is fulfilled
            if (needed <= existing) {
                orderRemaining.getProductCloneCountMap().remove(productSku);
                productTaken = needed;
            }
            // Some more clones needed
            else {
                orderRemaining.getProductCloneCountMap().put(productSku, needed - existing);
                productTaken = existing;
            }
        }
        // Ware does not have the product
        else {
            productTaken = 0;
        }

        return new PipedPart(orderRemaining, productSku, productTaken);
    }

}
