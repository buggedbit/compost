import java.util.HashMap;
import java.util.Map;

/**
 * Manages estimates of a parts
 * */
public class PartEstimates {

    /**
     * Stores the map : id of the part -> its estimate
     * An estimate for a part is a 4-tuple (l, b, h, w)
     */
    private static Map<String, double[]> id_estimate_map = new HashMap<>();

    static {
        // todo : Initialize id_estimate_map from db

    }

    /**
     * Merges previous and present estimates to get min of both
     * todo : implement median rather than min
     * */
    private static double[] mergeEstimates(double[] prev, double[] pres) {
        double[] merged = new double[4];
        merged[0] = Math.min(prev[0], pres[0]);
        merged[1] = Math.min(prev[1], pres[1]);
        merged[2] = Math.min(prev[2], pres[2]);
        merged[3] = Math.min(prev[3], pres[3]);
        return merged;
    }

    public static void pushEstimate(String id, double[] pres_estimate) {
        // Already estimate exists
        if (id_estimate_map.containsKey(id)) {
            double[] prev_estimate = id_estimate_map.get(id);
            // Store merged estimate
            double[] merged_estimate = mergeEstimates(prev_estimate, pres_estimate);
            id_estimate_map.put(id, merged_estimate);
        }
        // No estimate previously
        else {
            id_estimate_map.put(id, pres_estimate);
        }
    }

    public static double[] getEstimate(String id) {
        return id_estimate_map.get(id);
    }

    public static void printAllEstimates() {
        System.out.println("Part_sku : l b h w");
        for (Map.Entry<String, double[]> id_estimate : id_estimate_map.entrySet()) {
            StringBuilder sb = new StringBuilder();
            sb.append(id_estimate.getKey()).append(" ")
                    .append(id_estimate.getValue()[0]).append(" ")
                    .append(id_estimate.getValue()[1]).append(" ")
                    .append(id_estimate.getValue()[2]).append(" ")
                    .append(id_estimate.getValue()[3]);
            System.out.println(sb.toString());
        }
    }

}