package Abstract;

import java.util.HashMap;
import java.util.Map;

/**
 * constant     : integer
 * variable     : float
 * upper_limit  : float
 *
 * Term : constant * variable
 *      constant > 0
 *      variable > 0
 *
 * A SInequality is defined as
 *      SUMMATION(term_i) <= upper_limit
 *      term_i      > 0 (for all i)
 *      upper_limit > 0
 */
public class SInequality {

    /**
     * Map : Variable -> Constant
     * */
    Map<String, Integer> terms = new HashMap<>();
    /**
     * Upper limit
     * */
    float upper_limit;

    /**
     * Assert upper_limit > 0
     * */
    public SInequality(float upper_limit) {
        this.upper_limit = upper_limit;
    }

    /**
     * Assert constant > 0
     * */
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
     * If the SInequality has the variable
     *      Substitutes the value in variable and subtracts from upper_limit
     *      If the upper_limit becomes <= 0
     *             Does not keep the substitution
     *             Throws HugeEstimateException
     * */
    public void substitute(String variable, float value) {
        // If variable exists
        if (this.terms.containsKey(variable)) {
            int constant = this.terms.get(variable);
            float term_value = constant * value;
            // If upper_limit becomes <= 0
            if (this.upper_limit <= term_value) {
                throw new HugeEstimateException();
            }
            // Else
            else {
                this.upper_limit -= term_value;
                this.terms.remove(variable);
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, Integer> entry : this.terms.entrySet()) {
            sb.append(entry.getValue()).append("*").append(entry.getKey()).append(" + ");
        }
        sb.append("<= ").append(String.valueOf(this.upper_limit));
        return sb.toString();
    }

    public static void main(String[] args) {
        SInequality a = new SInequality(600);
        a.addTerm(100, "a");
        a.addTerm(100, "a");
        a.addTerm(100, "b");
        a.addTerm(200, "c");
        System.out.println(a);
        try {
            a.substitute("c", 3);
            System.out.println(a);
        } catch (HugeEstimateException e) {
            e.printStackTrace();
        }
    }

}
