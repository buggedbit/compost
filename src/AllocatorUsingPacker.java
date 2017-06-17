import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


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

	    //TODO Get Order partList. 
	    ArrayList<Part> sortedPartList = new ArrayList<>();
	    
	    //Store availability of parts and find the rare items. 
	    Map<Integer, Part> priorityMap = new HashMap<>();
	    for (Part part : sortedPartList) {
			
	    	part.availability = new HashSet<>();
			
	    	for (Integer i = 0; i < sortedWarehouses.size(); i++) {
				if(sortedWarehouses.get(i).inventory.containsKey(part.id))
					part.availability.add(i);
			}
	    	if(part.availability.size() == 1){
	    		Integer whIndex = part.availability.iterator().next();
	    		priorityMap.put(whIndex, part);
	    	}
	    }
	    
	    //*Sort the Priority Map in descending order of distance to customer.
	    //(So small items can fill without an extra box)
	    ArrayList<Integer> sortedPriorityWarehouses= new ArrayList<>();
	    for (Integer i : priorityMap.keySet())
			sortedPriorityWarehouses.add(i);
	    Collections.sort(sortedPriorityWarehouses, Collections.reverseOrder());
	    
	    //Sort order w.r.t availability and volume.
	    Collections.sort(sortedPartList, (Part p1, Part p2)-> p1.preferentialCompare(p2));
	}
}
