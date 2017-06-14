package com.partsavatar.abstracts.leftovers;

import com.partsavatar.abstracts.SInequality;

import java.util.Vector;

public interface LeftOversDAO {

    Vector<SInequality> getAll();

    boolean clearTableAndInsert(Vector<SInequality> partEstimates);

}
