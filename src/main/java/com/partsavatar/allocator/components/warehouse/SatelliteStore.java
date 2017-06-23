package com.partsavatar.allocator.components.warehouse;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;
import lombok.NonNull;

@Data
public class SatelliteStore {
	private List<Warehouse> warehouses;
	private @NonNull Long distance;
	public void addWarehouse(Warehouse w) {
		if(warehouses == null)
			warehouses = new ArrayList<>();
		warehouses.add(w);
	}
}
