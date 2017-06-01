package packer;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class FillMultipleBoxes {
	ArrayList<Box> boxes;
	FillMultipleBoxes(ArrayList<Box> b){
		boxes = b;
	}
	
	void volSort(){
		Collections.sort(boxes, new Comparator<Box>() {
			@Override
	        public int compare(Box b1, Box b2) {
				return  b1.volCompareTo(b2);
	        }
	    });
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
	
	public void fill(Order ord) throws IOException{
		volSort();
		Float totalVoid = (float) 0;
		Integer volInitalItems = ord.getVol();
		while(!ord.isDone()){
			FinalAlgortihmBaseClass tmp;
			if(ord.numOfItems() <= 6)
				tmp = new FinalAlgorithmBelow6Items(new Box(0, 0, 0,"",1));
			else
				tmp = new FinalAlgorithmAbove6Items(new Box(0, 0, 0,"",1));
			System.out.println("XX");
			Float minVoid = (float)boxes.get(0).getVol();
			Integer mostAccBoxLoc = 0;
			for (int i = 0; i < boxes.size(); i++) {
				Order copy = ord.copy();
				tmp.setBox(new Box(boxes.get(i).dimension.x, boxes.get(i).dimension.y, boxes.get(i).dimension.z,"",1));
				tmp.MainAlgo(copy);
				Float currVoid = (float)((100 - tmp.calcAcc())*boxes.get(i).getVol()/(100.0*tmp.b.getPartsVol()));
				if(currVoid < minVoid && tmp.calcAcc() > 0){
					minVoid = currVoid;
					mostAccBoxLoc = i;
				}
			}
			tmp.setBox(boxes.get(mostAccBoxLoc));
			boxes.get(mostAccBoxLoc).num++;
			ord = tmp.MainAlgo(ord);
			totalVoid+=(float)((100 - tmp.calcAcc())*boxes.get(mostAccBoxLoc).getVol()/100.0);
			tmp.publish();
		}
		Integer numUsedBoxes = 0;
		for (Box box : boxes) {
			numUsedBoxes += box.num - 1;
		}
		System.out.println("BOXES USED:" + numUsedBoxes);
		System.out.println("ACC:" + 100 * (float)(volInitalItems/(totalVoid + volInitalItems)));
	}
	
	public static Order makeRandomOrder(Integer min,Integer max,Integer qty_min,Integer qty_max, Integer diffParts){
		Random r = new Random();
		//Integer min = 4, max = 20, qty_min = 1, qty_max = 3;
		ArrayList<Part> ps = new ArrayList<>();
		for (Integer i = 0; i < diffParts; i++) {
			Integer x = r.nextInt((max - min) + 1) + min;
			Integer y = r.nextInt((max - min) + 1) + min;
			Integer z = r.nextInt((max - min) + 1) + min;
			Integer q = r.nextInt((qty_max - qty_min) + 1) + qty_min;
			Part p = new Part("Part" + i.toString(),x,y,z,10,q);
			ps.add(p);
		}
		return new Order(ps);
	}
	
	public static void main(String[] args) throws IOException {
		ArrayList<Box> b = new ArrayList<>();
		b.add(new Box(40, 20, 20,"Bo	x1",1));
		b.add(new Box(50, 30, 30,"Box2",1));
		b.add(new Box(20, 20, 40,"Box3",1));
		b.add(new Box(10, 10, 20,"Box4",1));
		b.add(new Box(100, 50, 50,"Box5",1));
		FillMultipleBoxes tmp = new FillMultipleBoxes(b);
		tmp.publish();
		
		Part p1 = new Part("Part1",24,25,25,10,2);
		Part p2 = new Part("part2",30,27,8,10,5);
		Part p3 = new Part("Part3",30,14,23,10,4);
		Part p4 = new Part("Part4",13,21,19,10,3);
		Part p5 = new Part("Part5",12,25,27,10,3);
		ArrayList<Part> p = new ArrayList<>();
		p.add(p1);p.add(p2);p.add(p3);p.add(p4);p.add(p5);
		
//		Order new_order = new Order(p);
		Order new_order = makeRandomOrder(4, 40, 1, 5, 5);
		new_order.publish();
		System.out.println(new_order);
		File f = new File("Packing.csv");
		if(f.exists())
			f.delete();
		tmp.fill(new_order);
	}
}
