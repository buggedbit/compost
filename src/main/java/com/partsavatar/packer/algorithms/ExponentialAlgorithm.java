package com.partsavatar.packer.algorithms;


import com.partsavatar.packer.components.Box;
import com.partsavatar.packer.components.Part;
import com.partsavatar.packer.components.Vector3D;
import com.partsavatar.packer.components.WarehouseOrder;

import lombok.NonNull;
import lombok.Data;

public class ExponentialAlgorithm extends BacktrackAlgortihmBaseClass {
	
	public OrderData exponentialAlgorithm(final Box b, final WarehouseOrder newWarehouseOrder) {
		return fillBox(new OrderData(b, newWarehouseOrder), new Vector3D(0, 0, 0), 0);
    }
	
	@Data
	public class OrderData{
		@NonNull
		Box box;
		@NonNull
		WarehouseOrder order;
		
		public OrderData copy() {
			return new OrderData(box.copy(),order.copy());
		}
	}
	
    private OrderData fillSectionC(final OrderData data, final Vector3D partDim, final Vector3D origLeftBottom, final Integer i) {
        Vector3D newBoxDim = new Vector3D(partDim.getX(), data.box.getDimension().getY() - partDim.getY(), partDim.getZ());
        Vector3D newLeftBottom = new Vector3D(origLeftBottom.getX(), origLeftBottom.getY() + partDim.getY(), origLeftBottom.getZ());

        OrderData copyData = data.copy();
        copyData.box.setDimension(newBoxDim);
        copyData = fillBox(copyData, newLeftBottom, i);       
        copyData.box.setDimension(data.box.getDimension());
        return copyData;
    }

    private OrderData fillSectionB(final OrderData data, final Vector3D partDim, final Vector3D origLeftBottom, final Integer i) {
        Vector3D newBoxDim = new Vector3D(partDim.getX(), data.box.getDimension().getY(), data.box.getDimension().getZ() - partDim.getZ());
        Vector3D newLeftBottom = new Vector3D(origLeftBottom.getX(), origLeftBottom.getY(), origLeftBottom.getZ() + partDim.getZ());

        OrderData copyData = data.copy();
        copyData.box.setDimension(newBoxDim);
        copyData = fillBox(copyData, newLeftBottom, i);       
        copyData.box.setDimension(data.box.getDimension());
        return copyData;
    }

    private OrderData fillSectionA(final OrderData data, final Vector3D partDim, final Vector3D origLeftBottom, final Integer i) {
        Vector3D newBoxDim = new Vector3D(data.box.getDimension().getX() - partDim.getX(), data.box.getDimension().getY(), data.box.getDimension().getZ());
        Vector3D newLeftBottom = new Vector3D(origLeftBottom.getX() + partDim.getX(), origLeftBottom.getY(), origLeftBottom.getZ());

        OrderData copyData = data.copy();
        copyData.box.setDimension(newBoxDim);
        copyData = fillBox(copyData, newLeftBottom, i);       
        copyData.box.setDimension(data.box.getDimension());
        return copyData;
    }

    private OrderData fillBox(final OrderData data, final Vector3D leftBottom, final Integer i) {
        //Get first part in volume sorted orderList that could fit in the box 
    	Integer iCopy = i;
    	OrderData dataCopy = data.copy();
    			
    	while (dataCopy.order.getOrderList().size() > iCopy && (dataCopy.order.getOrderList().get(iCopy).getQuantity() == 0 ||
                !dataCopy.box.getDimension().rotateAndCheckIsEqualOrGreater(dataCopy.order.getOrderList().get(iCopy)))) {
            iCopy++;
        }
    	if (iCopy == dataCopy.order.getOrderList().size()) {
    		//No item in order fits in the box
    		return dataCopy;
        }
        else {
            Integer currQty = dataCopy.order.getOrderList().get(iCopy).getQuantity();
            dataCopy.order.getOrderList().get(iCopy).setQuantity(currQty - 1);

            Part p = dataCopy.order.getOrderList().get(iCopy).copy();
            p.setPosition(leftBottom);
            p.setQuantity(1);
            dataCopy.box.addPart(p);
            
            //Divide the box (with part placed at Left Bottom Behind corner) in 3 boxes and fill them recursively 
            dataCopy = fillSectionC(dataCopy, p.getDimension(), leftBottom, iCopy);
            dataCopy = fillSectionB(dataCopy, p.getDimension(), leftBottom, iCopy);    
            dataCopy = fillSectionA(dataCopy, p.getDimension(), leftBottom, iCopy);
        }
    	return dataCopy;
    }
}
