import Abstract.SInequality;
import Physical.Shipment;
import com.opencsv.CSVReader;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

/**
 * Main program
 */
public class Main {

    /**
     * Reads boxes and items in it
     * <br/>
     * The file path and format contract happens here
     * Assert all shipments are unique
     * Assert the file paths
     * Assert the formats
     * <br/>
     * Validates and cleans new data
     * 1. Empty Boxes - removes them from returning map
     * Returns a map : shipment id -> shipment
     */
    private static Map<String, Shipment> readNewShipments() throws IOException {
        final String boxes_path = "../db/raw/new/boxes.csv";
        final String part_clone_count_maps_path = "../db/raw/new/part_clone_count_maps.csv";

        Map<String, Shipment> shipments = new HashMap<>();

        CSVReader boxes_reader = new CSVReader(new FileReader(boxes_path));
        // Ignore CSV heading
        String[] box_fields = boxes_reader.readNext();
        // Reads boxes
        while ((box_fields = boxes_reader.readNext()) != null) {

            // Format
            // 1 -> order id
            // 2 -> shipment id
            // 10 -> l
            // 11 -> b
            // 12 -> h
            // 13 -> w

            // Assert
            // l, b, h > 0
            // w > 0
            shipments.put(
                    box_fields[2],
                    new Shipment(
                            box_fields[1],
                            box_fields[2],
                            Float.parseFloat(box_fields[10]),
                            Float.parseFloat(box_fields[11]),
                            Float.parseFloat(box_fields[12]),
                            Float.parseFloat((box_fields[13]))
                    ));
        }
        System.out.println("Read boxes finished");

        CSVReader pc_map_reader = new CSVReader(new FileReader(part_clone_count_maps_path));
        // Ignore CSV heading
        String[] pc_map_fields = pc_map_reader.readNext();
        // Reads part clone_count maps
        while ((pc_map_fields = pc_map_reader.readNext()) != null) {

            // Format
            // 0 -> shipment id
            // 1 -> sku
            // 6 -> quantity
            String shipment_id = pc_map_fields[0];
            String sku = pc_map_fields[1];
            int quantity = Integer.parseInt(pc_map_fields[6]);

            // Assert
            // quantity > 0
            Shipment shipment = shipments.get(shipment_id);
            if (shipment == null) {
                System.out.println("Warning : Unknown box found " + shipment_id);
            } else {
                shipment.addPart(sku, quantity);
            }
        }
        System.out.println("Read part clone count maps finished");

        // Validate Bad Cases ///////////////////////////////////////////////////////////////////////////////////

        Vector<String> empty_boxes = new Vector<>();

        // Clean data
        for (Map.Entry<String, Shipment> shipment : shipments.entrySet()) {

            // Keep track of Empty Shipments
            if (shipment.getValue().part_clone_count_map.size() == 0) {
                empty_boxes.add(shipment.getKey());
                System.out.println("Warning : Empty box found " + shipment.getKey());
            }
        }

        // Removing Empty box
        for (String empty_box : empty_boxes) {
            shipments.remove(empty_box);
            System.out.println("Note : Empty box ignored " + empty_box);
        }

        return shipments;
    }

    /**
     * Prepares and returns all Full SInequality Sets, from both old and new data
     * Full set = Set of all n-SInequalities among available
     * All full sets is a map : cardinality -> full set
     */
    private static Map<Integer, Vector<SInequality>> extractAllFullSets() throws IOException {
        Map<Integer, Vector<SInequality>> full_sets = new HashMap<>();

        // Old data
        // todo implement this

        // New data
        Map<String, Shipment> shipments = Main.readNewShipments();
        // For each shipment
        for (Map.Entry<String, Shipment> shipment : shipments.entrySet()) {

            SInequality sInequality = shipment.getValue().extractSInequality();
            int cardinality = sInequality.getCardinality();

            // If already there add
            if (full_sets.containsKey(cardinality)) {
                full_sets.get(cardinality).add(sInequality);
            }
            // Else create one
            else {
                Vector<SInequality> full_set = new Vector<>();
                full_set.add(sInequality);
                full_sets.put(cardinality, full_set);
            }

        }

        return full_sets;
    }

    /**
     * Prepares and returns all Similar SInequality sets, from all Full SInequality Sets
     * todo : instead of removing unsolvable similar sets store them for future use
     * Similar set with a signature = Set of all n-SInequalities among available with given signature
     * All similar sets is a map : signature -> similar set
     */
    private static Map<Set<String>, Vector<SInequality>> extractAllSimilarSets() throws IOException {
        Map<Set<String>, Vector<SInequality>> similar_sets = new HashMap<>();

        // All full sets
        Map<Integer, Vector<SInequality>> full_sets = extractAllFullSets();
        // Prepare similar sets
        // For each full set
        for (Map.Entry<Integer, Vector<SInequality>> full_set : full_sets.entrySet()) {
            // For each sInequality
            for (SInequality sInequality : full_set.getValue()) {

                Set<String> signature = sInequality.getSignature();

                // If already similar set exists
                if (similar_sets.containsKey(signature)) {
                    similar_sets.get(signature).add(sInequality);
                }
                // Else create one
                else {
                    Vector<SInequality> similar_set = new Vector<>();
                    similar_set.add(sInequality);
                    similar_sets.put(signature, similar_set);
                }

            }
        }

        // Removes the unsolvable similar sets
        Vector<Set<String>> unsolvable_similar_sets = new Vector<>();

        for (Map.Entry<Set<String>, Vector<SInequality>> similar_set : similar_sets.entrySet()) {

            // If number of variables > number of SInequalities
            // Keep track of such sets
            if (similar_set.getKey().size() > similar_set.getValue().size()) {
                unsolvable_similar_sets.add(similar_set.getKey());
                System.out.println("Note : Unsolvable similar set found " + similar_set.getKey());
            }
        }

        // Remove all unsolvable similar sets
        for (Set<String> unsolvable_similar_set : unsolvable_similar_sets) {
            similar_sets.remove(unsolvable_similar_set);
            System.out.println("Note : Unsolvable similar set removed " + unsolvable_similar_set);
        }

        return similar_sets;
    }

    public static void main(String[] args) throws IOException {
        Map<Set<String>, Vector<SInequality>> similar_sets = extractAllSimilarSets();
        for (Map.Entry<Set<String>, Vector<SInequality>> similar_set : similar_sets.entrySet()) {
            if (similar_set.getKey().size() <= similar_set.getValue().size()) {
                System.out.println(similar_set.getKey());
                System.out.println(similar_set.getKey().size());
                System.out.println(similar_set.getValue().size());
            } else {
                System.out.println("Note : Unsolvable similar set found " + similar_set.getKey());
            }
        }

    }

}