import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Manages estimates of a parts
 */
public class PartEstimates {

    /**
     * Stores the map : id of the part -> its estimate
     * An estimate for a part is a 4-tuple (l, b, h, w)
     */
    private static Map<String, double[]> id_estimate_map = new HashMap<>();

    static {
        final String estimate_table_path = "../db/estimates/estimate_table";

        try {
            FileReader fr = new FileReader(estimate_table_path);
            BufferedReader br = new BufferedReader(fr);

            int no_of_estimates = Integer.parseInt(br.readLine());
            String id;
            String estimate;
            String[] dimensions;
            for (int i = 0; i < no_of_estimates; i++) {

                id = br.readLine();
                estimate = br.readLine();
                dimensions = estimate.split(" ");
                id_estimate_map.put(id, new double[]{
                        Double.parseDouble(dimensions[0]),
                        Double.parseDouble(dimensions[1]),
                        Double.parseDouble(dimensions[2]),
                        Double.parseDouble(dimensions[3]),
                });

            }

            br.close();
            fr.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Writes all the estimates to specified file
     * The file path and format contract happens here
     * Assert the file paths
     */
    public static void storeEstimates() throws IOException {
        final String estimate_table = "../db/estimates/estimate_table";

        // Write to file
        BufferedWriter bw = new BufferedWriter(new FileWriter(estimate_table));

        // Total number of estimates
        bw.write(id_estimate_map.size() + "\n");
        // Each estimate
        for (Map.Entry<String, double[]> id_estimate : id_estimate_map.entrySet()) {
            StringBuilder sb = new StringBuilder();
            sb.append(id_estimate.getKey()).append('\n')
                    .append(id_estimate.getValue()[0]).append(' ')
                    .append(id_estimate.getValue()[1]).append(' ')
                    .append(id_estimate.getValue()[2]).append(' ')
                    .append(id_estimate.getValue()[3]).append('\n');
            bw.write(sb.toString());
            bw.flush();
        }
    }

    /**
     * Merges previous and present estimates to get min of both
     */
    private static double[] mergeEstimates(double[] prev, double[] pres) {
        double[] merged = new double[4];
        merged[0] = Math.min(prev[0], pres[0]);
        merged[1] = Math.min(prev[1], pres[1]);
        merged[2] = Math.min(prev[2], pres[2]);
        merged[3] = Math.min(prev[3], pres[3]);
        return merged;
    }

    /**
     * Pushes the new estimate into the table of estimates
     */
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
            sb.append(id_estimate.getKey()).append(' ')
                    .append(id_estimate.getValue()[0]).append(' ')
                    .append(id_estimate.getValue()[1]).append(' ')
                    .append(id_estimate.getValue()[2]).append(' ')
                    .append(id_estimate.getValue()[3]);
            System.out.println(sb.toString());
        }
    }

}