package com.partsavatar.packer.components;

import lombok.Data;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.List;

public @Data class Vector3D {
    @NonNull
    private Integer x, y, z;
    
    @Data
	public
    class Direction {
        @NonNull
        private Integer val;
        @NonNull
        private Character c;

        public int compareTo(final Direction d) {
            if (this.val < d.val)
                return 1;
            else if (this.val > d.val)
                return -1;
            else
                return 0;
        }

    }
    
    public List<Direction> sort() {
        Integer max = x, middle = y, min = z;

        List<Direction> tmp = new ArrayList<Direction>();
        tmp.add(new Direction(max, 'x'));
        tmp.add(new Direction(middle, 'y'));
        tmp.add(new Direction(min, 'z'));

        tmp.sort(Direction::compareTo);
        return tmp;
    }  
}
