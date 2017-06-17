package com.partsavatar;

import com.partsavatar.allocationtypes.OptimizeCostPrice;
import com.partsavatar.allocationtypes.OptimizeShippingDuration;
import com.partsavatar.allocationtypes.OptimizeShippingPrice;
import com.partsavatar.components.Order;
import com.partsavatar.components.Warehouse;
import com.partsavatar.exceptions.OrderCannotBeFullfilledException;
import com.partsavatar.mapsapi.Response;
import com.partsavatar.mapsapi.google.GoogleMaps;
import lombok.NonNull;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * todo : implement satellite stores
 */
public class Allocator {


    /**
     * MapAPI response is preserved, i.e. given list of warehouses (and an order) the mapAPI responses list corresponds to former
     * Assert unique warehouses
     */
    private static Map<Response, Warehouse> getResponseWarehouseMap(@NonNull final Order order, @NonNull final ArrayList<Warehouse> warehouses) throws IOException, ParseException {
        Map<Response, Warehouse> ans = new HashMap<>();

        String[] destinations = {order.getDeliveryAddress().getRaw()};
        String[] origins = new String[warehouses.size()];
        for (int i = 0; i < warehouses.size(); i++) {
            origins[i] = warehouses.get(i).getAddress().getRaw();
        }

        ArrayList<Response> map_responses = GoogleMaps.getDistancesAndTimes(origins, destinations);

        for (int i = 0; i < warehouses.size(); i++) {
            ans.put(map_responses.get(i), warehouses.get(i));
        }

        return ans;
    }

    private static void allocateOrder(@NonNull final Order order, @NonNull final ArrayList<Warehouse> warehouses) throws IOException, ParseException, OrderCannotBeFullfilledException {
        Map<Response, Warehouse> response_warehouse_map = getResponseWarehouseMap(order, warehouses);
        Map<Warehouse, Map<String, Integer>> distance_allocation = OptimizeShippingPrice.allocate(order, response_warehouse_map);
        Map<Warehouse, Map<String, Integer>> duration_allocation = OptimizeShippingDuration.allocate(order, response_warehouse_map);
        Map<Warehouse, Map<String, Integer>> cost_price_allocation = OptimizeCostPrice.allocate(order, warehouses);
    }

    public static void main(String[] args) throws IOException, ParseException {

    }
}
