package com.partsavatar.allocator.allocationtypes;

import com.partsavatar.allocator.api.google.Response;
import com.partsavatar.allocator.components.Order;
import com.partsavatar.allocator.components.Warehouse;
import com.partsavatar.allocator.exceptions.OrderCannotBeFullfilledException;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class OptimizeShippingDuration {

    public static Map<Warehouse, Map<String, Integer>> allocate(@NonNull final Order order, @NonNull final Map<Response, Warehouse> response_warehouse_map) throws OrderCannotBeFullfilledException {
        // Get all responses
        ArrayList<Response> all_responses = new ArrayList<>();
        for (Map.Entry<Response, Warehouse> response_warehouse_pair : response_warehouse_map.entrySet()) {
            all_responses.add(response_warehouse_pair.getKey());
        }

        // Sort all responses wrt distance, in turn sorting the warehouses
        all_responses.sort(Response::compareDuration);

        // Answer
        Map<Warehouse, Map<String, Integer>> allocation = new HashMap<>();

        // Make a copy of the order
        Order duration_copy = new Order(order);
        // Pipe through the sorted warehouses greedily
        for (Response response : all_responses) {
            Warehouse warehouse = response_warehouse_map.get(response);
            Map<String, Integer> order_taken = warehouse.pipeOrderGreedily(duration_copy);
            allocation.put(warehouse, order_taken);
        }

        // Order not fulfilled
        if (!duration_copy.getPartCloneCountMap().isEmpty()) {
            throw new OrderCannotBeFullfilledException();
        }
        // Order fulfilled
        else {
            return allocation;
        }
    }

}
