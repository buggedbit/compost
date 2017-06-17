package com.partsavatar.packer.components;

import lombok.Data;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.List;


public @Data
class WarehouseOrder {
    @NonNull
    List<Part> orderList;

    public WarehouseOrder copy() {
        List<Part> pList = new ArrayList<Part>();
        for (Part p : orderList) {
            pList.add(new Part(p.id, p.dimension, p.weight, p.quantity));
        }
        return new WarehouseOrder(pList);
    }

    public void volSort() {
        orderList.sort(Part::volCompareTo);
    }

    public Integer numOfItems() {
        Integer num = 0;
        for (Part part : orderList) {
            num += part.quantity;
        }
        return num;
    }

    public Integer getVol() {
        Integer vol = 0;
        for (Part p : orderList) {
            vol += p.getVol() * p.quantity;
        }
        return vol;
    }

    public Integer getMinPartSize() {
        Integer min = 10000;
        for (Part part : orderList) {
            Integer minDim = Math.min(part.dimension.getX(), Math.min(part.dimension.getY(), part.dimension.getZ()));
            min = Math.min(min, minDim);
        }
        return min;
    }
}


