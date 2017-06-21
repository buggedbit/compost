package com.partsavatar.allocator.components;

import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;

import java.util.HashMap;
import java.util.Map;

@Getter
@ToString
public class CustomerOrder {
    @NonNull
    private AddressInfo deliveryAddress;
    private Map<String, Integer> productCloneCountMap = new HashMap<>();

    public CustomerOrder(@NonNull final AddressInfo deliveryAddress) {
        this.deliveryAddress = new AddressInfo(deliveryAddress);
    }

    public CustomerOrder(@NonNull final CustomerOrder customerOrder) {
        this.deliveryAddress = new AddressInfo(customerOrder.deliveryAddress);
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
