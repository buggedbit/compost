package Components;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class Order {
	public List<Part> order_list;
	public Order(List<Part> p){
		order_list = p;
	}
	
	public Order copy(){
		List<Part> pList = new ArrayList<>(); 
		for (Part p : order_list) {
			pList.add(new Part(p.id, p.dimension.x, p.dimension.y, p.dimension.z, p.weight, p.quantity));
		}
		return new Order(pList);
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
	public Integer numOfItems(){
		Integer out =0;
		for (Part part : order_list) {
			out += part.quantity;
		}
		return out;
	}
	public Integer getVol(){
		Integer vol = 0;
		for (Part p : order_list) {
			vol+= p.getVol()*p.quantity;
		}
		return vol;
	}

	public Integer minPart() {
		Integer min = 10000;
		for (Part part : order_list) {
			Integer minDim = Math.min(part.dimension.x,Math.min(part.dimension.y,part.dimension.z));
			min = Math.min(min,minDim);
		}
		return min;
	}
	public void publish(String s) throws IOException{
		FileWriter fw = new FileWriter(".\\Outputs\\"+s);
		fw.append("PartID,DimX,DimY,DimZ,Quantity,Weight\n");
		for(Part part: order_list){
        	fw.append(part.id + ",");
        	fw.append(part.dimension.x + ",");
        	fw.append(part.dimension.y + ",");
        	fw.append(part.dimension.z + ",");
        	fw.append(part.quantity + ",");
        	fw.append(part.weight + "\n");
        }
        fw.flush();
        fw.close();
	}
}


