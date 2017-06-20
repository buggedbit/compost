package com.partsavatar.packer.algorithms;

import com.partsavatar.packer.components.Box;
import com.partsavatar.packer.components.PackingComponent;
import com.partsavatar.packer.components.WarehouseOrder;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;


public class FillMultipleBoxes {
	private static Integer SWITCH_BW_2_ALGO = 4;
	
	public static ArrayList<Box> packOrder(final ArrayList<Box> availableBoxes, final WarehouseOrder newWarehouseOrder) {
        PriorityQueue<PackingComponent> heap = new PriorityQueue<PackingComponent>(PackingComponent::compareTo);

        PackingComponent initialVoidSpace = new PackingComponent(0, 0, 0., newWarehouseOrder.copy());
        initialVoidSpace.setBoxes(new ArrayList<Box>());

        PackingComponent vs = FillMultipleBoxes.fillBoxes(heap, availableBoxes, initialVoidSpace, newWarehouseOrder.getVol());
        //System.out.println("ACC:" + vs.getFinalAccuracy());

        return vs.getBoxes();
    }
	
	private static void calcBoxNumbers(final List<Box> filledBoxes, final List<Box> availableBoxes) {
        for (Box b : availableBoxes) {
            Integer maxIndex = 0;
            for (Box box : filledBoxes) {
                if (box.getId().equals(b.getId()) && box.getNum() > maxIndex) {
                    maxIndex = box.getNum();
                }
            }
            b.setNum(maxIndex + 1);
        }
    }
    
    private static PackingComponent fillBoxes(final PriorityQueue<PackingComponent> heap, final List<Box> availableBoxes, final PackingComponent initialPacking, final Integer volInitialItems) {
        calcBoxNumbers(initialPacking.getBoxes(), availableBoxes);
        
        availableBoxes.sort(Box::volCompareTo);
        
        //Forward Propagation in one direction towards completing the order (storing important alternatives in the way)
        while (initialPacking.getRemainingWarehouseOrder().numOfItems() != 0) {
            Integer thisStepVoid = availableBoxes.get(0).getVol(), leastVoidSpaceBoxLoc = 0;
            Double thisStepCompletion = 0.;

            for (int i = 0; i < availableBoxes.size(); i++) {

                WarehouseOrder tmpWarehouseOrder = initialPacking.getRemainingWarehouseOrder().copy();
                Box tmpBox = availableBoxes.get(i).copy();
                if (initialPacking.getRemainingWarehouseOrder().numOfItems() <= SWITCH_BW_2_ALGO) {
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
                        if (tmpVoid + initialPacking.getCurrVoid() >= vs.getCurrVoid() && initialPacking.getCurrCompletion() + tmpCompletion <= vs.getCurrCompletion()) {
                            shouldBeAdded = false;
                            break;
                        }
                    }
                    if (shouldBeAdded) {
                        PackingComponent tmp1 = new PackingComponent(initialPacking.getCurrStep() + 1, tmpVoid + initialPacking.getCurrVoid(), initialPacking.getCurrCompletion() + tmpCompletion, tmpWarehouseOrder);
                        tmp1.setBoxes(initialPacking.copyBoxes());
                        tmp1.addBox(tmpBox.copy());
                        heap.add(tmp1);
                    }
                }
            }
            //update the packing by chosen box from above
            initialPacking.setCurrVoid(initialPacking.getCurrVoid() + thisStepVoid);
            initialPacking.setCurrCompletion(initialPacking.getCurrCompletion() + thisStepCompletion);
            initialPacking.setCurrStep(initialPacking.getCurrStep() + 1);

            //remove this updated stage from packing
            if (!heap.isEmpty() && heap.peek().getCurrStep() == initialPacking.getCurrStep() && initialPacking.getCurrCompletion() < 100) {
                heap.remove();
            }
            else {
               while (!heap.isEmpty() && heap.peek().getCurrStep() == initialPacking.getCurrStep()) {
            	   heap.remove();
               }
            }
            
            Box box = availableBoxes.get(leastVoidSpaceBoxLoc).copy();

            Integer tmpNum = availableBoxes.get(leastVoidSpaceBoxLoc).getNum();
            availableBoxes.get(leastVoidSpaceBoxLoc).setNum(tmpNum + 1);

            if (initialPacking.getRemainingWarehouseOrder().numOfItems() <= SWITCH_BW_2_ALGO) {
                initialPacking.setRemainingWarehouseOrder(BruteForceBacktrackAlgorithmBelow6Items.backtrackAlgorithm(box, initialPacking.getRemainingWarehouseOrder()));
            }else {
                initialPacking.setRemainingWarehouseOrder(BacktrackAlgorithmAbove6Items.backtrackAlgorithm(box, initialPacking.getRemainingWarehouseOrder()));
            }
            initialPacking.addBox(box.copy());
        }
        
        initialPacking.setFinalAccuracy(volInitialItems * 100.0 / (initialPacking.getCurrVoid() + volInitialItems));
        
        //Iterating over alternative stages of Packing in heap to check for better possibility
        PackingComponent finalStage = initialPacking.copy();
        while (!heap.isEmpty()) {
            PackingComponent vs = heap.remove();
            vs = fillBoxes(heap, availableBoxes, vs, volInitialItems);
            if (vs.getFinalAccuracy() >= finalStage.getFinalAccuracy()) {
                finalStage = vs.copy();
            }
        }
        return finalStage;
    }
}