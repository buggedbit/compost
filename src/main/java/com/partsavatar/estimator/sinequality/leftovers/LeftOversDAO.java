package com.partsavatar.sinequality.leftovers;

import com.partsavatar.sinequality.SInequality;

import java.util.Vector;

public interface LeftOversDAO {

    public Vector<SInequality> getAll();

    public boolean clearTableAndSave(final Vector<SInequality> partEstimates);

}
