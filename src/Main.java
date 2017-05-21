import Abstract.SInequality;
import Physical.Shipment;
import com.opencsv.CSVReader;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

/**
 * Main program
 */
public class Main {

    /**
     * Reads shipments and items in it
     * <br/>
     * The file path and format contract happens here
     * Assert all shipments are unique
     * Assert the file paths
     * Assert the formats
     * <br/>
     * Returns a map : shipment id -> shipment
     */
    private static Map<String, Shipment> readNewShipments() throws IOException {
        final String shipments_file_path = "../db/raw/new/shipments.csv";
        final String packed_parts_file_path = "../db/raw/new/shipped_items.csv";

        Map<String, Shipment> shipments = new HashMap<>();

        CSVReader shipment_reader = new CSVReader(new FileReader(shipments_file_path));
        // Ignore CSV heading
        String[] shipment_fields = shipment_reader.readNext();
        // Reads shipments
        while ((shipment_fields = shipment_reader.readNext()) != null) {

            // Format
            // 1 -> order id
            // 2 -> shipment id
            // 10 -> l
            // 11 -> b
            // 12 -> h
            // 13 -> w

            // assuming
            // l, b, h > 0
            // w > 0
            shipments.put(
                    shipment_fields[2],
                    new Shipment(
                            shipment_fields[1],
                            shipment_fields[2],
                            Float.parseFloat(shipment_fields[10]),
                            Float.parseFloat(shipment_fields[11]),
                            Float.parseFloat(shipment_fields[12]),
                            Float.parseFloat((shipment_fields[13]))
                    ));
        }
        System.out.println("Read shipments finished");

        CSVReader pp_reader = new CSVReader(new FileReader(packed_parts_file_path));
        // Ignore CSV heading
        String[] pp_fields = pp_reader.readNext();
        // Reads items in shipments
        while ((pp_fields = pp_reader.readNext()) != null) {
            // Format
            // 0 -> shipment id
            // 1 -> sku
            // 6 -> quantity
            String shipment_id = pp_fields[0];
            String sku = pp_fields[1];
            int quantity = Integer.parseInt(pp_fields[6]);

            // assuming
            // quantity > 0
            Shipment shipment = shipments.get(shipment_id);
            if (shipment == null) {
                System.out.println("Warning : unknown shipment found " + shipment_id);
            } else {
                shipment.addPart(sku, quantity);
            }
        }
        System.out.println("Read packed parts finished");

        return shipments;
    }

    /**
     * Prepares and returns all Full SInequality Sets
     * Full set = Set of all n-SInequalities among available
     */
    private static Map<Integer, Vector<SInequality>> extractAllFullSets() throws IOException {
        Map<Integer, Vector<SInequality>> full_sets = new HashMap<>();

        // Old data
        // todo implement this

        // New data
        Map<String, Shipment> shipments = Main.readNewShipments();
        for (Map.Entry<String, Shipment> entry : shipments.entrySet()) {

            SInequality sInequality = entry.getValue().extractSInequality();
            int cardinality = sInequality.getCardinality();

            // If already there add
            if (full_sets.containsKey(cardinality)) {
                full_sets.get(cardinality).add(sInequality);
            }
            // Else create
            else {
                Vector<SInequality> full_set = new Vector<>();
                full_set.add(sInequality);
                full_sets.put(cardinality, full_set);
            }

        }

        return full_sets;
    }

    public static void main(String[] args) throws IOException {
        Map<Integer, Vector<SInequality>> full_sets = extractAllFullSets();
        // For each full set
        for (Map.Entry<Integer, Vector<SInequality>> entry : full_sets.entrySet()) {
            //
            for (SInequality person : entry.getValue()) {
                System.out.println(person.getCardinality());
            }
        }
    }

}