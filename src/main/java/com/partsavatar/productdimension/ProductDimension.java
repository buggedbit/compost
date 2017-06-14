package com.partsavatar.productdimension;

public class ProductDimension {
    private final String sku;
    private double length;
    private double breadth;
    private double height;
    private double weight;

    public ProductDimension(String sku, double length, double breadth, double height, double weight) {
        this.sku = sku;
        this.length = length;
        this.breadth = breadth;
        this.height = height;
        this.weight = weight;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(sku)
                .append(" l=").append(this.length)
                .append(" b=").append(this.breadth)
                .append(" h=").append(this.height)
                .append(" w=").append(this.weight);
        return sb.toString();
    }

    public String getSku() {
        return sku;
    }

    public double getLength() {
        return length;
    }

    public double getBreadth() {
        return breadth;
    }

    public double getHeight() {
        return height;
    }

    public double getWeight() {
        return weight;
    }

    public void setLength(double length) {
        this.length = length;
    }

    public void setBreadth(double breadth) {
        this.breadth = breadth;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }
}
