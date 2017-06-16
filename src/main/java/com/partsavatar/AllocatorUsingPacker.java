package com.partsavatar;

import com.partsavatar.components.Order;
import com.partsavatar.components.Warehouse;
import com.partsavatar.mapsapi.Response;

import java.util.ArrayList;
import java.util.Map;


public class AllocatorUsingPacker {

    public static void Algo(Order order, Map<Response, Warehouse> responseWarehouseMap) {
        // Get all responses
        ArrayList<Response> allResponses = new ArrayList<>();
        allResponses.addAll(responseWarehouseMap.keySet());

        // Sort all responses w.r.t distance, in turn sorting the warehouses
        allResponses.sort(Response::compareDistance);

        // Sorting warehouses w.r.t distance
        ArrayList<Warehouse> sortedWarehouses = new ArrayList<>();
        for (Response r : allResponses)
            sortedWarehouses.add(responseWarehouseMap.get(r));
    }
}
