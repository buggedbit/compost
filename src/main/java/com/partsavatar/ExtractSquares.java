package com.partsavatar;

import com.partsavatar.sinequality.SInequality;
import com.partsavatar.sinequality.leftovers.LeftOvers;
import com.partsavatar.shipment.Shipment;
import com.partsavatar.shipment.ShipmentDAOImpl;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

class ExtractSquares {

    private static Vector<SInequality> getAllSInequalities() throws IOException {
        Vector<SInequality> all = new Vector<>();

        Vector<SInequality> old = LeftOvers.readAllLeftOvers();
        all.addAll(old);

        Vector<Shipment> newShipments = new ShipmentDAOImpl().getShipmentsByCreatedDate(null);
        for (Shipment shipment : newShipments) {
            SInequality newSInequality = new SInequality(shipment);
            all.add(newSInequality);
        }

        return all;
    }

    private static Map<Integer, Vector<SInequality>>
    extractFullSets(final Vector<SInequality> all) throws IOException {
        Map<Integer, Vector<SInequality>> full_sets = new HashMap<>();

        for (SInequality sInequality : all) {

            int cardinality = sInequality.getCardinality();

            if (full_sets.containsKey(cardinality)) {
                full_sets.get(cardinality).add(sInequality);
            } else {
                Vector<SInequality> full_set = new Vector<>();
                full_set.add(sInequality);
                full_sets.put(cardinality, full_set);
            }

        }

        return full_sets;
    }

    private static Map<Set<String>, Vector<SInequality>>
    extractSimilarSets(final Map<Integer, Vector<SInequality>> fullSets) throws IOException {
        Map<Set<String>, Vector<SInequality>> similar_sets = new HashMap<>();

        for (Map.Entry<Integer, Vector<SInequality>> full_set : fullSets.entrySet()) {
            for (SInequality sInequality : full_set.getValue()) {

                Set<String> signature = sInequality.getSignature();

                if (similar_sets.containsKey(signature)) {
                    similar_sets.get(signature).add(sInequality);
                } else {
                    Vector<SInequality> similar_set = new Vector<>();
                    similar_set.add(sInequality);
                    similar_sets.put(signature, similar_set);
                }

            }
        }

        Vector<Set<String>> unsolvable_similar_sets = new Vector<>();

        for (Map.Entry<Set<String>, Vector<SInequality>> similar_set : similar_sets.entrySet()) {

            if (similar_set.getKey().size() > similar_set.getValue().size()) {
                unsolvable_similar_sets.add(similar_set.getKey());
                //  System.out.println("Note : Unsolvable similar set found " + similar_set.getKey());
            }
        }

        for (Set<String> unsolvable_similar_set : unsolvable_similar_sets) {

            LeftOvers.add(similar_sets.get(unsolvable_similar_set));

            similar_sets.remove(unsolvable_similar_set);
            //  System.out.println("Note : Unsolvable similar set removed " + unsolvable_similar_set);
        }

        return similar_sets;
    }

    private static int noSquaresFormedInThisSimilar = 0;

    private static int squaresPerSimilarLimit = 100;

    private static void
    extractSquaresFromSimilar(final Vector<SInequality> similarSet, final int cardinality,
                              Vector<SInequality> buffer, int buffer_i, int input_i,
                              Vector<Vector<SInequality>> squareSets) {
        if (noSquaresFormedInThisSimilar >= squaresPerSimilarLimit) {
            return;
        }

        if (buffer_i == cardinality) {
            Vector<SInequality> square = new Vector<>(buffer);
            squareSets.add(square);
            noSquaresFormedInThisSimilar++;
            return;
        }

        if (input_i >= similarSet.size())
            return;

        buffer.set(buffer_i, similarSet.get(input_i));

        extractSquaresFromSimilar(similarSet, cardinality, buffer, buffer_i + 1, input_i + 1, squareSets);

        extractSquaresFromSimilar(similarSet, cardinality, buffer, buffer_i, input_i + 1, squareSets);

    }

    private static Map<Set<String>, Vector<Vector<SInequality>>>
    extractSquareSets(final Map<Set<String>, Vector<SInequality>> similarSets)
            throws IOException {
        Map<Set<String>, Vector<Vector<SInequality>>> square_sets = new HashMap<>();

        for (Map.Entry<Set<String>, Vector<SInequality>> similar_set_m : similarSets.entrySet()) {

            Vector<SInequality> similar_set = similar_set_m.getValue();
            int cardinality = similar_set_m.getKey().size();

            Vector<SInequality> buffer = new Vector<>();
            for (int i = 0; i < cardinality; i++) {
                buffer.add(null);
            }
            Vector<Vector<SInequality>> square_sets_from_this = new Vector<>();

            noSquaresFormedInThisSimilar = 0;
            extractSquaresFromSimilar(similar_set, cardinality, buffer, 0, 0, square_sets_from_this);

            square_sets.put(similar_set_m.getKey(), square_sets_from_this);

        }

        return square_sets;
    }

    static Map<Set<String>, Vector<Vector<SInequality>>> getAllSquaresSets()
            throws IOException {
        Vector<SInequality> all = getAllSInequalities();
        Map<Integer, Vector<SInequality>> full_sets = extractFullSets(all);
        Map<Set<String>, Vector<SInequality>> similar_sets = extractSimilarSets(full_sets);
        Map<Set<String>, Vector<Vector<SInequality>>> square_sets = extractSquareSets(similar_sets);
        return square_sets;
    }

}
