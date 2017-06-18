package com.partsavatar.allocator.components;

import lombok.Getter;
import lombok.NonNull;

import java.util.HashMap;
import java.util.Map;

@Getter
public class CustomerOrder {
    @NonNull
    private Address deliveryAddress;
    private Map<String, Integer> productCloneCountMap = new HashMap<>();

    public CustomerOrder(@NonNull final Address deliveryAddress) {
        this.deliveryAddress = new Address(deliveryAddress);
    }

    public CustomerOrder(@NonNull final CustomerOrder customerOrder) {
        this.deliveryAddress = new Address(customerOrder.deliveryAddress);
        this.productCloneCountMap = new HashMap<>(customerOrder.productCloneCountMap);
    }

    public void addPart(@NonNull final String part, final int cloneCount) {
        if (cloneCount < 0) throw new IllegalArgumentException();

        if (this.productCloneCountMap.containsKey(part)) {
            int prev_clone_count = this.productCloneCountMap.get(part);
            this.productCloneCountMap.put(part, prev_clone_count + cloneCount);
        } else {
            this.productCloneCountMap.put(part, cloneCount);
        }

    }

    public void updatePart(@NonNull final String part, final int finalCloneCount) {
        if (finalCloneCount < 0) throw new IllegalArgumentException();
        else if (finalCloneCount > 0)
            this.productCloneCountMap.put(part, finalCloneCount);
        else {
            this.productCloneCountMap.remove(part);
        }
    }
}
