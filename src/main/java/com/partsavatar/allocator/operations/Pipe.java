package com.partsavatar.allocator.operations;

import com.partsavatar.allocator.components.CustomerOrder;
import com.partsavatar.allocator.components.warehouse.ProductInfo;
import com.partsavatar.allocator.components.warehouse.Warehouse;
import lombok.NonNull;

import java.util.HashMap;
import java.util.Map;

public class Pipe {

    // DO NOT finalize param customerOrder
    public static Map<String, Integer> pipeOrderGreedily(@NonNull final Warehouse warehouse, @NonNull CustomerOrder customerOrder) {
        Map<String, Integer> order_taken = new HashMap<>();

        // For every product in the customerOrder
        for (Map.Entry<String, Integer> productCloneCount : customerOrder.getProductCloneCountMap().entrySet()) {
            String productSku = productCloneCount.getKey();

            ProductInfo productInfo = warehouse.getProductInfo(productSku);

            // This warehouse has the product
            if (productInfo != null) {
                int needed = productCloneCount.getValue();
                int existing = productInfo.getCloneCount();

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
    public static int pipeProductGreedily(@NonNull final Warehouse warehouse, @NonNull CustomerOrder customerOrder, @NonNull final String productSku) {
        if (customerOrder.getProductCloneCountMap().get(productSku) == null) return -1;

        int productOrderTaken;

        ProductInfo productInfo = warehouse.getProductInfo(productSku);

        // Warehouse has the product
        if (productInfo != null) {
            int needed = customerOrder.getProductCloneCountMap().get(productSku);
            int existing = productInfo.getCloneCount();

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
