package Algorithms;

import java.util.*;
import Components.*;
import Components.Vector;

public class Helper {
	public static Order makeRandomOrder(Integer min,Integer max,Integer qty_min,Integer qty_max, Integer diffParts){
		Random r = new Random();
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
	public static ArrayList<Box> makeBoxes(Integer min, Integer num, Double rate){
		Random r = new Random();
		ArrayList<Vector> aspectRatios = new ArrayList<>();
		aspectRatios.add(new Vector(1, 1, 1));
		aspectRatios.add(new Vector(1, 2, 3));
		aspectRatios.add(new Vector(2, 2, 1));
		aspectRatios.add(new Vector(1, 1, 2));
		aspectRatios.add(new Vector(1, 1, 3));
		
		ArrayList<Box> out = new ArrayList<>();
		Double alpha = 2.0;
		for (Integer i = 0; i < num; i++) {
			Integer max = min + 5;
			Integer minn = min - 3;
			Vector aspect = aspectRatios.get(r.nextInt((4 - 0) + 1) + 0);
			Integer x = r.nextInt((max - minn) + 1) + minn;
			Integer y = r.nextInt((max - minn) + 1) + minn;
			Integer z = r.nextInt((max - minn) + 1) + minn;			
			out.add(new Box(x*aspect.x, y*aspect.y, z*aspect.z, "Box" + i.toString(), 1));
			min= (int)(min + alpha);
		}
		return out;
	}
}
/*
		ArrayList<Box> b = new ArrayList<>();	
		b.add(new Box(7, 9, 24,"Box1",1));
		b.add(new Box(25, 30, 26,"Box2",1));
		b.add(new Box(31, 33, 31,"Box3",1));
		b.add(new Box(38, 40, 39,"Box4",1));
		ArrayList<Box> b = Helper.makeBoxes(5, 20, 0.2);
		
		Part p1 = new Part("Part1",7,7,2,10,1);
		Part p2 = new Part("part2",12,7,6,10,1);
		Part p3 = new Part("Part3",9,8,4,10,3);
		Part p4 = new Part("Part4",6,6,2,10,2);
		ArrayList<Part> p = new ArrayList<>();
		p.add(p1);p.add(p2);p.add(p3);p.add(p4);	
		
		Double avgAcc = 0.;
		ArrayList<Double> lessAcc = new ArrayList<>();
		
		Integer count = 0, number  = 1;
		long starttime = System.nanoTime();
		for (int i = 0; i < number; i++) {
			Order new_order = Helper.makeRandomOrder(6, 40, 1, 2, 4);
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
*/