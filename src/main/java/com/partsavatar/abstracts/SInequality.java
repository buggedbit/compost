package com.partsavatar.abstracts;

import com.partsavatar.physicals.Shipment;
import lombok.ToString;

import java.util.*;

@ToString()
public class SInequality {
    private Map<String, Integer> terms = new HashMap<>();
    private double[] upperLimits = new double[4];

    public SInequality(final double length, final double breadth, final double height, final double weight) {
        if (length <= 0
                || breadth <= 0
                || height <= 0
                || weight <= 0) throw new IllegalArgumentException();

        double hbl[] = {
                height,
                breadth,
                length
        };
        Arrays.sort(hbl);

        this.upperLimits[0] = hbl[2];
        this.upperLimits[1] = hbl[1];
        this.upperLimits[2] = hbl[0];

        this.upperLimits[3] = weight;
    }

    public SInequality(final Shipment shipment) {
        this(shipment.getLength(), shipment.getBreadth(), shipment.getHeight(), shipment.getWeight());
        for (Map.Entry<String, Integer> skuCloneCountMap : shipment.getPartCloneCountMap().entrySet()) {
            this.addTerm(skuCloneCountMap.getValue(), skuCloneCountMap.getKey());
        }
    }

    public void addTerm(final int constant, final String variable) {
        if (constant <= 0) throw new IllegalArgumentException();

        if (this.terms.containsKey(variable)) {
            int prev_constant = this.terms.get(variable);
            this.terms.put(variable, constant + prev_constant);
        } else {
            this.terms.put(variable, constant);
        }
    }

    public int getCardinality() {
        return this.terms.size();
    }

    public Set<String> getSignature() {
        Set<String> variableSet = new HashSet<>();
        for (Map.Entry<String, Integer> entry : this.terms.entrySet()) {
            variableSet.add(entry.getKey());
        }
        return variableSet;
    }

    public double[] getCoefficientRow() {
        Map<String, Integer> sorted_terms = new TreeMap<>(this.terms);

        double[] coefficient_row = new double[this.terms.size()];
        int i = 0;
        for (Map.Entry<String, Integer> sorted_term : sorted_terms.entrySet()) {
            coefficient_row[i] = (double) sorted_term.getValue();
            i++;
        }
        return coefficient_row;
    }

    public Vector<String> getVariableRow() {
        Map<String, Integer> sorted_terms = new TreeMap<>(this.terms);

        Vector<String> variable_row = new Vector<>();
        for (Map.Entry<String, Integer> sorted_term : sorted_terms.entrySet()) {
            variable_row.add(sorted_term.getKey());
        }
        return variable_row;
    }

    public double[] getConstantRow() {
        return this.upperLimits;
    }

}









