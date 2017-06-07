package Algorithms;

import Components.*;

public class InitialAttemptAlgo extends FinalAlgortihmBaseClass {
	
	public InitialAttemptAlgo(Box b) {
		super(b);
	}
	void fillSectionC(Order newOrder, Vector partDim, Vector boxDim, Vector origLeftBottom, Integer i){
		Vector newBoxDim = new Vector(partDim.x, boxDim.y - partDim.y, partDim.z);
		Vector newLeftBottom = new Vector(origLeftBottom.x, origLeftBottom.y + partDim.y, origLeftBottom.z);
		
		b.dimension = newBoxDim;
		fill(newOrder,newLeftBottom,i);
		b.dimension = boxDim;
	}
	void fillSectionB(Order newOrder, Vector partDim, Vector boxDim, Vector origLeftBottom, Integer i){
		Vector newBoxDim = new Vector(partDim.x, boxDim.y, boxDim.z - partDim.z);
		Vector newLeftBottom = new Vector(origLeftBottom.x, origLeftBottom.y, origLeftBottom.z + partDim.z);
		
		b.dimension = newBoxDim;
		fill(newOrder,newLeftBottom,i);
		b.dimension = boxDim;
	}
	void fillSectionA(Order newOrder, Vector partDim, Vector boxDim, Vector origLeftBottom, Integer i){
		Vector newBoxDim = new Vector(boxDim.x - partDim.x, boxDim.y, boxDim.z);
		Vector newLeftBottom = new Vector(origLeftBottom.x + partDim.x, origLeftBottom.y, origLeftBottom.z);
		
		b.dimension = newBoxDim;
		fill(newOrder,newLeftBottom,i);
		b.dimension = boxDim;
	}
	
	public void fill(Order ord,Vector leftBottom, Integer i){
		// check for parts in order. check next while qty = 0 or that part is bigger than box size
		while(ord.order_list.size() > i && (ord.order_list.get(i).quantity == 0 || 
		!b.dimension.rotateAndCheckIsEqualOrGreater(ord.order_list.get(i)))){
			i++;
		}
		if(i == ord.order_list.size())//No other item in order fits the box
			return;
		else{//update order, box, left bottom for section c
			ord.order_list.get(i).quantity--; 
		
			Part p = new Part(ord.order_list.get(i), leftBottom, 1);
			b.parts.add(p);
			
			fillSectionC(ord, p.dimension, b.dimension, leftBottom, i);
			fillSectionB(ord, p.dimension, b.dimension, leftBottom, i);
			fillSectionA(ord, p.dimension, b.dimension, leftBottom, i);
		}
		
	}

}
