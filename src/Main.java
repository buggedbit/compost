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
     * Allocates order among warehouses optimizing cost price
     */
    private static Map<Warehouse, Map<String, Integer>> allocateOptimizingCostPrice(Order order, ArrayList<Warehouse> warehouses) {
        // Answer
        Map<Warehouse, Map<String, Integer>> allocation = new HashMap<>();

        // Make a copy of the order
        Order cost_price_copy = new Order(order);

        // Initialize order in all warehouses
        for (Warehouse warehouse : warehouses) {
            allocation.put(warehouse, new HashMap<>());
        }

        // For each ordered part
        for (Map.Entry<String, Integer> ordered_part : cost_price_copy.part_clone_count_map.entrySet()) {
            String part_id = ordered_part.getKey();

            // Sort warehouses according to cost price of this part
            warehouses.sort(new Comparator<Warehouse>() {
                /**
                 * Sort all warehouses wrt cost price of the ordered part
                 * A warehouse containing part < A warehouse NOT containing part
                 * If both warehouses does not contain the part they are equal
                 */
                @Override
                public int compare(Warehouse w1, Warehouse w2) {

                    // If both warehouses do not contain
                    if (!w1.inventory.containsKey(part_id) &&
                            !w2.inventory.containsKey(part_id)) {
                        // w1 == w2
                        return 0;
                    }
                    // w1 contains the part and w2 does not contain
                    else if (w1.inventory.containsKey(part_id) &&
                            !w2.inventory.containsKey(part_id)) {
                        // w1 < w2 -> return -1
                        return -1;
                    }
                    // w2 contains the part and w1 does not contain
                    else if (!w1.inventory.containsKey(part_id) &&
                            w2.inventory.containsKey(part_id)) {
                        // w1 > w2 -> return 1
                        return 1;
                    }
                    // Both contain the part
                    else {
                        double w1_cost_price = w1.inventory.get(part_id).cost_price;
                        double w2_cost_price = w2.inventory.get(part_id).cost_price;

                        if (w1_cost_price < w2_cost_price) return -1;
                        else if (w1_cost_price == w2_cost_price) return 0;
                        else return 1;
                    }

                }
            });

            // Pipe the part through the sorted warehouses greedily
            for (Warehouse warehouse : warehouses) {
                int part_order_taken = warehouse.pipePartGreedily(cost_price_copy, part_id);
                if (part_order_taken != 0) {
                    Map<String, Integer> wh_allocation = allocation.get(warehouse);
                    // As all parts in an order are unique
                    // wh_allocation will not previously have a key == part_id
                    wh_allocation.put(part_id, part_order_taken);
                }
            }

            // If still this part remains in the cost_price_copy -> order cannot be fulfilled
            if (cost_price_copy.part_clone_count_map.containsKey(part_id)) {
                System.out.println("Order cannot be fulfilled");
                return null;
            }
        }

        // At this point order can definitely be fulfilled
        return allocation;
    }

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
        Map<Warehouse, Map<String, Integer>> cost_price_allocation = allocateOptimizingCostPrice(order, warehouses);
    }

    public static void main(String[] args) throws IOException, ParseException {

    }
}
