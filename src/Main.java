import Jama.Matrix;
import PartEstimates.PartEstimate;
import PartEstimates.PartEstimatesTable;
import com.opencsv.CSVReader;

import java.io.*;
import java.util.*;

/**
 * Main program
 * todo : handle typos
 */
public class Main {

    private static boolean LOG = false;

    // Read -----------------------------------------------------------------------------------------------

    /**
     * Reads new shipments from the specified file
     * <br/>
     * The file path and format contract happens here
     * Assert all shipments are unique
     * Assert the file paths
     * Assert the formats
     * <br/>
     * Validates and cleans new data
     * 1. Empty Boxes - removes them from returning map
     * Returns a map : shipment id -> shipment
     * fixme : if new data is entered between reading and clearing that data is lost
     */
    private static Map<String, Shipment> readNewShipments() throws IOException {
        final String boxes_path = "../db/raw/new/boxes.csv";
        final String part_clone_count_maps_path = "../db/raw/new/part_clone_count_maps.csv";

        Map<String, Shipment> shipments = new HashMap<>();

        CSVReader boxes_reader = new CSVReader(new FileReader(boxes_path));
        // Ignore CSV heading
        String[] box_fields;
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
                            Double.parseDouble(box_fields[10]),
                            Double.parseDouble(box_fields[11]),
                            Double.parseDouble(box_fields[12]),
                            Double.parseDouble((box_fields[13]))
                    ));
        }
        if (LOG) System.out.println("Read boxes finished");

        // Clear the file
        PrintWriter boxes_writer = new PrintWriter(boxes_path);
        boxes_writer.write("");
        boxes_writer.close();

        CSVReader pc_map_reader = new CSVReader(new FileReader(part_clone_count_maps_path));
        // Ignore CSV heading
        String[] pc_map_fields;
        // Reads part clone_count maps
        while ((pc_map_fields = pc_map_reader.readNext()) != null) {

            // Format
            // 0 -> shipment id
            // 1 -> id
            // 6 -> quantity
            String shipment_id = pc_map_fields[0];
            String sku = pc_map_fields[1];
            int quantity = Integer.parseInt(pc_map_fields[6]);

            // Assert
            // quantity > 0
            Shipment shipment = shipments.get(shipment_id);
            if (shipment == null) {
                if (LOG) System.out.println("Warning : Unknown box found " + shipment_id);
            } else {
                shipment.addPart(sku, quantity);
            }
        }
        if (LOG) System.out.println("Read part clone count maps finished");

        // Clear the file
        PrintWriter pc_map_writer = new PrintWriter(part_clone_count_maps_path);
        pc_map_writer.write("");
        pc_map_writer.close();

        // Validate Bad Cases ///////////////////////////////////////////////////////////////////////////////////

        Vector<String> empty_boxes = new Vector<>();

        // Clean data
        for (Map.Entry<String, Shipment> shipment : shipments.entrySet()) {

            // Keep track of Empty Shipments
            if (shipment.getValue().part_clone_count_map.size() == 0) {
                empty_boxes.add(shipment.getKey());
                if (LOG) System.out.println("Warning : Empty box found " + shipment.getKey());
            }
        }

        // Removing Empty box
        for (String empty_box : empty_boxes) {
            shipments.remove(empty_box);
            if (LOG) System.out.println("Note : Empty box ignored " + empty_box);
        }

        return shipments;
    }

    /**
     * Reads old sInequalities from the specified file
     * <br/>
     * The file path and format contract happens here
     * Assert the file paths
     * Assert the formats
     */
    private static Vector<SInequality> readOldSInequalities() throws IOException {
        Vector<SInequality> old_sInequalities = new Vector<>();

        final String old_sInequalities_path = "../db/raw/old/sInequalities";

        BufferedReader br = new BufferedReader(new FileReader(old_sInequalities_path));

        int no_of_old_sInequalities = Integer.parseInt(br.readLine());
        for (int i = 0; i < no_of_old_sInequalities; i++) {

            old_sInequalities.add(new SInequality(br));

        }

        return old_sInequalities;
    }

    /**
     * Returns all SInequalities from both old and new data
     */
    private static Vector<SInequality> getAllSInequalities() throws IOException {
        Vector<SInequality> all = new Vector<>();

        // Old data
        Vector<SInequality> old = Main.readOldSInequalities();
        all.addAll(old);

        // New data
        Map<String, Shipment> shipments = Main.readNewShipments();
        // For each shipment
        for (Map.Entry<String, Shipment> id_shipment_map : shipments.entrySet()) {

            SInequality sInequality = id_shipment_map.getValue().extractSInequality();
            all.add(sInequality);

        }

        return all;
    }

    // Extract -----------------------------------------------------------------------------------------------

    /**
     * Prepares and returns all Full SInequality Sets, from all SInequalities
     * Full set = Set of all n-SInequalities among available
     * All full sets is a map : cardinality -> full set
     */
    private static Map<Integer, Vector<SInequality>> extractAllFullSets() throws IOException {
        Map<Integer, Vector<SInequality>> full_sets = new HashMap<>();

        Vector<SInequality> all = Main.getAllSInequalities();
        // For each shipment
        for (SInequality sInequality : all) {

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
     * Filters out the unsolvable similar sets i.e. when cardinality > # SInequalities in similar set
     * Similar set with a signature = Set of all n-SInequalities among available with given signature
     * All similar sets is a map : signature -> similar set
     */
    private static Map<Set<String>, Vector<SInequality>> extractAllSimilarSets() throws IOException {
        Map<Set<String>, Vector<SInequality>> similar_sets = new HashMap<>();

        // All full sets
        Map<Integer, Vector<SInequality>> full_sets = Main.extractAllFullSets();
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
                if (LOG) System.out.println("Note : Unsolvable similar set found " + similar_set.getKey());
            }
        }

        // Remove all unsolvable similar sets from returning map
        for (Set<String> unsolvable_similar_set : unsolvable_similar_sets) {

            // Keep track of unsolvable similar set's sInequalities
            LeftOvers.add(similar_sets.get(unsolvable_similar_set));

            similar_sets.remove(unsolvable_similar_set);
            if (LOG) System.out.println("Note : Unsolvable similar set removed " + unsolvable_similar_set);
        }

        return similar_sets;
    }

    /**
     * Store the number of square sets formed in the present similar set
     */
    private static int no_squares_formed_in_this_similar = 0;

    /**
     * The upper limit of the square sets formed for a similar set
     */
    private static int squares_per_similar_limit = 100;

    /**
     * Prepares and puts into param square_sets min(all, squares_per_similar_limit) possible Square SInequality sets, from a given Similar SInequality set
     * Assert param similar_set to be a similar set
     * Reference : geeks for geeks
     * <br/>
     * Selects 'cardinality' number of SInequalities from 'similar_set' into 'buffer'
     * There are
     * ------similar_set.size()
     * ------------------------C
     * -------------------------cardinality
     * number of combinations
     * Stops if the number of square sets formed equals squares_per_similar_limit
     */
    private static void extractSquaresFromSimilar(Vector<SInequality> similar_set, int cardinality, Vector<SInequality> buffer, int buffer_i, int input_i, Vector<Vector<SInequality>> square_sets) {
        if (Main.no_squares_formed_in_this_similar >= squares_per_similar_limit) {
            return;
        }

        if (buffer_i == cardinality) {
            Vector<SInequality> square = new Vector<>(buffer);
            square_sets.add(square);
            Main.no_squares_formed_in_this_similar++;
            return;
        }

        // When no more elements are there to put in data[]
        if (input_i >= similar_set.size())
            return;

        // current is included
        buffer.set(buffer_i, similar_set.get(input_i));

        // put next at next location
        Main.extractSquaresFromSimilar(similar_set, cardinality, buffer, buffer_i + 1, input_i + 1, square_sets);

        // current is excluded, replace it with next (Note that i+1 is passed, but index is not changed)
        Main.extractSquaresFromSimilar(similar_set, cardinality, buffer, buffer_i, input_i + 1, square_sets);

    }

    /**
     * Prepares and returns all Square SInequality sets, from all Similar SInequality sets
     * Square set = A set of “n” n-sInequalities, in which all sInequalities have the same signature
     */
    private static Map<Set<String>, Vector<Vector<SInequality>>> extractAllSquareSets() throws IOException {
        Map<Set<String>, Vector<Vector<SInequality>>> square_sets = new HashMap<>();

        // All similar sets
        // with unsolvable sets filtered
        Map<Set<String>, Vector<SInequality>> similar_sets = Main.extractAllSimilarSets();

        // For every similar set
        for (Map.Entry<Set<String>, Vector<SInequality>> similar_set_m : similar_sets.entrySet()) {

            Vector<SInequality> similar_set = similar_set_m.getValue();
            int cardinality = similar_set_m.getKey().size();

            // Prepare buffer
            Vector<SInequality> buffer = new Vector<>();
            for (int i = 0; i < cardinality; i++) {
                buffer.add(null);
            }
            // Prepares all square sets from this similar set
            Vector<Vector<SInequality>> square_sets_from_this = new Vector<>();

            // Extract all square sets from this similar set
            Main.no_squares_formed_in_this_similar = 0;
            Main.extractSquaresFromSimilar(similar_set, cardinality, buffer, 0, 0, square_sets_from_this);

            // Puts the signature as key
            // All square sets from this similar set as value
            square_sets.put(similar_set_m.getKey(), square_sets_from_this);

        }

        return square_sets;
    }

    // Estimate and Push -----------------------------------------------------------------------------------------------

    /**
     * Assert index of an id equals the index of its estimate in new_estimates
     */
    private static void pushNewEstimates(Vector<String> ids, double[][] new_estimates) {
        for (int i = 0; i < ids.size(); i++) {
            PartEstimate ith = new PartEstimate(ids.get(i),
                    new_estimates[i][0],
                    new_estimates[i][1],
                    new_estimates[i][2],
                    new_estimates[i][3]
            );
            PartEstimatesTable.pushEstimate(ith);
        }
    }

    /**
     * Estimates values from the given square set, in all possible ways
     * Ways of estimation:
     * ==================
     * 1. simple linear solution
     * todo 2. intelligent merge
     * <p>
     * Assert param square_set is a square set
     * Assert there is at least one element in param square_set i.e. empty boxes are removed from new data
     * todo : throw the unused sInequalities into old data
     */
    private static void estimateFromSquareSet(Vector<SInequality> square_set) {

        // Declare array of arrays
        // The coefficient matrix
        double[][] a = new double[square_set.size()][square_set.size()];
        // The constant matrix
        double[][] b = new double[square_set.size()][4];

        // Fill the array of arrays
        // For each sInequality
        for (int i = 0; i < square_set.size(); i++) {
            SInequality sInequality = square_set.get(i);
            // Keep track of coefficient row
            a[i] = sInequality.getCoefficientRow();
            // Keep track of constant row
            b[i] = sInequality.getConstantRow();
        }

        // Create Coefficient matrix and constant matrix
        Matrix A = new Matrix(a);
        Matrix B = new Matrix(b);

        // Try to solve
        if (A.det() != 0) {
            // X = A^-1 * B
            Matrix X = A.solve(B);
            double[][] estimates = X.getArray();

            boolean has_non_positive_dimension = false;

            for (double[] estimate : estimates) {
                for (double dimension : estimate) {
                    if (dimension <= 0) {
                        has_non_positive_dimension = true;
                        break;
                    }
                }
                if (has_non_positive_dimension) break;
            }

            // Has non positive dimension
            if (has_non_positive_dimension) {
                // Pass
                // todo : specially solve these
            }
            // Else
            else {
                // At least one element exists in param square_set
                Main.pushNewEstimates(square_set.get(0).getVariableRow(), estimates);
            }

        }
        // Singular coefficient matrix
        else {
            if (LOG) System.out.println("Note : Singular matrix found");
        }

    }

    // Store -----------------------------------------------------------------------------------------------

    /**
     * Writes all the left overs to specified file
     * The file path and format contract happens here
     * Assert the file paths
     */
    private static void storeLeftOvers() throws IOException {
        final String old_sInequalities_path = "../db/raw/old/sInequalities";

        // Write to file
        BufferedWriter bw = new BufferedWriter(new FileWriter(old_sInequalities_path));

        bw.write(LeftOvers.size() + "\n");
        for (SInequality sInequality : LeftOvers.getAll()) {
            bw.write(sInequality.format());
            bw.flush();
        }
    }

    /**
     * Writes all the estimates to specified file
     */
    private static void storeEstimates() throws IOException {
        PartEstimatesTable.writeAllEstimates();
    }

    // -----------------------------------------------------------------------------------------------

    public static void main(String[] args) throws IOException {

        // Read and Extract
        Map<Set<String>, Vector<Vector<SInequality>>> square_sets = Main.extractAllSquareSets();

        // Estimate and Push
        for (Map.Entry<Set<String>, Vector<Vector<SInequality>>> square_sets_from_this : square_sets.entrySet()) {
            for (Vector<SInequality> square_set : square_sets_from_this.getValue()) {
                Main.estimateFromSquareSet(square_set);
            }
        }

        // Store Left Overs
        Main.storeLeftOvers();
        // Store Estimates
        Main.storeEstimates();
    }

}