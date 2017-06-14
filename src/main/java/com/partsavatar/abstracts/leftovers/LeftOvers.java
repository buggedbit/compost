package com.partsavatar.abstracts.leftovers;

import com.partsavatar.abstracts.SInequality;

import java.util.Vector;

public class LeftOvers {
    private static Vector<SInequality> ALL = new Vector<>();

    private static Vector<SInequality> readAllLeftOvers() {
        LeftOversDAOImpl leftOversDAOImpl = new LeftOversDAOImpl();
        return leftOversDAOImpl.getAll();
    }

    public static boolean saveAllLeftOvers() {
        LeftOversDAOImpl leftOversDAOImpl = new LeftOversDAOImpl();
        return leftOversDAOImpl.clearTableAndSave(ALL);
    }

    public static void add(SInequality sInequality) {
        ALL.add(sInequality);
    }

    public static void add(Vector<SInequality> sInequalities) {
        ALL.addAll(sInequalities);
    }

    public static int size() {
        return ALL.size();
    }

    public static Vector<SInequality> getAll() {
        return ALL;
    }
}
