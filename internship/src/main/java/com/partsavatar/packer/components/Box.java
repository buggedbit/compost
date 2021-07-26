package com.partsavatar.packer.components;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Data
@EqualsAndHashCode(exclude="dimension")
public class Box {
    @NonNull
    private Vector3D dimension;
    @Setter
    private Map<Vector3D,Part> partPositionMap;
    @NonNull
    private String id;

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

    public void addPart(final Part p,final Vector3D position) {
    	if(partPositionMap == null)
    		partPositionMap = new HashMap<>();
		partPositionMap.put(position, p);
    }

    public Integer getVol() {
        return dimension.getX() * dimension.getY() * dimension.getZ();
    }
    public Double getWeight() {
    	Double wt = 0.;
    	for (Part part : partPositionMap.values()) {
			wt+= part.getWeight();
		}
    	return wt;
    }
    public Integer getPartsVol() {
        Integer sum = 0;
        for (Part part : partPositionMap.values()) {
            sum += part.getVol();
        }
        return sum;
    }

    public Box copy() {
        Box b = new Box(dimension, id);
        b.setPartPositionMap(copyParts());
        return b;
    }

    public Map<Vector3D,Part> copyParts() {
        if (partPositionMap == null)
            return null;
        Map<Vector3D,Part> copy = new HashMap<>();
        for (Vector3D pos : partPositionMap.keySet()) {
            Part copyOfP = partPositionMap.get(pos).copy();
            copy.put(pos,copyOfP);
        }
        return copy;
    }
}
