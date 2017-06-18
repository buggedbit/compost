package com.partsavatar.packer.components;

import lombok.Data;
import lombok.NonNull;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

public @Data
class Box {
    @NonNull
    Vector3D dimension;
    @Setter
    List<Part> parts;
    @NonNull
    String id;
    Integer num;

    public Integer volCompareTo(Box b) {
        Integer lessThan = 1;
        Integer greaterThan = -1;

        if (this.getVol() < b.getVol())
            return lessThan;
        else if (this.getVol() > b.getVol())
            return greaterThan;
        else
            return 0;
    }

    public void addPart(Part p) {
        parts.add(p);
    }

    public Integer getVol() {
        return dimension.getX() * dimension.getY() * dimension.getZ();
    }

    public Integer getPartsVol() {
        Integer sum = 0;
        for (Part part : parts) {
            sum += part.getVol();
        }
        return sum;
    }

    public Box copy() {
        Box b = new Box(dimension, id);
        b.setParts(copyParts());
        b.num = num;
        return b;
    }

    public ArrayList<Part> copyParts() {
        if (parts == null)
            return null;
        ArrayList<Part> copy = new ArrayList<Part>();
        for (Part p : parts) {
            Part copyOfP = p.copy();
            copyOfP.position = p.position;
            copy.add(copyOfP);
        }
        return copy;
    }
}
