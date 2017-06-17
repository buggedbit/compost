package com.partsavatar.allocationtypes;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.partsavatar.api.google.Response;
import com.partsavatar.components.Order;
import com.partsavatar.components.Warehouse;
import com.partsavatar.dao.packing.PackingDAO;
import com.partsavatar.dao.packing.PackingDAOImpl;
import com.partsavatar.estimates.Estimate;

import components.Box;

//TODO Preferential Treatment for some items.

public class AllocatorUsingPacker {
	static PackingDAO packer = new PackingDAOImpl();
	
	static void  Algo(Order order, Map<Response, Warehouse> responseWarehouseMap) throws NumberFormatException, IOException{
		// Get all responses
	    ArrayList<Response> allResponses = new ArrayList<>();
	    for (Response r : responseWarehouseMap.keySet())
	    	allResponses.add(r);

	    // Sort all responses w.r.t distance, in turn sorting the warehouses
	    Collections.sort(allResponses, (Response r1, Response r2) -> r1.compareDistance(r2));
	  
	    // Sorting warehouses w.r.t distance 
	    ArrayList<Warehouse> sortedWarehouses = new ArrayList<>();
	    for (Response r : allResponses)
	    	sortedWarehouses.add(responseWarehouseMap.get(r));

	    //Store availability of parts and find the rare items. 
	    Map<Integer, ArrayList<String>> priorityMap = new HashMap<>();
	    Map<String, ArrayList<Integer>> availability = new HashMap<>(); 
	    for (String partId : order.getPartCloneCountMap().keySet()) {
			
	    	availability.put(partId, new ArrayList<>());
			
	    	for (Integer i = 0; i < sortedWarehouses.size(); i++) {
				if(sortedWarehouses.get(i).getInventory().containsKey(partId))
					availability.get(partId).add(i);
			}
	    	if(availability.get(partId).size() == 1){
	    		Integer whIndex = availability.get(partId).get(0);
	    		if(!priorityMap.containsKey(whIndex))
	    			priorityMap.put(whIndex, new ArrayList<>());
	    		priorityMap.get(whIndex).add(partId);
	    		order.updatePart(partId, 0);
	    	}
	    }
	    
	    //*Sort the Priority Map in descending order of distance to customer.
	    //(So small items can fill without an extra box)
	    ArrayList<Integer> sortedPriorityWarehouseIndices= new ArrayList<>();
	    for (Integer i : priorityMap.keySet())
			sortedPriorityWarehouseIndices.add(i);
	    Collections.sort(sortedPriorityWarehouseIndices, Collections.reverseOrder());
	    
	    
	    //Fill Priority 1 items
	    for (Integer whIndex : sortedPriorityWarehouseIndices) {
			fill(sortedWarehouses.get(whIndex), order, priorityMap.get(whIndex));
		}
	}

	private static void fill(Warehouse warehouse, Order order, ArrayList<String> priorityPartList) throws NumberFormatException, IOException {
		ArrayList<components.Part> orderList = new ArrayList<>();
		for (String partId : priorityPartList) {
			orderList.add(Estimate.estimatePart(partId, warehouse.pipePartGreedily(order, partId)));
		}
		
		components.Order newOrder = new components.Order(orderList);
		List<Box> filledBoxes = packer.getPacking(packer.getAvailableBoxes(),newOrder);
		Integer currPartVol = getPartVol(filledBoxes);
		Integer currBoxVol = getBoxVol(filledBoxes);
		while(getPartVol(filledBoxes) >= currPartVol && filledBoxes.size() <= currBoxVol){
			orderList.add(Estimate.estimatePart(null, currBoxVol));
		}
	}

	private static Integer getPartVol(List<Box> filledBoxes) {
		Integer partsVol = 0;
		for (Box box : filledBoxes) 
			partsVol+= box.getPartsVol();
		return partsVol;
	}
	private static Integer getBoxVol(List<Box> filledBoxes) {
		Integer boxVol = 0;
		for (Box box : filledBoxes)
			boxVol+= box.getVol();
		return boxVol;
	}
	
}

