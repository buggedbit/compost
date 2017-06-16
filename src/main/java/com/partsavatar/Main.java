package com.partsavatar;

import com.partsavatar.abstracts.SInequality;
import com.partsavatar.abstracts.leftovers.LeftOvers;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

/**
 * todo : store the latest new data fetch date
 * todo : validate the DAO return values
 * todo : handle typos
 */
public class Main {

    public static void main(String[] args) throws IOException {

        Map<Set<String>, Vector<Vector<SInequality>>> squareSets = SquaresExtractor.getAllSquaresSets();

        try {
            Estimator.estimateFromAllSquareSets(squareSets);
        } catch (Exception e) {
            e.printStackTrace();
        }

        LeftOvers.saveAllLeftOvers();
    }

}