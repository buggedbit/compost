package com.partsavatar.allocator.allocationtypes;

import com.easypost.model.Address;
import com.easypost.model.Parcel;
import com.partsavatar.allocator.Allocator;
import com.partsavatar.allocator.api.easyPost.EasyPostAPI;
import com.partsavatar.allocator.api.google.Response;
import com.partsavatar.allocator.components.AddressInfo;
import com.partsavatar.allocator.components.CustomerOrder;
import com.partsavatar.allocator.components.warehouse.Warehouse;
import com.partsavatar.allocator.components.warehouse.WarehouseDAO;
import com.partsavatar.allocator.components.warehouse.WarehouseDAOImpl;
import com.partsavatar.allocator.estimates.Estimate;
import com.partsavatar.allocator.operations.Pipe;
import com.partsavatar.packer.components.Box;
import com.partsavatar.packer.components.Part;
import com.partsavatar.packer.components.WarehouseOrder;
import com.partsavatar.packer.dao.packing.PackingDAO;
import com.partsavatar.packer.dao.packing.PackingDAOImpl;

import java.io.IOException;
import java.util.*;

import org.json.simple.parser.ParseException;

// TODO : Preferential Treatment for some items.
public class AllocatorUsingPacker {
    private static final Double THRESHOLD = 75.0;
    private static final PackingDAO PACKER = new PackingDAOImpl();

    public static Map<Warehouse, Map<String, Integer>> allocateWarhouseOrderUsingPacker
    	(final CustomerOrder customerOrder, final Map<Response, Warehouse> responseWarehouseMap)
    		throws NumberFormatException, IOException {
        
    	ArrayList<Response> allResponses = new ArrayList<>();
        allResponses.addAll(responseWarehouseMap.keySet());

        allResponses.sort(Response::compareDistance);
        ArrayList<Warehouse> sortedWarehouses = new ArrayList<>();
        for (Response r : allResponses)
            sortedWarehouses.add(responseWarehouseMap.get(r));

        //Store availability of parts and find the rare items.
        Map<Integer, List<String>> priorityMap = new HashMap<>();
        
        Map<String, List<Integer>> partAvailability = new HashMap<>(); 		//Multiple time use
        Map<Integer, List<String>> warehouseAvailability = new HashMap<>(); //One time use
        
        for (String partId : customerOrder.getProductCloneCountMap().keySet()) {

            partAvailability.put(partId, new ArrayList<>());

            for (Integer i = 0; i < sortedWarehouses.size(); i++) {
                if (sortedWarehouses.get(i).containsProduct(partId)) {
                    partAvailability.get(partId).add(i);
                    if(!warehouseAvailability.containsKey(i)) {
                    	warehouseAvailability.put(i, new ArrayList<>());
                    }
                    warehouseAvailability.get(i).add(partId);
                }
            }
            if (partAvailability.get(partId).size() == 1) {
                Integer whIndex = partAvailability.get(partId).get(0);
                warehouseAvailability.get(whIndex).remove(partId);
                if (!priorityMap.containsKey(whIndex)) {
                    priorityMap.put(whIndex, new ArrayList<>());
                }
                priorityMap.get(whIndex).add(partId);
                customerOrder.updatePart(partId, 0);
            }
        }

        //*Sort the Priority Map in descending customerOrder of distance to customer.
        //(So small items can fill without an extra box)
        List<Integer> sortedPriorityWarehouseIndices = new ArrayList<>();
        sortedPriorityWarehouseIndices.addAll(priorityMap.keySet());
        sortedPriorityWarehouseIndices.sort(Collections.reverseOrder());


        Map<Warehouse, Map<String, Integer>> orderCompleted = new HashMap<>();
        //Fill Priority items
        for (Integer whIndex : sortedPriorityWarehouseIndices) {
        	List<String> filledParts = fillPrimaryItems(orderCompleted, sortedWarehouses.get(whIndex), 
            		customerOrder, priorityMap.get(whIndex), warehouseAvailability.get(whIndex));
        	updateAvailability(filledParts, warehouseAvailability);
        	if(isOrderComplete(filledParts, warehouseAvailability)) {
        		return orderCompleted;
        	}
        }

        //Fill Remaining items in remaining warehouses (nearest warehouse first)
        for (int i = 0; i < sortedWarehouses.size(); i++) {
    	   if(warehouseAvailability.containsKey(i) && warehouseAvailability.get(i).size() != 0) {
    		   if (!orderCompleted.containsKey(sortedWarehouses.get(i))) {
	               orderCompleted.put(sortedWarehouses.get(i), new HashMap<>());
	    	   }
	    	   else {
	               continue;
	    	   }
	    	   List<String> filledParts = fillSecondaryItems(orderCompleted, sortedWarehouses.get(i), customerOrder, warehouseAvailability.get(i));    
	           updateAvailability(filledParts, warehouseAvailability);
	           if(isOrderComplete(filledParts, warehouseAvailability)) {
	       			return orderCompleted;
	       	   }
    	   }
        }
        return orderCompleted;
    }

