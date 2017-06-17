package com.partsavatar.estimator;

import com.partsavatar.estimator.sinequality.SInequality;
import com.partsavatar.estimator.sinequality.leftovers.LeftOvers;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

/**
 * todo : remove noSquaresFormedInThisSimilar field
 * todo : change double to int
 * todo : store the latest new data fetch date
 * todo : validate the DAO return values
 * todo : improve pom
 * todo : handle typos
 */
public class Estimator {

    public static void main(String[] args) throws IOException {
        Map<Set<String>, Vector<Vector<SInequality>>> squareSets = ExtractSquares.getAllSquaresSets();
        EstimateFromSquares.estimateFromAllSquareSets(squareSets);
        LeftOvers.saveAllLeftOvers();
    }

}