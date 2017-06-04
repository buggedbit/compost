package packer;
import java.io.*;
import java.util.*;

public class FillMultipleBoxes {
	ArrayList<Box> boxes;
	PriorityQueue<VoidSpace> heap;
	HashMap<String, Integer> boxUsage;
	public void updateBoxStats(VoidSpace vs) {
		for (Box box : vs.boxes) {
			String name = box.id.split("-")[0];
			if(!boxUsage.containsKey(name))
				boxUsage.put(name, 1);
			else
				boxUsage.put(name, boxUsage.get(name) + 1);
		}
	}
	
	@SuppressWarnings("serial")
	public FillMultipleBoxes(ArrayList<Box> b){
		boxes = b;
		heap = new PriorityQueue<>(new Comparator<VoidSpace>() {
			@Override
			public int compare(VoidSpace o1, VoidSpace o2) {
				return o1.compareTo(o2); 
			}
		});
		boxUsage = new HashMap<String, Integer>(){
			@Override
			public String toString() {
				String s = "{" + boxUsage.size() + "}\n";
				for(String name : boxUsage.keySet()){
					s+= name + " : " + boxUsage.get(name).toString() + "\n";
				}
				return s;
			}
		};
	}
	public void publish() throws IOException{
		FileWriter fw = new FileWriter("Boxes.csv");
		fw.append("BoxID,BoxX,BoxY,BoxZ,Cost\n");
		for(Box box: boxes){
        	fw.append(box.id + ",");
        	fw.append(box.dimension.x + ",");
        	fw.append(box.dimension.y + ",");
        	fw.append(box.dimension.z + ",");
        	fw.append("0\n");
        }
        fw.flush();
        fw.close();
	}
	public String toString(){
		return heap.toString();
	}
	
	public void volSort(){		Collections.sort(boxes, new Comparator<Box>() {
			@Override
	        public int compare(Box b1, Box b2) {
				return  b1.volCompareTo(b2);
	        }
	    });
	}
	void calcBoxNumbers(ArrayList<Box> filledBoxes){
		for (Box b: boxes) {
			Integer maxIndex = 0;
			for (Box box : filledBoxes) {
				String name[] = box.id.split("-");
				if(name[0].equals(b.id) && Integer.valueOf(name[1]) > maxIndex)
					maxIndex = Integer.valueOf(name[1]);
			}
			b.num = maxIndex+1;
		}	
	}	
	public VoidSpace fill(VoidSpace v,Integer volInitalItems) throws IOException{
     	calcBoxNumbers(v.boxes);
		volSort();

		while(v.remaining.numOfItems() != 0){			
			FinalAlgortihmBaseClass tmp;
			if(v.remaining.numOfItems() <= 6)
				tmp = new FinalAlgorithmBelow6Items(new Box(0, 0, 0,"",1));
			else
				tmp = new FinalAlgorithmAbove6Items(new Box(0, 0, 0,"",1));
			
			Integer thisStepVoid = boxes.get(0).getVol(), leastVoidSpaceBoxLoc = 0;
			Double thisStepCompletion = 0.;
//			System.err.println( "XXXXXXX");
			for (int i = 0; i < boxes.size(); i++) {
				
				Order copy = v.remaining.copy();
				tmp.setBox(new Box(boxes.get(i).dimension.x, boxes.get(i).dimension.y, boxes.get(i).dimension.z, boxes.get(i).id, boxes.get(i).num));
				copy = tmp.MainAlgo(copy);
				
				Integer tmpVoid = tmp.b.getVol() - tmp.b.getPartsVol();
				Double tmpCompletion = tmp.b.getPartsVol()*100.0/volInitalItems;
				
				if(thisStepVoid > tmpVoid && tmp.b.getPartsVol() > 0){
					thisStepVoid = tmpVoid; thisStepCompletion = tmpCompletion;
					leastVoidSpaceBoxLoc = i;
				}
				if(tmp.b.getPartsVol() > 0){
					Boolean shouldBeAdded = true;
					for (VoidSpace vs : heap) {
						if(tmpVoid + v.currVoid >= vs.currVoid && v.currCompletion + tmpCompletion <= vs.currCompletion){
							shouldBeAdded = false;
							break;
						}
					}
					if(shouldBeAdded){
						VoidSpace tmp1 = new VoidSpace(v.currStep + 1, tmpVoid + v.currVoid, v.currCompletion + tmpCompletion,copy);
						tmp1.boxes = v.copyBoxes();
						tmp1.boxes.add(new Box(tmp.b.dimension.x, tmp.b.dimension.y, tmp.b.dimension.z, tmp.b.id + "-" + tmp.b.num ,0));
						tmp1.boxes.get(tmp1.boxes.size()-1).parts = tmp.b.copyParts();
						heap.add(tmp1);
					}
				}
			}
			v.currVoid += thisStepVoid;
			v.currCompletion+=thisStepCompletion;	
			v.currStep++;
			if(!heap.isEmpty() && heap.peek().currStep == v.currStep && v.currCompletion < 100)
				heap.remove();
			else{
				if(heap.isEmpty())
					System.err.println("WTF");
				while(!heap.isEmpty() && heap.peek().currStep == v.currStep)
					heap.remove();
			}
			tmp.setBox(boxes.get(leastVoidSpaceBoxLoc));
			v.boxes.add(new Box(tmp.b.dimension.x, tmp.b.dimension.y, tmp.b.dimension.z, tmp.b.id + "-" + tmp.b.num ,0));
			boxes.get(leastVoidSpaceBoxLoc).num++;
			v.remaining = tmp.MainAlgo(v.remaining);
			v.boxes.get(v.boxes.size()-1).parts = tmp.b.copyParts();
		}
		v.FinalAccuracy = volInitalItems * 100.0/(v.currVoid + volInitalItems);
		while(!heap.isEmpty()){
			VoidSpace vs = heap.remove();
			vs = fill(vs,volInitalItems);
			if(vs.FinalAccuracy > v.FinalAccuracy){
				v = vs.copyVoidSpace();	
			}
		}
		return v;
	}
	
