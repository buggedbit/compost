package packer;

import java.util.ArrayList;

public class Box {
	Vector dimension;
	ArrayList<Part> parts;
	String id;
	Integer num;
	public Box(Integer  l, Integer  w, Integer h, String s,Integer n) {
		dimension = new Vector(l, w, h);
		parts = new ArrayList<>();
		num = n;
		id = s;
	}
	public int volCompareTo(Box b) {
	    Integer lessThan = 1;
	    Integer greaterThan = -1;
	    
		if(this.getVol() < b.getVol())
	    	return lessThan;
	    else if(this.getVol() > b.getVol())
	    	return greaterThan;
	    else
	    	return 0;
	}
	public Integer getVol(){
		return dimension.x*dimension.y*dimension.z;
	}
	public Integer getPartsVol(){
		Integer sum = 0;
		for (Part part : parts) {
			sum+=part.getVol();
		}
		return sum;
	}
	public String toString() {
		return id + "-"+ num +":[" + dimension.toString() + "]\n" + parts.toString() + "\n";
	}
}
