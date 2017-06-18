package com.partsavatar.allocator.allocationtypes;

import com.partsavatar.allocator.api.google.Response;
import com.partsavatar.allocator.components.CustomerOrder;
import com.partsavatar.allocator.components.warehouse.Warehouse;
import com.partsavatar.allocator.estimates.Estimate;
import com.partsavatar.packer.components.Box;
import com.partsavatar.packer.components.Part;
import com.partsavatar.packer.components.WarehouseOrder;
import com.partsavatar.packer.dao.packing.PackingDAO;
import com.partsavatar.packer.dao.packing.PackingDAOImpl;

import java.io.IOException;
import java.util.*;


// todo : Preferential Treatment for some items.
public class AllocatorUsingPacker {
    private static Double threshold = 75.0;
    private static PackingDAO packer = new PackingDAOImpl();

    static Map<Warehouse, Map<String, Integer>> Algo(CustomerOrder customerOrder, Map<Response, Warehouse> responseWarehouseMap) throws NumberFormatException, IOException {
        // Get all responses
        ArrayList<Response> allResponses = new ArrayList<>();
        allResponses.addAll(responseWarehouseMap.keySet());

        // Sort all responses w.r.t distance, in turn sorting the warehouses
        allResponses.sort(Response::compareDistance);

        // Sorting warehouses w.r.t distance
        ArrayList<Warehouse> sortedWarehouses = new ArrayList<>();
        for (Response r : allResponses)
            sortedWarehouses.add(responseWarehouseMap.get(r));

        //Store availability of parts and find the rare items.
        Map<Integer, ArrayList<String>> priorityMap = new HashMap<>();
        Map<String, ArrayList<Integer>> availability = new HashMap<>();
        for (String partId : customerOrder.getProductCloneCountMap().keySet()) {

            availability.put(partId, new ArrayList<>());

            for (Integer i = 0; i < sortedWarehouses.size(); i++) {
                if (sortedWarehouses.get(i).containsProduct(partId)) {
                    availability.get(partId).add(i);
                    sortedWarehouses.get(i).addToAvailable(partId);
                }
            }
            if (availability.get(partId).size() == 1) {
                Integer whIndex = availability.get(partId).get(0);
                if (!priorityMap.containsKey(whIndex))
                    priorityMap.put(whIndex, new ArrayList<>());
                priorityMap.get(whIndex).add(partId);
                customerOrder.updatePart(partId, 0);
            }
        }

        //*Sort the Priority Map in descending customerOrder of distance to customer.
        //(So small items can fill without an extra box)
        ArrayList<Integer> sortedPriorityWarehouseIndices = new ArrayList<>();
        sortedPriorityWarehouseIndices.addAll(priorityMap.keySet());
        sortedPriorityWarehouseIndices.sort(Collections.reverseOrder());


        Map<Warehouse, Map<String, Integer>> orderCompleted = new HashMap<>();
        //Fill Priority items
        for (Integer whIndex : sortedPriorityWarehouseIndices) {
            orderCompleted = fillPrimaryItems(orderCompleted, sortedWarehouses.get(whIndex), customerOrder, priorityMap.get(whIndex));
        }

        //Fill Remaining items
        for (Warehouse warehouse : sortedWarehouses) {
            if (!orderCompleted.containsKey(warehouse))
                orderCompleted.put(warehouse, new HashMap<>());
            else
                continue;
            orderCompleted = fillSecondaryItems(orderCompleted, warehouse, customerOrder);
        }
        return orderCompleted;
    }

    private static Map<Warehouse, Map<String, Integer>> fillSecondaryItems(Map<Warehouse, Map<String, Integer>> orderCompleted, Warehouse warehouse, CustomerOrder customerOrder) throws NumberFormatException, IOException {

        ArrayList<Part> orderList = new ArrayList<>();

        Map<String, Integer> tmpOrder = warehouse.pipeOrderGreedily(customerOrder);
        for (String partId : tmpOrder.keySet()) {
            orderList.add(Estimate.estimatePart(partId, tmpOrder.get(partId)));
            orderCompleted.get(warehouse).put(partId, tmpOrder.get(partId));
        }

        List<Box> filledBoxes = packer.getPacking(packer.getAvailableBoxes(), new WarehouseOrder(orderList));
        Double currAcc = getAcc(filledBoxes);

        if (currAcc > threshold || filledBoxes.size() == 1)
            return orderCompleted;

        warehouse.ascendingSort();
        for (String partId : warehouse.getAvailable()) {

            Integer indexInOrder = -1;
            for (int i = 0; i < orderList.size(); i++) {
                if (orderList.get(i).getId().equals(partId)) {
                    indexInOrder = i;
                    break;
                }
            }

            for (int qty = tmpOrder.get(partId) - 1; qty >= 0; qty--) {
                orderList.get(indexInOrder).setQuantity(qty);

                filledBoxes = packer.getPacking(packer.getAvailableBoxes(), new WarehouseOrder(orderList));
                if (getAcc(filledBoxes) > threshold || filledBoxes.size() == 1) {
                    orderCompleted.get(warehouse).put(partId, qty);
                    return orderCompleted;
                } else
                    continue;
            }
        }
        return orderCompleted;
    }

    private static Map<Warehouse, Map<String, Integer>> fillPrimaryItems(Map<Warehouse, Map<String, Integer>> orderCompleted, Warehouse warehouse, CustomerOrder customerOrder, ArrayList<String> priorityPartList) throws NumberFormatException, IOException {

        if (!orderCompleted.containsKey(warehouse))
            orderCompleted.put(warehouse, new HashMap<>());

        ArrayList<Part> orderList = new ArrayList<>();
        for (String partId : priorityPartList) {
            Integer qty = warehouse.pipeProductGreedily(customerOrder, partId);
            orderList.add(Estimate.estimatePart(partId, qty));
            orderCompleted.get(warehouse).put(partId, qty);
        }

        List<Box> filledBoxes = packer.getPacking(packer.getAvailableBoxes(), new WarehouseOrder(orderList));

        Integer currBoxVol = getBoxVol(filledBoxes);

        warehouse.descendingSort();
        for (String partId : warehouse.getAvailable()) {

            Integer maxQty = warehouse.pipeProductGreedily(customerOrder, partId);
            orderList.add(Estimate.estimatePart(partId, maxQty));

            filledBoxes = packer.getPacking(packer.getAvailableBoxes(), new WarehouseOrder(orderList));

            if (currBoxVol <= getBoxVol(filledBoxes))
                orderCompleted.get(warehouse).put(partId, maxQty);
            else {
                Boolean orderUpdated = false;
                for (int qty = maxQty - 1; qty >= 0; qty--) {
                    orderList.get(orderList.size() - 1).setQuantity(qty);

                    filledBoxes = packer.getPacking(packer.getAvailableBoxes(), new WarehouseOrder(orderList));
                    if (currBoxVol <= getBoxVol(filledBoxes)) {
                        orderCompleted.get(warehouse).put(partId, qty);
                        orderUpdated = true;
                        break;
                    } else
                        continue;
                }
                if (!orderUpdated)
                    break;
            }
        }
        return orderCompleted;
    }

    private static Integer getBoxVol(List<Box> filledBoxes) {
        Integer boxVol = 0;
        for (Box box : filledBoxes)
            boxVol += box.getVol();
        return boxVol;
    }

    private static Double getAcc(List<Box> filledBoxes) {
        Double partVol = 0.0;
        for (Box box : filledBoxes)
            partVol += box.getVol();
        return 100.0 * partVol / getBoxVol(filledBoxes);
    }

}

