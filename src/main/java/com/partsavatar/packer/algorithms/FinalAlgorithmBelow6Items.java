package com.partsavatar.packer.algorithms;


import com.partsavatar.packer.components.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;

public class FinalAlgorithmBelow6Items extends FinalAlgortihmBaseClass {

    private static void upwardFill(Stack<Surface> s, Box b, Order ord, Vector leftBottom, Vector sliceDim, HashMap<Vector, Vector> unused) {
        Integer i = 0;
        while (ord.getOrderList().size() > i && (ord.getOrderList().get(i).getQuantity() == 0 ||
                !sliceDim.checkIsEqualOrGreater(ord.getOrderList().get(i)))) {
            i++;
        }
        if (i == ord.getOrderList().size()) {//No fit found
            if (!(sliceDim.getX() == 0 || sliceDim.getY() == 0 || sliceDim.getZ() == 0)) {
                unused.put(leftBottom, new Vector(sliceDim.getX(), 0, sliceDim.getZ()));
            }
            return;
        } else {
            Integer currQty = ord.getOrderList().get(i).getQuantity();
            ord.getOrderList().get(i).setQuantity(currQty - 1);

            Part p = ord.getOrderList().get(i).copy();
            p.setPosition(leftBottom);
            p.setQuantity(1);
            b.addPart(p);

            s.push(new Surface(leftBottom, p.getDimension())); // top surface of part

            upwardFill(s, b, ord, new Vector(leftBottom.getX(), leftBottom.getY() + p.getDimension().getY(), leftBottom.getZ()),
                    new Vector(p.getDimension().getX(), sliceDim.getY() - p.getDimension().getY(), p.getDimension().getZ()), unused);
        }
    }

    private static void backwardFill(Stack<Surface> s, Box b, Order ord, Surface surf, HashMap<Vector, Vector> unused) {
        while (s.peek() != surf) {
            Surface top = s.pop();
            Surface bottom = s.peek();

            //try to fill front portion BIGGER
            Vector frontSliceDim = new Vector(bottom.getSurface().getX(), b.getDimension().getY() - bottom.getLeftUpperBehind().getY(), bottom.getSurface().getZ() - top.getSurface().getZ());
            Vector frontSlicelbb = new Vector(bottom.getLeftUpperBehind().getX(), bottom.getLeftUpperBehind().getY(), bottom.getLeftUpperBehind().getZ() + top.getSurface().getZ());
            fillBox(s, b, ord, frontSlicelbb, frontSliceDim, unused);

            //try to fill front portion SMALLER
            Vector sideSliceDim = new Vector(bottom.getSurface().getX() - top.getSurface().getX(), b.getDimension().getY() - bottom.getLeftUpperBehind().getY(), top.getSurface().getZ());
            Vector sideSlicelbb = new Vector(bottom.getLeftUpperBehind().getX() + top.getSurface().getX(), bottom.getLeftUpperBehind().getY(), bottom.getLeftUpperBehind().getZ());
            fillBox(s, b, ord, sideSlicelbb, sideSliceDim, unused);
        }
        s.pop();
    }

    private static void fillBox(Stack<Surface> s, Box b, Order ord, Vector leftBottom, Vector sliceDim, HashMap<Vector, Vector> unused) {
        Integer i = 0;
        while (ord.getOrderList().size() > i && (ord.getOrderList().get(i).getQuantity() == 0 ||
                !sliceDim.checkIsEqualOrGreater(ord.getOrderList().get(i)))) {
            i++;
        }
        if (i == ord.getOrderList().size()) { // No fit found
            if (!(sliceDim.getX() == 0 || sliceDim.getY() == 0 || sliceDim.getZ() == 0))
                unused.put(leftBottom, new Vector(sliceDim.getX(), 0, sliceDim.getZ()));
            return;
        } else {
            Integer currQty = ord.getOrderList().get(i).getQuantity();
            ord.getOrderList().get(i).setQuantity(currQty - 1);


            Surface partBottom = new Surface(leftBottom, new Vector(sliceDim.getX(), 0, sliceDim.getZ()));
            s.push(partBottom);// bottom surface of box

            Part p = ord.getOrderList().get(i).copy();
            p.setPosition(leftBottom);
            p.setQuantity(1);
            b.addPart(p);

            s.push(new Surface(leftBottom, p.getDimension())); // top surface of part

            upwardFill(s, b, ord, new Vector(leftBottom.getX(), leftBottom.getY() + p.getDimension().getY(), leftBottom.getZ()),
                    new Vector(p.getDimension().getX(), sliceDim.getY() - p.getDimension().getY(), p.getDimension().getZ()), unused);
            backwardFill(s, b, ord, partBottom, unused); // Fill completely only the column with bottom surface as bottom of box
        }
    }

    private static Order expandOrder(Order ord) {
        List<Part> expList = new ArrayList<Part>();
        for (Part p : ord.getOrderList()) {
            Integer qty = p.getQuantity();
            for (int i = 0; i < qty; i++) {
                expList.add(new Part(p.getId(), new Vector(p.getDimension().getX(), p.getDimension().getY(), p.getDimension().getZ()), p.getWeight(), 1));
            }
        }
        return new Order(expList);

    }

