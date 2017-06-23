package com.partsavatar.allocator.allocationtypes;

import com.easypost.model.Address;
import com.easypost.model.Parcel;
import com.partsavatar.allocator.Allocator;
import com.partsavatar.allocator.api.easyPost.EasyPostAPI;
import com.partsavatar.allocator.api.google.Response;
import com.partsavatar.allocator.components.AddressInfo;
import com.partsavatar.allocator.components.CustomerOrder;
import com.partsavatar.allocator.components.SatelliteStore;
import com.partsavatar.allocator.components.warehouse.Warehouse;
import com.partsavatar.allocator.components.warehouse.WarehouseDAO;
import com.partsavatar.allocator.components.warehouse.WarehouseDAOImpl;
import com.partsavatar.allocator.estimates.Estimate;
import com.partsavatar.allocator.exceptions.OrderCannotBeFullfilledException;
import com.partsavatar.allocator.operations.Pipe;
import com.partsavatar.allocator.operations.Pipe.PipedOrder;
import com.partsavatar.allocator.operations.Pipe.PipedProduct;
import com.partsavatar.packer.components.Box;
import com.partsavatar.packer.components.Part;
import com.partsavatar.packer.components.WarehouseOrder;
import com.partsavatar.packer.dao.packing.PackingDAO;
import com.partsavatar.packer.dao.packing.PackingDAOImpl;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.*;

//TODO: Preferential Treatment for some items.
public class AllocatorUsingPacker {
    private static final Double THRESHOLD = 75.0;
    private static final PackingDAO PACKER = new PackingDAOImpl();

    @Getter
    @Setter
    @ToString
    public class FilledOrder {
        private CustomerOrder remainingOrder;
        private List<String> filledParts;

        public FilledOrder(CustomerOrder currentOrder) {
            remainingOrder = new CustomerOrder(currentOrder);
            filledParts = new ArrayList<>();
        }

        public void addFilledPart(String partId) {
            filledParts.add(partId);
        }

        public void removeFilledPart(String partId) {
            filledParts.remove(partId);
        }

        public void addOrderPart(String partId, Integer qty) {
            remainingOrder.addPart(partId, qty);
        }
    }

