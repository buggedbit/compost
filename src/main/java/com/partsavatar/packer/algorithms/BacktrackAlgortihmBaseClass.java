package com.partsavatar.packer.algorithms;

import com.partsavatar.packer.components.Box;
import com.partsavatar.packer.components.Vector3D;
import com.partsavatar.packer.components.WarehouseOrder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class BacktrackAlgortihmBaseClass {

    static HashMap<Vector3D, Vector3D> combineUnused(final HashMap<Vector3D, Vector3D> unused) {
        HashMap<Vector3D, Vector3D> unusedRemaining = new HashMap<Vector3D, Vector3D>();
        HashMap<Vector3D, ArrayList<Vector3D>> sameXYSurface = new HashMap<Vector3D, ArrayList<Vector3D>>();

        for (Vector3D key : unused.keySet()) {
            Vector3D tmp = new Vector3D(key.getX(), key.getY(), 0);
            if (!sameXYSurface.containsKey(tmp))
                sameXYSurface.put(tmp, new ArrayList<Vector3D>());
            sameXYSurface.get(tmp).add(key);
        }

        for (Vector3D key : sameXYSurface.keySet()) {
            ArrayList<Vector3D> tmpList = sameXYSurface.get(key);
            if (tmpList.size() != 1) {
                Collections.sort(tmpList, (Vector3D p1, Vector3D p2) -> p1.getZ().compareTo(p2.getZ()));

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
                    unusedRemaining.put(tmpList.get(k), new Vector3D(surfX, 0, surfZ));
                    k = ++i;
                }
            }
        }
        return unusedRemaining;
    }

    static Float calcAcc(final Box b) {
        return (float) (b.getPartsVol() * 100.0 / b.getVol());
    }

    static Float prevCalcAcc(final WarehouseOrder ord, final Box b) {
        return (float) (ord.getVol() * 100.0 / b.getVol());
    }

    static WarehouseOrder backtrackAlgorithm(final Box b, final WarehouseOrder new_Warehouse_order) {
        return null;
    }
}
