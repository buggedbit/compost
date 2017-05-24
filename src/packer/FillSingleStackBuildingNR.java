package packer;

import java.util.*;

public class FillSingleStackBuildingNR {
	Box b;
	Stack<Surface> s;
	
	public FillSingleStackBuildingNR(Box b) {
		this.b = b;
		this.s = new Stack<>();
	}
	
	private void upwardFill(Order ord,Vector leftBottom, Vector sliceDim){
		// check for parts in order. check next while qty = 0 or that part is bigger than box size
		Integer i = 0;
		while(ord.order_list.size() > i && (ord.order_list.get(i).quantity == 0 || 
		!sliceDim.isEqualOrGreater(ord.order_list.get(i).dimension))){
			i++;
		}
		if(i == ord.order_list.size()){//No other item in order fits the box
			return;
		}
		else{//update order, box, left bottom for section c
			ord.order_list.get(i).quantity--; 
			
			Part p = new Part(ord.order_list.get(i), leftBottom, 1);
			b.parts.add(p);
			s.push(new Surface(leftBottom, p.dimension));
			
			upwardFill(ord, new Vector(leftBottom.x, leftBottom.y + p.dimension.y, leftBottom.z), new Vector(p.dimension.x, sliceDim.y - p.dimension.y, p.dimension.z));
		}
	}
	private void backwardFill(Order ord) {
		while(!s.empty()){
			Surface top =  s.pop();
			if(s.empty())
				break;
			Surface bottom = s.peek();
			
			Vector frontSliceDim = new Vector(bottom.surface.x, b.dimension.y - bottom.leftUpperBehind.y, bottom.surface.z - top.surface.z);
			Vector frontSlicelbb = new Vector(bottom.leftUpperBehind.x, bottom.leftUpperBehind.y, bottom.leftUpperBehind.z + top.surface.z);
			
			Integer i = 0;
			while(ord.order_list.size() > i && (ord.order_list.get(i).quantity == 0 || 
			!frontSliceDim.isEqualOrGreater(ord.order_list.get(i).dimension))){
				i++;
			}
			if(i != ord.order_list.size()){
				ord.order_list.get(i).quantity--; 
				
				Part p = new Part(ord.order_list.get(i), frontSlicelbb, 1);
				b.parts.add(p);
				upwardFill(ord, new Vector(frontSlicelbb.x, frontSlicelbb.y + p.dimension.y, frontSlicelbb.z), frontSliceDim);
				while(!s.peek().equals(bottom)){
					s.pop();
				}
			}
			
			Vector sideSliceDim = new Vector(bottom.surface.x - top.surface.x, b.dimension.y - bottom.leftUpperBehind.y, top.surface.z);
			Vector sideSlicelbb = new Vector(bottom.leftUpperBehind.x + top.surface.x, bottom.leftUpperBehind.y, bottom.leftUpperBehind.z);
			
			i = 0;
			while(ord.order_list.size() > i && (ord.order_list.get(i).quantity == 0 || 
			!sideSliceDim.isEqualOrGreater(ord.order_list.get(i).dimension))){
				i++;
			}
			if(i == ord.order_list.size()){//No other item in order fits the box
				return;
			}
			else{
				ord.order_list.get(i).quantity--; 
				
				Part p = new Part(ord.order_list.get(i), sideSlicelbb, 1);
				b.parts.add(p);
				upwardFill(ord, new Vector(sideSlicelbb.x, sideSlicelbb.y + p.dimension.y, sideSlicelbb.z), sideSliceDim);
				while(!s.peek().equals(bottom)){
					s.pop();
				}
			}
		}
	}
	
	private Integer fillBoxWidth(Order ord, Vector leftBottom, Vector sliceDim){
		Integer i = 0;
		while(ord.order_list.size() > i && (ord.order_list.get(i).quantity == 0 || 
		!sliceDim.isEqualOrGreater(ord.order_list.get(i).dimension))){
			i++;
		}
		if(i == ord.order_list.size()){//No other item in order fits the box
			return 0;
		}
		else{//update order, box, left bottom for section c
			ord.order_list.get(i).quantity--; 
			
			Part p = new Part(ord.order_list.get(i), leftBottom, 1);
			b.parts.add(p);
			s.push(new Surface(leftBottom, p.dimension));
			
			upwardFill(ord, new Vector(leftBottom.x, leftBottom.y + p.dimension.y, leftBottom.z), new Vector(p.dimension.x, b.dimension.y - p.dimension.y, p.dimension.z));
			backwardFill(ord);
			fillBoxWidth(ord, new Vector(leftBottom.x, leftBottom.y, leftBottom.z + p.dimension.z),new Vector(p.dimension.x, sliceDim.y, sliceDim.z - p.dimension.z));
			return leftBottom.x + p.dimension.x;
		}
	}
	private void fillBox(Order ord) {
		Integer x = fillBoxWidth(ord,new Vector(0, 0, 0),b.dimension);
		while(x!=0){
			x = fillBoxWidth(ord,new Vector(x, 0, 0),new Vector(b.dimension.x - x, b.dimension.y, b.dimension.z));
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
		
		FillSingleStackBuildingNR  tmp = new FillSingleStackBuildingNR(new Box(10,10,7));
		Float a = tmp.prev_calcAcc(new_order);
		
		tmp.fillBox(new_order) ;
		System.out.println(tmp.b);
		System.out.println(a);
		System.out.println(tmp.calcAcc());
	}
}
