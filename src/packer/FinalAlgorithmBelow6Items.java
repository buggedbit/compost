package packer;
import java.util.*;

public class FinalAlgorithmBelow6Items extends FinalAlgortihmBaseClass{
	
	public FinalAlgorithmBelow6Items(Box b) {
		super(b);
	}
	
	private void upwardFill(Order ord,Vector leftBottom, Vector sliceDim){//fill a column recursively in upward dir
		Integer i = 0;	
		while(ord.order_list.size() > i && (ord.order_list.get(i).quantity == 0 || 
		!sliceDim.checkIsEqualOrGreater(ord.order_list.get(i)))){
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
		!sliceDim.checkIsEqualOrGreater(ord.order_list.get(i)))){
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
	
	public static Order expandOrder(Order ord){
		List<Part> expList = new ArrayList<>();
		for (Part p : ord.order_list) {
			Integer qty = p.quantity;
			for (int i = 0; i < qty; i++) {
				expList.add(new Part(p.id, p.dimension.x, p.dimension.y, p.dimension.z, p.weight, 1));
			}
		}
		return new Order(expList);
		
	}
	public static ArrayList<Part> rotate(Part p){
		ArrayList<Part> out= new ArrayList<>();
		Integer x = p.dimension.x, y = p.dimension.y, z = p.dimension.z; 
		if(x != y && y != z && z != x){
			out.add(new Part(p.id, x, y, z, p.weight, 1));
			out.add(new Part(p.id, x, z, y, p.weight, 1));
			out.add(new Part(p.id, y, x, z, p.weight, 1));
			out.add(new Part(p.id, y, z, x, p.weight, 1));
			out.add(new Part(p.id, z, x, y, p.weight, 1));
			out.add(new Part(p.id, z, y, x, p.weight, 1));
		}
		else if(x == y && y == z){
			out.add(new Part(p.id, x, x, x, p.weight, 1));
		}
		else if(x == y){
			out.add(new Part(p.id, x, x, z, p.weight, 1));
			out.add(new Part(p.id, x, z, x, p.weight, 1));
			out.add(new Part(p.id, z, x, x, p.weight, 1));
		}
		else if(y == z){
			out.add(new Part(p.id, x, z, z, p.weight, 1));
			out.add(new Part(p.id, z, x, z, p.weight, 1));
			out.add(new Part(p.id, z, z, x, p.weight, 1));
		}
		else if(x == z){
			out.add(new Part(p.id, x, x, y, p.weight, 1));
			out.add(new Part(p.id, x, y, x, p.weight, 1));
			out.add(new Part(p.id, y, x, x, p.weight, 1));
		}
		return out;
	}
	public static ArrayList<Order> genRotations(Order ord, Integer start){
		ArrayList<Order> out = new ArrayList<>();
		ArrayList<Part> startRotations = rotate(ord.order_list.get(start));
		if(start!= ord.order_list.size()-1){
			ArrayList<Order> tmp  = genRotations(ord, start + 1);
			for (Part p : startRotations) {	
				for (Order t : tmp) {
					ArrayList<Part> orderList = new ArrayList<>();
					for (Part s : t.order_list) {
						orderList.add(new Part(s.id, s.dimension.x, s.dimension.y, s.dimension.z, s.weight, 1));
					}
					out.add(new Order(orderList));
					out.get(out.size()-1).order_list.add(p);
				}
			}
		}
		else{
			for (Part p : startRotations) {	
				List<Part> tmp = new ArrayList<>();
				tmp.add(p);
				out.add(new Order(tmp));
			}
		}
		return out;
	}
	
	@Override
	public Order MainAlgo(Order new_order){
		ArrayList<Order> rotatedOrders = genRotations(expandOrder(new_order), 0);
		
		Float acc = (float) 0;
		for (Order ord : rotatedOrders) {	
			for (Part q : ord.order_list) {
				q.quantity =1;
			}
			ord.volSort();
			FinalAlgorithmBelow6Items  tmp = new FinalAlgorithmBelow6Items(new Box(b.dimension.x,b.dimension.y,b.dimension.z,b.id,b.num));
			Float prev = tmp.prev_calcAcc(ord);
			tmp.fillBox(ord,new Vector(0, 0, 0),tmp.b.dimension) ;		
			HashMap<Vector, Vector> unFilled = tmp.combineUnused();
			for (Vector key : unFilled.keySet()) {
				tmp.fillBox(ord, key, new Vector(unFilled.get(key).x, tmp.b.dimension.y - key.y, unFilled.get(key).z));
			}
			if(tmp.calcAcc() > acc){
				System.out.println("PREV :" + prev.toString());
				this.b = tmp.b;
				this.s = tmp.s;
				this.unused = tmp.unused;
				new_order = ord.copy();
				acc = tmp.calcAcc();
				System.out.println("CURR :" + tmp.calcAcc().toString());
			}
		}
		System.out.println("FINAL :" + acc.toString());
		return new_order;
	}
	
	public static void main(String[] args) {
		Part p1 = new Part("A",6,16,19,10,3);
		Part p2 = new Part("D",18,11,9,10,4);
//		Part p3 = new Part("E",24,9,9,10,1);
		
		ArrayList<Part> p = new ArrayList<>();
		p.add(p1);p.add(p2);//p.add(p3);
		
		Order new_order = new Order(p);
		FinalAlgorithmBelow6Items tmp = new FinalAlgorithmBelow6Items(new Box(40,20,20,"Box1",1));
		tmp.MainAlgo(new_order);
		System.out.println(tmp.b);
	}
}
