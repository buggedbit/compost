package packer;

import java.util.ArrayList;
import java.util.Random;

public class FillSingleExponentialNR {
	Box b;
	public FillSingleExponentialNR(Box b) {
		this.b = b;
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
	
	public void  fill(Order ord,Vector leftBottom, Integer i){
		// check for parts in order. check next while qty = 0 or that part is bigger than box size
		while(ord.order_list.size() > i && (ord.order_list.get(i).quantity == 0 || 
		!b.dimension.isEqualOrGreater(ord.order_list.get(i).dimension))){
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

	public Float calcAcc(){
		Integer fillVol = 0;
		for (Part p : b.parts) {
			fillVol+= p.getVol();
		}
		return  (100* (fillVol /(float) (b.dimension.x*b.dimension.y*b.dimension.z)));
	}
	public Float prev_calcAcc(Order ord){
		return  (100* (ord.getVol() /(float) (b.dimension.x*b.dimension.y*b.dimension.z)));
	}
	
	public static void main(String[] args){
		Random r = new Random();
		Integer min = 4, max = 40, qty_min = 1, qty_max = 5;
		ArrayList<Part> ps = new ArrayList<>();
		for (Integer i = 0; i < 5; i++) {
			Integer x = r.nextInt((max - min) + 1) + min;
			Integer y = r.nextInt((max - min) + 1) + min;
			Integer z = r.nextInt((max - min) + 1) + min;
			Integer q = r.nextInt((qty_max - qty_min) + 1) + qty_min;
			Part p = new Part(i.toString(),x,y,z,10,q);
			ps.add(p);
		}
		
		Part p1 = new Part("A",1,2,3,10,3);
		Part p2 = new Part("B",2,5,7,10,5);
		Part p3 = new Part("C",10,1,1,10,7);
		Part p4 = new Part("D",3,3,3,10,1);
		
		ArrayList<Part> p = new ArrayList<>();
		p.add(p2);p.add(p3);p.add(p4);p.add(p1);
		
		Order new_order = new Order(p);
		new_order.volSort();
		System.out.println(new_order);
		
		FillSingleExponentialNR  tmp = new FillSingleExponentialNR(new Box(10,10,7));
		Float a = tmp.prev_calcAcc(new_order);
		
		tmp.fill(new_order, new Vector(0,0,0), 0);
		
		System.out.println(tmp.b);
		System.out.println(a);
		System.out.println(tmp.calcAcc());
	}

}
