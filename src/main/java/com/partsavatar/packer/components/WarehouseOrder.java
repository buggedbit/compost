package com.partsavatar.packer.components;

import lombok.Data;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class WarehouseOrder {
    @NonNull
    private Map<Part, Integer> orderMap;

    public WarehouseOrder copy() {
    	Map<Part, Integer> pList = new HashMap<>();
        for (Part p : orderMap.keySet()) {
            pList.put(new Part(p.getId(), p.getDimension(), p.getWeight()), orderMap.get(p));
        }
        return new WarehouseOrder(pList);
    }

    public List<Part> volSort() {
        List<Part> list = new ArrayList<>(orderMap.keySet());
        list.sort(Part::volCompareTo);
        return list;
    }

    public Integer numOfItems() {
        Integer num = 0;
        for (Part part : orderMap.keySet()) {
            num += orderMap.get(part);
        }
        return num;
    }

    public Integer getVol() {
        Integer vol = 0;
        for (Part p : orderMap.keySet()) {
            vol += p.getVol() * orderMap.get(p);
        }
        return vol;
    }

    public Integer getMinPartSize() {
        Integer min = 10000;
        for (Part part : orderMap.keySet()) {
            Integer minDim = Math.min(part.getDimension().getX(), Math.min(part.getDimension().getY(), part.getDimension().getZ()));
            min = Math.min(min, minDim);
        }
        return min;
    }
}


