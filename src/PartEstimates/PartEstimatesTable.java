package PartEstimates;

import PartEstimates.DAO.PartEstimateDAOImpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Manages estimates of parts
 */
public class PartEstimatesTable {

    /**
     * Map : id of the part -> its part estimate instance
     */
    private static Map<String, PartEstimate> TABLE = new HashMap<>();

    static {
        readAllEstimates();
    }

    /**
     * Reads all the estimates from specified file
     * The file path and format contract happens here
     * Assert the file paths
     */
    private static void readAllEstimates() {
        PartEstimateDAOImpl partEstimateDAOImpl = new PartEstimateDAOImpl();
        ArrayList<PartEstimate> all = partEstimateDAOImpl.getAll();

        for (PartEstimate partEstimate : all) {
            TABLE.put(partEstimate.id, partEstimate);
        }
    }

    /**
     * Writes all the estimates to specified file
     * The file path and format contract happens here
     * Assert the file paths
     */
    public static void writeAllEstimates() {
        ArrayList<PartEstimate> partEstimates = new ArrayList<>();

        for (Map.Entry<String, PartEstimate> row : TABLE.entrySet()) {
            partEstimates.add(row.getValue());
        }

        PartEstimateDAOImpl partEstimateDAOImpl = new PartEstimateDAOImpl();
        partEstimateDAOImpl.clearAndInsert(partEstimates);
    }

    /**
     * Updates or inserts the estimate of the part
     */
    public static void pushEstimate(PartEstimate new_estimate) {
        final String id = new_estimate.id;

        // Already estimate exists
        if (TABLE.containsKey(id)) {
            PartEstimate old_estimate = TABLE.get(id);
            // Store merged estimate
            PartEstimate merged_estimate = PartEstimate.getMergedEstimate(old_estimate, new_estimate);
            TABLE.put(id, merged_estimate);
        }
        // No estimate previously
        else {
            TABLE.put(id, new_estimate);
        }
    }

    public static void printAllEstimates() {
        for (Map.Entry<String, PartEstimate> row : TABLE.entrySet()) {
            System.out.println(row.getValue().toString());
        }
    }

}