package com.partsavatar.packer.components;

import lombok.Data;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.List;

@Data
public class WarehouseOrder {
    @NonNull
    private List<Part> orderList;

    public WarehouseOrder copy() {
        List<Part> pList = new ArrayList<Part>();
        for (Part p : orderList) {
            pList.add(new Part(p.getId(), p.getDimension(), p.getWeight(), p.getQuantity()));
        }
        return new WarehouseOrder(pList);
    }

    public void volSort() {
        orderList.sort(Part::volCompareTo);
    }

    public Integer numOfItems() {
        Integer num = 0;
        for (Part part : orderList) {
            num += part.getQuantity();
        }
        return num;
    }

    public Integer getVol() {
        Integer vol = 0;
        for (Part p : orderList) {
            vol += p.getVol() * p.getQuantity();
        }
        return vol;
    }

    public Integer getMinPartSize() {
        Integer min = 10000;
        for (Part part : orderList) {
            Integer minDim = Math.min(part.getDimension().getX(), Math.min(part.getDimension().getY(), part.getDimension().getZ()));
            min = Math.min(min, minDim);
        }
        return min;
    }
}


