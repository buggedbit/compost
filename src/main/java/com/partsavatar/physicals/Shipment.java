package com.partsavatar.physicals;

import lombok.Getter;
import lombok.ToString;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Getter
@ToString(exclude = {"partCloneCountMap"})
public class Shipment {

    private String sku;
    private String orderId;
    private Map<String, Integer> partCloneCountMap = new HashMap<>();

    private double length;
    private double breadth;
    private double height;
    private double weight;

    public Shipment(final String orderId, final String sku, final double length, final double breadth, final double height, final double weight) {
        this.orderId = orderId;
        this.sku = sku;

        double hbl[] = {
                height,
                breadth,
                length
        };
        Arrays.sort(hbl);
        this.length = hbl[2];
        this.breadth = hbl[1];
        this.height = hbl[0];

        this.weight = weight;
    }

    public void addPart(final String part, final int quantity) {
        if (this.partCloneCountMap.containsKey(part)) {
            int prev_quantity = this.partCloneCountMap.get(part);
            this.partCloneCountMap.put(part, quantity + prev_quantity);
        } else {
            this.partCloneCountMap.put(part, quantity);
        }
    }

}
