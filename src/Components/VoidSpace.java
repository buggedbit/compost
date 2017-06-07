package Components;

import java.io.*;
import java.util.*;

public class VoidSpace {
	public Integer currStep, currVoid;
	public Double currCompletion, FinalAccuracy;
	public Order remaining;
	public ArrayList<Box> boxes;

	public ArrayList<Box> copyBoxes(){
		ArrayList<Box> copy = new ArrayList<>();
		for (Box box : boxes) {
			copy.add(new Box(box.dimension.x, box.dimension.y, box.dimension.z, box.id, box.num));
			copy.get(copy.size()-1).parts = box.copyParts();
		}
		return copy;
	}
	public VoidSpace copyVoidSpace(){
		VoidSpace vs = new VoidSpace(currStep, currVoid, currCompletion, remaining);
		vs.FinalAccuracy = FinalAccuracy;
		vs.boxes = copyBoxes();
		return vs;
	}
	public VoidSpace() {
		currStep = currVoid= 0;
		currCompletion = 0.;
		remaining = new Order(null);
		boxes = new ArrayList<>();
	}
	public VoidSpace(Integer s, Integer vol, Double com, Order o) {
		currStep = s;currVoid= vol;
		currCompletion = com;
		remaining = o;
		boxes = new ArrayList<>();
	}

	
	public void publish(String s) throws IOException{
		FileWriter fw = new FileWriter(".\\Outputs\\"+s);
		fw.append("PartID,DimX,DimY,DimZ,PosX,PosY,PosZ,BoxID,Weight\n");
		for(Box b: boxes){
			for(Part part: b.parts){
	        	fw.append(part.id + ",");
	        	fw.append(part.dimension.x - 0.5 + ",");
	        	fw.append(part.dimension.y - 0.5 + ",");
	        	fw.append(part.dimension.z - 0.5 + ",");
	        	fw.append(part.position.x + ",");
	        	fw.append(part.position.y + ",");
	        	fw.append(part.position.z + ",");
	        	fw.append(b.id + ",");
	        	fw.append(part.weight + "\n");
			}
		}
        fw.flush();
        fw.close();
	}
	
	public String toString(){
		return currStep.toString() + " : " + currVoid.toString() + " : " + currCompletion.toString() + "\n"+ boxes.toString()+ "\n";
	}
	
	public int compareTo(VoidSpace v) {
		Integer lessThan = 1;
		Integer greaterThan = -1;
		if(v.currStep < currStep)
			return greaterThan;
		else if(v.currStep > currStep)
			return lessThan;
		else{
			if(v.currVoid < currVoid)
				return lessThan;
			else if(v.currVoid > currVoid)
				return greaterThan;
			else
				return 0;
		}	
		
	}
}
