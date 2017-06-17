package com.partsavatar.packer.components;

import lombok.*;

import java.util.ArrayList;

@ToString(exclude = "remainingOrder")
@RequiredArgsConstructor(access = AccessLevel.PUBLIC)
public @Data
class PackingComponent {
    @NonNull
    Integer currStep, currVoid;
    @NonNull
    Double currCompletion;
    @NonNull
    WarehouseOrder remainingWarehouseOrder;
    ArrayList<Box> boxes;
    Double finalAccuracy;

    public ArrayList<Box> copyBoxes() {
        ArrayList<Box> copy = new ArrayList<Box>();
        for (Box box : boxes) {
            Box b = new Box(box.dimension, box.id);
            b.num = box.num;
            copy.add(b);
            copy.get(copy.size() - 1).setParts(box.copyParts());
        }
        return copy;
    }

    public PackingComponent copy() {
        PackingComponent vs = new PackingComponent(currStep, currVoid, currCompletion, remainingWarehouseOrder);
        vs.finalAccuracy = finalAccuracy;
        vs.boxes = copyBoxes();
        return vs;
    }

    public int compareTo(PackingComponent v) {
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

    public void addBox(Box box) {
        boxes.add(box);
    }
}