    public Map<Warehouse, Map<String, Integer>> allocateWarehouseOrderUsingPacker(final CustomerOrder customerOrder,
                                                                                  final Map<Response, Warehouse> responseWarehouseMap) throws NumberFormatException, IOException, OrderCannotBeFullfilledException {
        ArrayList<Response> allResponses = new ArrayList<>();
        allResponses.addAll(responseWarehouseMap.keySet());

        //create satellite store categorization
        allResponses.sort(Response::compareDistance);
        List<String> sortedSatelliteStores = new ArrayList<>();
        Map<String, SatelliteStore> satelliteStoreMap = new HashMap<>();
        Map<String, Long> satelliteDistanceMap = new HashMap<>();
        Set<String> satelliteStores = new HashSet<>();
        for (Response r : allResponses) {
            Warehouse warehouse = responseWarehouseMap.get(r);
            String sat = warehouse.getSatelliteStore();
            if (sat == null) {
                String whId = warehouse.getId();
                satelliteStoreMap.put(whId, new SatelliteStore());
                satelliteStoreMap.get(whId).addWarehouse(warehouse);
                sortedSatelliteStores.add(whId);
                satelliteDistanceMap.put(whId, r.getDistance());
            } else {
                if (satelliteStores.contains(sat)) {
                    satelliteStoreMap.get(sat).addWarehouse(warehouse);
                } else {
                    satelliteStoreMap.put(sat, new SatelliteStore());
                    satelliteStoreMap.get(sat).addWarehouse(warehouse);
                    satelliteStores.add(sat);
                    sortedSatelliteStores.add(sat);
                    satelliteDistanceMap.put(sat, r.getDistance());
                }
            }
        }


        //Store availability of parts and find the rare items.
        Map<String, List<String>> priorityMap = new HashMap<>();            //warehouse - list of parts

        Map<String, List<String>> partAvailability = new HashMap<>();        //part - list of warehouse
        Map<String, List<String>> warehouseAvailability = new HashMap<>();  //warehouse - list of parts

        for (String partId : customerOrder.getProductCloneCountMap().keySet()) {

            partAvailability.put(partId, new ArrayList<>());

            for (String satId : satelliteStoreMap.keySet()) {

                for (Warehouse wh : satelliteStoreMap.get(satId).getWarehouses()) {

                    if (wh.containsProduct(partId)) {
                        partAvailability.get(partId).add(satId);
                        if (!warehouseAvailability.containsKey(satId)) {
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
        //TODO Reverse Sort
        sortedPriorityWarehouses.sort(Comparator.comparing(s -> satelliteDistanceMap.get(s)));

        Map<String, Map<String, Integer>> orderCompleted = new HashMap<>();
        FilledOrder filledOrder = new FilledOrder(customerOrder);
        //Fill Priority items
        for (String whId : sortedPriorityWarehouses) {
            filledOrder = fillPrimaryItems(orderCompleted, satelliteStoreMap.get(whId).getWarehouses(), whId,
                    filledOrder.getRemainingOrder(), priorityMap.get(whId), warehouseAvailability.get(whId));
            updateAvailability(filledOrder.getFilledParts(), warehouseAvailability);

            if (filledOrder.getRemainingOrder().getProductCloneCountMap().size() == 0) {
                return segregateIntoWarehouses(orderCompleted, satelliteStoreMap);
            }
        }

        //Fill Remaining items in remaining warehouses (nearest warehouse first)
        for (String whId : sortedSatelliteStores) {
            if (warehouseAvailability.containsKey(whId) && warehouseAvailability.get(whId).size() != 0) {
                if (!orderCompleted.containsKey(whId)) {
                    orderCompleted.put(whId, new HashMap<>());
                } else {
                    continue;
                }
                filledOrder = fillSecondaryItems(orderCompleted, satelliteStoreMap.get(whId).getWarehouses(), whId,
                        filledOrder.getRemainingOrder(), warehouseAvailability.get(whId));
                updateAvailability(filledOrder.getFilledParts(), warehouseAvailability);

                if (isOrderComplete(filledOrder.getFilledParts(), warehouseAvailability)) {
                    if (filledOrder.getRemainingOrder().getProductCloneCountMap().size() == 0) {
                        return segregateIntoWarehouses(orderCompleted, satelliteStoreMap);
                    } else {
                        throw new OrderCannotBeFullfilledException();
                    }

                }
            }
        }
        if (filledOrder.getRemainingOrder().getProductCloneCountMap().size() == 0) {
            return segregateIntoWarehouses(orderCompleted, satelliteStoreMap);
        } else {
            throw new OrderCannotBeFullfilledException();
        }
    }

    private FilledOrder fillSecondaryItems(Map<String, Map<String, Integer>> orderCompleted, final List<Warehouse> warehouseList,
                                           String whId, final CustomerOrder customerOrder, final List<String> warehouseAvailability) throws NumberFormatException, IOException {

        FilledOrder filledOrder = new FilledOrder(customerOrder);

        //update order completed list and convert customerOrder to warehouseOrder for rare parts in this warehouse
        Map<Part, Integer> orderList = new HashMap<>();
        Map<String, Integer> pipedOrder = new HashMap<>();
        for (Warehouse warehouse : warehouseList) {

            PipedOrder tmpPipedOrder = Pipe.pipeOrderGreedily(warehouse, filledOrder.getRemainingOrder());
            Map<String, Integer> tmpOrder = tmpPipedOrder.getOrderTaken();
            filledOrder.setRemainingOrder(tmpPipedOrder.getOrderRemaining());

            for (String partId : tmpOrder.keySet()) {
                Part part = Estimate.getEstimateForProduct(partId);
                Integer initialQty = 0;
                if (orderList.containsKey(part)) {
                    initialQty += orderList.get(part);
                }
                orderList.put(part, initialQty + tmpOrder.get(partId));
                orderCompleted.get(whId).put(partId, initialQty + tmpOrder.get(partId));
                pipedOrder.put(partId, initialQty + tmpOrder.get(partId));

                if (!filledOrder.getRemainingOrder().getProductCloneCountMap().containsKey(partId)) {
                    filledOrder.addFilledPart(partId);
                }
            }
        }


        List<Box> filledBoxes = PACKER.getPacking(PACKER.getAvailableBoxes(), new WarehouseOrder(orderList));
        Double currAcc = getAcc(filledBoxes);

        if (currAcc > THRESHOLD || filledBoxes.size() == 1) {
            System.out.println(currAcc);
            return filledOrder;
        }

        //Sort secondary parts available in warehouse according to increasing volume
        warehouseAvailability.sort((o1, o2) -> {
            Part p1 = Estimate.getEstimateForProduct(o1);
            Part p2 = Estimate.getEstimateForProduct(o2);
            return p1.volCompareTo(p2);
        });

        // Remove parts from order to achieve a higher accuracy if possible (small items first)
        for (String partId : warehouseAvailability) {
            Part pEstimate = Estimate.getEstimateForProduct(partId);

            for (int qty = pipedOrder.get(partId) - 1; qty >= 0; qty--) {
                orderList.put(pEstimate, qty);

                filledOrder.addOrderPart(partId, 1);

                filledBoxes = PACKER.getPacking(PACKER.getAvailableBoxes(), new WarehouseOrder(orderList));
                if (getAcc(filledBoxes) > THRESHOLD || filledBoxes.size() == 1) { //TODO change condition according to prices
                    orderCompleted.get(whId).put(partId, qty);
                    filledOrder.removeFilledPart(partId);
                    System.out.println(getAcc(filledBoxes));
                    return filledOrder;
                } else {
                    continue;
                }
            }

            //If reached here means removing this part completely also doesn't increase accuracy
            orderCompleted.get(whId).put(partId, 0);
            filledOrder.removeFilledPart(partId);
        }
        return filledOrder;
    }

    private FilledOrder fillPrimaryItems(Map<String, Map<String, Integer>> orderCompleted, final List<Warehouse> warehouseList, String whId,
                                         final CustomerOrder customerOrder, final List<String> priorityPartList, final List<String> warehouseAvailability) throws NumberFormatException, IOException {

        if (!orderCompleted.containsKey(whId)) {
            orderCompleted.put(whId, new HashMap<>());
        }

        FilledOrder filledOrder = new FilledOrder(customerOrder);

        //update order completed list and convert customerOrder to warehouseOrder for rare parts in this warehouse
        Map<Part, Integer> orderList = new HashMap<>();
        for (String partId : priorityPartList) {
            Integer qty = 0;
            for (Warehouse warehouse : warehouseList) {
                PipedProduct tmpPipedProduct = Pipe.pipeProductGreedily(warehouse, filledOrder.getRemainingOrder(), partId);
                qty += tmpPipedProduct.getQuantityTaken();
                filledOrder.setRemainingOrder(tmpPipedProduct.getOrderRemaining());
            }
            orderList.put(Estimate.getEstimateForProduct(partId), qty);
            orderCompleted.get(whId).put(partId, qty);
        }

        List<Box> filledBoxes = PACKER.getPacking(PACKER.getAvailableBoxes(), new WarehouseOrder(orderList));

        Float currBoxCost = getCost(filledBoxes, customerOrder.getDeliveryAddress().getEasyPostAddress(),
                warehouseList.get(0).getAddress().getEasyPostAddress());

        //Sort secondary parts available in warehouse according to decreasing volume
        warehouseAvailability.sort((o1, o2) -> {
            Part p1 = Estimate.getEstimateForProduct(o1);
            Part p2 = Estimate.getEstimateForProduct(o2);
            return p2.volCompareTo(p1);
        });

        // Fill all possible parts in same box (large items first)
        for (String partId : warehouseAvailability) {
            Integer maxQty = 0;
            for (Warehouse warehouse : warehouseList) {
                PipedProduct tmpPipedProduct = Pipe.pipeProductGreedily(warehouse, filledOrder.getRemainingOrder(), partId);
                maxQty += tmpPipedProduct.getQuantityTaken();
                filledOrder.setRemainingOrder(tmpPipedProduct.getOrderRemaining());
            }
            Part pEstimate = Estimate.getEstimateForProduct(partId);
            orderList.put(pEstimate, maxQty);

            filledBoxes = PACKER.getPacking(PACKER.getAvailableBoxes(), new WarehouseOrder(orderList));
            Float tmpBoxCost = getCost(filledBoxes, customerOrder.getDeliveryAddress().getEasyPostAddress(),
                    warehouseList.get(0).getAddress().getEasyPostAddress());

            if (currBoxCost <= tmpBoxCost) {
                orderCompleted.get(whId).put(partId, maxQty);
                if (!filledOrder.getRemainingOrder().getProductCloneCountMap().containsKey(partId))
                    filledOrder.addFilledPart(partId);
            } else {
                Boolean orderUpdated = false;
                for (int qty = maxQty - 1; qty >= 0; qty--) {
                    orderList.put(pEstimate, qty);
                    filledOrder.addOrderPart(partId, 1);

                    filledBoxes = PACKER.getPacking(PACKER.getAvailableBoxes(), new WarehouseOrder(orderList));
                    tmpBoxCost = getCost(filledBoxes, customerOrder.getDeliveryAddress().getEasyPostAddress(),
                            warehouseList.get(0).getAddress().getEasyPostAddress());
                    if (currBoxCost <= tmpBoxCost) {
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
        System.out.println(getAcc(filledBoxes));
        return filledOrder;
    }

    private Float getCost(final List<Box> filledBoxes, final Address customerAddress, final Address warehouseAddress) {
        Float cost = 0f;
        for (Box box : filledBoxes) {
            Parcel parcel = EasyPostAPI.getParcel(box.getWeight(), box.getDimension().getY(), box.getDimension().getZ(), box.getDimension().getX());
            Map<String, Float> rateMap = EasyPostAPI.getRate(EasyPostAPI.getShipment(warehouseAddress, customerAddress, parcel));
            cost += rateMap.get(EasyPostAPI.getSERVICES()[0]);
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
        return 100 * partVol / boxVol;
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
            if (warehouseAvailability.get(s).size() != 0) {
                return false;
            }
        }
        return true;
    }

    private Map<Warehouse, Map<String, Integer>> segregateIntoWarehouses(
            Map<String, Map<String, Integer>> orderCompleted, Map<String, SatelliteStore> satelliteStoreMap) {
        Map<Warehouse, Map<String, Integer>> finalAllocation = new HashMap<>();
        for (String sat : orderCompleted.keySet()) {
            for (Warehouse warehouse : satelliteStoreMap.get(sat).getWarehouses()) {
                finalAllocation.put(warehouse, Pipe.pipeWareHouseGreedily(warehouse, orderCompleted.get(sat)));
            }
        }

        return finalAllocation;
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
        AllocatorUsingPacker allocatorUsingPacker = new AllocatorUsingPacker();
        Map<Warehouse, Map<String, Integer>> completedAllocation = allocatorUsingPacker.allocateWarehouseOrderUsingPacker(customerOrder, responseWarehouseMap);
        System.out.println(completedAllocation);
        System.out.println("TIME:" + (System.nanoTime() - startTime) / 1000000000.0);
    }

}

