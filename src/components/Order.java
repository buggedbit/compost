package components;
import java.util.*;

import lombok.Data;
import lombok.NonNull;


public  @Data class Order {
	@NonNull List<Part> orderList;
	
	public Order copy(){
		List<Part> pList = new ArrayList<Part>(); 
		for (Part p : orderList) {
			pList.add(new Part(p.id, p.dimension, p.weight, p.quantity));
		}
		return new Order(pList);
	}
	

	public void volSort(){
		Collections.sort(orderList, (Part p1, Part p2)->p1.volCompareTo(p2));
	}
	
	public Integer numOfItems(){
		Integer num =0;
		for (Part part : orderList) {
			num += part.quantity;
		}
		return num;
	}
	
	public Integer getVol(){
		Integer vol = 0;
		for (Part p : orderList) {
			vol+= p.getVol()*p.quantity;
		}
		return vol;
	}

	public Integer getMinPartSize() {
		Integer min = 10000;
		for (Part part : orderList) {
			Integer minDim = Math.min(part.dimension.getX(), Math.min(part.dimension.getY(),part.dimension.getZ()));
			min = Math.min(min,minDim);
		}
		return min;
	}
}


