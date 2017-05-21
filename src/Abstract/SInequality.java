package Abstract;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Shipment Inequality
 * <br/>
 * constant     : integer
 * variable     : float
 * upper_limits  : float
 * <br/>
 * Term : constant * variable
 * constant > 0
 * variable > 0
 * <br/>
 * A SInequality has following props
 * 1. SUMMATION(term_i) <= upper_limit_i
 * 2. term_i      > 0 (for all i)
 * 3. upper_limit_i > 0
 * <br/>
 * There are four upper limits length, breadth, height, weight for now (can be any number > 0)
 * For each upper limit, variable for term changes but not constant
 * <br/>
 * Ex:
 * ===
 * 4*x + 5*y <= U
 * 4*xl + 5*yl <= Ul
 * 4*xb + 5*yb <= Ub
 * 4*xh + 5*yh <= Uh
 * 4*xw + 5*yw <= Uw
 */
public class SInequality {

    /**
     * Map : Variable -> Constant
     */
    Map<String, Integer> terms = new HashMap<>();
    /**
     * Upper limits
     */
    float[] upper_limits = new float[4];

    /**
     * Assert params > 0
     * Assert length >= breadth >= height
     */
    public SInequality(float length, float breadth, float height, float weight) {
        this.upper_limits[0] = length;
        this.upper_limits[1] = breadth;
        this.upper_limits[2] = height;
        this.upper_limits[3] = weight;
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

    /**
     * Returns the number of variables in the SInequality
     */
    public int getVariableCount() {
        return this.terms.size();
    }

    /**
     * Cardinality = variable count
     */
    public int getCardinality() {
        return this.getVariableCount();
    }

    /**
     * Returns set of variables in the SInequality
     */
    public Set<String> getVariableSet() {
        Set<String> variableSet = new HashSet<>();
        for (Map.Entry<String, Integer> entry : this.terms.entrySet()) {
            variableSet.add(entry.getKey());
        }
        return variableSet;
    }

    /**
     * Signature = variable set
     */
    public Set<String> getSignature() {
        return this.getVariableSet();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, Integer> entry : this.terms.entrySet()) {
            sb.append(entry.getValue()).append("*").append(entry.getKey()).append(" + ");
        }
        sb.append(">>");
        for (int i = 0; i < this.upper_limits.length; ++i) {
            sb.append(" UL").append(i + 1).append("=").append(this.upper_limits[i]);
        }
        return sb.toString();
    }

}









