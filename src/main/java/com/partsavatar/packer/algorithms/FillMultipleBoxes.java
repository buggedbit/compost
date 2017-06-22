package com.partsavatar.packer.algorithms;

import com.partsavatar.packer.components.Box;
import com.partsavatar.packer.components.PackingComponent;
import com.partsavatar.packer.components.Part;
import com.partsavatar.packer.components.Vector3D;
import com.partsavatar.packer.components.WarehouseOrder;
import com.partsavatar.packer.dao.packing.PackingDAOImpl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Random;


public class FillMultipleBoxes {
	private static Integer SWITCH_BW_2_ALGO = 4;
	
	public static List<Box> packOrder(final ArrayList<Box> availableBoxes, final WarehouseOrder newWarehouseOrder) {
        PriorityQueue<PackingComponent> heap = new PriorityQueue<PackingComponent>(PackingComponent::compareTo);

        PackingComponent initialVoidSpace = new PackingComponent(0, 0, 0., newWarehouseOrder.copy());
        initialVoidSpace.setBoxMap(new HashMap<>());
        
        Map <Box,Integer> availableBoxMap = new HashMap<>(); 
        for (Box box : availableBoxes) {
			availableBoxMap.put(box, 1);
		}
        PackingComponent vs = FillMultipleBoxes.fillBoxes(heap, availableBoxMap, initialVoidSpace, newWarehouseOrder.getVol());
        System.out.println("ACC:" + vs.getFinalAccuracy());
        List<Box> filledBoxes = new ArrayList<>();
        for (Box box : vs.getBoxMap().keySet()) {
        	String id = box.getId();
        	for (Integer i = 0; i < vs.getBoxMap().get(box).size(); i++) {
        		vs.getBoxMap().get(box).get(i).setId(id + "-" + i.toString());
				filledBoxes.add(vs.getBoxMap().get(box).get(i));
			}
        }
        
        return filledBoxes;
    }
	
	private static void calcBoxNumbers(final Map<Box, List<Box>> filledBoxes, Map<Box,Integer> availableBoxes) {
        for (Box b : availableBoxes.keySet()) {
            Integer maxIndex = 0;
            for (Box box : filledBoxes.keySet()) {
                if (box.getId().equals(b.getId()) && filledBoxes.get(box).size() > maxIndex) {
                    maxIndex = filledBoxes.get(box).size();
                }
            }
            availableBoxes.get(maxIndex + 1);
        }
    }
    
