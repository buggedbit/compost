package com.partsavatar.physicals;

import com.partsavatar.abstracts.SInequality;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Shipment {

    String id;
    String order_id;
    public Map<String, Integer> part_clone_count_map = new HashMap<>();

    double length;
    double breadth;
    double height;
    double weight;

    public Shipment(String order_id, String id, double length, double breadth, double height, double weight) {
        this.order_id = order_id;
        this.id = id;

        double hbl[] = {
                height,
                breadth,
                length
        };
        Arrays.sort(hbl);
        this.length = hbl[2];
        this.breadth = hbl[1];
        this.height = hbl[0];

        this.weight = weight;
    }

    public void addPart(String part, int quantity) {
        // Old part
        if (this.part_clone_count_map.containsKey(part)) {
            int prev_quantity = this.part_clone_count_map.get(part);
            this.part_clone_count_map.put(part, quantity + prev_quantity);
        }
        // New part
        else {
            this.part_clone_count_map.put(part, quantity);
        }
    }

    public SInequality extractSInequality() {
        SInequality sInequality = new SInequality(this.length, this.breadth, this.height, this.weight);
        for (Map.Entry<String, Integer> entry : this.part_clone_count_map.entrySet()) {
            sInequality.addTerm(entry.getValue(), entry.getKey());
        }
        return sInequality;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("length = ")
                .append(this.length)
                .append(" breadth = ")
                .append(this.breadth)
                .append(" height = ")
                .append(this.height)
                .append(" weight = ")
                .append(this.weight);
        for (Map.Entry<String, Integer> entry : this.part_clone_count_map.entrySet()) {
            sb.append("\n").append(entry.getKey()).append(" -> ").append(entry.getValue());
        }
        sb.append("\n");
        return sb.toString();
    }

}
