package com.partsavatar.packer.algorithms;

import com.partsavatar.packer.components.*;

import java.util.HashMap;
import java.util.Stack;


public class BacktrackAlgorithmAbove6Items extends BacktrackAlgortihmBaseClass {

    private static void upwardFill(final Stack<Surface> s, final Box b, final WarehouseOrder ord, final Vector3D leftBottom, final Vector3D sliceDim, final HashMap<Vector3D, Vector3D> unused) {
        Integer i = 0;
        while (ord.getOrderList().size() > i && (ord.getOrderList().get(i).getQuantity() == 0 ||
                !sliceDim.bestRotateAndCheckIsEqualOrGreater(ord.getMinPartSize(), ord.getOrderList().get(i)))) {
//		!sliceDim.rotateAndCheckIsEqualOrGreater(ord.order_list.get(i)))){		
            i++;
        }
        if (i == ord.getOrderList().size()) {//No fit found
            if (!(sliceDim.getX() == 0 || sliceDim.getY() == 0 || sliceDim.getZ() == 0)) {
                unused.put(leftBottom, new Vector3D(sliceDim.getX(), 0, sliceDim.getZ()));
            }
            return;
        } else {
            Integer currQty = ord.getOrderList().get(i).getQuantity();
            ord.getOrderList().get(i).setQuantity(currQty - 1);

            Part p = ord.getOrderList().get(i).copy();
            p.setPosition(leftBottom);
            p.setQuantity(1);
            b.addPart(p);

            s.push(new Surface(leftBottom, p.getDimension())); // top getSurface() of part

            upwardFill(s, b, ord, new Vector3D(leftBottom.getX(), leftBottom.getY() + p.getDimension().getY(), leftBottom.getZ()),
                    new Vector3D(p.getDimension().getX(), sliceDim.getY() - p.getDimension().getY(), p.getDimension().getZ()), unused);
        }
    }

    private static void backwardFill(final Stack<Surface> s, final Box b, final WarehouseOrder ord, final Surface surf, final HashMap<Vector3D, Vector3D> unused) {
        while (s.peek() != surf) {
            Surface top = s.pop();
            Surface bottom = s.peek();

            //try to fill front portion BIGGER
            Vector3D frontSliceDim = new Vector3D(bottom.getSurface().getX(), b.getDimension().getY() - bottom.getLeftUpperBehind().getY(), bottom.getSurface().getZ() - top.getSurface().getZ());
            Vector3D frontSlicelbb = new Vector3D(bottom.getLeftUpperBehind().getX(), bottom.getLeftUpperBehind().getY(), bottom.getLeftUpperBehind().getZ() + top.getSurface().getZ());
            fillBox(s, b, ord, frontSlicelbb, frontSliceDim, unused);

            //try to fill front portion SMALLER
            Vector3D sideSliceDim = new Vector3D(bottom.getSurface().getX() - top.getSurface().getX(), b.getDimension().getY() - bottom.getLeftUpperBehind().getY(), top.getSurface().getZ());
            Vector3D sideSlicelbb = new Vector3D(bottom.getLeftUpperBehind().getX() + top.getSurface().getX(), bottom.getLeftUpperBehind().getY(), bottom.getLeftUpperBehind().getZ());
            fillBox(s, b, ord, sideSlicelbb, sideSliceDim, unused);
        }
        s.pop();
    }

    private static void fillBox(final Stack<Surface> s, final Box b, final WarehouseOrder ord, final Vector3D leftBottom, final Vector3D sliceDim, final HashMap<Vector3D, Vector3D> unused) {
        Integer i = 0;
        while (ord.getOrderList().size() > i && (ord.getOrderList().get(i).getQuantity() == 0 ||
                !sliceDim.bestRotateAndCheckIsEqualOrGreater(ord.getMinPartSize(), ord.getOrderList().get(i)))) {
        	//	!sliceDim.rotateAndCheckIsEqualOrGreater(ord.order_list.get(i)))){		
            i++;
        }
        if (i == ord.getOrderList().size()) { // No fit found
            if (!(sliceDim.getX() == 0 || sliceDim.getY() == 0 || sliceDim.getZ() == 0))
                unused.put(leftBottom, new Vector3D(sliceDim.getX(), 0, sliceDim.getZ()));
            return;
        } else {
            Integer currQty = ord.getOrderList().get(i).getQuantity();
            ord.getOrderList().get(i).setQuantity(currQty - 1);

            Surface partBottom = new Surface(leftBottom, new Vector3D(sliceDim.getX(), 0, sliceDim.getZ()));
            s.push(partBottom);// bottom getSurface() of box

            Part p = ord.getOrderList().get(i).copy();
            p.setPosition(leftBottom);
            p.setQuantity(1);
            b.addPart(p);

            s.push(new Surface(leftBottom, p.getDimension())); // top getSurface() of part

            upwardFill(s, b, ord, new Vector3D(leftBottom.getX(), leftBottom.getY() + p.getDimension().getY(), leftBottom.getZ()),
                    new Vector3D(p.getDimension().getX(), sliceDim.getY() - p.getDimension().getY(), p.getDimension().getZ()), unused);
            backwardFill(s, b, ord, partBottom, unused); // Fill completely only the column with bottom getSurface() as bottom of box
        }
    }

    static WarehouseOrder backtrackAlgorithm(final Box b, final WarehouseOrder newWarehouseOrder) {
        newWarehouseOrder.volSort();
//		Float initialVol = (float)new_order.getVol();
//		System.out.println("WITHOUT PREV :" + prev_calcAcc(new_order).toString());
        HashMap<Vector3D, Vector3D> tmpUnused = new HashMap<>();
        fillBox(new Stack<Surface>(), b, newWarehouseOrder, new Vector3D(0, 0, 0), b.getDimension(), tmpUnused);

        HashMap<Vector3D, Vector3D> unFilled = combineUnused(tmpUnused);
        for (Vector3D key : unFilled.keySet()) {
            fillBox(new Stack<Surface>(), b, newWarehouseOrder, key, new Vector3D(unFilled.get(key).getX(), b.getDimension().getY() - key.getY(), unFilled.get(key).getZ()), tmpUnused);
        }
//		System.out.println("WITHOUT FINAL BOX:" + b.getDimension().toString() + " ACC:" + calcAcc().toString());
//		System.out.println("WITHOUT VOID:" + (100-calcAcc())*b.getVol()/100);
//		System.out.println("%COMPLETION:" + b.getPartsVol()*100/initialVol);
        return newWarehouseOrder;
    }
}
