package com.partsavatar.estimator.productdimension;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@ToString
public class ProductDimension {
    @NonNull
    private String sku;
    private double length;
    private double breadth;
    private double height;
    private double weight;

}
