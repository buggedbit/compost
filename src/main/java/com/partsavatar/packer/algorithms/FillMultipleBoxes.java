package com.partsavatar.packer.algorithms;

import com.partsavatar.packer.components.Box;
import com.partsavatar.packer.components.Order;
import com.partsavatar.packer.components.PackingComponent;
import com.partsavatar.packer.dao.packing.PackingDAOImpl;
import com.partsavatar.packer.testing.Testing;

import java.io.IOException;
import java.util.*;


public class FillMultipleBoxes {
	
	private static void calcBoxNumbers(ArrayList<Box> filledBoxes, ArrayList<Box> availableBoxes ){
		for (Box b: availableBoxes) {
			Integer maxIndex = 0;
			for (Box box : filledBoxes) {
				if(box.getId().equals(b.getId()) && box.getNum() > maxIndex)
					maxIndex = box.getNum();
			}
			b.setNum(maxIndex+1);
		}	
	}	
	
	private static PackingComponent fill(PriorityQueue<PackingComponent> heap, ArrayList<Box> availableBoxes, PackingComponent v, Integer volInitalItems){
     	calcBoxNumbers(v.getBoxes(), availableBoxes);
     	Collections.sort(availableBoxes, (Box b1, Box b2) -> b1.volCompareTo(b2));

		while(v.getRemainingOrder().numOfItems() != 0){			
			Integer thisStepVoid = availableBoxes.get(0).getVol(), leastVoidSpaceBoxLoc = 0;
			Double thisStepCompletion = 0.;
			
			for (int i = 0; i < availableBoxes.size(); i++) {
				
				Order tmpOrder = v.getRemainingOrder().copy();
				Box tmpBox = availableBoxes.get(i).copy();
				if(v.getRemainingOrder().numOfItems() <= 6)
					tmpOrder = FinalAlgorithmBelow6Items.MainAlgo(tmpBox,tmpOrder);
				else
					tmpOrder = FinalAlgorithmAbove6Items.MainAlgo(tmpBox,tmpOrder);
					
				Integer tmpVoid = tmpBox.getVol() - tmpBox.getPartsVol();
				Double tmpCompletion = tmpBox.getPartsVol()*100.0/volInitalItems;
				
				if(thisStepVoid > tmpVoid && tmpBox.getPartsVol() > 0){
					thisStepVoid = tmpVoid; thisStepCompletion = tmpCompletion;
					leastVoidSpaceBoxLoc = i;
				}
				if(tmpBox.getPartsVol() > 0){
					Boolean shouldBeAdded = true;
					for (PackingComponent vs : heap) {
						if(tmpVoid + v.getCurrVoid() >= vs.getCurrVoid() && v.getCurrCompletion() + tmpCompletion <= vs.getCurrCompletion()){
							shouldBeAdded = false;
							break;
						}
					}
					if(shouldBeAdded){
						PackingComponent tmp1 = new PackingComponent(v.getCurrStep() + 1, tmpVoid + v.getCurrVoid(), v.getCurrCompletion() + tmpCompletion, tmpOrder);
						tmp1.setBoxes(v.copyBoxes());
						tmp1.addBox(tmpBox.copy());
						heap.add(tmp1);
					}
				}
			}
			v.setCurrVoid(v.getCurrVoid() + thisStepVoid);
			v.setCurrCompletion(v.getCurrCompletion()+thisStepCompletion);	
			v.setCurrStep(v.getCurrStep()+1);
			
			if(!heap.isEmpty() && heap.peek().getCurrStep() == v.getCurrStep() && v.getCurrCompletion() < 100)
				heap.remove();
			else{
				if(heap.isEmpty())
					System.err.println("WTF");
				while(!heap.isEmpty() && heap.peek().getCurrStep() == v.getCurrStep())
					heap.remove();
			}
			
			Box box = availableBoxes.get(leastVoidSpaceBoxLoc).copy();
			
			Integer tmpNum = availableBoxes.get(leastVoidSpaceBoxLoc).getNum();
			availableBoxes.get(leastVoidSpaceBoxLoc).setNum(tmpNum+1);
			
			if(v.getRemainingOrder().numOfItems() <= 6)
				v.setRemainingOrder(FinalAlgorithmBelow6Items.MainAlgo(box, v.getRemainingOrder()));
			else
				v.setRemainingOrder(FinalAlgorithmAbove6Items.MainAlgo(box, v.getRemainingOrder()));
			v.addBox(box.copy());
		}
		
		v.setFinalAccuracy(volInitalItems * 100.0/(v.getCurrVoid() + volInitalItems));
		
		while(!heap.isEmpty()){
			PackingComponent vs = heap.remove();
			vs = fill(heap, availableBoxes, vs, volInitalItems);
			if(vs.getFinalAccuracy() >= v.getFinalAccuracy()){
				v = vs.copy();	
			}
		}
		return v;
	}
	
	public static ArrayList<Box> pack(ArrayList<Box> availableBoxes, Order newOrder){
		PriorityQueue<PackingComponent> heap = new PriorityQueue<PackingComponent>((PackingComponent o1, PackingComponent o2)-> o1.compareTo(o2));
		
		PackingComponent initialVoidSpace = new PackingComponent(0,0, 0.,newOrder);
		initialVoidSpace.setBoxes(new ArrayList<Box>());
		
		PackingComponent vs = FillMultipleBoxes.fill(heap, availableBoxes, initialVoidSpace, newOrder.getVol());
		System.out.println("ACC:" + vs.getFinalAccuracy());
		
		return vs.getBoxes();
	}
	
	public static void main(String[] args) throws NumberFormatException, IOException {
		long starttime = System.nanoTime();
		
		PackingDAOImpl tmp = new PackingDAOImpl();
		
		List<Box> availableBoxes = tmp.getAvailableBoxes();
		
		Order newOrder = Testing.makeRandomOrder(6, 40, 1, 2, 4);
		
		List<Box> filledBoxes = tmp.getPacking(availableBoxes, newOrder);
		tmp.storePacking(filledBoxes);
		
		System.out.println("TIME:" + (System.nanoTime()-starttime)/1000000000.0);

	}
}