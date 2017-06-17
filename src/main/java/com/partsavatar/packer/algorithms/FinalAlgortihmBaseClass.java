package com.partsavatar.packer.algorithms;

import com.partsavatar.packer.components.Box;
import com.partsavatar.packer.components.Vector;
import com.partsavatar.packer.components.WarehouseOrder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class FinalAlgortihmBaseClass {

    static HashMap<Vector, Vector> combineUnused(HashMap<Vector, Vector> unused) {
        HashMap<Vector, Vector> unusedRemaining = new HashMap<Vector, Vector>();
        HashMap<Vector, ArrayList<Vector>> sameXYSurface = new HashMap<Vector, ArrayList<Vector>>();

        for (Vector key : unused.keySet()) {
            Vector tmp = new Vector(key.getX(), key.getY(), 0);
            if (!sameXYSurface.containsKey(tmp))
                sameXYSurface.put(tmp, new ArrayList<Vector>());
            sameXYSurface.get(tmp).add(key);
        }

        for (Vector key : sameXYSurface.keySet()) {
            ArrayList<Vector> tmpList = sameXYSurface.get(key);
            if (tmpList.size() != 1) {
                Collections.sort(tmpList, (Vector p1, Vector p2) -> p1.getZ().compareTo(p2.getZ()));

                Integer surfZ = unused.get(tmpList.get(0)).getZ();
                Integer surfX = unused.get(tmpList.get(0)).getX();

                Integer k = 0;
                while (k < tmpList.size() - 1) {
                    Integer i = k;
                    while (i < tmpList.size() - 1 && tmpList.get(i).getZ() + unused.get(tmpList.get(i)).getZ() == tmpList.get(i + 1).getZ()) {
                        surfZ += unused.get(tmpList.get(i + 1)).getZ();
                        surfX = Math.min(surfX, unused.get(tmpList.get(i + 1)).getX());
                        i++;
                    }
                    unusedRemaining.put(tmpList.get(k), new Vector(surfX, 0, surfZ));
                    k = ++i;
                }
            }
        }
        return unusedRemaining;
    }

    static Float calcAcc(Box b) {
        return (float) (b.getPartsVol() * 100.0 / b.getVol());
    }

    static Float prev_calcAcc(WarehouseOrder ord, Box b) {
        return (float) (ord.getVol() * 100.0 / b.getVol());
    }

    static WarehouseOrder MainAlgo(Box b, WarehouseOrder new_Warehouse_order) {
        return null;
    }
}
