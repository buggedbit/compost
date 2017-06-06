import MapsAPI.Google.GoogleMaps;
import MapsAPI.Response;
import com.sun.org.apache.xpath.internal.operations.Or;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.*;

/**
 * Main
 * todo : implement satellite stores
 */
public class Main {

    /**
     * Allocates order among warehouses optimizing distance
     */
    private static Map<Warehouse, Map<String, Integer>> allocateOptimizingDistance(Order order, Map<Response, Warehouse> response_warehouse_map) {
        // Get all responses
        ArrayList<Response> all_responses = new ArrayList<>();
        for (Map.Entry<Response, Warehouse> response_warehouse_pair : response_warehouse_map.entrySet()) {
            all_responses.add(response_warehouse_pair.getKey());
        }

        // Sort all responses wrt distance, in turn sorting the warehouses
        all_responses.sort(new Comparator<Response>() {
            @Override
            public int compare(Response o1, Response o2) {
                if (o1.distance < o2.distance) {
                    return -1;
                } else if (o1.distance == o2.distance) {
                    return 0;
                } else {
                    return 1;
                }
            }
        });

        // Answer
        Map<Warehouse, Map<String, Integer>> allocation = new HashMap<>();

        // Make a copy of the order
        Order distance_copy = new Order(order);
        // Pipe through the sorted warehouses greedily
        for (Response response : all_responses) {
            Warehouse warehouse = response_warehouse_map.get(response);
            Map<String, Integer> order_taken = warehouse.pipeOrderGreedily(distance_copy);
            allocation.put(warehouse, order_taken);
        }

        // Order not fulfilled
        if (!distance_copy.part_clone_count_map.isEmpty()) {
            System.out.println("Order cannot be fulfilled");
            return null;
        }
        // Order fulfilled
        else {
            return allocation;
        }
    }

    /**
     * Allocates order among warehouses optimizing duration of travel
     */
    private static Map<Warehouse, Map<String, Integer>> allocateOptimizingDuration(Order order, Map<Response, Warehouse> response_warehouse_map) {
        // Get all responses
        ArrayList<Response> all_responses = new ArrayList<>();
        for (Map.Entry<Response, Warehouse> response_warehouse_pair : response_warehouse_map.entrySet()) {
            all_responses.add(response_warehouse_pair.getKey());
        }

        // Sort all responses wrt distance, in turn sorting the warehouses
        all_responses.sort(new Comparator<Response>() {
            @Override
            public int compare(Response o1, Response o2) {
                if (o1.duration < o2.duration) {
                    return -1;
                } else if (o1.duration == o2.duration) {
                    return 0;
                } else {
                    return 1;
                }
            }
        });

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
        if (!duration_copy.part_clone_count_map.isEmpty()) {
            System.out.println("Order cannot be fulfilled");
            return null;
        }
        // Order fulfilled
        else {
            return allocation;
        }
    }

    /**
     * Assert map response order is preserved, i.e. given list of warehouses (and an order) the map responses list corresponds to former
     * Assert unique warehouses
     */
    private static Map<Response, Warehouse> getResponseWarehouseMap(Order order, ArrayList<Warehouse> warehouses) throws IOException, ParseException {
        Map<Response, Warehouse> ans = new HashMap<>();

        String[] destinations = {order.delivery_address.raw};
        String[] origins = new String[warehouses.size()];
        for (int i = 0; i < warehouses.size(); i++) {
            origins[i] = warehouses.get(i).address.raw;
        }

        ArrayList<Response> map_responses = GoogleMaps.getDistancesAndTimes(origins, destinations);

        for (int i = 0; i < warehouses.size(); i++) {
            ans.put(map_responses.get(i), warehouses.get(i));
        }

        return ans;
    }

    /**
     * Assert shipping cost ~ shipping distance
     */
    private static void allocateOrder(Order order, ArrayList<Warehouse> warehouses) throws IOException, ParseException {
        Map<Response, Warehouse> response_warehouse_map = getResponseWarehouseMap(order, warehouses);
        Map<Warehouse, Map<String, Integer>> distance_allocation = allocateOptimizingDistance(order, response_warehouse_map);
        Map<Warehouse, Map<String, Integer>> duration_allocation = allocateOptimizingDuration(order, response_warehouse_map);

    }

    public static void main(String[] args) throws IOException, ParseException {
        
    }
}
