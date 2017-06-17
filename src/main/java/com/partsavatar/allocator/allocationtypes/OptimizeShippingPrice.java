package com.partsavatar.allocator.allocationtypes;

import com.partsavatar.allocator.api.google.Response;
import com.partsavatar.allocator.components.Order;
import com.partsavatar.allocator.components.Warehouse;
import com.partsavatar.allocator.exceptions.OrderCannotBeFullfilledException;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class OptimizeShippingPrice {

    public static Map<Warehouse, Map<String, Integer>> allocate(@NonNull final Order order, @NonNull final Map<Response, Warehouse> responseWarehouseMap) throws OrderCannotBeFullfilledException {
        // Get all responses
        ArrayList<Response> all_responses = new ArrayList<>();
        for (Map.Entry<Response, Warehouse> response_warehouse_pair : responseWarehouseMap.entrySet()) {
            all_responses.add(response_warehouse_pair.getKey());
        }

        // Sort all responses wrt distance, in turn sorting the warehouses
        Collections.sort(all_responses, Response::compareDistance);

        // Answer
        Map<Warehouse, Map<String, Integer>> allocation = new HashMap<>();

        // Make a copy of the order
        Order distance_copy = new Order(order);
        // Pipe through the sorted warehouses greedily
        for (Response response : all_responses) {
            Warehouse warehouse = responseWarehouseMap.get(response);
            Map<String, Integer> order_taken = warehouse.pipeOrderGreedily(distance_copy);
            allocation.put(warehouse, order_taken);
        }

        // Order not fulfilled
        if (!distance_copy.getPartCloneCountMap().isEmpty()) {
            throw new OrderCannotBeFullfilledException();
        }
        // Order fulfilled
        else {
            return allocation;
        }
    }

}