    private static List<String> fillSecondaryItems(final Map<Warehouse, Map<String, Integer>> orderCompleted, 
    		final Warehouse warehouse, final CustomerOrder customerOrder, final List<String> warehouseAvailability) 
    		throws NumberFormatException, IOException {

    	//update order completed list and convert customerOrder to warehouseOrder for rare parts in this warehouse
    	Map<Part,Integer> orderList = new HashMap<>();
    	List<String> filledParts = new ArrayList<>();
        Map<String, Integer> tmpOrder = Pipe.pipeOrderGreedily(warehouse, customerOrder);
        for (String partId : tmpOrder.keySet()) {
            orderList.put(Estimate.estimatePart(partId), tmpOrder.get(partId));
            orderCompleted.get(warehouse).put(partId, tmpOrder.get(partId));
            if(!customerOrder.getProductCloneCountMap().containsKey(partId))
            	filledParts.add(partId);
        }

        List<Box> filledBoxes = PACKER.getPacking(PACKER.getAvailableBoxes(), new WarehouseOrder(orderList));
        Double currAcc = getAcc(filledBoxes);
        
        if (currAcc > THRESHOLD || filledBoxes.size() == 1) { //TODO change condition according to prices
        	System.out.println(currAcc);
        	return filledParts;
        }
       //Sort secondary parts available in warehouse according to increasing volume
        warehouseAvailability.sort((o1, o2) -> {
            Part p1 = Estimate.estimatePart(o1);
            Part p2 = Estimate.estimatePart(o2);
            return p1.volCompareTo(p2);
        });
        
        // Remove parts from order to achieve a higher accuracy if possible (small items first)
        for (String partId : warehouseAvailability) {
        	 Part pEstimate = Estimate.estimatePart(partId);
        	 
            for (int qty = tmpOrder.get(partId) - 1; qty >= 0; qty--) {
                orderList.put(pEstimate,qty);

                filledBoxes = PACKER.getPacking(PACKER.getAvailableBoxes(), new WarehouseOrder(orderList));
                if (getAcc(filledBoxes) > THRESHOLD || filledBoxes.size() == 1) { //TODO change condition according to prices
                    orderCompleted.get(warehouse).put(partId, qty);
                    filledParts.remove(partId);
                    return filledParts;
                } 
                else {
                    continue;
                }
            }
            
            //If reached here means removing this part completely also doesn't increase accuracy
            orderCompleted.get(warehouse).put(partId, 0);
            filledParts.remove(partId);
        }
        return filledParts;
    }

