package com.partsavatar.packer.algorithms;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.partsavatar.packer.components.Part;
import com.partsavatar.packer.components.Vector3D;
import com.partsavatar.packer.components.Vector3D.Direction;

public class RotatingAlgorithm {
	
	private Integer getContribution(final Integer box, final Integer part, final Integer minPart) {
        // Heuristic values for cost of part so as to determine its best rotation
    	Integer cost = 0;
        if (box - part > minPart && box - part < 2 * minPart)
            cost += 25;
        else if (box - part > 2 * minPart && box - part < 5 * minPart)
            cost += 35;
        else if (box - part > 5 * minPart)
            cost += 50;
        else if (box - part == 0)
            cost += 100;
        return cost;
    }

    private Integer calculateCost(final List<Direction> inp, final Integer minPart) {
        if (inp.get(0).getVal() >= inp.get(3).getVal() && inp.get(1).getVal() >= inp.get(4).getVal() && inp.get(2).getVal() >= inp.get(5).getVal())
            return getContribution(inp.get(0).getVal(), inp.get(3).getVal(), minPart) +
                    getContribution(inp.get(1).getVal(), inp.get(4).getVal(), minPart) +
                    getContribution(inp.get(2).getVal(), inp.get(5).getVal(), minPart);
        return -1;
    }

    private List<Direction> getBestOrientation(final Vector3D boxDimfinal, final Integer minPart, final Part p) {
        List<Direction> tmpPart = p.getDimension().sort();
        List<Direction> tmpBox = boxDimfinal.sort();
        List<Direction> out = null;
        Integer maxCost = -1;
        for (int i = 0; i < 3; i++) {
            ArrayList<Direction> tmp1 = new ArrayList<Direction>();
            tmp1.addAll(Arrays.asList(tmpBox.get(0), tmpBox.get(1), tmpBox.get(2), tmpPart.get(i), tmpPart.get((i + 1) % 3), tmpPart.get((i + 2) % 3)));
            Integer tmpCost = calculateCost(tmp1, minPart);
            if (tmpCost > maxCost) {
                maxCost = tmpCost;
                out = tmp1;
            }
            ArrayList<Direction> tmp2 = new ArrayList<Direction>();
            tmp2.addAll(Arrays.asList(tmpBox.get(0), tmpBox.get(1), tmpBox.get(2), tmpPart.get(i), tmpPart.get((i + 2) % 3), tmpPart.get((i + 1) % 3)));
            tmpCost = calculateCost(tmp2, minPart);
            if (tmpCost > maxCost) {
                maxCost = tmpCost;
                out = tmp2;
            }
        }
        return out;
    }

	boolean rotateAndCheckIsEqualOrGreater(final Vector3D boxDim, Part p) {
        //Place longest part dimension along longest box dimension.  
    	List<Direction> tmpPart = p.getDimension().sort();
        List<Direction> tmpBox = boxDim.sort();

        Integer dimX = p.getDimension().getX();
        Integer dimY = p.getDimension().getY();
        Integer dimZ = p.getDimension().getZ();

        if (tmpBox.get(0).getVal() >= tmpPart.get(0).getVal() && 
        	tmpBox.get(1).getVal() >= tmpPart.get(1).getVal() && 
        	tmpBox.get(2).getVal() >= tmpPart.get(2).getVal()) {

            if (tmpBox.get(0).getC().equals('x'))
                dimX = tmpPart.get(0).getVal();
            else if (tmpBox.get(0).getC().equals('y'))
                dimY = tmpPart.get(0).getVal();
            else if (tmpBox.get(0).getC().equals('z'))
                dimZ = tmpPart.get(0).getVal();

            if (tmpBox.get(1).getC().equals('x'))
                dimX = tmpPart.get(1).getVal();
            else if (tmpBox.get(1).getC().equals('y'))
                dimY = tmpPart.get(1).getVal();
            else if (tmpBox.get(1).getC().equals('z'))
                dimZ = tmpPart.get(1).getVal();

            if (tmpBox.get(2).getC().equals('x'))
                dimX = tmpPart.get(2).getVal();
            else if (tmpBox.get(2).getC().equals('y'))
                dimY = tmpPart.get(2).getVal();
            else if (tmpBox.get(2).getC().equals('z'))
                dimZ = tmpPart.get(2).getVal();

            p.setDimension(new Vector3D(dimX, dimY, dimZ));

            return true;
        }
        if (boxDim.getX() >= dimX && boxDim.getY() >= dimY && boxDim.getZ() >= dimZ)
            return true;
        else
            return false;
    }

    boolean checkIsEqualOrGreater(final Vector3D boxDim, final Part p) {
    	// Check if part fits in box without rotating the part
        Integer dimX = p.getDimension().getX();
        Integer dimY = p.getDimension().getY();
        Integer dimZ = p.getDimension().getZ();

        if (boxDim.getX() >= dimX && boxDim.getY() >= dimY && boxDim.getZ() >= dimZ)
            return true;
        else
            return false;
    }
    
    boolean bestRotateAndCheckIsEqualOrGreater(final Vector3D boxDimfinal, final Integer minPart, Part p) {
    	List<Direction> req = getBestOrientation(boxDimfinal, minPart, p);
        if (req == null)
            return false;

        Integer dimX = p.getDimension().getX();
        Integer dimY = p.getDimension().getY();
        Integer dimZ = p.getDimension().getZ();

        if (req.get(0).getC().equals('x'))
            dimX = req.get(3).getVal();
        else if (req.get(0).getC().equals('y'))
            dimY = req.get(3).getVal();
        else if (req.get(0).getC().equals('z'))
            dimZ = req.get(3).getVal();

        if (req.get(1).getC().equals('x'))
            dimX = req.get(4).getVal();
        else if (req.get(1).getC().equals('y'))
            dimY = req.get(4).getVal();
        else if (req.get(1).getC().equals('z'))
            dimZ = req.get(4).getVal();

        if (req.get(2).getC().equals('x'))
            dimX = req.get(5).getVal();
        else if (req.get(2).getC().equals('y'))
            dimY = req.get(5).getVal();
        else if (req.get(2).getC().equals('z'))
            dimZ = req.get(5).getVal();

        p.setDimension(new Vector3D(dimX, dimY, dimZ));

        return true;
    }
    
}
