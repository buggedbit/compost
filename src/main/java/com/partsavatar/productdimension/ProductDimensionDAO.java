package com.partsavatar.productdimension;

public interface ProductDimensionDAO {

    ProductDimension getBySku(final String sku);

    boolean updateOrSave(final ProductDimension productDimension);

}
