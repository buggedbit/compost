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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;


public class PackingDAOImpl implements PackingDAO {
	private static final FillMultipleBoxes BOX_FILLER = new FillMultipleBoxes();
    @Override
    public List<Box> getPacking(final List<Box> availableBoxes, final WarehouseOrder warehouseOrder) {
        ArrayList<Box> boxes = (ArrayList<Box>) availableBoxes;
        return BOX_FILLER.packOrder(boxes, warehouseOrder);
    }

    @Override
    public List<Box> getAvailableBoxes() throws NumberFormatException, IOException {
        ArrayList<Box> boxes = new ArrayList<Box>();
        BufferedReader br = new BufferedReader(new FileReader("db/Boxes.csv"));
        String newLine = br.readLine();
        while ((newLine = br.readLine()) != null) {
            String[] parts = newLine.split(",");
            Box box = new Box(new Vector3D(Integer.valueOf(parts[1]), Integer.valueOf(parts[2]), Integer.valueOf(parts[3])), parts[0]);
            box.setPartPositionMap(new HashMap<>());
            boxes.add(box);
        }
        br.close();
        return boxes;
    }

    @Override
    public WarehouseOrder getNewOrder() {
        //(Temporary) Fixed Manual CustomerOrder
        Part p1 = new Part("Part1", new Vector3D(7, 7, 2), 10.);
        Part p2 = new Part("part2", new Vector3D(12, 7, 6), 10.);
        Part p3 = new Part("Part3", new Vector3D(9, 8, 4), 10.);
        Part p4 = new Part("Part4", new Vector3D(6, 6, 2), 10.);
        Map<Part, Integer> p = new HashMap<>();
        p.put(p1,1);
        p.put(p2,1);
        p.put(p3,3);
        p.put(p4,2);

        return new WarehouseOrder(p);
    }

    @Override
    public void storePacking(final List<Box> filledBoxes) {
        try {
            FileWriter fw = new FileWriter("Packing.csv");
            fw.append("PartID,DimX,DimY,DimZ,PosX,PosY,PosZ,BoxID,Weight\n");
            for (Box b : filledBoxes) {
                for (Entry<Vector3D, Part> part : b.getPartPositionMap().entrySet()) {
                    fw.append(part.getValue().getId()).append(",");
                    fw.append(String.valueOf(part.getValue().getDimension().getX() - 0.5)).append(",");
                    fw.append(String.valueOf(part.getValue().getDimension().getY() - 0.5)).append(",");
                    fw.append(String.valueOf(part.getValue().getDimension().getZ() - 0.5)).append(",");
                    fw.append(String.valueOf(part.getKey().getX())).append(",");
                    fw.append(String.valueOf(part.getKey().getY())).append(",");
                    fw.append(String.valueOf(part.getKey().getZ())).append(",");
                    fw.append(b.getId()).append(",");
                    fw.append(String.valueOf(part.getValue().getWeight())).append("\n");
                }
            }
            fw.flush();
            fw.close();
        } catch (IOException e) {
            System.err.println("Unable to read/write in the file Packing.csv");
        }
    }
}
