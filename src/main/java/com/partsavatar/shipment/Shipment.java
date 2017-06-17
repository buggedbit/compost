package com.partsavatar.shipment;

import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Getter
@ToString(exclude = {"partCloneCountMap"})
public class Shipment {

    @NonNull
    private String sku;
    @NonNull
    private String orderId;
    private Map<String, Integer> partCloneCountMap = new HashMap<>();

    private double length;
    private double breadth;
    private double height;
    private double weight;

    public Shipment(@NonNull final String orderId, @NonNull final String sku, final double length,
                    final double breadth, final double height, final double weight) {
        if (length <= 0
                || breadth <= 0
                || height <= 0
                || weight <= 0) throw new IllegalArgumentException();

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

    public void addPart(@NonNull final String part, final int quantity) {
        if (quantity < 0) throw new IllegalArgumentException();

        if (this.partCloneCountMap.containsKey(part)) {
            int prev_quantity = this.partCloneCountMap.get(part);
            this.partCloneCountMap.put(part, quantity + prev_quantity);
        } else {
            this.partCloneCountMap.put(part, quantity);
        }
    }

}