    private static ArrayList<Part> rotate(Part p) {
        ArrayList<Part> out = new ArrayList<Part>();
        Integer x = p.getDimension().getX(), y = p.getDimension().getY(), z = p.getDimension().getZ();
        if (x != y && y != z && z != x) {
            out.add(new Part(p.getId(), new Vector(x, y, z), p.getWeight(), 1));
            out.add(new Part(p.getId(), new Vector(x, z, y), p.getWeight(), 1));
            out.add(new Part(p.getId(), new Vector(y, x, z), p.getWeight(), 1));
            out.add(new Part(p.getId(), new Vector(y, z, x), p.getWeight(), 1));
            out.add(new Part(p.getId(), new Vector(z, x, y), p.getWeight(), 1));
            out.add(new Part(p.getId(), new Vector(z, y, x), p.getWeight(), 1));
        } else if (x == y && y == z) {
            out.add(new Part(p.getId(), new Vector(x, x, x), p.getWeight(), 1));
        } else if (x == y) {
            out.add(new Part(p.getId(), new Vector(x, x, z), p.getWeight(), 1));
            out.add(new Part(p.getId(), new Vector(x, z, x), p.getWeight(), 1));
            out.add(new Part(p.getId(), new Vector(z, x, x), p.getWeight(), 1));
        } else if (y == z) {
            out.add(new Part(p.getId(), new Vector(x, z, z), p.getWeight(), 1));
            out.add(new Part(p.getId(), new Vector(z, x, z), p.getWeight(), 1));
            out.add(new Part(p.getId(), new Vector(z, z, x), p.getWeight(), 1));
        } else if (x == z) {
            out.add(new Part(p.getId(), new Vector(x, x, y), p.getWeight(), 1));
            out.add(new Part(p.getId(), new Vector(x, y, x), p.getWeight(), 1));
            out.add(new Part(p.getId(), new Vector(y, x, x), p.getWeight(), 1));
        }
        return out;
    }

    private static ArrayList<Order> genRotations(Order ord, Integer start) {
        ArrayList<Order> out = new ArrayList<Order>();
        ArrayList<Part> startRotations = rotate(ord.getOrderList().get(start));
        if (start != ord.getOrderList().size() - 1) {
            ArrayList<Order> tmp = genRotations(ord, start + 1);
            for (Part p : startRotations) {
                for (Order t : tmp) {
                    ArrayList<Part> tmpOrderList = new ArrayList<Part>();
                    for (Part s : t.getOrderList()) {
                        tmpOrderList.add(new Part(s.getId(), new Vector(s.getDimension().getX(), s.getDimension().getY(), s.getDimension().getZ()), s.getWeight(), 1));
                    }
                    out.add(new Order(tmpOrderList));
                    out.get(out.size() - 1).getOrderList().add(p);
                }
            }
        } else {
            for (Part p : startRotations) {
                List<Part> tmp = new ArrayList<Part>();
                tmp.add(p);
                out.add(new Order(tmp));
            }
        }
        return out;
    }

    static Order MainAlgo(Box b, Order new_order) {
//		Float initialVol = (float)new_order.getVol();
        ArrayList<Order> rotatedOrders = genRotations(expandOrder(new_order), 0);
        Box bCopy = b.copy();
        Float acc = (float) 0;
        for (Order ord : rotatedOrders) {
            for (Part q : ord.getOrderList()) {
                q.setQuantity(1);
            }
            ord.volSort();


            Box tmpBox = bCopy.copy();
            HashMap<Vector, Vector> tmpUnused = new HashMap<>();
            fillBox(new Stack<Surface>(), tmpBox, ord, new Vector(0, 0, 0), tmpBox.getDimension(), tmpUnused);

            HashMap<Vector, Vector> unFilled = combineUnused(tmpUnused);
            tmpUnused.clear();
            for (Vector key : unFilled.keySet()) {
                fillBox(new Stack<Surface>(), tmpBox, ord, key, new Vector(unFilled.get(key).getX(),
                        tmpBox.getDimension().getY() - key.getY(), unFilled.get(key).getZ()), tmpUnused);
            }

            if (calcAcc(tmpBox) > acc) {
//				System.out.println("PREV :" + 100*initialVol/b.getVol());
                b.setParts(tmpBox.copyParts());
                new_order = ord.copy();
                acc = calcAcc(tmpBox);
//				System.out.println("CURR :" + calcAcc(b));
//				System.out.println("%COMPLETION:" + b.getPartsVol()*100.0/initialVol);
            }
        }
//		System.out.println("FINAL BOX:" + b.getDimension().toString() + " ACC:" + acc);
//		System.out.println("VOID:" + (100-acc)*b.getVol()/100);
//		System.out.println("%COMPLETION:" + b.getPartsVol()*100.0/initialVol);
        return new_order;
    }
}
