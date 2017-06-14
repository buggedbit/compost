package PartEstimates;

import java.util.*;

public class PartEstimatesTable {

    private static Map<String, PartEstimate> TABLE = new HashMap<>();

    private static Set<String> NEW_ROWS = new HashSet<>();

    private static Set<String> UPDATED_ROWS = new HashSet<>();

    static {
        readAllEstimates();
    }

    private static void readAllEstimates() {
        PartEstimateDAOImpl partEstimateDAOImpl = new PartEstimateDAOImpl();
        ArrayList<PartEstimate> all = partEstimateDAOImpl.getAll();

        for (PartEstimate partEstimate : all) {
            TABLE.put(partEstimate.id, partEstimate);
        }
    }

    public static boolean saveAllEstimates() {
        ArrayList<PartEstimate> partEstimates = new ArrayList<>();

        for (Map.Entry<String, PartEstimate> row : TABLE.entrySet()) {
            partEstimates.add(row.getValue());
        }

        PartEstimateDAOImpl partEstimateDAOImpl = new PartEstimateDAOImpl();
        return partEstimateDAOImpl.clearTableAndInsert(partEstimates);
    }

    public static void pushEstimate(PartEstimate new_estimate) {
        final String id = new_estimate.id;

        // Already estimate exists
        if (TABLE.containsKey(id)) {
            PartEstimate old_estimate = TABLE.get(id);
            // Store merged estimate
            PartEstimate merged_estimate = PartEstimate.getMergedEstimate(old_estimate, new_estimate);
            TABLE.put(id, merged_estimate);
            // Keep track of this updated PartEstimate
            // Even if this is updated many times as set is being used adding will do
            UPDATED_ROWS.add(id);
        }
        // No estimate previously
        else {
            TABLE.put(id, new_estimate);
            // Keep track of this new PartEstimate
            NEW_ROWS.add(id);
        }
    }

    public static void printAllEstimates() {
        for (Map.Entry<String, PartEstimate> row : TABLE.entrySet()) {
            System.out.println(row.getValue().toString());
        }
    }

}