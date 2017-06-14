package PartEstimates;

import java.io.*;
import java.util.ArrayList;

public class PartEstimateDAOImpl implements PartEstimateDAO {

    @Override
    public ArrayList<PartEstimate> getAll() {
        ArrayList<PartEstimate> all = new ArrayList<>();
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
                all.add(new PartEstimate(id,
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
    public boolean clearTableAndInsert(ArrayList<PartEstimate> partEstimates) {
        final String estimate_table = "../db/estimates/estimate_table";

        try {
            // Write to file
            BufferedWriter bw = new BufferedWriter(new FileWriter(estimate_table));
            // Total number of estimates
            bw.write(partEstimates.size() + "\n");
            // Each estimate
            for (PartEstimate partEstimate : partEstimates) {
                bw.write(partEstimate.csvFormat());
                bw.flush();
            }
            return true;

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

    }

}
