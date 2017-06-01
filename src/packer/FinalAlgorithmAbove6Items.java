package packer;
import java.io.IOException;
import java.util.*;

public class FinalAlgorithmAbove6Items extends FinalAlgortihmBaseClass{
	
	public FinalAlgorithmAbove6Items(Box b) {
		super(b);
	}
	
	private void upwardFill(Order ord,Vector leftBottom, Vector sliceDim){//fill a column recursively in upward dir
		Integer i = 0;	
		while(ord.order_list.size() > i && (ord.order_list.get(i).quantity == 0 || 
		!sliceDim.bestRotateAndCheckIsEqualOrGreater(ord.minPart(),ord.order_list.get(i)))){
//		!sliceDim.rotateAndCheckIsEqualOrGreater(ord.order_list.get(i)))){		
			i++;
		}
		if(i == ord.order_list.size()){//No fit found
			if(!(sliceDim.x == 0 || sliceDim.y == 0 || sliceDim.z == 0)){
				unused.put(leftBottom, new Vector(sliceDim.x, 0, sliceDim.z));
			}
			return;
		}
		else{
			ord.order_list.get(i).quantity--; 
			
			Part p = new Part(ord.order_list.get(i), leftBottom, 1);
			b.parts.add(p);
			s.push(new Surface(leftBottom, p.dimension)); // top surface of part
			
			upwardFill(ord, new Vector(leftBottom.x, leftBottom.y + p.dimension.y, leftBottom.z), new Vector(p.dimension.x, sliceDim.y - p.dimension.y, p.dimension.z));
		}
	}
	private void backwardFill(Order ord, Surface surf) {
		while(s.peek() != surf){
			Surface top =  s.pop();
			Surface bottom = s.peek();
			
			//try to fill front portion BIGGER
			Vector frontSliceDim = new Vector(bottom.surface.x, b.dimension.y - bottom.leftUpperBehind.y, bottom.surface.z - top.surface.z);
			Vector frontSlicelbb = new Vector(bottom.leftUpperBehind.x, bottom.leftUpperBehind.y, bottom.leftUpperBehind.z + top.surface.z);
			fillBox(ord,frontSlicelbb,frontSliceDim);
			
			//try to fill front portion SMALLER
			Vector sideSliceDim = new Vector(bottom.surface.x - top.surface.x, b.dimension.y - bottom.leftUpperBehind.y, top.surface.z);
			Vector sideSlicelbb = new Vector(bottom.leftUpperBehind.x + top.surface.x, bottom.leftUpperBehind.y, bottom.leftUpperBehind.z);
			fillBox(ord,sideSlicelbb,sideSliceDim);
		}
		s.pop();
	}

	public void fillBox(Order ord, Vector leftBottom, Vector sliceDim){
		Integer i = 0;
		while(ord.order_list.size() > i && (ord.order_list.get(i).quantity == 0 || 
		!sliceDim.bestRotateAndCheckIsEqualOrGreater(ord.minPart(),ord.order_list.get(i)))){
//		!sliceDim.rotateAndCheckIsEqualOrGreater(ord.order_list.get(i)))){		
			i++;
		}
		if(i == ord.order_list.size()){ // No fit found
			if(!(sliceDim.x == 0 || sliceDim.y == 0 || sliceDim.z == 0))
				unused.put(leftBottom, new Vector(sliceDim.x, 0, sliceDim.z));
			return;
		}
		else{
			ord.order_list.get(i).quantity--; 
			
			Surface partBottom = new Surface(leftBottom,new Vector(sliceDim.x, 0, sliceDim.z));
			s.push(partBottom);// bottom surface of box
			
			Part p = new Part(ord.order_list.get(i), leftBottom, 1);
			b.parts.add(p);
			s.push(new Surface(leftBottom, p.dimension)); // top surface of part
			
			upwardFill(ord, new Vector(leftBottom.x, leftBottom.y + p.dimension.y, leftBottom.z), new Vector(p.dimension.x, sliceDim.y - p.dimension.y, p.dimension.z));
			backwardFill(ord,partBottom); // Fill completely only the column with bottom surface as bottom of box
		}
	}
	@Override	
	public Order MainAlgo(Order new_order){
		new_order.volSort();
		System.out.println("WITHOUT PREV :" + prev_calcAcc(new_order).toString());
		fillBox(new_order,new Vector(0, 0, 0),b.dimension) ;		
		HashMap<Vector, Vector> unFilled = combineUnused();
		for (Vector key : unFilled.keySet()) {
			fillBox(new_order , key, new Vector(unFilled.get(key).x, b.dimension.y - key.y, unFilled.get(key).z));
		}
		System.out.println("WITHOUT FINAL :" + calcAcc().toString());
		return new_order;
	}
	public static void main(String[] args) throws IOException{//3 3 1 3 2
//		Part p1 = new Part("Part1",18,8,10,10,1);
//		Part p2 = new Part("part2",13,5,14,10,2);
//		Part p3 = new Part("Part3",15,15,9,10,2);
//		
//		ArrayList<Part> p = new ArrayList<>();
//		p.add(p1);p.add(p2);p.add(p3);
//		
//		Order new_order = new Order(p);
		Order new_order = makeRandomOrder(5, 20, 1, 3, 5);
		new_order.publish();
		System.out.println(new_order);
		FinalAlgorithmAbove6Items tmp = new FinalAlgorithmAbove6Items(new Box(40,40,20,"Box1",1));
		tmp.MainAlgo(new_order);
		System.err.println(tmp.b);
		tmp.publish();
	}
}
