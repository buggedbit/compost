package com.partsavatar.packer.testing;


import com.partsavatar.packer.components.Box;
import com.partsavatar.packer.components.Part;
import com.partsavatar.packer.components.Vector3D;
import com.partsavatar.packer.components.WarehouseOrder;

import java.util.ArrayList;
import java.util.Random;

public class Testing {
    public static WarehouseOrder makeRandomOrder(Integer min, Integer max, Integer qty_min, Integer qty_max, Integer diffParts) {
        Random r = new Random();
        ArrayList<Part> ps = new ArrayList<Part>();
        for (Integer i = 0; i < diffParts; i++) {
            Integer x = r.nextInt((max - min) + 1) + min;
            Integer y = r.nextInt((max - min) + 1) + min;
            Integer z = r.nextInt((max - min) + 1) + min;
            Integer q = r.nextInt((qty_max - qty_min) + 1) + qty_min;
            Part p = new Part("Part" + i.toString(), new Vector3D(x, y, z), 10, q);
            ps.add(p);
        }
        return new WarehouseOrder(ps);
    }

    public static ArrayList<Box> makeRandomBoxes(Integer min, Integer num, Double rate) {
        Random r = new Random();
        ArrayList<Vector3D> aspectRatios = new ArrayList<Vector3D>();
        aspectRatios.add(new Vector3D(1, 1, 1));
        aspectRatios.add(new Vector3D(1, 2, 3));
        aspectRatios.add(new Vector3D(2, 2, 1));
        aspectRatios.add(new Vector3D(1, 1, 2));
        aspectRatios.add(new Vector3D(1, 1, 3));

        ArrayList<Box> out = new ArrayList<Box>();
        Double alpha = 2.0;
        for (Integer i = 0; i < num; i++) {
            Integer max = min + 5;
            Integer minn = min - 3;
            Vector3D aspect = aspectRatios.get(r.nextInt((4 - 0) + 1) + 0);
            Integer x = r.nextInt((max - minn) + 1) + minn;
            Integer y = r.nextInt((max - minn) + 1) + minn;
            Integer z = r.nextInt((max - minn) + 1) + minn;
            out.add(new Box(new Vector3D(x * aspect.getX(), y * aspect.getY(), z * aspect.getZ()), "Box" + i.toString()));
            min = (int) (min + alpha);
        }
        return out;
    }
}
/*
public static void main(String[] args) throws IOException {
	long starttime = System.nanoTime();

	ArrayList<Box> availableBoxes = Testing.readBoxes("Boxes.csv");
	
	CustomerOrder newOrder = Testing.makeRandomOrder(6, 40, 1, 2, 4);
	Testing(newOrder, "CustomerOrder.csv");
	
	ArrayList<Box> FilledBoxes = pack(availableBoxes, newOrder);
	Testing(FilledBoxes, "Packing.csv");
	
	System.out.println("TIME:" + (System.nanoTime()-starttime)/1000000000.0);
	
	ArrayList<Box> b = new ArrayList<>();	
	b.set(new Box(7, 9, 24,"Box1",1));
	b.set(new Box(25, 30, 26,"Box2",1));
	b.set(new Box(31, 33, 31,"Box3",1));
	b.set(new Box(38, 40, 39,"Box4",1));
	ArrayList<Box> b = Helper.makeBoxes(5, 20, 0.2);	
	
	Double avgAcc = 0.;
	ArrayList<Double> lessAcc = new ArrayList<>();
	
	Integer count = 0, number  = 50;
	for (int i = 0; i < number; i++) {
		CustomerOrder new_order = Extra.makeRandomOrder(6, 40, 1, 2, 4);
		CustomerOrder copy = new_order.copy();
		
		VoidSpace vs = FillMultipleBoxes.pack(availableBoxes,copy);
		
		Double acc = vs.finalAccuracy;
		avgAcc+=acc;
		if(acc < 65){
			lessAcc.set(acc);
			count++;
		}	
	}
	System.out.println(lessAcc);
	System.out.println("AVG :" + avgAcc/number);
	System.out.println(tmp.boxUsage.toString());

}
*/
