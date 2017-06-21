package com.partsavatar.allocator;

import com.partsavatar.allocator.allocationtypes.OptimizeCostPrice;
import com.partsavatar.allocator.allocationtypes.OptimizeShippingDistance;
import com.partsavatar.allocator.allocationtypes.OptimizeShippingDuration;
import com.partsavatar.allocator.api.google.GoogleMaps;
import com.partsavatar.allocator.api.google.Response;
import com.partsavatar.allocator.components.AddressInfo;
import com.partsavatar.allocator.components.CustomerOrder;
import com.partsavatar.allocator.components.warehouse.Warehouse;
import com.partsavatar.allocator.components.warehouse.WarehouseDAO;
import com.partsavatar.allocator.components.warehouse.WarehouseDAOImpl;
import com.partsavatar.allocator.exceptions.OrderCannotBeFullfilledException;
import lombok.NonNull;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

/**
 * todo : implement satellite stores
 * todo : implement same day delivery
 */
public class Allocator {

    /**
     * MapAPI response is preserved, i.e. given list of warehouses (and an customerOrder) the mapAPI responses list corresponds to former
     * Assert unique warehouses
     */
    private static Map<Response, Warehouse> getResponseWarehouseMap(@NonNull final CustomerOrder customerOrder, @NonNull final Vector<Warehouse> warehouses) throws IOException, ParseException {
        Map<Response, Warehouse> ans = new HashMap<>();

        String[] destinations = {customerOrder.getDeliveryAddress().getRaw()};
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

    private static void allocateOrder(@NonNull final CustomerOrder customerOrder, @NonNull final Vector<Warehouse> warehouses) throws IOException, ParseException, OrderCannotBeFullfilledException {
        Map<Warehouse, Map<String, Integer>> costPriceAllocation = OptimizeCostPrice.allocate(customerOrder, warehouses);
        System.out.println(costPriceAllocation);
        Map<Response, Warehouse> responseWarehouseMap = getResponseWarehouseMap(customerOrder, warehouses);
        Map<Warehouse, Map<String, Integer>> distanceAllocation = OptimizeShippingDistance.allocate(customerOrder, responseWarehouseMap);
        System.out.println(distanceAllocation);
        Map<Warehouse, Map<String, Integer>> durationAllocation = OptimizeShippingDuration.allocate(customerOrder, responseWarehouseMap);
        System.out.println(durationAllocation);

    }

    public static void main(String[] args) throws ParseException, OrderCannotBeFullfilledException, IOException {
        CustomerOrder customerOrder = new CustomerOrder(new AddressInfo("800 Boulevard René-Lévesque O, Montréal, QC H3B, Canada"));
        customerOrder.addPart("e2vzypowd3", 5);

        WarehouseDAO warehouseDAO = new WarehouseDAOImpl();
        Vector<Warehouse> warehouses = warehouseDAO.getAll();

        allocateOrder(customerOrder, warehouses);
    }
}
