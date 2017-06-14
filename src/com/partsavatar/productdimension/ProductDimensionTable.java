package com.partsavatar.productdimension;

import java.util.*;

public class ProductDimensionTable {

    private static Map<String, ProductDimension> TABLE = new HashMap<>();

    private static Set<String> NEW_ROWS = new HashSet<>();

    private static Set<String> UPDATED_ROWS = new HashSet<>();

    static {
        readAllEstimates();
    }

    private static void readAllEstimates() {
        ProductDimensionDAOImpl productDimensionDAOImpl = new ProductDimensionDAOImpl();
        ArrayList<ProductDimension> all = productDimensionDAOImpl.getAll();

        for (ProductDimension productDimension : all) {
            TABLE.put(productDimension.id, productDimension);
        }
    }

    public static boolean saveAllEstimates() {
        ArrayList<ProductDimension> productDimensions = new ArrayList<>();

        for (Map.Entry<String, ProductDimension> row : TABLE.entrySet()) {
            productDimensions.add(row.getValue());
        }

        ProductDimensionDAOImpl productDimensionDAOImpl = new ProductDimensionDAOImpl();
        return productDimensionDAOImpl.clearTableAndInsert(productDimensions);
    }

    public static void pushEstimate(ProductDimension new_estimate) {
        final String id = new_estimate.id;

        // Already estimate exists
        if (TABLE.containsKey(id)) {
            ProductDimension old_estimate = TABLE.get(id);
            // Store merged estimate
            ProductDimension merged_estimate = ProductDimension.getMergedEstimate(old_estimate, new_estimate);
            TABLE.put(id, merged_estimate);
            // Keep track of this updated ProductDimension
            // Even if this is updated many times as set is being used adding will do
            UPDATED_ROWS.add(id);
        }
        // No estimate previously
        else {
            TABLE.put(id, new_estimate);
            // Keep track of this new ProductDimension
            NEW_ROWS.add(id);
        }
    }

    public static void printAllEstimates() {
        for (Map.Entry<String, ProductDimension> row : TABLE.entrySet()) {
            System.out.println(row.getValue().toString());
        }
    }

}