package com.partsavatar.packer.algorithms;


import com.partsavatar.packer.components.Box;
import com.partsavatar.packer.components.Part;
import com.partsavatar.packer.components.Vector3D;
import com.partsavatar.packer.components.WarehouseOrder;

public class ExponentialAlgorithm extends BacktrackAlgortihmBaseClass {
	
	static WarehouseOrder exponentialAlgortihm(final Box b, final WarehouseOrder newWarehouseOrder) {
        fillBox(b, newWarehouseOrder, new Vector3D(0, 0, 0), 0);
        return newWarehouseOrder;
    }
		
    private static void fillSectionC(final Box b, final WarehouseOrder newWarehouseOrder, final Vector3D partDim, final Vector3D boxDim, final Vector3D origLeftBottom, final Integer i) {
        Vector3D newBoxDim = new Vector3D(partDim.getX(), boxDim.getY() - partDim.getY(), partDim.getZ());
        Vector3D newLeftBottom = new Vector3D(origLeftBottom.getX(), origLeftBottom.getY() + partDim.getY(), origLeftBottom.getZ());

        b.setDimension(newBoxDim);
        fillBox(b, newWarehouseOrder, newLeftBottom, i);
        b.setDimension(boxDim);
    }

    private static void fillSectionB(final Box b, final WarehouseOrder newWarehouseOrder, final Vector3D partDim, final Vector3D boxDim, final Vector3D origLeftBottom, final Integer i) {
        Vector3D newBoxDim = new Vector3D(partDim.getX(), boxDim.getY(), boxDim.getZ() - partDim.getZ());
        Vector3D newLeftBottom = new Vector3D(origLeftBottom.getX(), origLeftBottom.getY(), origLeftBottom.getZ() + partDim.getZ());

        b.setDimension(newBoxDim);
        fillBox(b, newWarehouseOrder, newLeftBottom, i);
        b.setDimension(boxDim);
    }

    private static void fillSectionA(final Box b, final WarehouseOrder newWarehouseOrder, final Vector3D partDim, final Vector3D boxDim, final Vector3D origLeftBottom, final Integer i) {
        Vector3D newBoxDim = new Vector3D(boxDim.getX() - partDim.getX(), boxDim.getY(), boxDim.getZ());
        Vector3D newLeftBottom = new Vector3D(origLeftBottom.getX() + partDim.getX(), origLeftBottom.getY(), origLeftBottom.getZ());

        b.setDimension(newBoxDim);
        fillBox(b, newWarehouseOrder, newLeftBottom, i);
        b.setDimension(boxDim);
    }

    private static void fillBox(final Box b, final WarehouseOrder ord, final Vector3D leftBottom, final Integer i) {
        //Get first part in volume sorted orderList that could fit in the box 
    	Integer iCopy = i;
    	while (ord.getOrderList().size() > i && (ord.getOrderList().get(i).getQuantity() == 0 ||
                !b.getDimension().rotateAndCheckIsEqualOrGreater(ord.getOrderList().get(i)))) {
            iCopy++;
        }
    	if (i == ord.getOrderList().size()) {
    		//No item in order fits in the box
    		return;
        }
        else {
            Integer currQty = ord.getOrderList().get(i).getQuantity();
            ord.getOrderList().get(i).setQuantity(currQty - 1);

            Part p = ord.getOrderList().get(i).copy();
            p.setPosition(leftBottom);
            p.setQuantity(1);
            b.addPart(p);
            
            //Divide the box (with part placed at Left Bottom Behind corner) in 3 boxes and fill them recursively 
            fillSectionC(b, ord, p.getDimension(), b.getDimension(), leftBottom, iCopy);
            fillSectionB(b, ord, p.getDimension(), b.getDimension(), leftBottom, iCopy);
            fillSectionA(b, ord, p.getDimension(), b.getDimension(), leftBottom, iCopy);
        }

    }
}
