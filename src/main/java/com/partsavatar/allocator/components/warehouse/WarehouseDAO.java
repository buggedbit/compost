package com.partsavatar.allocator.components.warehouse;

import java.util.Vector;

public interface WarehouseDAO {

    /**
     * Return null if any problem
     */
    public Vector<Warehouse> getAll();

    /**
     * Return -1 if there is no such part or some problem
     */
    public int getCloneCount(final String warehouseId, final String productSku);

    /**
     * Return -1 if there is no such part or some problem
     */
    public double getCostPrice(final String warehouseId, final String productSku);

    public boolean containsProduct(final String warehouseId, final String productSku);
}
