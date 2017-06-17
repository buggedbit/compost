package com.partsavatar.productdimension;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@ToString
public class ProductDimension {
    @NonNull
    private final String sku;
    private double length;
    private double breadth;
    private double height;
    private double weight;

}
