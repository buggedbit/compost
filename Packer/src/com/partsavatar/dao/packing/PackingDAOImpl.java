package com.partsavatar.dao.packing;

import java.io.*;
import java.util.*;

import algorithms.FillMultipleBoxes;
import components.Box;
import components.Order;
import components.Part;
import components.Vector;

public class PackingDAOImpl implements PackingDAO {
	
	@Override
	public List<Box> getPacking(List<Box> availableBoxes, Order order) {
		ArrayList<Box> boxes = (ArrayList<Box>) availableBoxes; 
		return FillMultipleBoxes.pack(boxes, order);
	}

	@Override
	public List<Box> getAvailableBoxes() throws NumberFormatException, IOException {
		ArrayList<Box> boxes = new ArrayList<Box>();
		BufferedReader br= new BufferedReader(new FileReader("Boxes.csv"));
		String newLine=br.readLine();
		while ((newLine = br.readLine()) != null) {
		    String[] parts=newLine.split(",");
		    Box box =new Box(new Vector(Integer.valueOf(parts[1]), Integer.valueOf(parts[2]), Integer.valueOf(parts[3])), parts[0]);
		    box.setNum(1);
		    box.setParts(new ArrayList<>());
		    boxes.add(box);
		}
		br.close();
		return boxes;
	}

	@Override
	public Order getNewOrder() {
		/**(Temporary) Fixed Manual Order*/
		Part p1 = new Part("Part1",new Vector(7,7,2),10,1);
		Part p2 = new Part("part2",new Vector(12,7,6),10,1);
		Part p3 = new Part("Part3",new Vector(9,8,4),10,3);
		Part p4 = new Part("Part4",new Vector(6,6,2),10,2);
		ArrayList<Part> p = new ArrayList<>();
		p.add(p1);p.add(p2);p.add(p3);p.add(p4);
		
		return new Order(p);
	}

	@Override
	public void storePacking(List<Box> filledBoxes) {
		try {
			FileWriter fw = new FileWriter("Packing.csv");
			fw.append("PartID,DimX,DimY,DimZ,PosX,PosY,PosZ,BoxID,Weight\n");
			for(Box b: filledBoxes){
				for(Part part: b.getParts()){
		        	fw.append(part.getId() + ",");
		        	fw.append(part.getDimension().getX() - 0.5 + ",");
		        	fw.append(part.getDimension().getY() - 0.5 + ",");
		        	fw.append(part.getDimension().getZ() - 0.5 + ",");
		        	fw.append(part.getPosition().getX() + ",");
		        	fw.append(part.getPosition().getY() + ",");
		        	fw.append(part.getPosition().getZ() + ",");
		        	fw.append(b.getId() +"-"+b.getNum() + ",");
		        	fw.append(part.getWeight()+ "\n");
				}
			}
	        fw.flush();
	        fw.close();
		} catch (IOException e) {
			System.err.println("Unable to read/write in the file Packing.csv");
		}
	}
}
