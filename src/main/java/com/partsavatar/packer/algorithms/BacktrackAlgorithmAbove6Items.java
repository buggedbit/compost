package com.partsavatar.packer.algorithms;

import com.partsavatar.packer.components.*;

import java.util.HashMap;
import java.util.List;
import java.util.Stack;


public class BacktrackAlgorithmAbove6Items extends BacktrackAlgortihmBaseClass {

	static WarehouseOrder backtrackAlgorithm(Box b, final WarehouseOrder newWarehouseOrder) {
        WarehouseOrder copyOrder = newWarehouseOrder.copy();
        HashMap<Vector3D, Vector3D> tmpUnused = new HashMap<>();
        fillBox(new Stack<Surface>(), b, copyOrder, copyOrder.volSort(), new Vector3D(0, 0, 0), b.getDimension(), tmpUnused);

        //Combine adjacent unused surface, create bigger volume, try to fill it with remaining order
        HashMap<Vector3D, Vector3D> unFilled = combineUnused(tmpUnused);
        for (Vector3D key : unFilled.keySet()) {
            fillBox(new Stack<Surface>(), b, copyOrder, copyOrder.volSort(), key,
            		new Vector3D(unFilled.get(key).getX(), b.getDimension().getY() - key.getY(), unFilled.get(key).getZ()), tmpUnused);
        }
        return copyOrder;
    }
	
    private static void upwardFill(Stack<Surface> s, Box b, WarehouseOrder ord, final List<Part> sortedOrderList,
    		final Vector3D leftBottom, final Vector3D sliceDim, HashMap<Vector3D, Vector3D> unused) {
    	//Fill a column by placing repeatedly boxes in left bottom behind corner
    	Integer i = 0;
        while (sortedOrderList.size() > i && (ord.getOrderMap().get(sortedOrderList.get(i)) == 0 ||
                !sliceDim.bestRotateAndCheckIsEqualOrGreater(ord.getMinPartSize(), sortedOrderList.get(i)))) {
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
    

    private static void backwardFill(Stack<Surface> s, Box b, WarehouseOrder ord, final List<Part> sortedOrderList,
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

    
    private static void fillBox(Stack<Surface> s, Box b, WarehouseOrder ord, final List<Part> sortedOrderList,
    	final Vector3D leftBottom, final Vector3D sliceDim, HashMap<Vector3D, Vector3D> unused) {
    	//Fill the box by combination of forward (upward Fill) and backward ( backward Fill) propagation
    	
    	Integer i = 0;
        while (sortedOrderList.size() > i && (ord.getOrderMap().get(sortedOrderList.get(i)) == 0 ||
                !sliceDim.bestRotateAndCheckIsEqualOrGreater(ord.getMinPartSize(), sortedOrderList.get(i)))) {
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
}
