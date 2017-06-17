package com.partsavatar.packer.algorithms;

import com.partsavatar.packer.components.Box;
import com.partsavatar.packer.components.PackingComponent;
import com.partsavatar.packer.components.WarehouseOrder;
import com.partsavatar.packer.dao.packing.PackingDAOImpl;
import com.partsavatar.packer.testing.Testing;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;


public class FillMultipleBoxes {

    private static void calcBoxNumbers(ArrayList<Box> filledBoxes, ArrayList<Box> availableBoxes) {
        for (Box b : availableBoxes) {
            Integer maxIndex = 0;
            for (Box box : filledBoxes) {
                if (box.getId().equals(b.getId()) && box.getNum() > maxIndex)
                    maxIndex = box.getNum();
            }
            b.setNum(maxIndex + 1);
        }
    }

    private static PackingComponent fill(PriorityQueue<PackingComponent> heap, ArrayList<Box> availableBoxes, PackingComponent v, Integer volInitialItems) {
        calcBoxNumbers(v.getBoxes(), availableBoxes);
        availableBoxes.sort(Box::volCompareTo);

        while (v.getRemainingWarehouseOrder().numOfItems() != 0) {
            Integer thisStepVoid = availableBoxes.get(0).getVol(), leastVoidSpaceBoxLoc = 0;
            Double thisStepCompletion = 0.;

            for (int i = 0; i < availableBoxes.size(); i++) {

                WarehouseOrder tmpWarehouseOrder = v.getRemainingWarehouseOrder().copy();
                Box tmpBox = availableBoxes.get(i).copy();
                if (v.getRemainingWarehouseOrder().numOfItems() <= 6)
                    tmpWarehouseOrder = FinalAlgorithmBelow6Items.MainAlgo(tmpBox, tmpWarehouseOrder);
                else
                    tmpWarehouseOrder = FinalAlgorithmAbove6Items.MainAlgorithm(tmpBox, tmpWarehouseOrder);

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

            if (v.getRemainingWarehouseOrder().numOfItems() <= 6)
                v.setRemainingWarehouseOrder(FinalAlgorithmBelow6Items.MainAlgo(box, v.getRemainingWarehouseOrder()));
            else
                v.setRemainingWarehouseOrder(FinalAlgorithmAbove6Items.MainAlgorithm(box, v.getRemainingWarehouseOrder()));
            v.addBox(box.copy());
        }

        v.setFinalAccuracy(volInitialItems * 100.0 / (v.getCurrVoid() + volInitialItems));

        while (!heap.isEmpty()) {
            PackingComponent vs = heap.remove();
            vs = fill(heap, availableBoxes, vs, volInitialItems);
            if (vs.getFinalAccuracy() >= v.getFinalAccuracy()) {
                v = vs.copy();
            }
        }
        return v;
    }

    public static ArrayList<Box> pack(ArrayList<Box> availableBoxes, WarehouseOrder newWarehouseOrder) {
        PriorityQueue<PackingComponent> heap = new PriorityQueue<PackingComponent>(PackingComponent::compareTo);

        PackingComponent initialVoidSpace = new PackingComponent(0, 0, 0., newWarehouseOrder);
        initialVoidSpace.setBoxes(new ArrayList<Box>());

        PackingComponent vs = FillMultipleBoxes.fill(heap, availableBoxes, initialVoidSpace, newWarehouseOrder.getVol());
        System.out.println("ACC:" + vs.getFinalAccuracy());

        return vs.getBoxes();
    }

    public static void main(String[] args) throws NumberFormatException, IOException {
        long startTime = System.nanoTime();

        PackingDAOImpl tmp = new PackingDAOImpl();

        List<Box> availableBoxes = tmp.getAvailableBoxes();

        WarehouseOrder newWarehouseOrder = Testing.makeRandomOrder(6, 40, 1, 2, 4);

        List<Box> filledBoxes = tmp.getPacking(availableBoxes, newWarehouseOrder);
        tmp.storePacking(filledBoxes);

        System.out.println("TIME:" + (System.nanoTime() - startTime) / 1000000000.0);

    }
}