package com.partsavatar.allocator.components.warehouse;

import java.util.Vector;

public interface WarehouseDAO {

    /**
     * Return null if any problem
     */
    public Vector<Warehouse> getAll();

    /**
     * Return null if there is no such part or some problem
     */
    public ProductInfo getProductInfo(final String warehouseId, final String sku);

}
