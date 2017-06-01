package packer;

import java.util.ArrayList;

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

	public static void main(String[] args){
		Part p1 = new Part("A",16,20,10,10,3);
		Part p2 = new Part("D",9,11,8,10,3);
		Part p3 = new Part("E",16,12,8,10,1);
		
		ArrayList<Part> p = new ArrayList<>();
		p.add(p1);p.add(p2);p.add(p3);
		
		Order new_order = new Order(p);
//		Order new_order = makeRandomOrder(4, 30, 1, 5, 5);
		new_order.volSort();
		System.out.println(new_order);
		
		InitialAttemptAlgo  tmp = new InitialAttemptAlgo(new Box(40,20,20,"Box1",1));
		Float a = tmp.prev_calcAcc(new_order);
		
		tmp.fill(new_order, new Vector(0,0,0), 0);
		
		System.out.println(tmp.b);
		System.out.println(a);
		System.out.println(tmp.calcAcc());
	}
}
