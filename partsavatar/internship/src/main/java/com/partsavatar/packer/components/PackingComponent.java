package com.partsavatar.packer.components;

import lombok.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ToString(exclude = "remainingWarehouseOrder")
@RequiredArgsConstructor(access = AccessLevel.PUBLIC)
@Data
public class PackingComponent {
    @NonNull
    private Integer currStep, currVoid;
    @NonNull
    private Double currCompletion;
    @NonNull
    private WarehouseOrder remainingWarehouseOrder;
    private Map<Box, List<Box>> boxMap;
    private Double finalAccuracy;

    public Map<Box, List<Box>> copyBoxMap() {
    	Map<Box, List<Box>> copy = new HashMap<>();
        for (Box box : boxMap.keySet()) {
        	copy.put(box,new ArrayList<>());
        	for (Box insideBox : boxMap.get(box)) {
        		Box b = new Box(insideBox.getDimension(), insideBox.getId());
                b.setPartPositionMap(insideBox.copyParts());
                copy.get(box).add(insideBox);
        	}
        }
        return copy;
    }

    public PackingComponent copy() {
        PackingComponent vs = new PackingComponent(currStep, currVoid, currCompletion, remainingWarehouseOrder);
        vs.finalAccuracy = finalAccuracy;
        vs.boxMap = copyBoxMap();
        return vs;
    }

    public int compareTo(final PackingComponent v) {
        Integer lessThan = 1;
        Integer greaterThan = -1;
        if (v.currStep < currStep)
            return greaterThan;
        else if (v.currStep > currStep)
            return lessThan;
        else {
            if (v.currVoid < currVoid)
                return lessThan;
            else if (v.currVoid > currVoid)
                return greaterThan;
            else
                return 0;
        }
    }

    public void addBox(final Box box) {
        Box tmpBox = new Box(box.getDimension(), box.getId());
    	if(boxMap == null)
    		boxMap = new HashMap<>();
    	if(!boxMap.containsKey(tmpBox))
    		boxMap.put(tmpBox, new ArrayList<>());
        boxMap.get(tmpBox).add(box);
    }
}
