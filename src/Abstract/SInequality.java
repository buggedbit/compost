package Abstract;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.*;

public class SInequality {
    private Map<String, Integer> terms = new HashMap<>();
    private double[] upper_limits = new double[4];

    /**
     * Assert params > 0
     */
    public SInequality(double length, double breadth, double height, double weight) {
        double hbl[] = {
                height,
                breadth,
                length
        };
        Arrays.sort(hbl);

        this.upper_limits[0] = hbl[2];
        this.upper_limits[1] = hbl[1];
        this.upper_limits[2] = hbl[0];

        this.upper_limits[3] = weight;
    }

    public SInequality(BufferedReader br) throws IOException {
        // Number of terms
        int no_of_terms = Integer.parseInt(br.readLine());
        // Adding each term
        String variable;
        int co_efficient;
        for (int i = 0; i < no_of_terms; i++) {
            variable = br.readLine();
            co_efficient = Integer.parseInt(br.readLine());
            this.terms.put(variable, co_efficient);
        }
        // Upper limits
        this.upper_limits[0] = Double.parseDouble(br.readLine());
        this.upper_limits[1] = Double.parseDouble(br.readLine());
        this.upper_limits[2] = Double.parseDouble(br.readLine());
        this.upper_limits[3] = Double.parseDouble(br.readLine());
    }

    /**
     * Assert constant > 0
     */
    public void addTerm(int constant, String variable) {
        // Old term
        if (this.terms.containsKey(variable)) {
            int prev_constant = this.terms.get(variable);
            this.terms.put(variable, constant + prev_constant);
        }
        // New term
        else {
            this.terms.put(variable, constant);
        }
    }

    public int getVariableCount() {
        return this.terms.size();
    }

    public int getCardinality() {
        return this.getVariableCount();
    }

    public Set<String> getVariableSet() {
        Set<String> variableSet = new HashSet<>();
        for (Map.Entry<String, Integer> entry : this.terms.entrySet()) {
            variableSet.add(entry.getKey());
        }
        return variableSet;
    }

    public Set<String> getSignature() {
        return this.getVariableSet();
    }

    public double[] getCoefficientRow() {
        // Sort the terms according to variable
        Map<String, Integer> sorted_terms = new TreeMap<>(this.terms);

        // Pick the coefficients
        double[] coefficient_row = new double[this.terms.size()];
        int i = 0;
        for (Map.Entry<String, Integer> sorted_term : sorted_terms.entrySet()) {
            coefficient_row[i] = (double) sorted_term.getValue();
            i++;
        }
        return coefficient_row;
    }

    public Vector<String> getVariableRow() {
        // Sort the terms according to variable
        Map<String, Integer> sorted_terms = new TreeMap<>(this.terms);

        // Pick the variables
        Vector<String> variable_row = new Vector<>();
        for (Map.Entry<String, Integer> sorted_term : sorted_terms.entrySet()) {
            variable_row.add(sorted_term.getKey());
        }
        return variable_row;
    }

    public double[] getConstantRow() {
        return this.upper_limits;
    }

    public String format() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.terms.size()).append('\n');
        for (Map.Entry<String, Integer> term : terms.entrySet()) {
            sb.append(term.getKey()).append('\n').append(term.getValue()).append('\n');
        }
        for (double upper_limit : this.upper_limits) {
            sb.append(upper_limit).append('\n');
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, Integer> entry : this.terms.entrySet()) {
            sb.append(entry.getValue()).append("*").append(entry.getKey()).append(" + ");
        }
        sb.append("<=");
        for (int i = 0; i < this.upper_limits.length; ++i) {
            sb.append(" UL").append(i + 1).append("=").append(this.upper_limits[i]);
        }
        return sb.toString();
    }

}









