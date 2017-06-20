package com.partsavatar.packer.dao.packing;

import com.partsavatar.packer.algorithms.FillMultipleBoxes;
import com.partsavatar.packer.components.Box;
import com.partsavatar.packer.components.Part;
import com.partsavatar.packer.components.Vector3D;
import com.partsavatar.packer.components.WarehouseOrder;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class PackingDAOImpl implements PackingDAO {

    @Override
    public List<Box> getPacking(List<Box> availableBoxes, WarehouseOrder warehouseOrder) {
        ArrayList<Box> boxes = (ArrayList<Box>) availableBoxes;
        return FillMultipleBoxes.packOrder(boxes, warehouseOrder);
    }

    @Override
    public List<Box> getAvailableBoxes() throws NumberFormatException, IOException {
        ArrayList<Box> boxes = new ArrayList<Box>();
        BufferedReader br = new BufferedReader(new FileReader("Boxes.csv"));
        String newLine = br.readLine();
        while ((newLine = br.readLine()) != null) {
            String[] parts = newLine.split(",");
            Box box = new Box(new Vector3D(Integer.valueOf(parts[1]), Integer.valueOf(parts[2]), Integer.valueOf(parts[3])), parts[0]);
            box.setNum(1);
            box.setParts(new ArrayList<>());
            boxes.add(box);
        }
        br.close();
        return boxes;
    }

    @Override
    public WarehouseOrder getNewOrder() {
        /**(Temporary) Fixed Manual CustomerOrder*/
        Part p1 = new Part("Part1", new Vector3D(7, 7, 2), 10, 1);
        Part p2 = new Part("part2", new Vector3D(12, 7, 6), 10, 1);
        Part p3 = new Part("Part3", new Vector3D(9, 8, 4), 10, 3);
        Part p4 = new Part("Part4", new Vector3D(6, 6, 2), 10, 2);
        ArrayList<Part> p = new ArrayList<>();
        p.add(p1);
        p.add(p2);
        p.add(p3);
        p.add(p4);

        return new WarehouseOrder(p);
    }

    @Override
    public void storePacking(List<Box> filledBoxes) {
        try {
            FileWriter fw = new FileWriter("Packing.csv");
            fw.append("PartID,DimX,DimY,DimZ,PosX,PosY,PosZ,BoxID,Weight\n");
            for (Box b : filledBoxes) {
                for (Part part : b.getParts()) {
                    fw.append(part.getId()).append(",");
                    fw.append(String.valueOf(part.getDimension().getX() - 0.5)).append(",");
                    fw.append(String.valueOf(part.getDimension().getY() - 0.5)).append(",");
                    fw.append(String.valueOf(part.getDimension().getZ() - 0.5)).append(",");
                    fw.append(String.valueOf(part.getPosition().getX())).append(",");
                    fw.append(String.valueOf(part.getPosition().getY())).append(",");
                    fw.append(String.valueOf(part.getPosition().getZ())).append(",");
                    fw.append(b.getId()).append("-").append(String.valueOf(b.getNum())).append(",");
                    fw.append(String.valueOf(part.getWeight())).append("\n");
                }
            }
            fw.flush();
            fw.close();
        } catch (IOException e) {
            System.err.println("Unable to read/write in the file Packing.csv");
        }
    }
}
