package com.partsavatar.estimator.sinequality.leftovers;

import com.partsavatar.estimator.sinequality.SInequality;
import lombok.NonNull;

import java.util.Vector;

public class LeftOvers {
    private static Vector<SInequality> Bucket = new Vector<>();

    public static Vector<SInequality> readAllLeftOvers() {
        LeftOversDAOImpl leftOversDAOImpl = new LeftOversDAOImpl();
        return leftOversDAOImpl.getAll();
    }

    public static boolean saveAllLeftOvers() {
        LeftOversDAOImpl leftOversDAOImpl = new LeftOversDAOImpl();
        return leftOversDAOImpl.clearTableAndSave(Bucket);
    }

    public static void add(@NonNull SInequality sInequality) {
        Bucket.add(sInequality);
    }

    public static void add(@NonNull Vector<SInequality> sInequalities) {
        Bucket.addAll(sInequalities);
    }

    public static int size() {
        return Bucket.size();
    }

}
