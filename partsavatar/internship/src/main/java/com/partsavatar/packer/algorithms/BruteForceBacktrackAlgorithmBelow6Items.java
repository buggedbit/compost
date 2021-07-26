package com.partsavatar.packer.algorithms;

import com.partsavatar.packer.components.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

public class BruteForceBacktrackAlgorithmBelow6Items extends BacktrackAlgortihmBaseClass {	
	@Override
	WarehouseOrder backtrackAlgorithm(Box b, final WarehouseOrder newWarehouseOrder) {
        WarehouseOrder copyWarehouseOrder = newWarehouseOrder.copy();
    	
    	List<WarehouseOrder> rotatedWarehouseOrders = genRotations(expandOrder(copyWarehouseOrder), 0);
        Box bCopy = b.copy();
        Float acc = (float) 0;
        
        // Brute Force calculation of best packing by iterating over all possible order rotations 
        for (WarehouseOrder ord : rotatedWarehouseOrders) {
            for (Part q : ord.getOrderMap().keySet()) {
            	ord.getOrderMap().put(q, 1);
            }

            Box tmpBox = bCopy.copy();
            HashMap<Vector3D, Vector3D> tmpUnused = new HashMap<>();
            fillBox(new Stack<Surface>(), tmpBox, ord, ord.volSort(), new Vector3D(0, 0, 0), tmpBox.getDimension(), tmpUnused);

            HashMap<Vector3D, Vector3D> unFilled = combineUnused(tmpUnused);
            tmpUnused.clear();
            for (Vector3D key : unFilled.keySet()) {
                fillBox(new Stack<Surface>(), tmpBox, ord, ord.volSort(), key, new Vector3D(unFilled.get(key).getX(),
                        tmpBox.getDimension().getY() - key.getY(), unFilled.get(key).getZ()), tmpUnused);
            }

            if (calcAcc(tmpBox) > acc) {
                b.setPartPositionMap(tmpBox.copyParts());
                copyWarehouseOrder = ord.copy();
                acc = calcAcc(tmpBox);
            }
        }
        return copyWarehouseOrder;
    }
    
	private void upwardFill(Stack<Surface> s, Box b, WarehouseOrder ord, final List<Part> sortedOrderList,
    		final Vector3D leftBottom, final Vector3D sliceDim, HashMap<Vector3D, Vector3D> unused) {
    	//Fill a column by placing repeatedly boxes in left bottom behind corner
    	Integer i = 0;
        while (sortedOrderList.size() > i && (ord.getOrderMap().get(sortedOrderList.get(i)) == 0 ||
                !ROTATE_CHECK.checkIsEqualOrGreater(sliceDim, sortedOrderList.get(i)))) {
            i++;
        }
        if (i == sortedOrderList.size()) {//No fit found
            if (!(sliceDim.getX() == 0 || sliceDim.getY() == 0 || sliceDim.getZ() == 0)) {
            	//Add the top to unused surface Map 
                unused.put(leftBottom, new Vector3D(sliceDim.getX(), 0, sliceDim.getZ()));
            }
            return;
        } 
        else {
            Integer currQty = ord.getOrderMap().get(sortedOrderList.get(i));
            ord.getOrderMap().put(sortedOrderList.get(i), currQty - 1);

           //Update packing of the box
            Part p = sortedOrderList.get(i).copy();
            b.addPart(p,leftBottom);
            
            //Push the top surface of part in the stack
            s.push(new Surface(leftBottom, p.getDimension())); // top getSurface() of part

            upwardFill(s, b, ord, sortedOrderList, new Vector3D(leftBottom.getX(), leftBottom.getY() + p.getDimension().getY(), leftBottom.getZ()),
                    new Vector3D(p.getDimension().getX(), sliceDim.getY() - p.getDimension().getY(), p.getDimension().getZ()), unused);
        }
    }

	private void backwardFill(Stack<Surface> s, Box b, WarehouseOrder ord, final List<Part> sortedOrderList,
		final Surface surf, HashMap<Vector3D, Vector3D> unused) {
    	//BackTrack from top of upward Filled column and fill the unused front and side surfaces generated in UpwardFill
    	while (s.peek() != surf) {
            Surface top = s.pop();
            Surface bottom = s.peek();

            //try to fill front portion BIGGER
            Vector3D frontSliceDim = new Vector3D(bottom.getSurface().getX(), b.getDimension().getY() - bottom.getLeftUpperBehind().getY(), bottom.getSurface().getZ() - top.getSurface().getZ());
            Vector3D frontSlicelbb = new Vector3D(bottom.getLeftUpperBehind().getX(), bottom.getLeftUpperBehind().getY(), bottom.getLeftUpperBehind().getZ() + top.getSurface().getZ());
            fillBox(s, b, ord, sortedOrderList, frontSlicelbb, frontSliceDim, unused);

            //try to fill side portion SMALLER
            Vector3D sideSliceDim = new Vector3D(bottom.getSurface().getX() - top.getSurface().getX(), b.getDimension().getY() - bottom.getLeftUpperBehind().getY(), top.getSurface().getZ());
            Vector3D sideSlicelbb = new Vector3D(bottom.getLeftUpperBehind().getX() + top.getSurface().getX(), bottom.getLeftUpperBehind().getY(), bottom.getLeftUpperBehind().getZ());
            fillBox(s, b, ord, sortedOrderList, sideSlicelbb, sideSliceDim, unused);
        }
        s.pop();
    }

