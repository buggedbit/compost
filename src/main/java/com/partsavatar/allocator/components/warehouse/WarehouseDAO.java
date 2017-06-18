package com.partsavatar.allocator.components.warehouse;

import java.util.Vector;

public interface WarehouseDAO {

    public Vector<Warehouse> getAll();

    /**
     * Return null if there is no such part
     */
    public Warehouse.ProductInfo getProductInfo(final String sku);

}