    private static PackingComponent fillBoxes(PriorityQueue<PackingComponent> heap, Map<Box,Integer> availableBoxMap,
    		final PackingComponent initialPacking, final Integer volInitialItems) {
        PackingComponent copyInitialPacking = initialPacking.copy();
 
        calcBoxNumbers(copyInitialPacking.getBoxMap(), availableBoxMap);
        
        List<Box> availableBoxes = new ArrayList<>(availableBoxMap.keySet());
        availableBoxes.sort(Box::volCompareTo);
        
        //Forward Propagation in one direction towards completing the order (storing important alternatives in the way)
        while (copyInitialPacking.getRemainingWarehouseOrder().numOfItems() != 0) {
            Integer thisStepVoid = availableBoxes.get(0).getVol(), leastVoidSpaceBoxLoc = 0;
            Double thisStepCompletion = 0.;

            for (int i = 0; i < availableBoxes.size(); i++) {

                WarehouseOrder tmpWarehouseOrder = copyInitialPacking.getRemainingWarehouseOrder().copy();
                Box tmpBox = availableBoxes.get(i).copy();
                if (copyInitialPacking.getRemainingWarehouseOrder().numOfItems() <= SWITCH_BW_2_ALGO) {
                    tmpWarehouseOrder = BruteForceBacktrackAlgorithmBelow6Items.backtrackAlgorithm(tmpBox, tmpWarehouseOrder);
                }
                else {
                    tmpWarehouseOrder = BacktrackAlgorithmAbove6Items.backtrackAlgorithm(tmpBox, tmpWarehouseOrder);
                }
                
                Integer tmpVoid = tmpBox.getVol() - tmpBox.getPartsVol();
                Double tmpCompletion = tmpBox.getPartsVol() * 100.0 / volInitialItems;
                
                //Find the box with least voidSpace (after packing order in that) 
                if (thisStepVoid > tmpVoid && tmpBox.getPartsVol() > 0) {
                    thisStepVoid = tmpVoid;
                    thisStepCompletion = tmpCompletion;
                    leastVoidSpaceBoxLoc = i;
                }
                
                //Insert packing stages, that can potentially lead to better accuracy later
                if (tmpBox.getPartsVol() > 0) {
                    Boolean shouldBeAdded = true;
                    for (PackingComponent vs : heap) {
                        if (tmpVoid + copyInitialPacking.getCurrVoid() >= vs.getCurrVoid() && 
                        		copyInitialPacking.getCurrCompletion() + tmpCompletion <= vs.getCurrCompletion()) {
                            shouldBeAdded = false;
                            break;
                        }
                    }
                    if (shouldBeAdded) {
                        PackingComponent tmp1 = new PackingComponent(copyInitialPacking.getCurrStep() + 1,
                        		tmpVoid + copyInitialPacking.getCurrVoid(), copyInitialPacking.getCurrCompletion() + tmpCompletion, tmpWarehouseOrder);
                        tmp1.setBoxMap(copyInitialPacking.copyBoxMap());
                        tmp1.addBox(tmpBox.copy());
                        heap.add(tmp1);
                    }
                }
            }
            //update the packing by chosen box from above
            copyInitialPacking.setCurrVoid(copyInitialPacking.getCurrVoid() + thisStepVoid);
            copyInitialPacking.setCurrCompletion(copyInitialPacking.getCurrCompletion() + thisStepCompletion);
            copyInitialPacking.setCurrStep(copyInitialPacking.getCurrStep() + 1);

            //remove this updated stage from packing
            if (!heap.isEmpty() && heap.peek().getCurrStep() == copyInitialPacking.getCurrStep() && copyInitialPacking.getCurrCompletion() < 100) {
                heap.remove();
            }
            else {
               while (!heap.isEmpty() && heap.peek().getCurrStep() == copyInitialPacking.getCurrStep()) {
            	   heap.remove();
               }
            }
            
            Box box = availableBoxes.get(leastVoidSpaceBoxLoc).copy();
            availableBoxMap.put(box, availableBoxMap.get(box) + 1);
            
            if (copyInitialPacking.getRemainingWarehouseOrder().numOfItems() <= SWITCH_BW_2_ALGO) {
                copyInitialPacking.setRemainingWarehouseOrder(BruteForceBacktrackAlgorithmBelow6Items.
                		backtrackAlgorithm(box, copyInitialPacking.getRemainingWarehouseOrder()));
            }else {
                copyInitialPacking.setRemainingWarehouseOrder(BacktrackAlgorithmAbove6Items.
                		backtrackAlgorithm(box, copyInitialPacking.getRemainingWarehouseOrder()));
            }
            copyInitialPacking.addBox(box.copy());
            
        }
        
        copyInitialPacking.setFinalAccuracy(volInitialItems * 100.0 / (copyInitialPacking.getCurrVoid() + volInitialItems));
        
        //Iterating over alternative stages of Packing in heap to check for better possibility
        PackingComponent finalStage = copyInitialPacking.copy();
        while (!heap.isEmpty()) {
            PackingComponent vs = heap.remove();
            vs = fillBoxes(heap, availableBoxMap, vs, volInitialItems);
            if (vs.getFinalAccuracy() >= finalStage.getFinalAccuracy()) {
                finalStage = vs.copy();
            }
        }
        return finalStage;
    }
    
    public static WarehouseOrder makeRandomOrder(Integer min, Integer max, Integer qty_min, Integer qty_max, Integer diffParts) {
        Random r = new Random();
        Map<Part, Integer> ps = new HashMap<>();
        for (Integer i = 0; i < diffParts; i++) {
            Integer x = r.nextInt((max - min) + 1) + min;
            Integer y = r.nextInt((max - min) + 1) + min;
            Integer z = r.nextInt((max - min) + 1) + min;
            Integer q = r.nextInt((qty_max - qty_min) + 1) + qty_min;
            Part p = new Part("Part" + i.toString(), new Vector3D(x, y, z), 10.);
            ps.put(p,q);
        }
        return new WarehouseOrder(ps);
    }
    
    public static void main(String[] args) throws NumberFormatException, IOException {
        long startTime = System.nanoTime();

        PackingDAOImpl tmp = new PackingDAOImpl();

        List<Box> availableBoxes = tmp.getAvailableBoxes();

        WarehouseOrder newWarehouseOrder = makeRandomOrder(6, 40, 1, 2, 4);

        List<Box> filledBoxes = tmp.getPacking(availableBoxes, newWarehouseOrder);
        tmp.storePacking(filledBoxes);

        System.out.println("TIME:" + (System.nanoTime() - startTime) / 1000000000.0);

    }
}