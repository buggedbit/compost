package com.partsavatar.productdimension;

import java.util.ArrayList;

public interface ProductDimensionDAO {

    ArrayList<ProductDimension> getAll();

    boolean clearTableAndInsert(ArrayList<ProductDimension> productDimensions);

}
