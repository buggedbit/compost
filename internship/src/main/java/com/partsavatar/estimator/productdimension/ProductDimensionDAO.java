package com.partsavatar.estimator.productdimension;

public interface ProductDimensionDAO {

    public ProductDimension getBySku(final String sku);

    public boolean updateOrSave(final ProductDimension productDimension);

}
