package com.partsavatar.allocator.components;

import com.partsavatar.allocator.components.warehouse.Warehouse;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class SatelliteStore {
    private List<Warehouse> warehouses = new ArrayList<>();

    public void addWarehouse(Warehouse w) {
        warehouses.add(w);
    }
}
