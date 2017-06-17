package com.partsavatar.packer.components;

import lombok.Data;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public @Data
class Vector {
    @NonNull
    Integer x, y, z;

    private ArrayList<Direction> sort() {
        Integer max = x, middle = y, min = z;

        ArrayList<Direction> tmp = new ArrayList<Direction>();
        tmp.add(new Direction(max, 'x'));
        tmp.add(new Direction(middle, 'y'));
        tmp.add(new Direction(min, 'z'));

        Collections.sort(tmp, (Direction d1, Direction d2) -> d1.compareTo(d2));
        return tmp;
    }

    public boolean rotateAndCheckIsEqualOrGreater(Part p) {
        ArrayList<Direction> tmpPart = p.dimension.sort();
        ArrayList<Direction> tmpBox = this.sort();

        Integer dimX = p.dimension.getX();
        Integer dimY = p.dimension.getY();
        Integer dimZ = p.dimension.getZ();

        if (tmpBox.get(0).val >= tmpPart.get(0).val && tmpBox.get(1).val >= tmpPart.get(1).val && tmpBox.get(2).val >= tmpPart.get(2).val) {

            if (tmpBox.get(0).c.equals('x'))
                dimX = tmpPart.get(0).val;
            else if (tmpBox.get(0).c.equals('y'))
                dimY = tmpPart.get(0).val;
            else if (tmpBox.get(0).c.equals('z'))
                dimZ = tmpPart.get(0).val;

            if (tmpBox.get(1).c.equals('x'))
                dimX = tmpPart.get(1).val;
            else if (tmpBox.get(1).c.equals('y'))
                dimY = tmpPart.get(1).val;
            else if (tmpBox.get(1).c.equals('z'))
                dimZ = tmpPart.get(1).val;

            if (tmpBox.get(2).c.equals('x'))
                dimX = tmpPart.get(2).val;
            else if (tmpBox.get(2).c.equals('y'))
                dimY = tmpPart.get(2).val;
            else if (tmpBox.get(2).c.equals('z'))
                dimZ = tmpPart.get(2).val;

            p.dimension = new Vector(dimX, dimY, dimZ);

            return true;
        }
        if (x >= dimX && y >= dimY && z >= dimZ)
            return true;
        else
            return false;
    }

    public boolean checkIsEqualOrGreater(Part p) {
        Integer dimX = p.dimension.getX();
        Integer dimY = p.dimension.getY();
        Integer dimZ = p.dimension.getZ();

        if (x >= dimX && y >= dimY && z >= dimZ)
            return true;
        else
            return false;
    }

    private static Integer getContribution(Integer box, Integer part, Integer minPart) {
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

    private static Integer calculateCost(ArrayList<Direction> inp, Integer minPart) {
        if (inp.get(0).val >= inp.get(3).val && inp.get(1).val >= inp.get(4).val && inp.get(2).val >= inp.get(5).val)
            return getContribution(inp.get(0).val, inp.get(3).val, minPart) +
                    getContribution(inp.get(1).val, inp.get(4).val, minPart) +
                    getContribution(inp.get(2).val, inp.get(5).val, minPart);
        return -1;
    }

    private ArrayList<Direction> getBestOrientation(Integer minPart, Part p) {
        ArrayList<Direction> tmpPart = p.dimension.sort();
        ArrayList<Direction> tmpBox = this.sort();
        ArrayList<Direction> out = null;
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

    public boolean bestRotateAndCheckIsEqualOrGreater(Integer minPart, Part p) {
        ArrayList<Direction> req = getBestOrientation(minPart, p);
        if (req == null)
            return false;

        Integer dimX = p.dimension.getX();
        Integer dimY = p.dimension.getY();
        Integer dimZ = p.dimension.getZ();

        if (req.get(0).c.equals('x'))
            dimX = req.get(3).val;
        else if (req.get(0).c.equals('y'))
            dimY = req.get(3).val;
        else if (req.get(0).c.equals('z'))
            dimZ = req.get(3).val;

        if (req.get(1).c.equals('x'))
            dimX = req.get(4).val;
        else if (req.get(1).c.equals('y'))
            dimY = req.get(4).val;
        else if (req.get(1).c.equals('z'))
            dimZ = req.get(4).val;

        if (req.get(2).c.equals('x'))
            dimX = req.get(5).val;
        else if (req.get(2).c.equals('y'))
            dimY = req.get(5).val;
        else if (req.get(2).c.equals('z'))
            dimZ = req.get(5).val;

        p.dimension = new Vector(dimX, dimY, dimZ);

        return true;
    }
}
