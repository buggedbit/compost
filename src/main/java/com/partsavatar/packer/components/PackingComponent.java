package com.partsavatar.packer.components;

import lombok.*;

import java.util.ArrayList;

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
    private ArrayList<Box> boxes;
    private Double finalAccuracy;

    public ArrayList<Box> copyBoxes() {
        ArrayList<Box> copy = new ArrayList<Box>();
        for (Box box : boxes) {
            Box b = new Box(box.getDimension(), box.getId());
            b.setNum(box.getNum());
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
        boxes.add(box);
    }
}
