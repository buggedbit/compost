package com.partsavatar.packer.algorithms;

import com.partsavatar.packer.components.Box;
import com.partsavatar.packer.components.PackingComponent;
import com.partsavatar.packer.components.Part;
import com.partsavatar.packer.components.Vector3D;
import com.partsavatar.packer.components.WarehouseOrder;
import com.partsavatar.packer.dao.packing.PackingDAOImpl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Random;


public class FillMultipleBoxes {
	private static Integer SWITCH_BW_2_ALGO = 4;
	
    private static void calcBoxNumbers(final List<Box> filledBoxes, final List<Box> availableBoxes) {
        for (Box b : availableBoxes) {
            Integer maxIndex = 0;
            for (Box box : filledBoxes) {
                if (box.getId().equals(b.getId()) && box.getNum() > maxIndex)
                    maxIndex = box.getNum();
            }
            b.setNum(maxIndex + 1);
        }
    }

    private static PackingComponent fillBoxes(final PriorityQueue<PackingComponent> heap, final List<Box> availableBoxes, final PackingComponent v, final Integer volInitialItems) {
        calcBoxNumbers(v.getBoxes(), availableBoxes);
        availableBoxes.sort(Box::volCompareTo);

        while (v.getRemainingWarehouseOrder().numOfItems() != 0) {
            Integer thisStepVoid = availableBoxes.get(0).getVol(), leastVoidSpaceBoxLoc = 0;
            Double thisStepCompletion = 0.;

            for (int i = 0; i < availableBoxes.size(); i++) {

                WarehouseOrder tmpWarehouseOrder = v.getRemainingWarehouseOrder().copy();
                Box tmpBox = availableBoxes.get(i).copy();
                if (v.getRemainingWarehouseOrder().numOfItems() <= SWITCH_BW_2_ALGO)
                    tmpWarehouseOrder = BruteForceBacktrackAlgorithmBelow6Items.backtrackAlgorithm(tmpBox, tmpWarehouseOrder);
                else
                    tmpWarehouseOrder = BacktrackAlgorithmAbove6Items.backtrackAlgorithm(tmpBox, tmpWarehouseOrder);

                Integer tmpVoid = tmpBox.getVol() - tmpBox.getPartsVol();
                Double tmpCompletion = tmpBox.getPartsVol() * 100.0 / volInitialItems;

                if (thisStepVoid > tmpVoid && tmpBox.getPartsVol() > 0) {
                    thisStepVoid = tmpVoid;
                    thisStepCompletion = tmpCompletion;
                    leastVoidSpaceBoxLoc = i;
                }
                if (tmpBox.getPartsVol() > 0) {
                    Boolean shouldBeAdded = true;
                    for (PackingComponent vs : heap) {
                        if (tmpVoid + v.getCurrVoid() >= vs.getCurrVoid() && v.getCurrCompletion() + tmpCompletion <= vs.getCurrCompletion()) {
                            shouldBeAdded = false;
                            break;
                        }
                    }
                    if (shouldBeAdded) {
                        PackingComponent tmp1 = new PackingComponent(v.getCurrStep() + 1, tmpVoid + v.getCurrVoid(), v.getCurrCompletion() + tmpCompletion, tmpWarehouseOrder);
                        tmp1.setBoxes(v.copyBoxes());
                        tmp1.addBox(tmpBox.copy());
                        heap.add(tmp1);
                    }
                }
            }
            v.setCurrVoid(v.getCurrVoid() + thisStepVoid);
            v.setCurrCompletion(v.getCurrCompletion() + thisStepCompletion);
            v.setCurrStep(v.getCurrStep() + 1);

            if (!heap.isEmpty() && heap.peek().getCurrStep() == v.getCurrStep() && v.getCurrCompletion() < 100)
                heap.remove();
            else {
                if (heap.isEmpty())
                    System.err.println("WTF");
                while (!heap.isEmpty() && heap.peek().getCurrStep() == v.getCurrStep())
                    heap.remove();
            }

            Box box = availableBoxes.get(leastVoidSpaceBoxLoc).copy();

            Integer tmpNum = availableBoxes.get(leastVoidSpaceBoxLoc).getNum();
            availableBoxes.get(leastVoidSpaceBoxLoc).setNum(tmpNum + 1);

            if (v.getRemainingWarehouseOrder().numOfItems() <= SWITCH_BW_2_ALGO)
                v.setRemainingWarehouseOrder(BruteForceBacktrackAlgorithmBelow6Items.backtrackAlgorithm(box, v.getRemainingWarehouseOrder()));
            else
                v.setRemainingWarehouseOrder(BacktrackAlgorithmAbove6Items.backtrackAlgorithm(box, v.getRemainingWarehouseOrder()));
            v.addBox(box.copy());
        }

        v.setFinalAccuracy(volInitialItems * 100.0 / (v.getCurrVoid() + volInitialItems));
        
        PackingComponent finalStage = v.copy();
        while (!heap.isEmpty()) {
            PackingComponent vs = heap.remove();
            vs = fillBoxes(heap, availableBoxes, vs, volInitialItems);
            if (vs.getFinalAccuracy() >= finalStage.getFinalAccuracy()) {
                finalStage = vs.copy();
            }
        }
        return finalStage;
    }

    public static ArrayList<Box> packOrder(final ArrayList<Box> availableBoxes, final WarehouseOrder newWarehouseOrder) {
        PriorityQueue<PackingComponent> heap = new PriorityQueue<PackingComponent>(PackingComponent::compareTo);

        PackingComponent initialVoidSpace = new PackingComponent(0, 0, 0., newWarehouseOrder);
        initialVoidSpace.setBoxes(new ArrayList<Box>());

        PackingComponent vs = FillMultipleBoxes.fillBoxes(heap, availableBoxes, initialVoidSpace, newWarehouseOrder.getVol());
        System.out.println("ACC:" + vs.getFinalAccuracy());

        return vs.getBoxes();
    }
    
    public static WarehouseOrder makeRandomOrder(Integer min, Integer max, Integer qty_min, Integer qty_max, Integer diffParts) {
        Random r = new Random();
        ArrayList<Part> ps = new ArrayList<Part>();
        for (Integer i = 0; i < diffParts; i++) {
            Integer x = r.nextInt((max - min) + 1) + min;
            Integer y = r.nextInt((max - min) + 1) + min;
            Integer z = r.nextInt((max - min) + 1) + min;
            Integer q = r.nextInt((qty_max - qty_min) + 1) + qty_min;
            Part p = new Part("Part" + i.toString(), new Vector3D(x, y, z), 10, q);
            ps.add(p);
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