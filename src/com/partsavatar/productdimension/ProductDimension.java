package com.partsavatar.productdimension;

import java.util.Objects;

public class ProductDimension {
    public final String id;
    public double length;
    public double breadth;
    public double height;
    public double weight;

    public ProductDimension(String id, double length, double breadth, double height, double weight) {
        this.id = id;
        this.length = length;
        this.breadth = breadth;
        this.height = height;
        this.weight = weight;
    }

    public static ProductDimension getMergedEstimate(ProductDimension p1, ProductDimension p2) {
        if (p1.id == null || p2.id == null) return null;
        if (!Objects.equals(p1.id, p2.id)) return null;

        return new ProductDimension(p1.id,
                Math.min(p1.length, p2.length),
                Math.min(p1.breadth, p2.breadth),
                Math.min(p1.height, p2.height),
                Math.min(p1.weight, p2.weight)
        );
    }

    public String csvFormat() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.id).append('\n')
                .append(this.length).append(' ')
                .append(this.breadth).append(' ')
                .append(this.height).append(' ')
                .append(this.weight).append('\n');
        return sb.toString();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(id)
                .append(" l=").append(this.length)
                .append(" b=").append(this.breadth)
                .append(" h=").append(this.height)
                .append(" w=").append(this.weight);
        return sb.toString();
    }

}
