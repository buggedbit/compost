package com.partsavatar.components;

import lombok.Getter;
import lombok.NonNull;

import java.util.HashMap;
import java.util.Map;

@Getter
public class Order {
    @NonNull
    private Address deliveryAddress;
    private Map<String, Integer> partCloneCountMap = new HashMap<>();

    public Order(@NonNull final Address deliveryAddress) {
        this.deliveryAddress = new Address(deliveryAddress);
    }

    public Order(@NonNull final Order order) {
        this.deliveryAddress = new Address(order.deliveryAddress);
        this.partCloneCountMap = new HashMap<>(order.partCloneCountMap);
    }

    public void addPart(@NonNull final String part, final int cloneCount) {
        if (cloneCount < 0) throw new IllegalArgumentException();

        if (this.partCloneCountMap.containsKey(part)) {
            int prev_clone_count = this.partCloneCountMap.get(part);
            this.partCloneCountMap.put(part, prev_clone_count + cloneCount);
        } else {
            this.partCloneCountMap.put(part, cloneCount);
        }

    }
    
    public void updatePart(@NonNull final String part, final int finalCloneCount) {
        if (finalCloneCount < 0) throw new IllegalArgumentException();
        else if(finalCloneCount > 0)
        	this.partCloneCountMap.put(part,finalCloneCount);
        else{
        	this.partCloneCountMap.remove(part);
    	} 
    }
}
