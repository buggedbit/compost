package packer;
import java.io.*;
import java.util.*;

public class FinalAlgortihmBaseClass {
	public Box b;
	public Stack<Surface> s;
	public HashMap<Vector, Vector> unused;
	
	public String toString(){
		return b.toString() + "\n" + s.toString();
	}
	
	public void setBox(Box bb){
		b.dimension.x = bb.dimension.x;
		b.dimension.y = bb.dimension.y;
		b.dimension.z = bb.dimension.z;
		b.id =bb.id;
		b.num = bb.num;
		s.clear();
		b.parts.clear();
		unused.clear();
	}
	@SuppressWarnings("serial")
	public FinalAlgortihmBaseClass(Box b) {
		this.b = b;
		this.s = new Stack<>();
		this.unused = new HashMap<Vector,Vector>(){
			@Override
			public String toString() {
				String s = "{" + unused.size() + "}\n";
				for(Vector key : unused.keySet()){
					s+= "LUB:" + key.toString() + "  " + "Sur:" + unused.get(key).toString() + "\n";
				}
				return s;
			}
		};
	}
	
	public HashMap<Vector, Vector> combineUnused(){
		HashMap<Vector, Vector>  newUnused = new HashMap<>();
		HashMap<Vector, ArrayList<Vector>> countY = new HashMap<>();
		for (Vector key : unused.keySet()) {
			Vector tmp = new Vector(key.x, key.y, 0);
			if(!countY.containsKey(tmp))
				countY.put(tmp, new ArrayList<Vector>());
			countY.get(tmp).add(key);
		}
		for (Vector key: countY.keySet()) {
			ArrayList<Vector> tmpList = countY.get(key); 
			if(tmpList.size() != 1){
				
				Collections.sort(tmpList, new Comparator<Vector>() {
					@Override
			        public int compare(Vector p1, Vector p2) {
						return  p1.z.compareTo(p2.z);
			        }
			    });
				Integer surfZ = unused.get(tmpList.get(0)).z;
				Integer surfX = unused.get(tmpList.get(0)).x;
				Integer k =0;
				while(k < tmpList.size() -1){
					Integer i = k;
					while(i < tmpList.size() -1 && tmpList.get(i).z + unused.get(tmpList.get(i)).z == tmpList.get(i+1).z){
						surfZ += unused.get(tmpList.get(i+1)).z;
						surfX = Math.min(surfX,unused.get(tmpList.get(i+1)).x);
						i++;
					}
					newUnused.put(tmpList.get(k), new Vector(surfX,0,surfZ));
					k = ++i;
				}
			}
		}	
		unused.clear();
		return newUnused;
	}
	
	public Float calcAcc(){
		return (float)(b.getPartsVol()*100.0/b.getVol());
	}
	public Float prev_calcAcc(Order ord){
		return (float)(ord.getVol() * 100.0/b.getVol());
	}
	
	public void publish(String s) throws IOException{
		File f = new File(s);
		if(!f.exists()) { 
			f.delete();
		}
		FileWriter fw = new FileWriter(s);
		fw.append("PartID,DimX,DimY,DimZ,PosX,PosY,PosZ,BoxID,Weight\n");
		for(Part part: b.parts){
        	fw.append(part.id + ",");
        	fw.append(part.dimension.x - 0.5 + ",");
        	fw.append(part.dimension.y - 0.5 + ",");
        	fw.append(part.dimension.z - 0.5 + ",");
        	fw.append(part.position.x + ",");
        	fw.append(part.position.y + ",");
        	fw.append(part.position.z + ",");
        	fw.append(b.id + "-" + b.num + ",");
        	fw.append(part.weight + "\n");
        }
        fw.flush();
        fw.close();
	}

	public Order MainAlgo(Order new_order) {
	 return null;
	}
}
