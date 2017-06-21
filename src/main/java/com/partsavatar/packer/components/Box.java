package com.partsavatar.packer.components;

import lombok.Data;
import lombok.NonNull;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Data
public class Box {
    @NonNull
    private Vector3D dimension;
    @Setter
    private List<Part> parts;
    @NonNull
    private String id;
    private Integer num;

    public Integer volCompareTo(final Box b) {
        Integer lessThan = 1;
        Integer greaterThan = -1;

        if (this.getVol() < b.getVol())
            return lessThan;
        else if (this.getVol() > b.getVol())
            return greaterThan;
        else
            return 0;
    }

    public void addPart(final Part p) {
    	if(parts == null)
    		parts = new ArrayList<>();
        parts.add(p);
    }

    public Integer getVol() {
        return dimension.getX() * dimension.getY() * dimension.getZ();
    }
    public Double getWeight() {
    	Double wt = 0.;
    	for (Part part : parts) {
			wt+= part.getWeight();
		}
    	return wt;
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

    public List<Part> copyParts() {
        if (parts == null)
            return null;
        List<Part> copy = new ArrayList<Part>();
        for (Part p : parts) {
            Part copyOfP = p.copy();
            copyOfP.setPosition(p.getPosition());
            copy.add(copyOfP);
        }
        return copy;
    }
}