    private static List<String> fillPrimaryItems(final Map<Warehouse, Map<String, Integer>> orderCompleted,
    		final Warehouse warehouse, final CustomerOrder customerOrder, final List<String> priorityPartList, 
    		final List<String> warehouseAvailability) throws NumberFormatException, IOException {
    	
        if (!orderCompleted.containsKey(warehouse)) {
            orderCompleted.put(warehouse, new HashMap<>());
        }
        
        //update order completed list and convert customerOrder to warehouseOrder for rare parts in this warehouse
        Map<Part,Integer> orderList = new HashMap<>();
        for (String partId : priorityPartList) {
            Integer qty = Pipe.pipeProductGreedily(warehouse, customerOrder, partId);
            orderList.put(Estimate.estimatePart(partId), qty);
            orderCompleted.get(warehouse).put(partId, qty);
        }
        
        List<Box> filledBoxes = PACKER.getPacking(PACKER.getAvailableBoxes(), new WarehouseOrder(orderList));

        Float currBoxCost = getCost(filledBoxes, customerOrder.getDeliveryAddress().getEasypostAddress(),
        			warehouse.getAddress().getEasypostAddress());

        //Sort secondary parts available in warehouse according to decreasing volume
        warehouseAvailability.sort((o1, o2) -> {
            Part p1 = Estimate.estimatePart(o1);
            Part p2 = Estimate.estimatePart(o2);
            return p2.volCompareTo(p1);
        });
        
        // Fill all possible parts in same box (large items first)
        List<String> filledParts = new ArrayList<>();
        for (String partId : warehouseAvailability) {

            Integer maxQty = Pipe.pipeProductGreedily(warehouse, customerOrder, partId);
            Part pEstimate = Estimate.estimatePart(partId);
            orderList.put(pEstimate, maxQty);

            filledBoxes = PACKER.getPacking(PACKER.getAvailableBoxes(), new WarehouseOrder(orderList));
            Float tmpBoxCost = getCost(filledBoxes, customerOrder.getDeliveryAddress().getEasypostAddress(),
        			warehouse.getAddress().getEasypostAddress());
            
            if (currBoxCost <= tmpBoxCost) { // TODO change condition according to prices
                orderCompleted.get(warehouse).put(partId, maxQty);
                if(!customerOrder.getProductCloneCountMap().containsKey(partId))
                	filledParts.add(partId);
            }
            else {
                Boolean orderUpdated = false;
                for (int qty = maxQty - 1; qty >= 0; qty--) {
                    orderList.put(pEstimate,qty);

                    filledBoxes = PACKER.getPacking(PACKER.getAvailableBoxes(), new WarehouseOrder(orderList));
                    tmpBoxCost = getCost(filledBoxes, customerOrder.getDeliveryAddress().getEasypostAddress(),
                			warehouse.getAddress().getEasypostAddress());
                    if (currBoxCost <= tmpBoxCost) { // TODO change condition according to prices
                        orderCompleted.get(warehouse).put(partId, qty);
                        orderUpdated = true;
                        break;
                    }
                }
                if (orderUpdated) {
                    break;
                }
            }
        }
        return filledParts;
    }

    private static Float getCost(final List<Box> filledBoxes, Address warehouseAddress, Address customerAddress) {
        Float cost = 0f;
    	for (Box box : filledBoxes) {
    		Parcel parcel = EasyPostAPI.getParcel(box.getWeight(), box.getDimension().getY(), box.getDimension().getZ(), box.getDimension().getX());
    		Map<String, Float> rateMap = EasyPostAPI.getRate(EasyPostAPI.getShipment(warehouseAddress, customerAddress, parcel));
    		cost+= rateMap.get(EasyPostAPI.getCARRIERS()[0]);
    	}
        return cost;
    }
    
    private static Double getAcc(final List<Box> filledBoxes) {
        Double boxVol = 0.;
        Double partVol = 0.;
    	for (Box box : filledBoxes) {
    		boxVol += box.getVol();
    		partVol += box.getPartsVol();
    	}
        return 100 * partVol/ boxVol;
    }
    
    private static void updateAvailability(final List<String> filledParts, 
    		final Map<Integer, List<String>> warehouseAvailability) {
    	for (String partId : filledParts) {
			for (Integer i : warehouseAvailability.keySet()) {
				warehouseAvailability.get(i).remove(partId);
			}
		}
    }
    
    private static boolean isOrderComplete(final List<String> filledParts, 
		final Map<Integer, List<String>> warehouseAvailability) {
    	for (Integer i : warehouseAvailability.keySet()) {
			if(warehouseAvailability.get(i).size() != 0) {
				return false;
			}
		}
    	return true;
    }
    public static void main(String[] args) throws IOException, ParseException {
    	long startTime = System.nanoTime();
    	CustomerOrder customerOrder = new CustomerOrder(new AddressInfo("11754 170 St NW, Edmonton, AB - T5S 1J7"));
        customerOrder.addPart("e2vzypowd3", 5);
        customerOrder.addPart("372gm82ope", 3);
        WarehouseDAO warehouseDAO = new WarehouseDAOImpl();
        Vector<Warehouse> warehouses = warehouseDAO.getAll();
        
        Map<Response, Warehouse> responseWarehouseMap = Allocator.getResponseWarehouseMap(customerOrder, warehouses);
        Map<Warehouse, Map<String, Integer>> answer = allocateWarhouseOrderUsingPacker(customerOrder, responseWarehouseMap);
        System.out.println(answer);
        System.out.println("TIME:" + (System.nanoTime() - startTime) / 1000000000.0);
   }

}

