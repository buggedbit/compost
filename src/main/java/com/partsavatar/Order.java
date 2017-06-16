package com.partsavatar;

import java.util.HashMap;
import java.util.Map;

public class Order {

    Address deliveryAddress;

    Map<String, Integer> partCloneCountMap = new HashMap<>();

    public Order(final Address deliveryAddress) {
        this.deliveryAddress = new Address(deliveryAddress);
    }

    public Order(final Order order) {
        this.deliveryAddress = new Address(order.deliveryAddress);
        this.partCloneCountMap = new HashMap<>(order.partCloneCountMap);
    }

    public void addPart(final String part, final int cloneCount) {
        if (cloneCount < 0) throw new IllegalArgumentException();

        if (this.partCloneCountMap.containsKey(part)) {
            int prev_clone_count = this.partCloneCountMap.get(part);
            this.partCloneCountMap.put(part, prev_clone_count + cloneCount);
        } else {
            this.partCloneCountMap.put(part, cloneCount);
        }

    }

}
