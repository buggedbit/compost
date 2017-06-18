package com.partsavatar.allocator.components.warehouse;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;

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