package com.partsavatar.productdimension;

import java.io.*;
import java.util.ArrayList;

public class ProductDimensionDAOImpl implements ProductDimensionDAO {

    @Override
    public ArrayList<ProductDimension> getAll() {
        ArrayList<ProductDimension> all = new ArrayList<>();
        ;

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
                all.add(new ProductDimension(id,
                        Double.parseDouble(dimensions[0]),
                        Double.parseDouble(dimensions[1]),
                        Double.parseDouble(dimensions[2]),
                        Double.parseDouble(dimensions[3])
                ));
            }
            br.close();
            fr.close();
            return all;

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

    }

    @Override
    public boolean clearTableAndInsert(ArrayList<ProductDimension> productDimensions) {
        final String estimate_table = "../db/estimates/estimate_table";

        try {
            // Write to file
            BufferedWriter bw = new BufferedWriter(new FileWriter(estimate_table));
            // Total number of estimates
            bw.write(productDimensions.size() + "\n");
            // Each estimate
            for (ProductDimension productDimension : productDimensions) {
                bw.write(productDimension.csvFormat());
                bw.flush();
            }
            return true;

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

    }

}