	public static void main(String[] args) throws IOException {
//		ArrayList<Box> b = new ArrayList<>();	
//		b.add(new Box(7, 9, 24,"Box1",1));
//		b.add(new Box(25, 30, 26,"Box2",1));
//		b.add(new Box(31, 33, 31,"Box3",1));
//		b.add(new Box(38, 40, 39,"Box4",1));
//		ArrayList<Box> b = Helper.makeBoxes(5, 20, 0.2);
		
		Part p1 = new Part("Part1",36,10,36,10,1);
		Part p2 = new Part("part2",30,7,22,10,1);
		Part p3 = new Part("Part3",6,8,14,10,1);
		Part p4 = new Part("Part4",29,12,27,10,1);
		ArrayList<Part> p = new ArrayList<>();
		p.add(p1);p.add(p2);p.add(p3);p.add(p4);		
		
		ArrayList<Box> b = Helper.readBoxes("Boxes.csv");
		FillMultipleBoxes tmp = new FillMultipleBoxes(b);
//		tmp.publish();
		
		Double avgAcc = 0.;
		ArrayList<Double> lessAcc = new ArrayList<>();
		Integer count = 0;
		Integer number  = 100;
		long starttime = System.nanoTime();
		
		for (int i = 0; i < number; i++) {
			Order new_order = Helper.makeRandomOrder(6, 40, 1, 2, 4);
//			Order new_order = new Order(p);
			Order copy = new_order.copy();
			copy.publish("Order.csv");
			
			VoidSpace vs = new VoidSpace(0,0, 0.,new_order);
			vs = tmp.fill(vs, vs.remaining.getVol());
			vs.publish("Packing.csv");
			tmp.updateBoxStats(vs);
			Double acc = vs.FinalAccuracy;
			avgAcc+=acc;
			if(acc < 65){
				lessAcc.add(acc);
				vs.publish("Packing"+count+".csv");
				copy.publish("Order"+count+".csv");
				count++;
			}
			
		}
		System.out.println("TIME:" + (System.nanoTime()-starttime)/1000000000);
		System.out.println(lessAcc);
		System.out.println("AVG :" + avgAcc/number);
		System.out.println(tmp.boxUsage.toString());
	}
}
