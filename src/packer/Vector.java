package packer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;



public class Vector {
	public Integer x, y, z;
	public Vector(Integer x, Integer  y, Integer  z){
		this.x = x; this.y = y; this.z = z;
	}
	public String toString(){
		return "x:" + x.toString() + " y:" + y.toString() +  " z:" + z.toString();
	}
	private ArrayList<Direction> sort(){
		Integer max = x, middle = y, min = z;
		ArrayList<Direction> tmp = new ArrayList<>();
		tmp.add(new Direction('x', max));tmp.add(new Direction('y', middle));tmp.add(new Direction('z', min));
		Collections.sort(tmp,new Comparator<Direction>() {
			@Override
	        public int compare(Direction d1, Direction d2) {
				if(d1.val < d2.val)
					return 1;
				else if (d1.val > d2.val)
					return -1;
				else
					return 0;
	        }
	    });
		return tmp; 
	}
	public boolean isEqualOrGreater(Part p){
		ArrayList<Direction> tmpPart = p.dimension.sort();
		ArrayList<Direction> tmpBox = this.sort();
		
		if(tmpBox.get(0).val >= tmpPart.get(0).val && tmpBox.get(1).val >= tmpPart.get(1).val && tmpBox.get(2).val >= tmpPart.get(2).val){
			
			if(tmpBox.get(0).c.equals('x'))
				p.dimension.x = tmpPart.get(0).val;
			else if(tmpBox.get(0).c.equals('y'))
				p.dimension.y = tmpPart.get(0).val;
			else if(tmpBox.get(0).c.equals('z'))
				p.dimension.z = tmpPart.get(0).val;
			
			if(tmpBox.get(1).c.equals('x'))
				p.dimension.x = tmpPart.get(1).val;
			else if(tmpBox.get(1).c.equals('y'))
				p.dimension.y = tmpPart.get(1).val;
			else if(tmpBox.get(1).c.equals('z'))
				p.dimension.z = tmpPart.get(1).val;
			
			if(tmpBox.get(2).c.equals('x'))
				p.dimension.x = tmpPart.get(2).val;
			else if(tmpBox.get(2).c.equals('y'))
				p.dimension.y = tmpPart.get(2).val;
			else if(tmpBox.get(2).c.equals('z'))
				p.dimension.z = tmpPart.get(2).val;
			
			return true;
		}
		else
			return false;
	}
}

