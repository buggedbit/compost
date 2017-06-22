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
import com.partsavatar.allocator.exceptions.OrderCannotBeFullfilledException;
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

    public Map<	String, Map<String, Integer>> allocateWarhouseOrderUsingPacker(final CustomerOrder customerOrder, 
    		final Map<Response, Warehouse> responseWarehouseMap)throws NumberFormatException, IOException, OrderCannotBeFullfilledException {
        CustomerOrder copyCustomerOrder = new CustomerOrder(customerOrder);
    	
    	ArrayList<Response> allResponses = new ArrayList<>();
        allResponses.addAll(responseWarehouseMap.keySet());
        
        //create satellite store categorization
        allResponses.sort(Response::compareDistance);
        List<String> sortedWarehouses = new ArrayList<>();
        Map<String, List<Warehouse>> warehouseMap = new HashMap<>();
        Set<String> satelliteStores = new HashSet<>();
        for (Response r : allResponses) {
        	Warehouse warehouse = responseWarehouseMap.get(r);
        	String sat = warehouse.getSatelliteStore();
            if(sat == null) {
            	warehouseMap.put(warehouse.getId(), new ArrayList<>());
            	warehouseMap.get(warehouse.getId()).add(warehouse);
            	sortedWarehouses.add(warehouse.getId());
            }
            else {
            	if(satelliteStores.contains(sat)) {
            		warehouseMap.get(sat).add(warehouse);
            	}
            	else {
            		warehouseMap.put(sat, new ArrayList<>());
            		warehouseMap.get(sat).add(warehouse);
            		satelliteStores.add(sat);
            		sortedWarehouses.add(sat);
            	}
            }      
        }
        
        
        //Store availability of parts and find the rare items.
        Map<String, List<String>> priorityMap = new HashMap<>();			//warehouse - list of parts
        
        Map<String, List<String>> partAvailability = new HashMap<>(); 		//part - list of warehouse
        Map<String, List<String>> warehouseAvailability = new HashMap<>();  //warehouse - list of parts
        
        for (String partId : copyCustomerOrder.getProductCloneCountMap().keySet()) {

            partAvailability.put(partId, new ArrayList<>());

            for (String satId : warehouseMap.keySet()) {
            	
            	for (Warehouse wh : warehouseMap.get(satId)) {
					
            		if(wh.containsProduct(partId)) {
						partAvailability.get(partId).add(satId);
	                    if(!warehouseAvailability.containsKey(satId)) {
	                    	warehouseAvailability.put(satId, new ArrayList<>());
	                    }
	                    warehouseAvailability.get(satId).add(partId);
					}
				
            	}
            }
            if (partAvailability.get(partId).size() == 1) {
                String satId = partAvailability.get(partId).get(0);
                // Remove rare parts from availability as they are definitely filled on spot
                warehouseAvailability.get(satId).remove(partId);
                if (!priorityMap.containsKey(satId)) {
                    priorityMap.put(satId, new ArrayList<>());
                }
                priorityMap.get(satId).add(partId);
            }
        }

        //*Sort the Priority Map in descending customerOrder of distance to customer.
        //(So small items can fill without an extra box)
        List<String> sortedPriorityWarehouses = new ArrayList<>();
        sortedPriorityWarehouses.addAll(priorityMap.keySet());
        //TODO sortedPriorityWarehouses.sort((s1, s2) ->;


        Map<String, Map<String, Integer>> orderCompleted = new HashMap<>();
        //Fill Priority items
        for (String whId : sortedPriorityWarehouses) {
        	List<String> filledParts = fillPrimaryItems(orderCompleted, warehouseMap.get(whId), whId, 
            		copyCustomerOrder, priorityMap.get(whId), warehouseAvailability.get(whId));
        	updateAvailability(filledParts, warehouseAvailability);
        	
        	if(isOrderComplete(filledParts, warehouseAvailability)) {
	        	if(copyCustomerOrder.getProductCloneCountMap().size() == 0) {
					return orderCompleted;
				}
				else {
					throw new OrderCannotBeFullfilledException();
				}
        	}
        }

        //Fill Remaining items in remaining warehouses (nearest warehouse first)
        for (String whId : sortedWarehouses) {
        	if(warehouseAvailability.containsKey(whId) && warehouseAvailability.get(whId).size() != 0) {
        		if (!orderCompleted.containsKey(whId)) {
        			orderCompleted.put(whId, new HashMap<>());
        		}
        		else {
        			continue;
        		}
        		List<String> filledParts = fillSecondaryItems(orderCompleted, warehouseMap.get(whId), whId, copyCustomerOrder, warehouseAvailability.get(whId));    
        		updateAvailability(filledParts, warehouseAvailability);
        		
        		if(isOrderComplete(filledParts, warehouseAvailability)) {
        			if(copyCustomerOrder.getProductCloneCountMap().size() == 0) {
        				return orderCompleted;
        			}
        			else {
        				throw new OrderCannotBeFullfilledException();
        			}
        		}
        	}	
        }
        if(copyCustomerOrder.getProductCloneCountMap().size() == 0) {
			return orderCompleted;
		}
		else {
			throw new OrderCannotBeFullfilledException();
		}
    }

    private List<String> fillSecondaryItems(Map<String, Map<String, Integer>> orderCompleted, final List<Warehouse> warehouseList,
    		String whId, CustomerOrder customerOrder, final List<String> warehouseAvailability) throws NumberFormatException, IOException {

    	//update order completed list and convert customerOrder to warehouseOrder for rare parts in this warehouse
    	Map<Part,Integer> orderList = new HashMap<>();
    	List<String> filledParts = new ArrayList<>();
    	Map<String, Integer> pipedOrder = new HashMap<>();
    	for (Warehouse warehouse : warehouseList) {
        	Map<String, Integer> tmpOrder = Pipe.pipeOrderGreedily(warehouse, customerOrder);
            for (String partId : tmpOrder.keySet()) {
            	Part part = Estimate.estimatePart(partId);
            	Integer initialQty = 0;
            	if(orderList.containsKey(part)) {
            		initialQty+= orderList.get(part);
            	}
            	orderList.put(part, initialQty + tmpOrder.get(partId));
        		orderCompleted.get(whId).put(partId, initialQty + tmpOrder.get(partId));
        		pipedOrder.put(partId, initialQty + tmpOrder.get(partId));
            	
                if(!customerOrder.getProductCloneCountMap().containsKey(partId)) {
                	filledParts.add(partId);
                }
            }
		}
    	

        List<Box> filledBoxes = PACKER.getPacking(PACKER.getAvailableBoxes(), new WarehouseOrder(orderList));
        Double currAcc = getAcc(filledBoxes);
        
        if (currAcc > THRESHOLD || filledBoxes.size() == 1) {
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
        	 
            for (int qty = pipedOrder.get(partId) - 1; qty >= 0; qty--) {
                orderList.put(pEstimate,qty);
                customerOrder.addPart(partId, 1);
                filledBoxes = PACKER.getPacking(PACKER.getAvailableBoxes(), new WarehouseOrder(orderList));
                if (getAcc(filledBoxes) > THRESHOLD || filledBoxes.size() == 1) { //TODO change condition according to prices
                    orderCompleted.get(whId).put(partId, qty);
                    filledParts.remove(partId);
                    return filledParts;
                } 
                else {
                    continue;
                }
            }
            
            //If reached here means removing this part completely also doesn't increase accuracy
            orderCompleted.get(whId).put(partId, 0);
            filledParts.remove(partId);
        }
        return filledParts;
    }

    private List<String> fillPrimaryItems(Map<String, Map<String, Integer>> orderCompleted, final List<Warehouse> warehouseList, String whId,
    		CustomerOrder customerOrder, final List<String> priorityPartList, final List<String> warehouseAvailability) throws NumberFormatException, IOException {
    	
    	if (!orderCompleted.containsKey(whId)) {
            orderCompleted.put(whId, new HashMap<>());
        }
        
        //update order completed list and convert customerOrder to warehouseOrder for rare parts in this warehouse
        Map<Part,Integer> orderList = new HashMap<>();
        for (String partId : priorityPartList) {
        	Integer qty = 0;
        	for (Warehouse warehouse : warehouseList) {
        		qty += Pipe.pipeProductGreedily(warehouse, customerOrder, partId);
			}
        	orderList.put(Estimate.estimatePart(partId), qty);
        	orderCompleted.get(whId).put(partId, qty);
        }
        
        List<Box> filledBoxes = PACKER.getPacking(PACKER.getAvailableBoxes(), new WarehouseOrder(orderList));

        Float currBoxCost = getCost(filledBoxes, customerOrder.getDeliveryAddress().getEasypostAddress(),
        		warehouseList.get(0).getAddress().getEasypostAddress());

        //Sort secondary parts available in warehouse according to decreasing volume
        warehouseAvailability.sort((o1, o2) -> {
            Part p1 = Estimate.estimatePart(o1);
            Part p2 = Estimate.estimatePart(o2);
            return p2.volCompareTo(p1);
        });
        
        // Fill all possible parts in same box (large items first)
        List<String> filledParts = new ArrayList<>();
        for (String partId : warehouseAvailability) {
        	Integer maxQty = 0;
        	for (Warehouse warehouse : warehouseList) {
        		 maxQty += Pipe.pipeProductGreedily(warehouse, customerOrder, partId);
			}
            Part pEstimate = Estimate.estimatePart(partId);
            orderList.put(pEstimate, maxQty);

            filledBoxes = PACKER.getPacking(PACKER.getAvailableBoxes(), new WarehouseOrder(orderList));
            Float tmpBoxCost = getCost(filledBoxes, customerOrder.getDeliveryAddress().getEasypostAddress(),
            		warehouseList.get(0).getAddress().getEasypostAddress());
            
            if (currBoxCost <= tmpBoxCost) { // TODO change condition according to prices
                orderCompleted.get(whId).put(partId, maxQty);
                if(!customerOrder.getProductCloneCountMap().containsKey(partId))
                	filledParts.add(partId);
            }
            else {
                Boolean orderUpdated = false;
                for (int qty = maxQty - 1; qty >= 0; qty--) {
                    orderList.put(pEstimate,qty);
					customerOrder.addPart(partId, 1);

                    filledBoxes = PACKER.getPacking(PACKER.getAvailableBoxes(), new WarehouseOrder(orderList));
                    tmpBoxCost = getCost(filledBoxes, customerOrder.getDeliveryAddress().getEasypostAddress(),
                    		warehouseList.get(0).getAddress().getEasypostAddress());
                    if (currBoxCost <= tmpBoxCost) { // TODO change condition according to prices
                        orderCompleted.get(whId).put(partId, qty);
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

    private Float getCost(final List<Box> filledBoxes, final Address warehouseAddress, final Address customerAddress) {
        Float cost = 0f;
    	for (Box box : filledBoxes) {
    		Parcel parcel = EasyPostAPI.getParcel(box.getWeight(), box.getDimension().getY(), box.getDimension().getZ(), box.getDimension().getX());
    		Map<String, Float> rateMap = EasyPostAPI.getRate(EasyPostAPI.getShipment(warehouseAddress, customerAddress, parcel));
    		cost+= rateMap.get(EasyPostAPI.getSERVICES()[0]);
    	}
        return cost;
    }
    
    private Double getAcc(final List<Box> filledBoxes) {
        Double boxVol = 0.;
        Double partVol = 0.;
    	for (Box box : filledBoxes) {
    		boxVol += box.getVol();
    		partVol += box.getPartsVol();
    	}
        return 100 * partVol/ boxVol;
    }
    
    private void updateAvailability(final List<String> filledParts, 
    		Map<String, List<String>> warehouseAvailability) {
    	for (String partId : filledParts) {
			for (String s : warehouseAvailability.keySet()) {
				warehouseAvailability.get(s).remove(partId);
			}
		}
    }
    
    private boolean isOrderComplete(final List<String> filledParts, 
		final Map<String, List<String>> warehouseAvailability) {
    	for (String s : warehouseAvailability.keySet()) {
			if(warehouseAvailability.get(s).size() != 0) {
				return false;
			}
		}
    	return true;
    }
    
    public static void main(String[] args) throws IOException, ParseException, NumberFormatException, OrderCannotBeFullfilledException {
    	long startTime = System.nanoTime();
    	CustomerOrder customerOrder = new CustomerOrder(new AddressInfo("11754 170 St NW, Edmonton, AB - T5S 1J7"));
        customerOrder.addPart("e2vzypowd3", 8);
        customerOrder.addPart("372gm82ope", 2);
        customerOrder.addPart("edydggpwde", 1);	
        customerOrder.addPart("394bmdjxye", 2);
        WarehouseDAO warehouseDAO = new WarehouseDAOImpl();
        Vector<Warehouse> warehouses = warehouseDAO.getAll();
        
        Map<Response, Warehouse> responseWarehouseMap = Allocator.getResponseWarehouseMap(customerOrder, warehouses);
        System.out.println("TIME:" + (System.nanoTime() - startTime) / 1000000000.0);
        AllocatorUsingPacker allocatorUsingPacker  = new AllocatorUsingPacker();
        Map<String, Map<String, Integer>> completedAllocation = allocatorUsingPacker.allocateWarhouseOrderUsingPacker(customerOrder, responseWarehouseMap);
        System.out.println(completedAllocation);
        System.out.println("TIME:" + (System.nanoTime() - startTime) / 1000000000.0);
   }
}

