package com.partsavatar.allocator.components.warehouse;

import com.partsavatar.allocator.components.Address;
import com.partsavatar.allocator.components.CustomerOrder;
import com.partsavatar.allocator.estimates.Estimate;
import com.partsavatar.packer.components.Part;
import lombok.*;

import java.util.*;

/**
 * Assert if warehouse has a product it definitely contains that product's cost price and clone count
 */
@Getter
@AllArgsConstructor
public class Warehouse {

    @NonNull
    private String id;
    @NonNull
    private Address address;

    @Getter
    @ToString
    @EqualsAndHashCode
    public class ProductInfo {
        @NonNull
        private String sku;
        private double costPrice;
        private int cloneCount;

        public ProductInfo(@NonNull final String sku, final double costPrice, final int cloneCount) {
            if (costPrice <= 0) throw new IllegalArgumentException();
            if (cloneCount < 0) throw new IllegalArgumentException();
            this.sku = sku;
            this.costPrice = costPrice;
            this.cloneCount = cloneCount;
        }

    }

    public static Vector<Warehouse> getAll() {
        return new WarehouseDAOImpl().getAll();
    }

    public ProductInfo getProductInfo(@NonNull final String sku) {
        return new WarehouseDAOImpl().getProductInfo(sku);
    }

    public boolean containsProduct(@NonNull final String sku) {
        return this.getProductInfo(sku) != null;
    }

    // DO NOT finalize param customerOrder
    public Map<String, Integer> pipeOrderGreedily(@NonNull CustomerOrder customerOrder) {
        Map<String, Integer> order_taken = new HashMap<>();

        // For every product in the customerOrder
        for (Map.Entry<String, Integer> productCloneCount : customerOrder.getProductCloneCountMap().entrySet()) {
            String productSku = productCloneCount.getKey();

            ProductInfo productInfo = this.getProductInfo(productSku);

            // This warehouse has the product
            if (productInfo != null) {
                int needed = productCloneCount.getValue();
                int existing = productInfo.cloneCount;

                // If the cloneCount of the product is > 0
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
    public int pipeProductGreedily(@NonNull CustomerOrder customerOrder, @NonNull final String productSku) {
        if (customerOrder.getProductCloneCountMap().get(productSku) == null) throw new IllegalArgumentException();

        int productOrderTaken;

        ProductInfo productInfo = this.getProductInfo(productSku);

        // Warehouse has the product
        if (productInfo != null) {
            int needed = customerOrder.getProductCloneCountMap().get(productSku);
            int existing = productInfo.cloneCount;

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

    List<String> available = new ArrayList<>();

    public void addToAvailable(String s) {
        available.add(s);
    }

    public void removeFromAvailable(String s) {
        available.remove(s);
    }

    public void descendingSort() {
        available.sort((o1, o2) -> {
            Part p1 = Estimate.estimatePart(o1, 1);
            Part p2 = Estimate.estimatePart(o2, 1);
            return p2.volCompareTo(p1);
        });
    }

    public void ascendingSort() {
        available.sort((o1, o2) -> {
            Part p1 = Estimate.estimatePart(o1, 1);
            Part p2 = Estimate.estimatePart(o2, 1);
            return p1.volCompareTo(p2);
        });
    }
}
