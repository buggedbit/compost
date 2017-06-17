package com.partsavatar.allocator.estimates;

import com.partsavatar.estimator.productdimension.ProductDimension;
import com.partsavatar.estimator.productdimension.ProductDimensionDAO;
import com.partsavatar.estimator.productdimension.ProductDimensionDAOImpl;
import com.partsavatar.packer.components.Part;
import com.partsavatar.packer.components.Vector;

public class Estimate {
    private static ProductDimensionDAO estimator = new ProductDimensionDAOImpl();

    public static Part estimatePart(String sku, Integer quantity) {
        ProductDimension estimate = estimator.getBySku(sku);
        return new Part(
                estimate.getSku(),
                new Vector(
                        (int) estimate.getLength(),
                        (int) estimate.getBreadth(),
                        (int) estimate.getHeight()
                ),
                (int) estimate.getWeight(),
                quantity);
    }
}
