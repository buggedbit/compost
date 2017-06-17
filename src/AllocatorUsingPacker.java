import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

import MapsAPI.Response;

public class AllocatorUsingPacker {
	static void  Algo(Order order, Map<Response, Warehouse> responseWarehouseMap){
		// Get all responses
	    ArrayList<Response> allResponses = new ArrayList<>();
	    for (Response r : responseWarehouseMap.keySet())
	    	allResponses.add(r);

	    // Sort all responses w.r.t distance, in turn sorting the warehouses
	    Collections.sort(allResponses, (Response r1, Response r2) -> r1.distanceCompare(r2));
	    
	    // Sorting warehouses w.r.t distance
	    ArrayList<Warehouse> sortedWarehouses = new ArrayList<>();
	    for (Response r : allResponses)
	    	sortedWarehouses.add(responseWarehouseMap.get(r));

	    
	
	}
}
