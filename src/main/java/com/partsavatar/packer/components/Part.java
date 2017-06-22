package com.partsavatar.packer.components;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.ToString;

@Data
@ToString(exclude= {"weight"})
@EqualsAndHashCode(exclude="dimension")
public class Part {
    @NonNull
    private String id;
    @NonNull
    private Vector3D dimension;
    @NonNull
    private Double weight;

    Integer getVol() {
        return dimension.getX() * dimension.getY() * dimension.getZ();
    }

    public int volCompareTo(final Part p) {
        Integer lessThan = 1;
        Integer greaterThan = -1;

        if (this.getVol() < p.getVol())
            return lessThan;
        else if (this.getVol() > p.getVol())
            return greaterThan;
        else
            return 0;
    }

    public Part copy() {
        Part p = new Part(id, dimension, weight);
        return p;
    }
}
