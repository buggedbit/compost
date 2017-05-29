package packer;

import java.util.*;

public class Order {
	List<Part> order_list;
	Order(List<Part> p){
		order_list = p;
	}
	
	public String toString() {
		return order_list.toString();
	}
	public void volSort(){
		Collections.sort(order_list, new Comparator<Part>() {
			@Override
	        public int compare(Part p1, Part p2) {
				return  p1.volCompareTo(p2);
	        }
	    });
	}
	public Integer getVol(){
		Integer vol = 0;
		for (Part p : order_list) {
			vol+= p.getVol()*p.quantity;
		}
		return vol;
	}
}