	private void fillBox(Stack<Surface> s, Box b, WarehouseOrder ord, final List<Part> sortedOrderList,
    	final Vector3D leftBottom, final Vector3D sliceDim, HashMap<Vector3D, Vector3D> unused) {
    	//Fill the box by combination of forward (upward Fill) and backward ( backward Fill) propagation
    	
    	Integer i = 0;
        while (sortedOrderList.size() > i && (ord.getOrderMap().get(sortedOrderList.get(i)) == 0 ||
        		!ROTATE_CHECK.checkIsEqualOrGreater(sliceDim, sortedOrderList.get(i)))) {
            i++;
        }
        if (i == sortedOrderList.size()) {//No fit found
            if (!(sliceDim.getX() == 0 || sliceDim.getY() == 0 || sliceDim.getZ() == 0)) {
            	//Add the top to unused surface Map 
                unused.put(leftBottom, new Vector3D(sliceDim.getX(), 0, sliceDim.getZ()));
            }
            return;
        } 
    	else {
    		
    		Integer currQty = ord.getOrderMap().get(sortedOrderList.get(i));
            ord.getOrderMap().put(sortedOrderList.get(i), currQty - 1);
            
            //Push initial bottom of box in stack
            Surface partBottom = new Surface(leftBottom, new Vector3D(sliceDim.getX(), 0, sliceDim.getZ()));
            s.push(partBottom);// bottom getSurface() of box

            //Update packing of the box
            Part p = sortedOrderList.get(i).copy();
            b.addPart(p,leftBottom);
            
            //Push top surface of part in stack
            s.push(new Surface(leftBottom, p.getDimension())); // top getSurface() of part

            upwardFill(s, b, ord, sortedOrderList, new Vector3D(leftBottom.getX(), leftBottom.getY() + p.getDimension().getY(), leftBottom.getZ()),
                    new Vector3D(p.getDimension().getX(), sliceDim.getY() - p.getDimension().getY(), p.getDimension().getZ()), unused);
            backwardFill(s, b, ord, sortedOrderList, partBottom, unused); // Fill completely only the column with bottom getSurface() as bottom of box
        }
    }

    private List<Part> expandOrder(final WarehouseOrder ord) {
    	//Return order with duplicate items s.t all items have quantity = 1
    	List<Part> expList = new ArrayList<>();
        for (Part p : ord.getOrderMap().keySet()) {
            for (int i = 0; i < ord.getOrderMap().get(p); i++) {
                expList.add(p.copy());
            }
        }
        return expList;

    }

    private List<Part> rotate(final Part p) {
    	//Return all possible rotations of the part
        List<Part> out = new ArrayList<Part>();
        Integer x = p.getDimension().getX(), y = p.getDimension().getY(), z = p.getDimension().getZ();
        if (x != y && y != z && z != x) {
            out.add(new Part(p.getId(), new Vector3D(x, y, z), p.getWeight()));
            out.add(new Part(p.getId(), new Vector3D(x, z, y), p.getWeight()));
            out.add(new Part(p.getId(), new Vector3D(y, x, z), p.getWeight()));
            out.add(new Part(p.getId(), new Vector3D(y, z, x), p.getWeight()));
            out.add(new Part(p.getId(), new Vector3D(z, x, y), p.getWeight()));
            out.add(new Part(p.getId(), new Vector3D(z, y, x), p.getWeight()));
        } else if (x == y && y == z) {
            out.add(new Part(p.getId(), new Vector3D(x, x, x), p.getWeight()));
        } else if (x == y) {
            out.add(new Part(p.getId(), new Vector3D(x, x, z), p.getWeight()));
            out.add(new Part(p.getId(), new Vector3D(x, z, x), p.getWeight()));
            out.add(new Part(p.getId(), new Vector3D(z, x, x), p.getWeight()));
        } else if (y == z) {
            out.add(new Part(p.getId(), new Vector3D(x, z, z), p.getWeight()));
            out.add(new Part(p.getId(), new Vector3D(z, x, z), p.getWeight()));
            out.add(new Part(p.getId(), new Vector3D(z, z, x), p.getWeight()));
        } else if (x == z) {
            out.add(new Part(p.getId(), new Vector3D(x, x, y), p.getWeight()));
            out.add(new Part(p.getId(), new Vector3D(x, y, x), p.getWeight()));
            out.add(new Part(p.getId(), new Vector3D(y, x, x), p.getWeight()));
        }
        return out;
    }

    private List<WarehouseOrder> genRotations(final List<Part> ord, final Integer start) {
        //Generate orders with all possible permutation and combinations of possible rotations 
    	List<WarehouseOrder> listOfOrders = new ArrayList<WarehouseOrder>();
        List<Part> startRotations = rotate(ord.get(start));
        if (start != ord.size() - 1) {
            List<WarehouseOrder> tmp = genRotations(ord, start + 1);
            for (Part p : startRotations) {
                for (WarehouseOrder t : tmp) {
                    Map<Part,Integer> tmpOrderList = new HashMap<>();
                    for (Part s : t.getOrderMap().keySet()) {
                        tmpOrderList.put(s.copy(),1);
                    }
                    listOfOrders.add(new WarehouseOrder(tmpOrderList));
                    listOfOrders.get(listOfOrders.size() - 1).getOrderMap().put(p, 1);
                }
            }
        } 
        else {
            for (Part p : startRotations) {
                Map<Part,Integer> tmp = new HashMap<>();
                tmp.put(p,1);
                listOfOrders.add(new WarehouseOrder(tmp));
            }
        }
        return listOfOrders;
    }
   
}
