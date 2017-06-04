package packer;

import java.util.*;
import java.io.*;

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
//			min =  (int)(min*(1 + rate));
//			rate = rate/(1 + 0.01*i);
			min= (int)(min + alpha);
//			alpha+= 0.2;
		}
		return out;
	}
	public static ArrayList<Box> readBoxes(String s) throws NumberFormatException, IOException{
		ArrayList<Box> out = new ArrayList<>();
		BufferedReader br= new BufferedReader(new FileReader(s));
		String newLine=br.readLine();
		while ((newLine = br.readLine()) != null) {
		    String[] parts=newLine.split(",");
		    out.add(new Box(Integer.valueOf(parts[1]), Integer.valueOf(parts[2]), Integer.valueOf(parts[3]), parts[0], 1));
		}
		br.close();
		return out;
	}
}
